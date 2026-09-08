# SpeedFast

Sistema de gestión de pedidos para una empresa de reparto a domicilio.

Cada tipo de pedido estima su tiempo de entrega con una fórmula distinta y aplica su propio criterio de asignación de repartidor. Un controlador de envíos despacha, cancela y consulta el historial trabajando únicamente contra los contratos que los pedidos implementan.

La jornada de reparto se ejecuta de forma concurrente: cada repartidor recorre su propia ruta al mismo tiempo que los demás.

## Requisitos

- JDK 21
- IntelliJ IDEA

## Estructura

```
SpeedFast/
├── src/
│   └── cl/
│       └── speedfast/
│           ├── interfaces/
│           │   ├── Despachable.java
│           │   ├── Cancelable.java
│           │   ├── Rastreable.java
│           │   └── package-info.java
│           ├── model/
│           │   ├── Pedido.java
│           │   ├── PedidoComida.java
│           │   ├── PedidoEncomienda.java
│           │   ├── PedidoExpress.java
│           │   ├── EstadoPedido.java
│           │   └── package-info.java
│           ├── gestores/
│           │   ├── ControladorDeEnvios.java
│           │   └── package-info.java
│           ├── concurrencia/
│           │   ├── Repartidor.java
│           │   └── package-info.java
│           └── main/
│               ├── Main.java
│               └── package-info.java
├── .idea/
├── SpeedFast.iml
└── README.md
```

| Paquete | Contenido |
|---|---|
| `cl.speedfast.interfaces` | Contratos de comportamiento |
| `cl.speedfast.model` | Modelo de dominio: la jerarquía de pedidos |
| `cl.speedfast.gestores` | Coordinación de las operaciones sobre los envíos |
| `cl.speedfast.concurrencia` | Ejecución concurrente de las entregas |
| `cl.speedfast.main` | Punto de entrada de la aplicación |

## Modelo de clases

`PedidoComida`, `PedidoEncomienda` y `PedidoExpress` heredan de `Pedido`, que implementa las tres interfaces.

| Clase | Responsabilidad |
|---|---|
| `Pedido` | Clase abstracta. Datos y comportamiento comunes a todo pedido. |
| `PedidoComida` | Pedidos de restaurante. |
| `PedidoEncomienda` | Documentos y paquetes. |
| `PedidoExpress` | Compras de supermercado o farmacia. |
| `EstadoPedido` | Estados del pedido dentro del proceso de entrega. |
| `ControladorDeEnvios` | Registra los envíos y coordina despacho, cancelación e historial. |
| `Repartidor` | Tarea concurrente. Recorre su ruta y entrega los pedidos que tiene asignados. |
| `Main` | Punto de entrada. Ejecuta la simulación de la jornada. |


