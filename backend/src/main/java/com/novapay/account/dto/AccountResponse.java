package com.novapay.account.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.novapay.account.Account;

public record AccountResponse(
    UUID id ,
    String name , 
    String type , 
    String currency , 
    String status , 
    BigDecimal balance , 
    String createdAt
) {
    public static AccountResponse of(Account a, BigDecimal balance) {
    return new AccountResponse(
        a.getId(), a.getName(), a.getType().name(),
        a.getCurrency(), a.getStatus().name(),
        balance, a.getCreatedAt().toString()
    );
}
}
