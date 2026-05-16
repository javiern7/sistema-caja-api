create table if not exists operational_contexts (
    id bigserial primary key,
    code varchar(50) not null unique,
    name varchar(150) not null,
    type varchar(20) not null,
    status varchar(20) not null,
    start_date date not null,
    end_date date null,
    description varchar(255) null
);

insert into operational_contexts (code, name, type, status, start_date, end_date, description)
values
    ('NEG-PRINCIPAL', 'Negocio principal', 'NEGOCIO', 'EN_CURSO', current_date, null, 'Contexto base para desarrollo local'),
    ('EVT-DEMO', 'Evento demo de validacion', 'EVENTO', 'PLANIFICADO', current_date + interval '1 day', current_date + interval '2 day', 'Evento de ejemplo para pruebas administrativas')
on conflict (code) do nothing;