```mermaid
---
config:
  class:
    hierarchicalNamespaces: false
---
classDiagram
    direction TB

    namespace cl.speedfast.interfaces {
        class Despachable {
            <<interface>>
            +despachar() void
        }
        class Cancelable {
            <<interface>>
            +cancelar() void
        }
        class Rastreable {
            <<interface>>
            +verHistorial() void
        }
    }

    namespace cl.speedfast.model {
        class Pedido {
            <<abstract>>
            -String idPedido
            -String direccionEntrega
            -double distanciaKm
            -String tipoPedido
            -String repartidor
            -EstadoPedido estado
            -List~String~ historial
            +mostrarResumen() void
            +calcularTiempoEntrega()* int
            +asignarRepartidor() void
            +asignarRepartidor(String nombreRepartidor) void
            +despachar() void
            +cancelar() void
            +cancelar(String motivo) void
            +verHistorial() void
            #confirmarAsignacion(String nombreRepartidor) void
            #cumpleRequisitos() boolean
            #mostrarEncabezado() void
            -registrarEvento(String descripcion) void
        }
        class PedidoComida {
            -boolean requiereMochilaTermica
            +calcularTiempoEntrega() int
            +asignarRepartidor() void
        }
        class PedidoEncomienda {
            -double pesoKg
            -String embalaje
            +calcularTiempoEntrega() int
            +asignarRepartidor() void
            #cumpleRequisitos() boolean
        }
        class PedidoExpress {
            -boolean disponibilidadInmediata
            +calcularTiempoEntrega() int
            +asignarRepartidor() void
            #cumpleRequisitos() boolean
        }
        class EstadoPedido {
            <<enumeration>>
            PENDIENTE
            ASIGNADO
            DESPACHADO
            CANCELADO
        }
    }

    namespace cl.speedfast.gestores {
        class ControladorDeEnvios {
            -List~Pedido~ envios
            +registrar(Pedido pedido) void
            +despachar(Despachable envio) void
            +cancelar(Cancelable envio) void
            +verHistorial() void
        }
    }

    namespace cl.speedfast.concurrencia {
        class Runnable {
            <<interface>>
            +run() void
        }
        class Repartidor {
            -String nombre
            -List~Pedido~ pedidosAsignados
            -ControladorDeEnvios controlador
            +asignar(Pedido pedido) void
            +mostrarRuta() void
            +run() void
            -informar(String mensaje) void
            -describir(Pedido pedido) String
        }
    }

    Despachable <|.. Pedido
    Cancelable <|.. Pedido
    Rastreable <|.. Pedido
    Rastreable <|.. ControladorDeEnvios

    Pedido <|-- PedidoComida
    Pedido <|-- PedidoEncomienda
    Pedido <|-- PedidoExpress

    Pedido "1" --> "1" EstadoPedido : estado
    ControladorDeEnvios "1" o-- "0..*" Pedido : envios

    ControladorDeEnvios ..> Despachable : usa
    ControladorDeEnvios ..> Cancelable : usa

    Runnable <|.. Repartidor
    Repartidor "1" o-- "0..*" Pedido : pedidosAsignados
    Repartidor "1" --> "1" ControladorDeEnvios : despacha a traves de

    classDef contrato fill:#e8f5e9,stroke:#2e7d32,color:#1b5e20
    classDef modelo fill:#fff3e0,stroke:#e65100,color:#5d2f00
    classDef gestor fill:#e3f2fd,stroke:#1565c0,color:#0d3c67
    classDef concurrente fill:#f3e5f5,stroke:#6a1b9a,color:#3d0d55

    cssClass "Despachable,Cancelable,Rastreable" contrato
    cssClass "Pedido,PedidoComida,PedidoEncomienda,PedidoExpress,EstadoPedido" modelo
    cssClass "ControladorDeEnvios" gestor
    cssClass "Runnable,Repartidor" concurrente
```

## Contratos

Cada interfaz declara una capacidad independiente. Una clase asume solo las que le corresponden y quien las consume no depende de implementaciones concretas.

| Interfaz | Método | Implementada por |
|---|---|---|
| `Despachable` | `despachar()` | `Pedido` y sus tres subclases |
| `Cancelable` | `cancelar()` | `Pedido` y sus tres subclases |
| `Rastreable` | `verHistorial()` | `Pedido` y sus tres subclases, `ControladorDeEnvios` |

Cada pedido rastrea sus propios eventos; el controlador rastrea las entregas realizadas.

`ControladorDeEnvios` recibe los envíos como `Despachable` y `Cancelable`, de modo que no conoce el tipo concreto del pedido que opera.

## Estados

| Estado | Significado |
|---|---|
| `PENDIENTE` | El pedido existe pero aún no tiene repartidor. |
| `ASIGNADO` | El pedido tiene un repartidor confirmado y puede despacharse. |
| `DESPACHADO` | El pedido salió a reparto y ya no admite cancelación. |
| `CANCELADO` | El pedido fue anulado antes de salir a reparto. |

## Pedido (clase abstracta)

Los atributos se reciben en el constructor y se exponen mediante *getters*. Solo `direccionEntrega` admite modificación posterior.

| Atributo | Tipo | Acceso |
|---|---|---|
| `idPedido` | `String` | lectura |
| `direccionEntrega` | `String` | lectura y escritura |
| `distanciaKm` | `double` | lectura |
| `tipoPedido` | `String` | lectura |
| `repartidor` | `String` | lectura |
| `estado` | `EstadoPedido` | lectura |
| `historial` | `List<String>` | consulta mediante `verHistorial()` |

### Métodos

| Firma | Visibilidad | Descripción |
|---|---|---|
| `mostrarResumen()` | `public` | Imprime la ficha del pedido: tipo, identificador, dirección, distancia, repartidor, tiempo estimado y estado. |
| `calcularTiempoEntrega()` | `public abstract` | Tiempo estimado de entrega, en minutos. |
| `asignarRepartidor()` | `public` | Aplica el criterio de asignación del pedido y asigna el repartidor que le corresponde. |
| `asignarRepartidor(String)` | `public` | Asigna manualmente el pedido al repartidor indicado. |
| `despachar()` | `public` | Envía a reparto un pedido con repartidor asignado. |
| `cancelar()` | `public` | Cancela el pedido sin dejar constancia de un motivo. |
| `cancelar(String)` | `public` | Cancela el pedido dejando constancia del motivo. |
| `verHistorial()` | `public` | Imprime los eventos registrados por el pedido, en orden de ocurrencia. |
| `confirmarAsignacion(String)` | `protected` | Punto único de confirmación que comparten ambas sobrecargas. |
| `cumpleRequisitos()` | `protected` | Condición que debe cumplir el pedido para ser asignado. |
| `mostrarEncabezado()` | `protected` | Imprime el identificador, tipo y dirección del pedido. |

