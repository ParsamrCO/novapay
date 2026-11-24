-- V3: FR-UM-03 — فیلدهای پروفایل
ALTER TABLE users
    ADD COLUMN avatar_url VARCHAR(500),
    ADD COLUMN bio        VARCHAR(500);