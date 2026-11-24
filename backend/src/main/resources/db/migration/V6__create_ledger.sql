-- V6: FR-LED-01/02/03/04
CREATE TABLE transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    type            VARCHAR(30) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'POSTED',
    description     VARCHAR(500),
    reversed_by_tx  UUID,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tx_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tx_reversed_by FOREIGN KEY (reversed_by_tx)
        REFERENCES transactions(id),
    CONSTRAINT ck_tx_type CHECK (type IN (
        'INCOME','EXPENSE','TRANSFER_INTERNAL','TRANSFER_EXTERNAL'
    )),
    CONSTRAINT ck_tx_status CHECK (status IN ('POSTED','REVERSED'))
);

CREATE INDEX idx_tx_user_id    ON transactions (user_id);
CREATE INDEX idx_tx_created_at ON transactions (created_at DESC);

-- ---- اسناد دفترکل ----
CREATE TABLE ledger_entries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id  UUID NOT NULL,
    account_id      UUID NOT NULL,
    direction       CHAR(1) NOT NULL,     -- 'D' = debit, 'C' = credit
    amount          NUMERIC(20,4) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_le_tx      FOREIGN KEY (transaction_id)
        REFERENCES transactions(id) ON DELETE CASCADE,
    CONSTRAINT fk_le_account FOREIGN KEY (account_id)
        REFERENCES accounts(id),
    CONSTRAINT ck_le_direction CHECK (direction IN ('D','C')),
    CONSTRAINT ck_le_amount    CHECK (amount > 0)
);

CREATE INDEX idx_le_tx_id      ON ledger_entries (transaction_id);
CREATE INDEX idx_le_account_id ON ledger_entries (account_id);