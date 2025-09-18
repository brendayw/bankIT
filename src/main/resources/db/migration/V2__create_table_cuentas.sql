create table cuentas(
    id bigint not null auto_increment,
    fecha_creacion date not null,
    balance double not null,
    tipo_cuenta varchar(50),
    tipo_moneda varchar(50),
    estado boolean not null,

    primary key (id)
);