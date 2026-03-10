package dev.thiagooliveira.tablesplit.application.dashboard;

import dev.thiagooliveira.tablesplit.domain.dashboard.Dashboard;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DashboardRepository {

  List<Dashboard> findByAccountId(UUID accountId);

  Optional<Dashboard> findByUserId(UUID userId);

  void save(Dashboard dashboard);
}
