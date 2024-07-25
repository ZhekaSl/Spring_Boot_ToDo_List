--liquibase formatted sql

--changeset Zheka:1
DROP TABLE projects CASCADE;

--changeset Zheka:2
CREATE TABLE base_projects
(
    id    varchar(32) primary key,
    color varchar(16),
    name  varchar(64) not null
);

--changeset Zheka:3
create table inboxes
(
    id      varchar(255) primary key not null references base_projects,
    user_id integer                  not null unique references users
);

--changeset Zheka:4
CREATE TABLE projects
(
    id                 varchar(32) primary key references base_projects not null,
    approval_required  boolean,
    invite_url_enabled boolean,
    user_id            integer references users                        not null,
    default_permission varchar(18)
);

--changeset Zheka:5
ALTER TABLE tasks
ALTER COLUMN project_id TYPE varchar(32);

--changeset Zheka:6
ALTER TABLE user_projects
    ALTER COLUMN project_id TYPE varchar(32);

--changeset Zheka:7
ALTER TABLE invitations
    ALTER COLUMN project_id TYPE varchar(32);












