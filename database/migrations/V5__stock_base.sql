create table if not exists stock_current (
    product_id bigint primary key,
    current_stock numeric(12,2) not null,
    updated_at timestamp without time zone not null default now(),
    constraint fk_stock_current_product
        foreign key (product_id) references products (id)
);

create table if not exists stock_movements (
    id bigserial primary key,
    product_id bigint not null,
    movement_type varchar(30) not null,
    quantity numeric(12,2) not null,
    reference_type varchar(50) not null,
    reference_id varchar(100) null,
    performed_by varchar(100) not null,
    occurred_at timestamp without time zone not null default now(),
    note varchar(255) null,
    constraint fk_stock_movements_product
        foreign key (product_id) references products (id)
);

insert into stock_current (product_id, current_stock, updated_at)
select p.id,
       case
           when p.code = 'PROD-001' then 20.00
           else 0.00
       end,
       now()
from products p
where p.stock_controlled = true
on conflict (product_id) do nothing;

insert into stock_movements (
    product_id, movement_type, quantity, reference_type, reference_id, performed_by, occurred_at, note
)
select p.id,
       'INICIAL',
       20.00,
       'SEED',
       'V5',
       'system',
       now(),
       'Carga inicial de stock para entorno local'
from products p
where p.code = 'PROD-001'
and not exists (
    select 1
    from stock_movements sm
    where sm.product_id = p.id
      and sm.reference_type = 'SEED'
      and sm.reference_id = 'V5'
);
