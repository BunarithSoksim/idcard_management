package net.orderzone.idcard.dto;

import jakarta.validation.constraints.NotBlank;

public record TemplateRequest(
        @NotBlank String code,
        @NotBlank String name,
        String organizationName,
        String layout,
        String primaryColor,
        String secondaryColor,
        String textColor,
        String tagline
) {}