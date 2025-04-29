# Backend Proyecto Final ISW I

Este proyecto es una aplicación Spring Boot que demuestra una implementación simple de backend para gestionar reservas de boletos.

## Características

- API RESTful para gestionar reservas de boletos
- Base de datos H2 en memoria
- Documentación OpenAPI (Swagger UI)
- JPA para persistencia de datos
- Limpieza automática de reservas expiradas

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Security
- Base de datos H2
- SpringDoc OpenAPI
- Maven
- Spring Scheduler

## Requisitos Previos

- Java 17 o superior
- Maven 3.6 o superior

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── mx/
│   │       └── uam/
│   │           └── tsis/
│   │               └── ticketmaster/
│   │                   ├── negocio/
│   │                   │   ├── modelo/
│   │                   │   │   ├── Event.java
│   │                   │   │   ├── Seat.java
│   │                   │   │   ├── TicketReservation.java
│   │                   │   │   ├── TicketType.java
│   │                   │   │   ├── User.java
│   │                   │   │   └── Ticket.java
│   │                   │   ├── TicketReservationService.java
│   │                   │   ├── EventService.java
│   │                   │   └── TicketService.java
│   │                   ├── datos/
│   │                   │   ├── SeatRepository.java
│   │                   │   ├── EventRepository.java
│   │                   │   ├── TicketTypeRepository.java
│   │                   │   ├── TicketReservationRepository.java
│   │                   │   ├── UserRepository.java
│   │                   │   └── TicketRepository.java
│   │                   ├── api/
│   │                   │   ├── TicketReservationController.java
│   │                   │   ├── TicketController.java
│   │                   │   └── EventController.java
│   │                   ├── jobs/
│   │                   │   └── ReservationCleanupJob.java
│   │                   ├── dto/
│   │                   │   ├── ApiResponses.java
│   │                   │   ├── EventSearchRequest.java
│   │                   │   ├── CreateEventRequest.java
│   │                   │   └── ApiResponse/
│   │                   │       └── TicketDetailResponse.java
│   │                   ├── config/
│   │                   │   └── AppConfig.java
│   │                   ├── TicketmasterApplication.java
│   │                   ├── DataInitializer.java
│   │                   ├── OpenApiConfig.java
│   │                   └── SecurityConfig.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── mx/
            └── uam/
                └── tsis/
                    └── ticketmaster/
                        ├── negocio/
                        │   ├── TicketReservationServiceTest.java
                        │   └── modelo/
                        │       └── TicketReservationTest.java
                        ├── datos/
                        │   └── TicketReservationRepositoryTest.java
                        └── api/
                            └── TicketReservationControllerTest.java
