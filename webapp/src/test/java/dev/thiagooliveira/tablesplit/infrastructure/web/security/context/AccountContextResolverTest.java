package dev.thiagooliveira.tablesplit.infrastructure.web.security.context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerContextModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

class AccountContextResolverTest {

  private AccountContextResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new AccountContextResolver();
  }

  // --- resolve(Authentication) ---

  @Test
  void shouldThrowAccessDeniedWhenAuthIsNull() {
    assertThrows(AccessDeniedException.class, () -> resolver.resolve((Authentication) null));
  }

  @Test
  void shouldThrowAccessDeniedWhenPrincipalIsNotAccountContext() {
    Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn("someString");

    assertThrows(AccessDeniedException.class, () -> resolver.resolve(auth));
  }

  @Test
  void shouldReturnAccountContextWhenPrincipalIsValid() {
    Authentication auth = mock(Authentication.class);
    AccountContext context = mock(AccountContext.class);
    when(auth.getPrincipal()).thenReturn(context);

    AccountContext result = resolver.resolve(auth);

    assertSame(context, result);
  }

  // --- resolve(Model) ---

  @Test
  void shouldThrowAccessDeniedWhenModelContextIsNull() {
    Model model = mock(Model.class);
    when(model.getAttribute("context")).thenReturn(null);

    assertThrows(AccessDeniedException.class, () -> resolver.resolve(model));
  }

  @Test
  void shouldThrowAccessDeniedWhenRestaurantIsNull() {
    Model model = mock(Model.class);
    ManagerContextModel managerCtx = mock(ManagerContextModel.class);
    when(model.getAttribute("context")).thenReturn(managerCtx);
    when(managerCtx.getRestaurant()).thenReturn(null);

    assertThrows(AccessDeniedException.class, () -> resolver.resolve(model));
  }

  @Test
  void shouldReturnManagerContextWhenModelIsValid() {
    Model model = mock(Model.class);
    ManagerContextModel managerCtx = mock(ManagerContextModel.class);
    ManagerContextModel.RestaurantContextModel restaurant =
        mock(ManagerContextModel.RestaurantContextModel.class);
    when(model.getAttribute("context")).thenReturn(managerCtx);
    when(managerCtx.getRestaurant()).thenReturn(restaurant);

    ManagerContextModel result = resolver.resolve(model);

    assertSame(managerCtx, result);
  }
}
