--liquibase formatted sql


--changeset ependi: 1
CREATE TABLE notification_task(
    id      integer                 not null,
    chatId  integer                 not null,
    message text                    not null,
    date    date                    not null,
    time    time                    not null
)
