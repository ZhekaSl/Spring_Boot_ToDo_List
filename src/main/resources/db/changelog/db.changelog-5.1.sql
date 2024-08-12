--liquibase formatted sql

--changeset Zheka:1
ALTER TABLE tasks
    DROP COLUMN time_included;