Cada asignación, despacho y cancelación queda anotada en el historial del pedido.

## Subclases

Cada subclase implementa `calcularTiempoEntrega()` y sobrescribe `asignarRepartidor()`.

| Clase | Tiempo de entrega | Criterio de asignación | Repartidor automático | Atributos propios |
|---|---|---|---|---|
| `PedidoComida` | 15 min + 2 min por km | El repartidor debe contar con mochila térmica | Makoto Kino | `requiereMochilaTermica: boolean` |
| `PedidoEncomienda` | 20 min + 1,5 min por km, ajustado a entero | Peso dentro del límite de 20 kg | Setsuna Meiou | `pesoKg: double`, `embalaje: String` |
| `PedidoExpress` | 10 min, más 5 min si la distancia supera los 5 km | Repartidor cercano con disponibilidad inmediata | Hotaru Tomoe | `disponibilidadInmediata: boolean` |

`PedidoEncomienda` y `PedidoExpress` sobrescriben además `cumpleRequisitos()`.

## ControladorDeEnvios

| Firma | Descripción |
|---|---|
| `registrar(Pedido)` | Incorpora un pedido a la gestión del controlador. |
| `despachar(Despachable)` | Envía a reparto el envío indicado. |
| `cancelar(Cancelable)` | Anula el envío indicado. |
| `verHistorial()` | Imprime las entregas realizadas y el repartidor que se hizo cargo de cada una. |

## Repartidor

Un repartidor es una tarea concurrente: implementa `Runnable` y su método `run()` recorre la ruta que tiene asignada.

| Atributo | Tipo | Descripción |
|---|---|---|
| `nombre` | `String` | Nombre con el que el repartidor se identifica en consola. |
| `pedidosAsignados` | `List<Pedido>` | Ruta del repartidor. |
| `controlador` | `ControladorDeEnvios` | Controlador a través del cual despacha sus entregas. |

| Firma | Descripción |
|---|---|
| `asignar(Pedido)` | Incorpora un pedido a la ruta y lo deja asignado a nombre del repartidor. |
| `mostrarRuta()` | Imprime la ruta y el tiempo estimado de cada entrega. |
| `run()` | Recorre la ruta entregando los pedidos uno tras otro. |

Por cada pedido de su ruta, `run()` informa la salida, lo despacha a través del controlador, simula el trayecto con `Thread.sleep()` de duración aleatoria entre 500 y 1500 ms, e informa la entrega. Los pedidos que ya no están en estado `ASIGNADO` —por ejemplo, uno cancelado antes de la salida— se omiten con un mensaje.

## Concurrencia

Las entregas se ejecutan en paralelo. `Main` envía los tres repartidores a un `ExecutorService` con un *pool* fijo de tres hilos y mantiene la simulación abierta hasta que todos terminan su ruta. Si la espera supera el plazo previsto, se ordena el regreso a la base.

| Aspecto | Resolución |
|---|---|
| Ejecución | `ExecutorService` con *pool* fijo de tres hilos |
| Término | Espera acotada a un minuto; vencido el plazo, las tareas se detienen |
| Estado compartido | Ninguno durante la ejecución concurrente |
| Interrupción | Se restaura la marca del hilo y se abandona la ruta |

El orden en que los repartidores escriben en consola cambia entre ejecuciones, porque la planificación de los hilos depende de la máquina virtual y del sistema operativo. El resultado no cambia: las mismas seis entregas realizadas y el pedido cancelado omitido.

## Diseño

El sistema se organiza en torno a cuatro piezas: una clase abstracta que reúne lo común a todo pedido, tres contratos de comportamiento, un gestor que opera sobre esos contratos y un repartidor que ejecuta las entregas de forma concurrente.

### Reutilización

La clase `Pedido` reúne los atributos y el comportamiento que comparten los tres tipos de pedido: los datos del envío, el resumen, la asignación de repartidor, el despacho, la cancelación y el historial. Las subclases definen únicamente aquello que cambia entre un tipo y otro: la fórmula del tiempo de entrega, el criterio de asignación y la condición que debe cumplirse para asignar.

