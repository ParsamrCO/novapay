package com.novapay.ledger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.novapay.account.Account;
import com.novapay.account.AccountRepository;
import com.novapay.account.AccountStatus;

import jakarta.transaction.Transactional;

@Service 
public class LedgerService {
    private TransactionRepository txRepo;
    private AccountRepository accountRepo;
    private LedgerEntryRepository entryRepo;

    public LedgerService(TransactionRepository txRepo , LedgerEntryRepository entryRepo , AccountRepository accountRepo){
        this.txRepo = txRepo ;
        this.entryRepo = entryRepo;
        this.accountRepo = accountRepo ; 
    }

    @Transactional
    public Transaction postTransaction(UUID userId,
                                       TransactionType type,
                                       String description,
                                       List<EntrySpec> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalStateException("LEDGER_MIN_ENTRIES");
        }

        // ۱. توازن
        BigDecimal totalD = BigDecimal.ZERO;
        BigDecimal totalC = BigDecimal.ZERO;
        for (EntrySpec e : entries) {
            if (e.amount() == null || e.amount().signum() <= 0) {
                throw new IllegalStateException("LEDGER_INVALID_AMOUNT");
            }
            if (e.direction() == Direction.D) totalD = totalD.add(e.amount());
            else totalC = totalC.add(e.amount());
        }
        if (totalD.compareTo(totalC) != 0) {
            throw new IllegalStateException("LEDGER_IMBALANCE");
        }

        // ۲. حساب‌ها معتبر باشن
        Set<UUID> accountIds = new HashSet<>();
        for (EntrySpec e : entries) accountIds.add(e.accountId());
        Map<UUID, Account> accounts = new HashMap<>();
        for (UUID aid : accountIds) {
            Account a = accountRepo.findById(aid)
                .orElseThrow(() -> new IllegalStateException("ACCOUNT_NOT_FOUND"));
            if (!a.getUserId().equals(userId)) {
                throw new IllegalStateException("ACCOUNT_FORBIDDEN");
            }
            if (a.getStatus() != AccountStatus.ACTIVE) {
                throw new IllegalStateException("ACCOUNT_CLOSED");
            }
            accounts.put(aid, a);
        }

        // ۳. ثبت تراکنش
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setType(type);
        tx.setStatus(TransactionStatus.POSTED);
        tx.setDescription(description);
        txRepo.save(tx);

        // ۴. ثبت اسناد
        for (EntrySpec e : entries) {
            LedgerEntry le = new LedgerEntry();
            le.setTransactionId(tx.getId());
            le.setAccountId(e.accountId());
            le.setDirection(e.direction());
            le.setAmount(e.amount());
            entryRepo.save(le);
        }

        return tx;
    }

    @Transactional
    public BigDecimal balanceOf(UUID accountId) {
        Account a = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalStateException("ACCOUNT_NOT_FOUND"));
        BigDecimal raw = entryRepo.rawBalance(accountId);  // D − C
        // Asset: raw همان موجودی است (D افزایش، C کاهش)
        // Liability: باید معکوس شود (افزایش بدهی = C، کاهش = D)
        return a.getType().isAsset() ? raw : raw.negate();
    }

    @Transactional
    public Transaction reverse(UUID userId, UUID originalTxId) {
        Transaction original = txRepo.findById(originalTxId)
            .orElseThrow(() -> new IllegalStateException("TX_NOT_FOUND"));
        if (!original.getUserId().equals(userId)) {
            throw new IllegalStateException("TX_FORBIDDEN");
        }
        if (original.getStatus() == TransactionStatus.REVERSED) {
            throw new IllegalStateException("TX_ALREADY_REVERSED");
        }

        List<LedgerEntry> originalEntries = entryRepo.findByTransactionId(originalTxId);

        // تراکنش معکوس
        Transaction reversal = new Transaction();
        reversal.setUserId(userId);
        reversal.setType(original.getType());
        reversal.setStatus(TransactionStatus.POSTED);
        reversal.setDescription("REVERSAL of " + originalTxId);
        txRepo.save(reversal);

        // اسناد با جهت برعکس
        for (LedgerEntry oe : originalEntries) {
            LedgerEntry re = new LedgerEntry();
            re.setTransactionId(reversal.getId());
            re.setAccountId(oe.getAccountId());
            re.setDirection(oe.getDirection() == Direction.D ? Direction.C : Direction.D);
            re.setAmount(oe.getAmount());
            entryRepo.save(re);
        }

        original.setStatus(TransactionStatus.REVERSED);
        original.setReversedByTx(reversal.getId());

        return reversal;
    }
    public record EntrySpec(UUID accountId ,Direction direction , BigDecimal amount ) {
    }
}
