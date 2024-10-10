package dev.ime.application.usecase;

import java.util.UUID;

import dev.ime.domain.query.Query;

public record GetByIdCrewMemberQuery(
		UUID crewMemberId
		) implements Query {

}
