package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Set;
import java.util.UUID;

public record CreateStaffCommand(
    UUID restaurantId,
    String firstName,
    String lastName,
    String email,
    String phone,
    String password,
    Language language,
    Set<Module> modules) {}
