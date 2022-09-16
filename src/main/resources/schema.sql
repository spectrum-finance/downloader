create table if not exists state (
    address Text not null,
    pool_id Text not null,
    timestamp BIGINT not null,
    lp_balance BIGINT not null,
    weight Text not null
);