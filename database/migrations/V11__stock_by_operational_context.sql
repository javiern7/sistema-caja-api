alter table stock_current rename to stock_current_legacy;
alter table stock_movements rename to stock_movements_legacy;

create table stock_current (
    operational_context_id bigint not null,
    product_id bigint not null,
    current_stock numeric(12,2) not null,
    updated_at timestamp without time zone not null default now(),
    constraint pk_stock_current primary key (operational_context_id, product_id),
    constraint fk_stock_current_context
        foreign key (operational_context_id) references operational_contexts (id),
    constraint fk_stock_current_product
        foreign key (product_id) references products (id)
);

create table stock_movements (
    id bigserial primary key,
    operational_context_id bigint not null,
    product_id bigint not null,
    movement_type varchar(30) not null,
    quantity numeric(12,2) not null,
    reference_type varchar(50) not null,
    reference_id varchar(100) null,
    performed_by varchar(100) not null,
    occurred_at timestamp without time zone not null default now(),
    note varchar(255) null,
    constraint fk_stock_movements_context
        foreign key (operational_context_id) references operational_contexts (id),
    constraint fk_stock_movements_product
        foreign key (product_id) references products (id)
);

insert into stock_current (operational_context_id, product_id, current_stock, updated_at)
select
    balances.operational_context_id,
    balances.product_id,
    balances.current_stock,
    now()
from (
    select
        purchase_balances.operational_context_id,
        purchase_balances.product_id,
        sum(purchase_balances.delta_stock) as current_stock
    from (
        select
            p.operational_context_id,
            pi.product_id,
            pi.quantity as delta_stock
        from purchases p
        join purchase_items pi on pi.purchase_id = p.id
        join products prod on prod.id = pi.product_id
        where prod.stock_controlled = true

        union all

        select
            p.operational_context_id,
            pi.product_id,
            -pi.cancelled_quantity as delta_stock
        from purchases p
        join purchase_items pi on pi.purchase_id = p.id
        join products prod on prod.id = pi.product_id
        where prod.stock_controlled = true
          and pi.cancelled_quantity > 0

        union all

        select
            s.operational_context_id,
            si.product_id,
            -si.quantity as delta_stock
        from sales s
        join sale_items si on si.sale_id = s.id
        join products prod on prod.id = si.product_id
        where prod.stock_controlled = true

        union all

        select
            s.operational_context_id,
            si.product_id,
            si.quantity as delta_stock
        from sales s
        join sale_items si on si.sale_id = s.id
        join products prod on prod.id = si.product_id
        where prod.stock_controlled = true
          and s.status = 'ANULADA'
    ) purchase_balances
    group by purchase_balances.operational_context_id, purchase_balances.product_id
) balances
where balances.current_stock <> 0;

insert into stock_movements (
    operational_context_id, product_id, movement_type, quantity, reference_type, reference_id, performed_by, occurred_at, note
)
select
    p.operational_context_id,
    pi.product_id,
    'ENTRADA',
    pi.quantity,
    'COMPRA',
    p.id::varchar(100),
    coalesce(u.username, 'system'),
    coalesce(p.created_at, now()),
    'Entrada por compra'
from purchases p
join purchase_items pi on pi.purchase_id = p.id
join products prod on prod.id = pi.product_id
left join users u on u.id = p.purchased_by_user_id
where prod.stock_controlled = true;

insert into stock_movements (
    operational_context_id, product_id, movement_type, quantity, reference_type, reference_id, performed_by, occurred_at, note
)
select
    p.operational_context_id,
    pi.product_id,
    'REVERSA',
    pi.cancelled_quantity,
    'COMPRA_ANULADA',
    p.id::varchar(100),
    coalesce(u.username, 'system'),
    coalesce(p.cancelled_at, now()),
    'Reversa por anulacion de compra'
from purchases p
join purchase_items pi on pi.purchase_id = p.id
join products prod on prod.id = pi.product_id
left join users u on u.id = p.cancelled_by_user_id
where prod.stock_controlled = true
  and pi.cancelled_quantity > 0;

insert into stock_movements (
    operational_context_id, product_id, movement_type, quantity, reference_type, reference_id, performed_by, occurred_at, note
)
select
    s.operational_context_id,
    si.product_id,
    'SALIDA',
    si.quantity,
    'VENTA',
    s.id::varchar(100),
    coalesce(u.username, 'system'),
    coalesce(s.created_at, now()),
    'Salida por venta'
from sales s
join sale_items si on si.sale_id = s.id
join products prod on prod.id = si.product_id
left join users u on u.id = s.sold_by_user_id
where prod.stock_controlled = true;

insert into stock_movements (
    operational_context_id, product_id, movement_type, quantity, reference_type, reference_id, performed_by, occurred_at, note
)
select
    s.operational_context_id,
    si.product_id,
    'REVERSA',
    si.quantity,
    'VENTA_ANULADA',
    s.id::varchar(100),
    coalesce(u.username, 'system'),
    coalesce(s.cancelled_at, now()),
    'Reposicion por anulacion de venta'
from sales s
join sale_items si on si.sale_id = s.id
join products prod on prod.id = si.product_id
left join users u on u.id = s.cancelled_by_user_id
where prod.stock_controlled = true
  and s.status = 'ANULADA';

drop table stock_movements_legacy;
drop table stock_current_legacy;
