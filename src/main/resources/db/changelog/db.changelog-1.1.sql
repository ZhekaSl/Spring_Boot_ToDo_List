--liquibase formatted sql

--changeset Zheka:1
ALTER TABLE tasks
    DROP COLUMN target_date;

--changeset Zheka:2
ALTER TABLE tasks
    ADD target_date DATE;

--changeset Zheka:3
ALTER TABLE tasks
    ADD target_time TIME;

--changeset Zheka:4
ALTER TABLE tasks
    RENAME COLUMN completed_date TO completed_timestamp;



