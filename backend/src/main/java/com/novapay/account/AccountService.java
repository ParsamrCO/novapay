package com.novapay.account;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.novapay.account.dto.AccountResponse;
import com.novapay.account.dto.CreateAccountRequest;
import com.novapay.ledger.LedgerService;

import jakarta.transaction.Transactional;

@Service 
public class AccountService {
    private final AccountRepository accountRepo;
    private final LedgerService ledgerService;

    public AccountService(AccountRepository accountRepo , LedgerService ledgerService ){
        this.accountRepo = accountRepo;
        this.ledgerService = ledgerService ;
    }

    @Transactional 
    public AccountResponse create(UUID userId , CreateAccountRequest req){
        String name = req.name().trim();
        if (accountRepo.existsByUserIdAndNameIgnoreCase(userId, name)) {
            throw new IllegalStateException("ACCOUNT_NAME_TAKEN");
        }

        Account a = new Account();
        a.setUserId(userId);
        a.setName(name);
        a.setStatus(AccountStatus.ACTIVE);
        a.setType(req.type());
        a.setCurrency("IRR");    
        accountRepo.save(a);            
        return AccountResponse.of(a, BigDecimal.ZERO);
    }

    @Transactional
    public List<AccountResponse> list(UUID userId){
        return accountRepo.findByUserIdOrderByCreatedAtAsc(userId).stream().map(a -> AccountResponse.of(a, ledgerService.balanceOf(a.getId()))).toList();
    }

    @Transactional 
    public AccountResponse get(UUID userId , UUID accountId){
        Account a = accountRepo.findByIdAndUserId(accountId, userId)
            .orElseThrow(() -> new IllegalStateException("ACCOUNT_NOT_FOUND"));
        return AccountResponse.of(a,ledgerService.balanceOf(a.getId()));
    }

    @Transactional
    public AccountResponse close(UUID userId, UUID accountId) {
        Account a = accountRepo.findByIdAndUserId(accountId, userId)
            .orElseThrow(() -> new IllegalStateException("ACCOUNT_NOT_FOUND"));
        if (a.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("ACCOUNT_ALREADY_CLOSED");
        }
        BigDecimal balance = ledgerService.balanceOf(accountId);
        if (balance.signum() != 0) {
            throw new IllegalStateException("ACCOUNT_NONZERO_BALANCE");
        }
        a.setStatus(AccountStatus.CLOSED);
        return AccountResponse.of(a, balance);
    }

}
