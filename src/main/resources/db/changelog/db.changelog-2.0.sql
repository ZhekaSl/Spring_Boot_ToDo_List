--liquibase formatted sql

--changeset Zheka:1
CREATE TABLE checklist_items
(
    id integer generated by default as identity,
    title varchar(256),
    completed boolean,
    target_date date,
    target_time time,
    task_id integer
);



