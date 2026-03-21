package dev.thiagooliveira.tablesplit.infrastructure.web.customer.order;

import dev.thiagooliveira.tablesplit.application.order.PlaceOrder;
import dev.thiagooliveira.tablesplit.application.order.model.OrderItemRequest;
import dev.thiagooliveira.tablesplit.application.order.model.PlaceOrderRequest;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.order.Order;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.order.model.CustomerOrderRequest;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/@{slug}/table/{tableCod}/order")
public class CustomerOrderController {

  private final GetRestaurant getRestaurant;
  private final PlaceOrder placeOrder;

  public CustomerOrderController(GetRestaurant getRestaurant, PlaceOrder placeOrder) {
    this.getRestaurant = getRestaurant;
    this.placeOrder = placeOrder;
  }

  @PostMapping
  public ResponseEntity<Order> placeOrder(
      @PathVariable String slug,
      @PathVariable String tableCod,
      @RequestBody CustomerOrderRequest request) {

    var restaurant = getRestaurant.execute(slug).orElseThrow();

    var placeOrderRequest = new PlaceOrderRequest();
    placeOrderRequest.setRestaurantId(restaurant.getId());
    placeOrderRequest.setTableCod(tableCod);
    placeOrderRequest.setServiceFee(restaurant.getServiceFee());

    var ticketRequest = new dev.thiagooliveira.tablesplit.application.order.model.TicketRequest();
    ticketRequest.setCustomerName(request.getCustomerName());
    ticketRequest.setItems(
        request.getItems().stream()
            .map(i -> new OrderItemRequest(i.getItemId(), i.getQuantity(), null))
            .collect(Collectors.toList()));

    placeOrderRequest.setTickets(java.util.List.of(ticketRequest));

    Order order = placeOrder.execute(placeOrderRequest);

    return ResponseEntity.ok(order);
  }
}
