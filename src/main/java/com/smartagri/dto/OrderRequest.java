package com.smartagri.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(@NotEmpty List<Item> items,
                           @NotBlank String address,
                           @NotBlank String paymentMethod) {
    public record Item(Long productId, Double quantity) {}
}
