package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.query.Query;

public record GetByIdSpacecraftQuery(UUID spacecraftId) implements Query{

}
