create table if not exists cash_boxes (
    id bigserial primary key,
    operational_context_id bigint not null,
    opened_by_user_id bigint not null,
    status varchar(20) not null,
    opening_amount numeric(12,2) not null,
    opening_observation varchar(255) null,
    opened_at timestamp without time zone not null default now(),
    expected_amount numeric(12,2) null,
    counted_amount numeric(12,2) null,
    difference_amount numeric(12,2) null,
    closing_observation varchar(255) null,
    closed_at timestamp without time zone null,
    closed_by_user_id bigint null,
    constraint fk_cash_boxes_context
        foreign key (operational_context_id) references operational_contexts (id),
    constraint fk_cash_boxes_opened_by
        foreign key (opened_by_user_id) references users (id),
    constraint fk_cash_boxes_closed_by
        foreign key (closed_by_user_id) references users (id)
);

create unique index if not exists ux_cash_boxes_user_open
    on cash_boxes (opened_by_user_id)
    where status = 'ABIERTA';

create unique index if not exists ux_cash_boxes_context_open
    on cash_boxes (operational_context_id)
    where status = 'ABIERTA';

create table if not exists cash_movements (
    id bigserial primary key,
    cash_box_id bigint not null,
    movement_type varchar(30) not null,
    amount numeric(12,2) not null,
    reference_type varchar(50) not null,
    reference_id varchar(100) null,
    performed_by varchar(100) not null,
    occurred_at timestamp without time zone not null default now(),
    observation varchar(255) null,
    constraint fk_cash_movements_cash_box
        foreign key (cash_box_id) references cash_boxes (id)
);

create index if not exists ix_cash_movements_cash_box
    on cash_movements (cash_box_id, occurred_at);
