package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.domain.security.Context;

public class ManagerModel<T> {
  private final Module module;
  private final Context context;
  private final T root;

  public ManagerModel(Module module, Context context, T root) {
    this.module = module;
    this.context = context;
    this.root = root;
  }

  public Module getModule() {
    return module;
  }

  public Context getContext() {
    return context;
  }

  public T getRoot() {
    return root;
  }
}
