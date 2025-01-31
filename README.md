# Simulador de Banco - Gestión de Clientes, Cuentas y Préstamos
Proyecto desarrollado en Java que permite la creación de
clientes y cuentas, así como la gestión de préstamos mediante
Spring Boot.

### Funcionalidades
#### Gestión de clientes:
* Creacion de un cliente.
* Consultar clientes.
* Consultar cliente por ID (dni).
* Eliminar (desactivar) el cliente.

#### Gestión de cuentas:
* Creacion de una cuenta.
* Consultar todas las cuentas.
* Consultar una cuenta por ID.
* Consultar las cuentas de un cliente por su ID.
* Eliminar (desactivar) una cuenta.

#### Gestión de préstamos:
* Solicitar un préstamo.
* Consultar todos los préstamos solicitados.
* Consultar un préstamo por su ID.
* Consultar los préstamos de un cliente por su ID.
* Pagar una cuota del préstamo.
* Cerrar el préstamo.

### Tecnologías Utilizadas
- Java 21.0.3
- Maven
- Spring Boot
- JUnit
- Mockito

### Contenidos

* Controller: gestiona las solicitudes HTTP y el mapeo de los endpoints correspondientes. 
  * Los Handlers que procesan las solicitudes
  * Los DTOs (Objetos de Transferencia de Datos) para estructurar y validar la información de entrada/salida
  * Los Validators que aseguran que los datos recibidos cumplan con los requisitos establecidos antes de ser procesados.

* Model: representa la estructura de los datos dentro de la aplicación, definiendo cómo se organizan y relacionan los distintos elementos de información.
  * Los enums establecen valores predefinidos para garantizar consistencia en los datos.
  * Las excepcions Manejan errores específicos de manera controlada.

* Persistence: gestiona el acceso y almacenamiento de datos.
  * Los entities representan los objetos de datos.
  * Los DAO definen las operaciones de acceso a los datos, permitiendo su almacenamiento en memoria. Al estar implementados mediante interfaces, facilitan el mantenimiento, la extensibilidad y la flexibilidad del sistema.

* Service: Se encarga de la lógica de negocio y las operaciones que interactuan con la capa de persistencia, gestionando cómo se interactúa con los datos y otros componentes del sistema. Al implementarse mediante interfaces, permiten una fácil actualización, mantenimiento y flexibilidad.

#### Tests: 
* Se encargan de verificar el correcto funcionamiento de las capas service y controller de la aplicación mediante pruebas unitarias, que validan el comportamiento de los métodos y aseguran que cada componente del sistema funcione de manera independiente.

### Endpoints y ejemplos de uso

### Cliente

#### Crear Cliente: 
* Método: POST
* URL:  /api/cliente
* Ejemplo de input:

        {
            "nombre": "Brenda",
            "apellido": "Yañez",
            "dni": 40860006,
            "fechaNacimiento": "1997-04-09",
            "telefono": "2916897129",
            "email": "brendayañez@gmail.com",
            "tipoPersona": "F",
            "banco": "Provincia"
        }
* Posibles errores:
  * Error Code: 400 Bad Request
    * Campos nulos o vacíos.
    * Tipos de datos no validos (en caso de tipoPersona y fechaNacimiento).
  * Error Code 409 Conflict
    * Ya existe un cliente con ese DNI.
    * El cliente es menor de edad.

#### Obtener todos los clientes
* Método: GET
* URL: /api/cliente
* Posibles errores:
  * Error Code: 404 Not Found
    * No se ha registrado ningun cliente.

#### Obtener cliente por DNI
* Método: GET
* URL: /api/cliente/{DNI}
* Posibles errores:
    * Error Code: 404 Not Found
        * El cliente no existe.

#### Desactiva cliente
* Método: DELETE
* URL: /api/cliente/{DNI}
* Posibles errores:
    * Error Code: 404 Not Found
        * El cliente no existe.

### Cuenta

#### Crear cuenta
* Método: POST
* URL: /api/cuenta
* Ejemplo de input:
  
        {
            "dniTitular": 40860006,
            "balance": 100.0,
            "tipoCuenta": "A",
            "tipoMoneda": "P"
        }
* Posibles errores:
  * Error Code: 400 Bad Request
    * Campos nulos o vacíos.
    * Tipos de datos no validos (en caso de tipoCuenta y tipoMoneda).
  * Error Code: 404 Not Found
    * El cliente con ese DNI no existe.
  * Error Code: 409 Conflict.
    * El cliente ya tiene una cuenta de ese tipo en ese tipo de moneda.

#### Obtener todas las cuentas
* Método: GET
* URL: /api/cuenta
* Posibles errores:
    * Error Code: 404 Not Found
        * No se han registrado cuentas.

#### Obtener cuenta por ID
* Método: GET
* URL: /api/cuenta/{ID}
* Posibles errores:
  * Error Code: 404 Not Found
    * La cuenta con ese ID no existe.

#### Obtener cuenta por DNI del cliente
* Método: GET
* URL: /api/cuenta/cliente/{dni}
* Posibles errores:
    * Error Code: 404 Not Found
        * El cliente no existe.

* Para consultar el balance luego de la solicitud y aprobación de un préstamo:
GET /api/cuenta/cliente/{dni}

### Préstamo

#### Solicitar préstamo:
* Método: POST
* URL:  /api/prestamo
* Ejemplo de input:
  
        {
            "numeroCliente": 40860006,
            "montoPrestamo": 150000.0,
            "tipoMoneda": "P",
            "plazoMeses": 12
        }
* Posibles errores:
  * Error Code: 400 Bad Request
    * Campos nulos o vacios.
    * Tipo de datos no válidos (tipoMoneda).
  * Error Code: 404 Not Found
    * El cliente no tiene cuenta en la moneda especificada.
    * El cliente no existe.

#### Obtener todos los préstamos
* Método: GET
* URL: /api/prestamo
* Posibles errores:
    * Error Code: 404 Not Found
        * No se encontraron préstamos.

#### Obtener todos los préstamos por DNI del cliente
* Método: GET
* URL: /api/prestamo/{dni}
* Posibles errores:
    * Error Code: 404 Not Found
        * El cliente no tiene préstamos registrados.

#### Pagar cuota del préstamo
    Para pagar una cuota del préstamo se debe tener en cuenta el ID que se imprime por terminal

* Método: PUT
* URL: /api/prestamo/pagar/{id}
* Posibles errores:
    * Error Code: 404 Not Found
        *  No se encontró un préstamo aprobado con ese ID.

#### Cerrar préstamo
    Para pagar una cuota del préstamo se debe tener en cuenta el ID que se imprime por terminal
* Método: DELETE
* URL: /api/prestamo/{ID}
* Posibles errores:
    * Error Code: 404 Not Found
        * El préstamo con ese ID no existe.