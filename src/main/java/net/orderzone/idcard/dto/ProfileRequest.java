package net.orderzone.idcard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.orderzone.idcard.model.BarcodeType;
import net.orderzone.idcard.model.ProfileType;

import java.time.LocalDate;

public record ProfileRequest(
        @NotBlank String fullName,
        @NotNull  ProfileType type,
        String department,
        String title,
        @Email String email,
        String phone,
        String bloodGroup,
        LocalDate dateOfBirth,
        LocalDate expiryDate,
        Long templateId,
        BarcodeType barcodeType
) {}