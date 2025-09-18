create table clientes(
    id bigint not null auto_increment,
    dni bigint not null unique,
    apellido varchar(100) not null,
    nombre varchar(100) not null,
    fecha_nacimiento date not null,
    telefono varchar(100) not null,
    email varchar(100) not null,
    activo boolean not null,
    tipo_persona varchar(50),
    fecha_alta date not null,

    primary key(id)
);