package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantOperationService {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void runInNewTransaction(Runnable action) {
    System.out.println("[TenantOperationService] Starting new transaction for tenant operation...");
    action.run();
  }
}
