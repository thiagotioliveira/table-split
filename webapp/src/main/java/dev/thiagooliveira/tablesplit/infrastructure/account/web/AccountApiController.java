package dev.thiagooliveira.tablesplit.infrastructure.account.web;

import dev.thiagooliveira.tablesplit.application.account.CancelAccount;
import dev.thiagooliveira.tablesplit.application.account.CancelAccount.CancellationResult;
import dev.thiagooliveira.tablesplit.application.account.RequestAccountCancellation;
import dev.thiagooliveira.tablesplit.domain.account.*;
import dev.thiagooliveira.tablesplit.domain.order.OrderRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.account.event.AccountCancelledEvent;
import dev.thiagooliveira.tablesplit.infrastructure.web.account.api.spec.v1.AccountApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.account.api.spec.v1.model.AccountDetailsResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.account.api.spec.v1.model.ConfirmCancellationRequest;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/account")
public class AccountApiController implements AccountApi {

  private final AccountRepository accountRepository;
  private final RestaurantRepository restaurantRepository;
  private final UserRepository userRepository;
  private final StaffRepository staffRepository;
  private final OrderRepository orderRepository;
  private final PendingAccountCancellationRepository pendingAccountCancellationRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final HttpServletRequest request;
  private final CancelAccount cancelAccount;
  private final RequestAccountCancellation requestAccountCancellation;

  public AccountApiController(
      AccountRepository accountRepository,
      RestaurantRepository restaurantRepository,
      UserRepository userRepository,
      StaffRepository staffRepository,
      OrderRepository orderRepository,
      PendingAccountCancellationRepository pendingAccountCancellationRepository,
      ApplicationEventPublisher eventPublisher,
      HttpServletRequest request,
      CancelAccount cancelAccount,
      RequestAccountCancellation requestAccountCancellation) {
    this.accountRepository = accountRepository;
    this.restaurantRepository = restaurantRepository;
    this.userRepository = userRepository;
    this.staffRepository = staffRepository;
    this.orderRepository = orderRepository;
    this.pendingAccountCancellationRepository = pendingAccountCancellationRepository;
    this.eventPublisher = eventPublisher;
    this.request = request;
    this.cancelAccount = cancelAccount;
    this.requestAccountCancellation = requestAccountCancellation;
  }

  private AccountContext getContext() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AccountContext)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    return (AccountContext) auth.getPrincipal();
  }

  @Override
  public ResponseEntity<AccountDetailsResponse> getAccountDetails() {
    AccountContext context = getContext();
    UUID accountId = context.getId();

    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

    Restaurant restaurant =
        restaurantRepository
            .findByAccountId(accountId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

    List<User> users = userRepository.findByAccountId(accountId);
    User adminUser =
        users.stream()
            .filter(u -> u.getRole() == Role.RESTAURANT_ADMIN)
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found"));

    List<Staff> staffList = staffRepository.findByRestaurantId(restaurant.getId());
    long activeStaffCount = staffList.stream().filter(Staff::isEnabled).count();
    int activeUsersCount = users.size() + (int) activeStaffCount;

    // Calculate orders this month
    ZonedDateTime now =
        ZonedDateTime.now(dev.thiagooliveira.tablesplit.domain.common.Time.getZoneId());
    ZonedDateTime startOfMonth =
        now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    ZonedDateTime endOfMonth = startOfMonth.plusMonths(1);
    var historySummary =
        orderRepository.getHistorySummary(restaurant.getId(), startOfMonth, endOfMonth);
    int ordersThisMonth = (int) historySummary.totalOrders();

    AccountDetailsResponse response = new AccountDetailsResponse();
    response.setId(account.getId());
    response.setPlan(account.getEffectivePlan().name());
    response.setName(restaurant.getName());
    response.setMainEmail(adminUser.getEmail());
    response.setCreatedAt(account.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    response.setTimezone(dev.thiagooliveira.tablesplit.domain.common.Time.getZoneId().getId());
    response.setOrdersThisMonth(ordersThisMonth);
    response.setActiveUsers(activeUsersCount);

    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> requestAccountCancellation() {
    AccountContext context = getContext();
    UUID accountId = context.getId();

    String baseUrl =
        request
            .getRequestURL()
            .toString()
            .replace(request.getRequestURI(), request.getContextPath());

    try {
      requestAccountCancellation.execute(accountId, context.getRestaurant().getName(), baseUrl);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> confirmAccountCancellation(
      @RequestBody @Valid ConfirmCancellationRequest confirmCancellationRequest) {
    AccountContext context = getContext();
    UUID accountId = context.getId();

    try {
      CancellationResult result =
          cancelAccount.execute(accountId, confirmCancellationRequest.getCode());
      eventPublisher.publishEvent(
          new AccountCancelledEvent(
              result.email(), result.firstName(), result.language(), result.restaurantName()));
    } catch (IllegalArgumentException | IllegalStateException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    return ResponseEntity.ok().build();
  }
}
