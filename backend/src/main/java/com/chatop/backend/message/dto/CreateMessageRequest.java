package com.chatop.backend.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMessageRequest(
    @JsonProperty("rental_id") @NotNull Integer rentalId,
    @JsonProperty("user_id") @NotNull Integer userId,
    @NotBlank String message
) {
}
