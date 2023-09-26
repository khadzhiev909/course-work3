--liquibase formatted sql


--changeset ependi:1
CREATE TABLE notification_task(
    id        int8          not null,
    chat_id   int8          not null,
    message   text          not null,
    data_time timestamp     not null
)
