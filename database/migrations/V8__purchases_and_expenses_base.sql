create table if not exists purchases (
    id bigserial primary key,
    operational_context_id bigint not null,
    provider_id bigint not null,
    purchased_by_user_id bigint not null,
    status varchar(20) not null,
    purchase_date date not null,
    document_type varchar(50) null,
    document_number varchar(100) null,
    payment_method varchar(50) null,
    subtotal_amount numeric(12,2) not null,
    total_amount numeric(12,2) not null,
    observation varchar(255) null,
    created_at timestamp without time zone not null default now(),
    cancelled_at timestamp without time zone null,
    cancelled_by_user_id bigint null,
    cancellation_reason varchar(255) null,
    constraint fk_purchases_context
        foreign key (operational_context_id) references operational_contexts (id),
    constraint fk_purchases_provider
        foreign key (provider_id) references providers (id),
    constraint fk_purchases_user
        foreign key (purchased_by_user_id) references users (id),
    constraint fk_purchases_cancelled_by
        foreign key (cancelled_by_user_id) references users (id)
);

create table if not exists purchase_items (
    id bigserial primary key,
    purchase_id bigint not null,
    product_id bigint not null,
    quantity numeric(12,2) not null,
    cancelled_quantity numeric(12,2) not null default 0,
    unit_cost numeric(12,2) not null,
    subtotal_amount numeric(12,2) not null,
    constraint fk_purchase_items_purchase
        foreign key (purchase_id) references purchases (id),
    constraint fk_purchase_items_product
        foreign key (product_id) references products (id)
);

create table if not exists expenses (
    id bigserial primary key,
    operational_context_id bigint not null,
    cash_box_id bigint null,
    recorded_by_user_id bigint not null,
    expense_type varchar(20) not null,
    category varchar(100) not null,
    description varchar(255) not null,
    payment_method varchar(50) null,
    amount numeric(12,2) not null,
    responsible varchar(120) null,
    observation varchar(255) null,
    expense_date date not null,
    created_at timestamp without time zone not null default now(),
    constraint fk_expenses_context
        foreign key (operational_context_id) references operational_contexts (id),
    constraint fk_expenses_cash_box
        foreign key (cash_box_id) references cash_boxes (id),
    constraint fk_expenses_user
        foreign key (recorded_by_user_id) references users (id)
);
