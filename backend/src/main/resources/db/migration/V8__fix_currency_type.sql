-- V8: تبدیل currency از CHAR(3) به VARCHAR(3)
ALTER TABLE accounts
    ALTER COLUMN currency TYPE VARCHAR(3) USING currency::VARCHAR;