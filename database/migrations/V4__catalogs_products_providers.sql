create table if not exists products (
    id bigserial primary key,
    code varchar(50) not null unique,
    name varchar(150) not null,
    unit_of_measure varchar(50) not null,
    sale_price numeric(12,2) not null,
    reference_cost numeric(12,2) not null,
    minimum_stock numeric(12,2) not null,
    stock_controlled boolean not null,
    active boolean not null,
    description varchar(255) null
);

create table if not exists providers (
    id bigserial primary key,
    name varchar(150) not null,
    document_number varchar(30) null,
    contact_name varchar(120) null,
    phone varchar(30) null,
    email varchar(120) null,
    active boolean not null
);

insert into products (
    code, name, unit_of_measure, sale_price, reference_cost, minimum_stock, stock_controlled, active, description
)
values
    ('PROD-001', 'Producto demo con stock', 'UNIDAD', 15.00, 9.50, 5.00, true, true, 'Producto base para pruebas locales'),
    ('PROD-002', 'Servicio demo sin stock', 'SERVICIO', 25.00, 0.00, 0.00, false, true, 'Servicio operativo de ejemplo')
on conflict (code) do nothing;

insert into providers (name, document_number, contact_name, phone, email, active)
values
    ('Proveedor demo', '20123456789', 'Contacto demo', '999888777', 'proveedor.demo@local.test', true)
on conflict do nothing;
