create table if not exists state (
    address Text not null,
    pool_id Text not null,
    timestamp BIGINT not null,
    erg_balance BIGINT not null,
    weight BIGINT not null
);