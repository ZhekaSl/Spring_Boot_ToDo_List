--liquibase formatted sql

--changeset Zheka:1
ALTER TABLE tasks
    DROP COLUMN target_date,
    DROP COLUMN target_time;

--changeset Zheka:2
ALTER TABLE checklist_items
    DROP COLUMN target_date,
    DROP COLUMN target_time;

--changeset Zheka:3
ALTER TABLE tasks
    ADD COLUMN due_timestamp TIMESTAMP,
    ADD COLUMN time_included boolean,
    ADD COLUMN time_zone     varchar(64);









