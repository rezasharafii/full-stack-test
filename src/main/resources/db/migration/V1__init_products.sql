create table products
(
    id           integer primary key generated always as identity,
    external_id  bigint      not null unique,
    title        text        not null,
    vendor       text,
    product_type text,
    created_at   timestamptz not null default now()
);

create table variants
(
    id         integer primary key generated always as identity,
    product_id bigint not null references products (id) on delete cascade,
    sku        text,
    price      numeric(10, 2)
);

create index idx_products_external_id on products (external_id);
create index idx_variants_product_id on variants (product_id);
