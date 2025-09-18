create table prestamos(

    id bigint not null auto_increment,
    cliente_id bigint not null,
    monto_solicitado double not null,
    monto_total double not null,
    moneda varchar(50),
    plazo_meses int not null,
    loan_status varchar(50),
    fecha_alta date not null,

    primary key (id),
    foreign key (cliente_id) references clientes(id)
);