```

## Cómo Compilar

Para compilar el proyecto, ejecute el siguiente comando en el directorio raíz del proyecto:

```bash
mvn clean install
```

Esto compilará el código, ejecutará las pruebas y creará un archivo JAR en el directorio `target`.

## Cómo Ejecutar

### Usando Maven

Para ejecutar la aplicación usando Maven:

```bash
mvn spring-boot:run
```

### Usando el archivo JAR

Después de construir el proyecto, puede ejecutar el archivo JAR generado:

```bash
java -jar target/ejemplobackend-0.0.1-SNAPSHOT.jar
```

## Acceso a la Aplicación

Una vez que la aplicación esté en ejecución, puede acceder a:

- **Documentación de la API**: http://localhost:8080/swagger-ui.html
- **Consola H2**: http://localhost:8080/h2-console

## Endpoints de la API

### API de Eventos

- `GET /api/v1/events` - Listar todos los eventos
- `GET /api/v1/events/{eventId}` - Obtener detalles de un evento
- `POST /api/v1/events` - Crear un nuevo evento
- `GET /api/v1/events/search` - Buscar eventos con múltiples criterios
- `GET /api/v1/events/promotions` - Obtener eventos en promoción

### API de Tickets

- `GET /api/v1/tickets/types/{ticketTypeId}` - Obtener información de un tipo de ticket
- `GET /api/v1/tickets/event/{eventId}/types` - Obtener tipos de tickets disponibles para un evento

### API de Reservaciones

- `GET /api/reservations` - Listar todas las reservaciones
- `GET /api/reservations/active` - Listar reservaciones activas
- `GET /api/reservations/events/{eventId}/seats/{ticketTypeId}` - Obtener asientos disponibles
- `POST /api/reservations` - Crear una nueva reservación
- `DELETE /api/reservations/{reservationId}` - Cancelar una reservación

## Desarrollo

La aplicación está configurada para usar el perfil "development" por defecto. Este perfil utiliza una base de datos H2 en memoria para facilitar el desarrollo y las pruebas.

## Pruebas

El proyecto incluye pruebas unitarias y de integración. Para ejecutar las pruebas:

```bash
mvn test
```

## Limpieza automática de reservas expiradas

El proyecto incluye un job automático que se encarga de limpiar las reservas de boletos que han expirado. Este proceso se realiza mediante la clase `ReservationCleanupJob`, que utiliza Spring Scheduler para ejecutarse cada minuto.

### ¿Cómo funciona?

- El job busca todas las reservas activas cuya fecha de expiración ya pasó.
- Por cada reserva expirada:
  - Restaura la cantidad de boletos disponibles al tipo de boleto correspondiente.
  - Marca la reserva como inactiva.
  - Guarda los cambios en la base de datos.

Esto asegura que los boletos reservados y no pagados vuelvan a estar disponibles para otros usuarios de manera automática y periódica.

### Ubicación del código

El código de este job se encuentra en:
```
src/main/java/mx/uam/tsis/ticketmaster/jobs/ReservationCleanupJob.java
```

## Inicialización de Datos

El archivo `DataInitializer.java` se encarga de inicializar la base de datos con datos de ejemplo al iniciar la aplicación. Esto es útil para pruebas y desarrollo, ya que proporciona un conjunto de datos predefinidos para trabajar.

### ¿Qué hace?

- **Crea un usuario administrador**: Inicializa un usuario con rol de administrador para gestionar la aplicación.
- **Crea un evento de muestra**: Inicializa un evento de muestra con detalles como nombre, descripción, lugar y fechas.
- **Crea tipos de boletos**: Inicializa dos tipos de boletos para el evento de muestra:
  - **VIP**: Acceso VIP con meet & greet, con un precio y cantidad disponibles.
  - **General**: Entrada general, con un precio y cantidad disponibles.

Esto permite que la aplicación tenga datos iniciales para realizar pruebas y demostraciones sin necesidad de ingresar manualmente la información.

## Licencia

Este proyecto es parte de los materiales del curso Ingenieria de Software I de PCyTi de la UAMI.

## Objetos de Transferencia de Datos (DTOs)

Los archivos en la carpeta `dto` (Data Transfer Objects) se utilizan para transferir datos entre diferentes capas de la aplicación. Estos objetos ayudan a encapsular y estructurar los datos que se envían y reciben a través de la API.

### Archivos en la carpeta `dto`

- **ApiResponses.java**: Contiene las clases de respuesta para la API, definiendo la estructura de los datos que se devuelven al cliente.
- **EventSearchRequest.java**: Define la estructura de la solicitud para buscar eventos, incluyendo parámetros como fecha, precio, categoría, etc.
- **CreateEventRequest.java**: Define la estructura de la solicitud para crear un nuevo evento, incluyendo todos los detalles necesarios.
- **ApiResponse/TicketDetailResponse.java**: Define la estructura de la respuesta para los detalles de un ticket, incluyendo información como precio, disponibilidad, etc.

Estos DTOs son esenciales para mantener una clara separación entre la lógica de negocio y la presentación de datos, facilitando la comunicación entre el cliente y el servidor. 
