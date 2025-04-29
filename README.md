# Ejemplo de Backend TSIS

Este proyecto es una aplicación Spring Boot que demuestra una implementación simple de backend para gestionar estudiantes y grupos. Sirve como ejemplo para el curso de TSIS (Tecnologías para Sistemas de Información).

## Características

- API RESTful para gestionar estudiantes y grupos
- Base de datos H2 en memoria
- Spring Security con autenticación básica
- Documentación OpenAPI (Swagger UI)
- JPA para persistencia de datos

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Security
- Base de datos H2
- SpringDoc OpenAPI
- Maven

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
│   │               └── ejemplobackend/
│   │                   ├── negocio/
│   │                   │   ├── modelo/
│   │                   │   │   ├── Alumno.java
│   │                   │   │   └── Grupo.java
│   │                   │   ├── AlumnoService.java
│   │                   │   └── GrupoService.java
│   │                   ├── datos/
│   │                   │   ├── AlumnoRepository.java
│   │                   │   └── GrupoRepository.java
│   │                   ├── presentacion/
│   │                   │   └── MainController.java
│   │                   ├── servicios/
│   │                   │   ├── AlumnoController.java
│   │                   │   └── GrupoController.java
│   │                   ├── EjemplobackendApplication.java
│   │                   ├── OpenApiConfig.java
│   │                   └── SecurityConfig.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── mx/
            └── uam/
                └── tsis/
                    └── ejemplobackend/
                        ├── negocio/
                        │   └── AlumnoServiceTest.java
                        └── servicios/
                            └── AlumnoControllerIntegrationTest.java
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
  - URL JDBC: jdbc:h2:mem:testdb
  - Usuario: sa
  - Contraseña: (dejar vacío)

## Endpoints de la API

### API de Estudiantes

- `GET /v1/alumnos` - Obtener todos los estudiantes
- `GET /v1/alumnos/{matricula}` - Obtener un estudiante por ID
- `POST /v1/alumnos` - Crear un nuevo estudiante
- `PUT /v1/alumnos/{matricula}` - Actualizar un estudiante

### API de Grupos

- `GET /v1/grupos` - Obtener todos los grupos
- `POST /v1/grupos` - Crear un nuevo grupo
- `POST /v1/grupos/{id}/alumnos?matricula=1234` - Agregar un estudiante a un grupo

## Autenticación

La API está protegida con autenticación básica:

- Usuario: tsis
- Contraseña: 1234

## Desarrollo

La aplicación está configurada para usar el perfil "development" por defecto. Este perfil utiliza una base de datos H2 en memoria para facilitar el desarrollo y las pruebas.

## Pruebas

El proyecto incluye pruebas unitarias y de integración. Para ejecutar las pruebas:

```bash
mvn test
```

## Licencia

Este proyecto es parte de los materiales del curso TSIS. 