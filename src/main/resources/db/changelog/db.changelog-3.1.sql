--liquibase formatted sql

--changeset Zheka:1
ALTER TABLE projects
    RENAME COLUMN default_role TO default_permission;









