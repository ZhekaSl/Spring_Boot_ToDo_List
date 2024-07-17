--liquibase formatted sql

--changeset Zheka:1
ALTER TABLE projects
    ALTER COLUMN color DROP NOT NULL;









