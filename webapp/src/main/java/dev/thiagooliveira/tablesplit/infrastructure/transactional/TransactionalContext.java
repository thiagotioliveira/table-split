package dev.thiagooliveira.tablesplit.infrastructure.transactional;

import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class TransactionalContext {

  public <T> T execute(Supplier<T> supplier) {
    return supplier.get();
  }

  public void execute(Runnable runnable) {
    runnable.run();
  }
}
