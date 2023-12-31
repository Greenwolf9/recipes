CREATE TABLE if not exists users
(
    email         varchar not null unique,
    user_password varchar not null,
    user_role     varchar,
    primary key (email)
);

CREATE TABLE if not exists recipes
(
    id          bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    recipe_name varchar not null,
    category    varchar not null,
    recipe_date datetime,
    description varchar not null,
    ingredients varchar not null,
    directions  varchar not null,
    user_email  varchar references users (email)
);



