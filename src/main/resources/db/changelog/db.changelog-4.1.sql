--liquibase formatted sql

--changeset Zheka:1
ALTER TABLE users_roles
    DROP CONSTRAINT users_roles_user_id_fkey;

--changeset Zheka:2
ALTER TABLE users_roles
    ADD CONSTRAINT users_roles_user_id_fkey
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;












