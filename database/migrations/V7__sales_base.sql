create table if not exists sales (
    id bigserial primary key,
    operational_context_id bigint not null,
    cash_box_id bigint not null,
    sold_by_user_id bigint not null,
    status varchar(20) not null,
    subtotal_amount numeric(12,2) not null,
    total_amount numeric(12,2) not null,
    observation varchar(255) null,
    internal_receipt_series varchar(20) not null,
    internal_receipt_number bigint not null,
    created_at timestamp without time zone not null default now(),
    cancelled_at timestamp without time zone null,
    cancelled_by_user_id bigint null,
    cancellation_reason varchar(255) null,
    constraint fk_sales_context
        foreign key (operational_context_id) references operational_contexts (id),
    constraint fk_sales_cash_box
        foreign key (cash_box_id) references cash_boxes (id),
    constraint fk_sales_sold_by
        foreign key (sold_by_user_id) references users (id),
    constraint fk_sales_cancelled_by
        foreign key (cancelled_by_user_id) references users (id),
    constraint uk_sales_receipt unique (internal_receipt_series, internal_receipt_number)
);

create table if not exists sale_items (
    id bigserial primary key,
    sale_id bigint not null,
    product_id bigint not null,
    quantity numeric(12,2) not null,
    unit_price numeric(12,2) not null,
    subtotal_amount numeric(12,2) not null,
    constraint fk_sale_items_sale
        foreign key (sale_id) references sales (id),
    constraint fk_sale_items_product
        foreign key (product_id) references products (id)
);

create table if not exists sale_payments (
    id bigserial primary key,
    sale_id bigint not null,
    payment_method varchar(50) not null,
    amount numeric(12,2) not null,
    constraint fk_sale_payments_sale
        foreign key (sale_id) references sales (id)
);
