package dev.ime.application.usecase;

import org.springframework.data.domain.Pageable;

import dev.ime.domain.query.Query;

public record GetAllShipclassQuery(Pageable pageable) implements Query{

}
