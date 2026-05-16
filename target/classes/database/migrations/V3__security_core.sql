create table if not exists permissions (
    code varchar(100) primary key,
    description varchar(150) not null
);

create table if not exists roles (
    id bigserial primary key,
    name varchar(50) not null unique,
    description varchar(200) null
);

create table if not exists role_permissions (
    role_id bigint not null,
    permission_code varchar(100) not null,
    primary key (role_id, permission_code),
    constraint fk_role_permissions_role
        foreign key (role_id) references roles (id),
    constraint fk_role_permissions_permission
        foreign key (permission_code) references permissions (code)
);

create table if not exists users (
    id bigserial primary key,
    username varchar(100) not null unique,
    password_hash varchar(255) not null,
    active boolean not null,
    role_id bigint not null,
    constraint fk_users_role
        foreign key (role_id) references roles (id)
);
