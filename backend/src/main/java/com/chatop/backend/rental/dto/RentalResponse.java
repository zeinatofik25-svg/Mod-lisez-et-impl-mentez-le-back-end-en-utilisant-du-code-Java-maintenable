package com.chatop.backend.rental.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record RentalResponse(
    Integer id,
    String name,
    double surface,
    double price,
    String picture,
    String description,
    @JsonProperty("owner_id") Integer ownerId,
    @JsonProperty("created_at") LocalDateTime createdAt,
    @JsonProperty("updated_at") LocalDateTime updatedAt
) {
}