Las dos versiones de `asignarRepartidor` terminan llamando al mismo método `confirmarAsignacion(String)`. Con esto, la regla de que un pedido solo se asigna si cumple sus requisitos queda escrita una sola vez y se aplica por igual en la asignación automática y en la manual.

### Escalabilidad

Para incorporar un cuarto tipo de pedido basta con heredar de `Pedido` e implementar `calcularTiempoEntrega()`. El despacho, la cancelación y el historial ya vienen resueltos en la clase base. No es necesario modificar ninguna clase existente, tampoco el controlador: como `ControladorDeEnvios` recibe los envíos declarados como `Despachable` y `Cancelable`, puede operar sobre el tipo nuevo sin conocerlo.

Las capacidades también pueden crecer por separado. Cada interfaz declara un solo método, de manera que una clase que en el futuro solo necesite cancelarse implementa `Cancelable` sin asumir las demás responsabilidades.

### Aislamiento entre hilos

Todo el estado compartido se construye antes de iniciar la ejecución concurrente: los pedidos se registran en el controlador y las rutas quedan armadas mientras corre un solo hilo. Durante la simulación cada repartidor opera únicamente sobre los pedidos de su propia ruta, de modo que dos hilos nunca escriben sobre el mismo objeto.

El diseño evita las condiciones de carrera en lugar de resolverlas con bloqueos: sin estructuras mutables compartidas no hay secciones críticas que proteger.

`Thread.sleep()` puede lanzar `InterruptedException` mientras un repartidor simula un trayecto. En ese caso se restaura la marca de interrupción del hilo, se informa la situación y se abandona la ruta, en lugar de descartar la excepción en silencio. `Main` aplica el mismo criterio cuando el hilo principal es interrumpido durante la espera.

### Mantenibilidad

Las responsabilidades están repartidas entre los paquetes: las reglas de negocio en el modelo, la coordinación de los envíos en el gestor, la ejecución concurrente de las entregas en `Repartidor` y el armado de la simulación en `Main`.

El estado del pedido se modela con el enum `EstadoPedido` y no con banderas booleanas separadas. De esta forma un pedido no puede quedar cancelado y despachado a la vez, y las transiciones válidas quedan concentradas en `despachar()` y `cancelar()`. Un cambio en la manera de cancelar tampoco obliga a modificar las clases que solo necesitan consultar el historial.

## Ejecución

Desde IntelliJ IDEA, ejecutar `Main`.

Desde la línea de comandos:

```bash
javac -encoding UTF-8 -d out/production/SpeedFast src/cl/speedfast/interfaces/*.java src/cl/speedfast/model/*.java src/cl/speedfast/gestores/*.java src/cl/speedfast/concurrencia/*.java src/cl/speedfast/main/*.java
```

```bash
java -cp out/production/SpeedFast cl.speedfast.main.Main
```

## Escenarios de prueba

`Main` simula una jornada completa de reparto con siete pedidos y tres repartidores, en cinco bloques:

1. **Pedidos de la jornada.** Los siete pedidos se registran en el controlador y cada uno muestra su ficha con `mostrarResumen()`.
2. **Asignación de repartidores.** Cada repartidor recibe su ruta mediante `asignar(Pedido)`, que aplica el criterio del tipo de pedido y lo deja a su nombre. Se imprime la ruta resultante de cada uno.
3. **Cambios antes de salir a reparto.** Un pedido se cancela a traves del controlador, que lo recibe como `Cancelable`. La ruta de Makoto Kino queda con un pedido que ya no debe entregarse.
4. **Salida a reparto.** Los tres repartidores se ejecutan concurrentemente mediante `ExecutorService`. Cada uno recorre su ruta, despacha sus pedidos, simula el trayecto y omite el pedido cancelado.
5. **Cierre de la jornada.** El controlador imprime las entregas realizadas y cada pedido muestra su historial de eventos.

| Repartidor | Ruta | Entregas |
|---|---|---|
| Usagi Tsukino | `PED-001`, `PED-002` | 2 |
| Makoto Kino | `PED-003`, `PED-004`, `PED-007` | 2, el tercero fue cancelado |
| Minako Aino | `PED-005`, `PED-006` | 2 |

Al finalizar, el historial registra seis entregas y el pedido `PED-007` queda como cancelado.

## Autora

Kamila Villablanca

## Contexto

Proyecto desarrollado para la asignatura Desarrollo Orientado a Objetos II, Duoc UC.
