package com.example.courtreserve.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ForeignKeyConstraintException extends RuntimeException {
    private final String entityName;
    private final Long entityId;
    private final List<String> dependentEntities;

    public ForeignKeyConstraintException(String entityName, Long entityId, List<String> dependentEntities) {
        super(String.format("Cannot delete %s with id %d. It has references in: %s",
                entityName, entityId, String.join(", ", dependentEntities)));
        this.entityName = entityName;
        this.entityId = entityId;
        this.dependentEntities = dependentEntities;
    }
}
