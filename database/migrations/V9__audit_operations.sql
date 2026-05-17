create table if not exists audit_operations (
    id bigserial primary key,
    module varchar(50) not null,
    operation_type varchar(50) not null,
    entity_type varchar(50) not null,
    entity_id varchar(100) not null,
    username varchar(100) not null,
    occurred_at timestamp without time zone not null default now(),
    detail varchar(500) null
);

create index if not exists ix_audit_operations_module_occurred
    on audit_operations (module, occurred_at desc);
