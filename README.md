# AgroSmart — Examen Final de Programación Avanzada

Backend de comercializacion agricola construido con Spring Boot WebFlux, JPA/Hibernate
y LangChain4j, para el examen final

**Autor:** Anthony Samir Leon Jativa
**Universidad:** Universidad de las Fuerzas Armadas ESPE

## Mi semilla personal

Los dos ultimos digitos de mi cedula son **93**, calculados así:

| Parametro | Cómo se obtuvo | Mi valor |
|-----------|----------------|----------|
| Tabla | `tbl_productos_base_` + 93 | `tbl_productos_base_93` |
| Puerto | 81 + 93 | `8193` |
| Categoria | ultimo digito (3) → tabla del enunciado | ☕ Cafe |

Comprobacion: el puerto `8193` empieza en 81 y termina en los mismos dos digitos
que mi tabla (93)

## Arquitectura

Una sola aplicacion Spring Boot sobre Netty (WebFlux), con persistencia JPA/Hibernate
y generacion de publicidad via LangChain4j:

    AgroSmartController (WebFlux, Mono/Flux)
    ProductoService (Project Reactor: map, filter, doOnNext, defaultIfEmpty, switchIfEmpty)
            Mono.fromCallable(...).subscribeOn(boundedElastic) → ProductoRepository (JPA, bloqueante) → PostgreSQL
            Mono.fromCallable(...).subscribeOn(boundedElastic) → AgroSmartAIService (LangChain4j, bloqueante)

## Como ejecutar el proyecto

### Requisitos

- Java 21
- Maven
- PostgreSQL 

### 1. Crear la base de datos

Con psql o "SQL Shell (psql)":

    CREATE DATABASE agrosmart_db;

### 2. Configurar credenciales

En `src/main/resources/application-prod.properties` ya estan declaradas mis
credenciales de conexion local:

    spring.datasource.url=jdbc:postgresql://localhost:5432/agrosmart_db
    spring.datasource.username=postgres
    spring.datasource.password=postgres

### 3. Ejecutar la aplicacion

    .\mvnw.cmd spring-boot:run

La app arranca con el perfil `prod` activo en el puerto **8193**. Al iniciar, Hibernate
crea la tabla `tbl_productos_base_93` (si no existiese) y un `CommandLineRunner` siembra
5 productos de categoria Cafe, 3 comercializables y 2 no comercializables

### 4. Ejecutar las pruebas

    .\mvnw.cmd test

11 pruebas, todas en verde, sin depender de PostgreSQL ni de internet

## Endpoints

| Método | Ruta | Retorno | Descripción |
|--------|------|---------|-------------|
| GET | `/api/productos` | `Flux<Producto>` | Lista los productos comercializables |
| GET | `/api/productos/{id}` | `Mono<Producto>` | Un producto por id; 404 si no existe el producto |
| GET | `/api/agrosmart/publicidad` | `Mono<String>` | Frase publicitaria generada por la IA |

### Ejemplos reales

Peticion:

    curl.exe http://localhost:8193/api/productos

Respuesta:

    [{"id":1,"nombre":"CAFE ARABIGO DE ALTURA","categoria":"Cafe","precioUsd":18.50,"correosNotificacion":["ventas@agrosmart.ec"]}, ...]

Peticion:

    curl.exe http://localhost:8193/api/productos/1

Respuesta:

    {"id":1,"nombre":"Cafe arabigo de altura","categoria":"Cafe","precioUsd":18.50,"correosNotificacion":["ventas@agrosmart.ec"]}

Peticion:

    curl.exe -i http://localhost:8193/api/productos/9999

Respuesta:

    HTTP/1.1 404 Not Found
    {"timestamp":"...","path":"/api/productos/9999","status":404,"error":"Not Found",...}

Peticion:

    curl.exe "http://localhost:8193/api/agrosmart/publicidad?producto=Cafe%20arabigo%20de%20altura&audiencia=cafeterias%20de%20especialidad"

Respuesta:

    Descubre el sabor único del café arábigo de altura: una experiencia sublime para tus clientes.

## Justificacion de los operadores reactivos que use

En `ProductoService.obtenerProductosComercializables()`:

- **`Mono.fromCallable(repository::findAll)`** — difiere la consulta bloqueante a JPA
  hasta que alguien se suscriba al flujo
- **`.subscribeOn(Schedulers.boundedElastic())`** — ejecuta esa consulta bloqueante en
  un pool de hilos aparte del event loop de Netty, para no congelarlo
- **`.flatMapMany(Flux::fromIterable)`** — convierte la lista materializada en un
  flujo elemento por elemento
- **`.map(ProductoMapper::toDominio)`** — convierte cada `ProductoEntity` en mi modelo
  inmutable `Producto`
- **`.map(ProductoFilters.A_MAYUSCULAS)`** — transforma el nombre a mayusculas
  devolviendo una instancia nueva
- **`.filter(ProductoFilters.IS_VALID)`** — descarta los productos no comercializables
  segun la regla de negocio
- **`.doOnNext(ProductoFilters.LOG_PRODUCTO)`** — efecto de trazabilidad en consola
  sin transformar el elemento
- **`.defaultIfEmpty(PRODUCTO_GENERICO)`** — si tras filtrar no queda ningún producto
  valido, el flujo igual se completa emitiendo un producto de respaldo

En `ProductoService.buscarPorId()`:

- **`.switchIfEmpty(Mono.error(new ProductoNoEncontradoException(id)))`** — a
  diferencia de `defaultIfEmpty`, esto cambia el flujo a un error cuando el id no
  existe, porque no encontrar un producto especifico es un caso de error real
  no algo que se pueda rellenar con un valor cualquiera

En `PublicidadService.generarPublicidad()`:

- **`.timeout(Duration.ofSeconds(30))`** — evita que la llamada al modelo de IA quede
  esperando indefinidamente
- **`.onErrorResume(...)`** — si el proveedor de IA falla, responde con un mensaje de
  respaldo en vez de que el endpoint se caiga

## El puente bloqueante → reactivo con `boundedElastic`

JPA/Hibernate y la llamada HTTP de LangChain4j son operaciones **bloqueantes**: el hilo
que las ejecuta se queda esperando la respuesta. WebFlux corre sobre el event loop de
Netty que atiende todas las peticiones concurrentes con un numero reducido de
hilos entonces si una de esas llamadas bloqueantes se ejecutara ahi directamente, un solo hilo
ocupado degradaria el rendimiento de toda la aplicación para todos los usuarios

Por eso, cada vez que necesito llamar al repositorio JPA o al servicio de IA envuelvo
la llamada en `Mono.fromCallable(...)` y la despacho con
`.subscribeOn(Schedulers.boundedElastic())`, un pool de hilos diseñado justamente para
trabajo bloqueante separado del event loop

## Estructura de paquetes

    src/main/java/ec/edu/espe/agrosmart/
    ├── AgroSmartApplication.java
    ├── controller/AgroSmartController.java
    ├── service/ProductoService.java
    ├── service/AgroSmartAIService.java
    ├── service/PublicidadService.java
    ├── repository/ProductoRepository.java
    ├── entity/ProductoEntity.java
    ├── domain/Producto.java
    ├── domain/ProductoFilters.java
    ├── mapper/ProductoMapper.java
    └── exception/ProductoNoEncontradoException.java
