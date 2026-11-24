-- V7: تضمین توازن بدهکار/بستانکار در سطح DB
-- Deferred: در پایان تراکنش چک می‌شود، نه بعد از هر INSERT

CREATE OR REPLACE FUNCTION check_ledger_balance()
RETURNS TRIGGER AS $$
DECLARE
    v_tx_id UUID;
    v_debit NUMERIC;
    v_credit NUMERIC;
BEGIN
    -- سطرهای متأثر را پیدا کن
    v_tx_id := COALESCE(NEW.transaction_id, OLD.transaction_id);

    SELECT
        COALESCE(SUM(CASE WHEN direction = 'D' THEN amount ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN direction = 'C' THEN amount ELSE 0 END), 0)
    INTO v_debit, v_credit
    FROM ledger_entries
    WHERE transaction_id = v_tx_id;

    IF v_debit <> v_credit THEN
        RAISE EXCEPTION 'LEDGER_IMBALANCE: transaction % debit=% credit=%', v_tx_id, v_debit, v_credit;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER trg_ledger_balance
    AFTER INSERT OR UPDATE ON ledger_entries
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW
    EXECUTE FUNCTION check_ledger_balance();