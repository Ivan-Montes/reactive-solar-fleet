package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record DeleteSpacecraftCommand(UUID spacecraftId) implements Command {

}
