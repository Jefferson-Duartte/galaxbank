CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    number VARCHAR(10) NOT NULL UNIQUE,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_accounts_balance_non_negative CHECK ( balance >= 0 )
);