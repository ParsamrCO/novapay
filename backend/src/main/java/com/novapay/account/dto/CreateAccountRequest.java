package com.novapay.account.dto;

import com.novapay.account.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
    @NotBlank @Size(min = 2 , max = 100) String name ,
    @NotNull AccountType type
) {}
