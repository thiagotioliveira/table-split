package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import java.util.Set;
import java.util.UUID;

public record UpdateStaffCommand(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String password,
    boolean enabled,
    Set<Module> modules) {}
