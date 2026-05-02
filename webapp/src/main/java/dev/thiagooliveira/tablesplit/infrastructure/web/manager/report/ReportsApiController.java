package dev.thiagooliveira.tablesplit.infrastructure.web.manager.report;

import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.report.spec.v1.api.ReportsApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.report.spec.v1.model.ReportsOverviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@org.springframework.web.bind.annotation.RequestMapping("/api/v1/manager/reports")
public class ReportsApiController implements ReportsApi {

  private final GetReportsOverview getReportsOverview;

  public ReportsApiController(GetReportsOverview getReportsOverview) {
    this.getReportsOverview = getReportsOverview;
  }

  @Override
  public ResponseEntity<ReportsOverviewResponse> getReportsOverview(Integer days) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AccountContext context = (AccountContext) auth.getPrincipal();

    return ResponseEntity.ok(
        getReportsOverview.execute(context.getRestaurant().getId(), days != null ? days : 30));
  }
}
