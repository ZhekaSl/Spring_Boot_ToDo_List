--liquibase formatted sql

--changeset Zheka:1
create table if not exists users
(
    id         integer generated by default as identity primary key,
    username   varchar(64) unique,
    password   varchar(128),
    firstname  varchar(32),
    birth_date date
);

--changeset Zheka:2
create table if not exists tasks
(
    id             integer generated by default as identity primary key,
    name           varchar(64),
    description    text,
    priority       varchar(18),
    target_date    timestamp,
    completed      boolean,
    completed_date timestamp,
    parent_task_id integer references tasks (id) on delete cascade,
    user_id        integer references users (id) on DELETE cascade
);

--changeset Zheka:3
create table if not exists roles
(
    id   smallint generated by default as identity primary key,
    name varchar(18) unique not null
);

--changeset Zheka:4
create table if not exists users_roles
(
    user_id integer references users (id),
    role_id smallint references roles (id),
    primary key (user_id, role_id)
);