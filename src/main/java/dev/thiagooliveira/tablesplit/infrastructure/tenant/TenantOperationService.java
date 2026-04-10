package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantOperationService {

  private static final Logger logger = LoggerFactory.getLogger(TenantOperationService.class);

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void runInNewTransaction(Runnable action) {
    logger.debug("Starting new transaction for tenant operation...");
    action.run();
  }
}
