# 🧭 DECISIONES.md — Bitácora de diseño

> **Instrucciones.** Completa **una entrada por fase**, en **primera persona** y
> **refiriéndote a tu propio código**: nombres reales de tus clases, tu tabla, tus
> líneas, tu salida real de terminal.
>
> ❌ **No puntúa** una justificación genérica que podría pegarse en cualquier proyecto
> (ej.: *"usé boundedElastic porque es una buena práctica para operaciones bloqueantes"*).
> ✅ **Sí puntúa** una justificación anclada a tu código (ej.: *"en `ProductoService`
> línea 34 envolví `productoRepository.findAll()` porque Hibernate abre la conexión
> JDBC en el hilo llamante; al probarlo sin `subscribeOn` vi en el log el hilo
> `reactor-http-nio-2`, que es el event loop de Netty"*).
>
> Estas mismas preguntas se te harán en la **defensa oral**.

---

## Datos

- **Nombre:**
- **Cédula:**
- **NN (dos últimos dígitos):**
- **Categoría asignada (según el último dígito):**

---

## Fase 1 — Configuración y perfiles

**1.1** ¿Qué archivo activa el perfil `prod` y qué línea exacta lo hace?

>

**1.2** Pega la línea del log de arranque donde se ve tu puerto y el perfil activo.

```

```

**1.3** ¿Qué habría pasado si dejabas `ddl-auto=create-drop` en lugar de `update`?
Responde pensando en tus datos sembrados.

>

**1.4** ¿Levantaste PostgreSQL con `compose.yaml` (Opción A) o con una instalación local
(Opción B)? ¿Qué ventaja tiene la que elegiste?

>

---

## Fase 2 — Persistencia con JPA/Hibernate

**2.1** ¿Cuál es el nombre exacto de tu tabla y de dónde salió ese nombre?

>

**2.2** Pega la salida de `psql -d agrosmart_db -c "\d tbl_productos_base_NN"` y
señala dónde se ve la restricción `unique` y el `length` de 120.

```

```

**2.3** ¿Por qué usaste `BigDecimal` y no `double` para `precio_usd`? Relaciónalo con el
tipo que generó Hibernate en PostgreSQL.

>

**2.4** ¿Cómo hiciste idempotente tu siembra y qué pasaría en el segundo arranque si no
lo fuera? (piensa en la restricción `unique` de `nombre_producto`)

>

---

## Fase 3 — Modelo inmutable y lógica funcional

**3.1** ¿Por qué tienes **dos** clases (`ProductoEntity` y `Producto`) en lugar de una?
¿Qué te impide hacer inmutable directamente la entidad de Hibernate?

>

**3.2** Escribe el código exacto de **tus dos** copias defensivas e indica en qué línea
está cada una.

```java

```

**3.3** ¿Por qué la copia defensiva **solo en el getter** no sería suficiente? Describe
el ataque concreto que quedaría abierto sobre **tu** clase.

>

**3.4** ¿Cómo implementaste `A_MAYUSCULAS` para no mutar el `Producto` recibido?

```java

```

---

## Fase 4 — Servicio reactivo y aislamiento del bloqueo

**4.1** Pega tu método `obtenerProductosComercializables()` completo.

```java

```

**4.2** ¿Qué pasa **exactamente** si eliminas
`.subscribeOn(Schedulers.boundedElastic())` de ese método? Si lo probaste, indica qué
hilo aparecía en el log antes y después.

>

**4.3** ¿Por qué `Mono.fromCallable(...)` y no `Mono.just(repository.findAll())`?
(pista: cuándo se ejecuta cada uno)

>

**4.4** En **tu** código, ¿dónde usaste `defaultIfEmpty` y dónde `switchIfEmpty`, y por
qué no son intercambiables en esos dos lugares?

>

**4.5** ¿Por qué `doOnNext` no sirve para transformar el elemento, si aparentemente
"recibe" el producto?

>

---

## Fase 5 — Módulo de IA con LangChain4j

**5.1** Pega tu interfaz `AgroSmartAIService` completa.

```java

```

**5.2** ¿Qué hace `@V("producto")` y qué pasaría si lo quitaras dejando solo el
parámetro?

>

**5.3** ¿En qué archivo y con qué líneas configuraste el modelo? ¿Por qué **no** hizo
falta declarar un `@Bean`?

>

**5.4** ¿Por qué la llamada a la IA también necesita `boundedElastic`, si no es una
consulta a base de datos?

>

**5.5** Si tu proveedor devolvió un error durante el examen, pega el mensaje real y la
respuesta que produjo tu `onErrorResume`.

```

```

---

## Fase 6 — API reactiva con WebFlux

**6.1** Pega la salida real de tus cuatro `curl`.

```

```

**6.2** ¿Cómo lograste que el id inexistente responda **404** y no 500?

>

**6.3** ¿Qué pasaría si tu controlador devolviera `List<Producto>` en lugar de
`Flux<Producto>`? ¿Seguiría compilando? ¿Seguiría siendo no bloqueante?

>

---

## Fase 7 — Pruebas unitarias

**7.1** Pega la salida real de tus pruebas (`./mvnw test` o `./gradlew test`).

```

```

**7.2** ¿Cuántos productos espera tu `expectNextCount(...)` y por qué ese número
concreto? Relaciónalo con tu semilla.

>

**7.3** ¿Por qué mockeaste `ProductoRepository` en lugar de dejar que la prueba consulte
PostgreSQL?

>

**7.4** ¿Qué demuestra `assertNotSame` que `assertEquals` **no** demuestra en tu prueba
de copia defensiva?

>

**7.5** ¿Por qué una prueba de un `Flux` que no llama a `verifyComplete()` (o a
`verify()`) no está probando nada?

>

---

## Fase 8 — Integración y cierre

**8.1** Pega tu `git log --oneline --graph --all`.

```

```

**8.2** ¿Qué fase te tomó más tiempo del previsto y por qué?

>

**8.3** Si tuvieras 30 minutos más, ¿qué mejorarías **primero** de tu entrega y por qué
esa y no otra?

>

**8.4** Declara honestamente qué herramientas consultaste durante el examen
(documentación, apuntes, asistentes de IA) y para qué. **Esta declaración no descuenta
puntaje**; su omisión o falsedad sí constituye falta de honestidad académica.

>
## Fase 1 (Realizado) — Configuración y perfiles

Active el perfil prod en application.properties porque necesito que la app arranque
siempre con mi configuracion de puerto (8193) y no con la de desarrollo por defecto
Al principio configure Docker Compose Support pero Docker Desktop no funciono en mi
maquina por falta de soporte de virtualizacion que no lo tengo activado en la bios y para no hacer 
mayor tiempo de duracion de la prueba cambie a la Opcion B
instale PostgreSQL localmente y declare spring.datasource.url, username y
password apuntando a mi base agrosmart_db creada con psql


## Fase 2 (Realizado) — Persistencia con JPA/Hibernate

Cree ProductoEntity mapeada a mi tabla tbl_productos_base_93 con las columnas exactas
que pide el enunciado: id_producto con IDENTITY, nombre_producto con length 120 y
unique, precio_usd como BigDecimal con precision 10 y escala 2. A diferencia de mi
modelo de dominio Producto, esta clase si tiene constructor vacio y setters porque
Hibernate los necesita para materializar los objetos al leer de la base de datos. Sembre los 5
productos (3 validos, 2 invalidos) con un CommandLineRunner que verifica
repository.count() == 0 para no duplicar en cada reinicio


## Fase 3 (Realizado) — Modelo inmutable y lógica funcional

Hice el Producto final y sin setters porque quiero que una vez creado, nadie pueda
cambiar su estado. La copia defensiva en el constructor evita que si alguien sigue
teniendo la lista de correos original la modifique y afecte mi objeto por dentro, la
del getter evita lo mismo pero al reves que quien reciba la lista desde afuera pueda
tocar mi estado interno. A_MAYUSCULAS no modifica el producto que recibe, construye uno
nuevo, por ultimo si mutara el original, estaria rompiendo la inmutabilidad que acabo de exigir


## Fase 4 (Realizado) — Servicio reactivo y aislamiento del bloqueo

Envolvi repository.findAll() en Mono.fromCallable().subscribeOn(boundedElastic) porque
JPA es una llamada bloqueante, si la ejecutara directo en el event loop de Netty,
bloquearia el hilo que atiende todas las peticiones concurrentes de la app. Use
defaultIfEmpty para el caso donde meciona que todos los productos son invalidos, porque el flujo sigue
completandose normalmente, solo cambia lo que se emite. En cambio use switchIfEmpty en
buscarPorId porque no encontrar un producto especifico por id es un error real, no un
caso vacio que se pueda rellenar con un valor generico

## Fase 5 (Realizado) — Módulo de IA con LangChain4j

Cree AgroSmartAIService como una interfaz con @AiService porque LangChain4j se encarga
de implementarla automaticamente en tiempo de ejecucion, leyendo el prompt de
@UserMessage y la configuracion que ya tenia en application-prod.properties. En
PublicidadService envolvi la llamada al modelo en boundedElastic porque es una llamada
HTTP bloqueante igual que JPA, y agregue onErrorResume porque la clave demo de
LangChain4j es compartida entre todo el curso y puede fallar, asi que
mi endpoint sigue respondiendo con un mensaje de respaldo en vez de caerse por completo 

## Fase 6 (Realizado) — API reactiva con WebFlux

Cree AgroSmartController con tres endpoints que solo conectan las rutas HTTP con los
servicios que ya tenia creados sin logica propia. Ninguno devuelve List ni un objeto bloqueante,
todo es Mono o Flux. Para el endpoint de publicidad tuve que agregar produces =
MediaType.TEXT_PLAIN_VALUE porque por defecto Spring devolvia el String envuelto en
JSON y el ejercicio pide en texto plano. Probe los 4 casos con curl: la lista de
productos uno por id, un id inexistente (confirme el 404) y la publicidad generada

## Fase 7 (Realizado) — Pruebas unitarias

Cubri ProductoTest con los getters y las dos copias defensivas (entrada y salida)
ProductoFiltersTest cubre el caso valido y los dos casos invalidos del Predicate
ProductoServiceTest mockea ProductoRepository con Mockito para no depender de
PostgreSQL y usa StepVerifier para probar los 3 casos: emision de los 3 validos,
emision del generico cuando todos son invalidos, y error cuando el id no existe
PublicidadServiceTest mockea AgroSmartAIService y cubre el camino correcto y el del fallo
del onErrorResume. Las 11 pruebas pasan en verde sin tocar la base de datos ni internet