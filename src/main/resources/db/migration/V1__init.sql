create sequence customer_seq start with 1 increment by 1;
create table customer (
   id bigint not null default customer_seq.nextval,
   first_name varchar(255) not null,
   last_name varchar(255) not null,
   primary key (id)
);


create sequence product_seq start with 1 increment by 1;
create table product (
    id bigint not null default product_seq.nextval,
    name varchar(255) not null,
    price decimal(19,2) check (price>=0),
    primary key (id)
);

create sequence user_seq start with 1 increment by 1;
create table user (
    id bigint not null default user_seq.nextval,
    username varchar(255) not null,
    password varchar(255) not null,
    primary key (id)
);