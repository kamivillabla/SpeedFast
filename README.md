# SpeedFast

Sistema de gestión de pedidos para una empresa de reparto a domicilio.

Cada tipo de pedido estima su tiempo de entrega con una fórmula distinta y aplica su propio criterio de asignación de repartidor. Un controlador de envíos despacha, cancela y consulta el historial trabajando únicamente contra los contratos que los pedidos implementan.

La jornada de reparto se ejecuta de forma concurrente: los pedidos llegan a una zona de carga común y los repartidores los retiran al mismo tiempo, coordinados para que cada pedido lo entregue un único repartidor.

El sistema se opera desde una interfaz gráfica de escritorio construida con Java Swing, en la que se registran los pedidos, se consulta el listado y se gestionan las entregas.

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
│           ├── modelo/
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
│           │   ├── ZonaDeCarga.java
│           │   ├── Repartidor.java
│           │   └── package-info.java
│           ├── vista/
│           │   ├── CampoValidado.java
│           │   ├── Validaciones.java
│           │   ├── VentanaPrincipal.java
│           │   ├── VentanaRegistroPedido.java
│           │   ├── VentanaListaPedidos.java
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
| `cl.speedfast.modelo` | Modelo de dominio: la jerarquía de pedidos |
| `cl.speedfast.gestores` | Coordinación de las operaciones sobre los envíos |
| `cl.speedfast.concurrencia` | Recurso compartido y ejecución concurrente de las entregas |
| `cl.speedfast.vista` | Ventanas Swing desde las que se opera el sistema |
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
| `ZonaDeCarga` | Recurso compartido. Almacena los pedidos en espera y controla su retiro. |
| `Repartidor` | Tarea concurrente. Retira pedidos de la zona de carga y los entrega. |
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

    namespace cl.speedfast.modelo {
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
            +setEstado(EstadoPedido estado) void
            +setRepartidor(String repartidor) void
            +toString() String
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
            EN_REPARTO
            ENTREGADO
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
        class ZonaDeCarga {
            -List~Pedido~ pedidosEnEspera
            +agregarPedido(Pedido pedido) void
            +retirarPedido() Pedido
            +pedidosEnEspera() int
        }
        class Repartidor {
            -String nombre
            -ZonaDeCarga zonaDeCarga
            -int entregasRealizadas
            +getEntregasRealizadas() int
            +run() void
            -informar(String mensaje) void
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
    ZonaDeCarga "1" o-- "0..*" Pedido : pedidosEnEspera
    Repartidor "3" --> "1" ZonaDeCarga : retira de

    classDef contrato fill:#e8f5e9,stroke:#2e7d32,color:#1b5e20
    classDef modelo fill:#fff3e0,stroke:#e65100,color:#5d2f00
    classDef gestor fill:#e3f2fd,stroke:#1565c0,color:#0d3c67
    classDef concurrente fill:#f3e5f5,stroke:#6a1b9a,color:#3d0d55

    cssClass "Despachable,Cancelable,Rastreable" contrato
    cssClass "Pedido,PedidoComida,PedidoEncomienda,PedidoExpress,EstadoPedido" modelo
    cssClass "ControladorDeEnvios" gestor
    cssClass "Runnable,ZonaDeCarga,Repartidor" concurrente
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
| `EN_REPARTO` | El pedido fue retirado de la zona de carga y va en camino a su destino. |
| `ENTREGADO` | El pedido llegó a su destino. |
| `CANCELADO` | El pedido fue anulado antes de salir a reparto. |

El recorrido habitual va de `PENDIENTE` a `ENTREGADO`. `CANCELADO` es la única salida anticipada y solo se admite mientras el pedido no haya salido a reparto.

## Pedido (clase abstracta)

Los atributos se reciben en el constructor y se exponen mediante *getters*. Solo `repartidor` y `estado` admiten modificación posterior: ambos cambian mientras el pedido avanza por la zona de carga.

| Atributo | Tipo | Acceso |
|---|---|---|
| `idPedido` | `String` | lectura |
| `direccionEntrega` | `String` | lectura |
| `distanciaKm` | `double` | lectura |
| `tipoPedido` | `String` | lectura |
| `repartidor` | `String` | lectura y escritura |
| `estado` | `EstadoPedido` | lectura y escritura |
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
| `setEstado(EstadoPedido)` | `public` | Actualiza el estado del pedido. |
| `setRepartidor(String)` | `public` | Registra al repartidor que se hace cargo del pedido. |
| `toString()` | `public` | Describe el pedido en una línea: tipo, identificador, destino y estado. |
| `confirmarAsignacion(String)` | `protected` | Punto único de confirmación que comparten ambas sobrecargas. |
| `cumpleRequisitos()` | `protected` | Condición que debe cumplir el pedido para ser asignado. |
| `mostrarEncabezado()` | `protected` | Imprime el identificador, tipo y dirección del pedido. |

Cada asignación, despacho y cancelación queda anotada en el historial del pedido.

## Subclases

Cada subclase implementa `calcularTiempoEntrega()` y sobrescribe `asignarRepartidor()` y `mostrarResumen()`, esta última para agregar a la ficha su dato propio.

| Clase | Tiempo de entrega | Criterio de asignación | Repartidor automático | Atributos propios |
|---|---|---|---|---|
| `PedidoComida` | 15 min + 2 min por km | El repartidor debe contar con mochila térmica | Makoto Kino | `requiereMochilaTermica: boolean` |
| `PedidoEncomienda` | 20 min + 1,5 min por km, ajustado a entero | Peso dentro del límite de 20 kg | Setsuna Meiou | `pesoKg: double`, `embalaje: String` |
| `PedidoExpress` | 10 min, más 5 min si la distancia supera los 5 km | Repartidor cercano con disponibilidad inmediata | Hotaru Tomoe | `disponibilidadInmediata: boolean` |

`PedidoEncomienda` y `PedidoExpress` sobrescriben además `cumpleRequisitos()`.

| Clase | Línea que agrega a la ficha |
|---|---|
| `PedidoComida` | Si el pedido requiere mochila térmica |
| `PedidoEncomienda` | El peso y el embalaje |
| `PedidoExpress` | Si hay disponibilidad inmediata |

## ControladorDeEnvios

| Firma | Descripción |
|---|---|
| `registrar(Pedido)` | Incorpora un pedido a la gestión del controlador. |
| `getEnvios()` | Entrega los envíos registrados, en el orden en que fueron incorporados. |
| `buscarIdRegistrado(String)` | Busca un identificador ya en uso, sin distinguir mayúsculas de minúsculas. |
| `despachar(Despachable)` | Envía a reparto el envío indicado. |
| `cancelar(Cancelable)` | Anula el envío indicado. |
| `verHistorial()` | Imprime los pedidos ya entregados y el repartidor que se hizo cargo de cada uno. |

## ZonaDeCarga

Los pedidos que esperan repartidor se acumulan en la zona de carga. Es la única estructura que los repartidores comparten y, por lo tanto, el punto donde se concentra la sincronización.

| Firma | Descripción |
|---|---|
| `agregarPedido(Pedido)` | Deposita un pedido a la espera de un repartidor. |
| `retirarPedido()` | Entrega el siguiente pedido en espera. Devuelve `null` cuando la zona queda vacía. |
| `pedidosEnEspera()` | Cantidad de pedidos que aún no tienen repartidor. |

Los tres métodos son `synchronized`: mientras un repartidor retira, ningún otro puede consultar ni modificar los pedidos en espera.

## Repartidor

Un repartidor es una tarea concurrente: implementa `Runnable` y su método `run()` retira pedidos de la zona de carga hasta agotarla.

| Atributo | Tipo | Descripción |
|---|---|---|
| `nombre` | `String` | Nombre con el que el repartidor se identifica en consola. |
| `zonaDeCarga` | `ZonaDeCarga` | Zona compartida desde la que retira sus pedidos. |
| `entregasRealizadas` | `int` | Entregas completadas durante el turno. |

| Firma | Descripción |
|---|---|
| `run()` | Retira y entrega pedidos hasta que la zona de carga queda vacía. |
| `getEntregasRealizadas()` | Entregas completadas por el repartidor. |

Por cada pedido retirado, `run()` lo marca `EN_REPARTO`, simula el trayecto con `Thread.sleep()` de duración aleatoria entre 500 y 1500 ms y lo deja `ENTREGADO`, informando cada paso en consola.

## Concurrencia

Los pedidos llegan a una zona de carga común y los repartidores los retiran en paralelo. Los repartidores implementan `Runnable`, de modo que se ejecutan sobre un `ExecutorService` con un *pool* fijo de hilos que permanece abierto hasta que todos terminan su turno.

| Aspecto | Resolución |
|---|---|
| Ejecución | `ExecutorService` con *pool* fijo de tres hilos |
| Recurso compartido | Una única instancia de `ZonaDeCarga` |
| Sección crítica | Comprobar si quedan pedidos y retirar uno |
| Mecanismo | Métodos `synchronized` sobre la zona de carga |
| Término | Espera acotada; vencido el plazo, las tareas se detienen |
| Interrupción | Se restaura la marca del hilo y el repartidor termina su turno |

El reparto del trabajo entre los repartidores cambia entre ejecuciones, porque la planificación de los hilos depende de la máquina virtual y del sistema operativo. Lo que no cambia es el resultado: cada pedido se entrega una sola vez y la zona de carga queda vacía.

## Interfaz gráfica

Las ventanas se construyen con Java Swing y comparten una única instancia de `ControladorDeEnvios`, que es donde residen los pedidos. Ninguna ventana guarda pedidos por su cuenta: consultan el controlador cada vez que necesitan mostrar datos, de modo que un pedido registrado aparece de inmediato en el listado.

| Ventana | Rol |
|---|---|
| `VentanaPrincipal` | Reúne las operaciones disponibles y abre la ventana que corresponde a cada una |
| `VentanaRegistroPedido` | Formulario de alta de pedidos, con validación de los datos ingresados |
| `VentanaListaPedidos` | Tabla de pedidos registrados y gestión de sus entregas |
| `CampoValidado` | Campo de formulario que señala sus propios errores mientras se escribe |
| `Validaciones` | Reglas de validación reutilizables, combinables por campo |

### VentanaPrincipal

Organiza sus componentes con `BorderLayout`: el encabezado al norte y, al centro, un `GridLayout` con los tres accesos del sistema.

| Acción | Destino | Pedidos que presenta | Operaciones |
|---|---|---|---|
| Registrar pedido | `VentanaRegistroPedido` | | Alta de pedidos |
| Listar pedidos | `VentanaListaPedidos` | Todos | Ninguna: solo consulta |
| Asignar repartidor / Iniciar entrega | `VentanaListaPedidos` | Los que esperan gestión | Asignar y despachar |

Consultar el inventario y gestionar las entregas ocurren sobre la misma tabla, porque asignar un repartidor o despachar un pedido exige elegirlo antes. Lo que distingue a una acción de la otra es qué muestra y qué permite: el listado completo es una vista de consulta y no ofrece botones de operación; la de gestión deja a la vista solo los pedidos que todavía admiten una acción y pone esas acciones a mano. Un pedido despachado o cancelado desaparece de la segunda, pero permanece en la primera.

Las ventanas se crean una sola vez y se reutilizan en las aperturas siguientes.

### VentanaRegistroPedido

Solicita los datos comunes a todo pedido —identificador, dirección, distancia y tipo— y los propios del tipo elegido. Los campos específicos se agrupan en un `CardLayout` que el `JComboBox` conmuta, de manera que el formulario solo pide lo que el pedido necesita.

| Tipo | Campos propios |
|---|---|
| Comida | Requiere mochila térmica |
| Encomienda | Peso en kilos y embalaje |
| Express | Disponibilidad inmediata |

Ningún pedido se crea con datos incompletos ni con valores que el modelo no pueda interpretar.

| Dato | Condiciones |
|---|---|
| ID del pedido | Obligatorio · entre 3 y 20 caracteres · letras, números y guiones, sin espacios · sin repetir entre los pedidos ya registrados |
| Dirección de entrega | Obligatoria · entre 5 y 120 caracteres · debe incluir letras |
| Distancia | Obligatoria · número finito entre 0,1 y 100 |
| Peso | Obligatorio · número finito entre 0,1 y 100 |
| Embalaje | Obligatorio · entre 3 y 50 caracteres · debe incluir letras |

Los campos numéricos admiten coma o punto como separador decimal, y rechazan los textos que no representan una cantidad, incluidos `NaN` e `Infinity`, que de otro modo superarían una simple comparación contra cero.

El límite superior del peso es físico, no reglamentario: una encomienda de más de veinte kilos se registra sin problemas y es el propio `PedidoEncomienda` el que luego rechaza asignarla y la deriva a revisión.

Para que ese desenlace no sorprenda al asignar, el campo advierte en el momento: al superar el peso que un repartidor puede llevar consigo, el cuadro se destaca en ámbar y anuncia que la encomienda requerirá vehículo de carga. Es una advertencia, no un rechazo, y el registro continúa con normalidad. El límite lo publica `PedidoEncomienda.PESO_MAXIMO_KG`, de modo que la vista lo consulta en lugar de repetirlo: la regla sigue perteneciendo al modelo.

Al pulsar *Guardar*, el formulario comprueba los campos que el tipo elegido exige, destaca los que estén pendientes y lleva el foco al primero de ellos. Ningún dato llega a `Pedido` sin haber pasado antes por sus reglas.

### Validaciones

Las reglas son funciones que reciben el contenido de un campo y devuelven el motivo del rechazo, o nada si el valor es aceptable. `Validaciones.todas(...)` las encadena y entrega la primera que se incumple, de modo que cada campo declara sus exigencias en el orden en que conviene informarlas.

Un campo distingue dos niveles. Un **error** deja el cuadro en rojo e impide registrar el pedido. Un **aviso** lo deja en ámbar para informar algo relevante sobre un valor que, aun así, es aceptable. Cuando ambos coinciden manda el error, porque un dato que no sirve no necesita matices.

| Regla | Exigencia |
|---|---|
| `obligatorio(mensaje)` | El campo tiene contenido |
| `longitudEntre(min, max)` | La extensión está dentro del rango |
| `formatoDeCodigo()` | Letras, números y guiones, sin espacios ni símbolos |
| `contieneLetras(mensaje)` | El texto no es solo números o signos |
| `numeroEntre(concepto, min, max)` | Es un número finito dentro del rango |

Salvo `obligatorio`, todas aceptan el campo vacío: esa condición la cubre una sola regla, y así un campo en blanco muestra un único mensaje en vez de varios a la vez.

### CampoValidado

Cada dato del formulario es un `CampoValidado`: una fila que agrupa la etiqueta, el cuadro de texto y su mensaje de error, con la regla de validación que le corresponde.

La comprobación ocurre mientras el usuario escribe. Un `DocumentListener` aplica la regla ante cada modificación del contenido, de modo que el cuadro de texto se rodea de un borde rojo y el motivo aparece bajo el campo en el momento en que el valor deja de ser aceptable, sin esperar al envío del formulario. En cuanto el valor se corrige, el borde y el mensaje desaparecen.

| Situación | Comportamiento |
|---|---|
| Formulario recién abierto | Sin advertencias: la regla se aplica ante cambios del usuario, no al construir la ventana |
| Valor inaceptable | Borde rojo y motivo bajo el campo |
| Valor corregido | El campo recupera su borde y el mensaje se retira |
| Formulario limpiado tras guardar | Los campos se vacían sin quedar marcados en rojo |
| Cambio de tipo de pedido | Los campos que dejan de ser exigibles descartan su advertencia |

El mensaje ocupa siempre una línea, con o sin error, para que el formulario no cambie de tamaño mientras se completa. La regla se entrega como una función que recibe el contenido del campo y devuelve el motivo del rechazo, o nada si el valor es aceptable: así cada campo define qué le resulta válido sin que la clase conozca los datos de un pedido.

### VentanaListaPedidos

Presenta los pedidos en un `JTable` gobernado por un `DefaultTableModel` de celdas no editables: la tabla informa el estado del sistema y no es la vía para modificarlo.

| Columna | Origen |
|---|---|
| ID | `getIdPedido()` |
| Tipo | `getTipoPedido()` |
| Dirección | `getDireccionEntrega()` |
| Distancia (km) | `getDistanciaKm()` |
| Repartidor | `getRepartidor()` |
| Tiempo estimado (min) | `calcularTiempoEntrega()` |
| Estado | `getEstado()` |

La ventana admite dos alcances. `mostrarTodos()` presenta el inventario completo en modo consulta: los botones de operación no aparecen, porque no es ahí donde se opera. `mostrarPorGestionar()` deja a la vista solo los pedidos pendientes o asignados —los que aún admiten una acción— y ofrece los botones para resolverlos. El título de la ventana anuncia cuál está activo.

Cada alcance declara su título, qué pedidos admite y si habilita la gestión, de modo que agregar una vista nueva no obliga a repartir esas decisiones por la clase.

La tabla se reconstruye desde el controlador tras cada operación y conserva el pedido elegido, identificándolo por su ID y no por la posición que ocupaba: así, operar dos veces seguidas sobre uno mismo no obliga a buscarlo de nuevo aunque la tabla haya cambiado de contenido.

Sobre el pedido seleccionado se ofrecen dos operaciones. **Asignar repartidor** pide un nombre: si se indica uno, el pedido se asigna a esa persona mediante `asignarRepartidor(String)`; si el campo queda en blanco, `asignarRepartidor()` aplica el criterio automático del tipo de pedido. **Iniciar entrega** despacha el pedido hacia su destino.

Ninguna de las dos operaciones da por hecho su resultado. Como el modelo rechaza las transiciones que no corresponden —un pedido sin repartidor no se despacha, uno que no cumple los requisitos de su tipo queda derivado a revisión—, la ventana compara el estado alcanzado con el esperado y avisa en consecuencia, informando el estado vigente cuando la operación no prospera.

## Diseño

El sistema se organiza en torno a cinco piezas: una clase abstracta que reúne lo común a todo pedido, tres contratos de comportamiento, un gestor que opera sobre esos contratos, una zona de carga que custodia los pedidos en espera y un repartidor que ejecuta las entregas de forma concurrente.

### Reutilización

La clase `Pedido` reúne los atributos y el comportamiento que comparten los tres tipos de pedido: los datos del envío, el resumen, la asignación de repartidor, el despacho, la cancelación y el historial. Las subclases definen únicamente aquello que cambia entre un tipo y otro: la fórmula del tiempo de entrega, el criterio de asignación y la condición que debe cumplirse para asignar.

Las dos versiones de `asignarRepartidor` terminan llamando al mismo método `confirmarAsignacion(String)`. Con esto, la regla de que un pedido solo se asigna si cumple sus requisitos queda escrita una sola vez y se aplica por igual en la asignación automática y en la manual.

### Escalabilidad

Para incorporar un cuarto tipo de pedido basta con heredar de `Pedido` e implementar `calcularTiempoEntrega()`. El despacho, la cancelación y el historial ya vienen resueltos en la clase base. No es necesario modificar ninguna clase existente, tampoco el controlador: como `ControladorDeEnvios` recibe los envíos declarados como `Despachable` y `Cancelable`, puede operar sobre el tipo nuevo sin conocerlo.

Las capacidades también pueden crecer por separado. Cada interfaz declara un solo método, de manera que una clase que en el futuro solo necesite cancelarse implementa `Cancelable` sin asumir las demás responsabilidades.

### Acceso al recurso compartido

Los tres repartidores trabajan sobre la misma zona de carga, de modo que retirar un pedido es una operación que varios hilos pueden intentar a la vez. La sección crítica no es solo la extracción: comprobar si quedan pedidos y retirar uno deben ocurrir como una única operación indivisible. Si la comprobación quedara fuera de la protección, dos repartidores podrían verificar que hay pedidos disponibles antes de que alguno retire.

Por eso `retirarPedido()` es `synchronized` completo y no un bloque parcial. Una vez retirado, el pedido ya no está en la zona: desde ese momento un solo repartidor trabaja sobre él, y marcarlo `EN_REPARTO` y `ENTREGADO` no requiere protección adicional.

Un solo mecanismo basta para el problema. No se combinan bloqueos explícitos, semáforos ni contadores atómicos, que aquí solo agregarían complejidad sin resolver nada que el monitor de la zona no cubra.

`Thread.sleep()` puede lanzar `InterruptedException` mientras un repartidor simula un trayecto. En ese caso se restaura la marca de interrupción del hilo, se informa la situación y el repartidor termina su turno sin tomar más pedidos. `Main` aplica el mismo criterio cuando el hilo principal es interrumpido durante la espera.

### Mantenibilidad

Las responsabilidades están repartidas entre los paquetes: las reglas de negocio en el modelo, la coordinación de los envíos en el gestor, la custodia del recurso compartido en `ZonaDeCarga`, la ejecución de las entregas en `Repartidor` y la interacción con el usuario en las ventanas de la vista.

Las ventanas no deciden nada sobre los pedidos: recogen datos, los validan como entrada de formulario y delegan en el modelo y en el controlador. Las reglas que gobiernan cuándo un pedido puede asignarse o despacharse viven en una sola parte, y la interfaz se limita a reflejar el resultado. Por eso un cambio en esas reglas no obliga a tocar la vista, y un cambio en la disposición de una ventana no puede alterar el comportamiento del sistema.

El estado del pedido se modela con el enum `EstadoPedido` y no con banderas booleanas separadas. De esta forma un pedido no puede quedar cancelado y despachado a la vez, y las transiciones válidas quedan concentradas en `despachar()` y `cancelar()`. Un cambio en la manera de cancelar tampoco obliga a modificar las clases que solo necesitan consultar el historial.

## Ejecución

`Main` crea el controlador de envíos y abre `VentanaPrincipal` en el hilo de despacho de eventos de Swing. El sistema parte sin pedidos: los datos se ingresan desde el formulario de registro.

Desde IntelliJ IDEA, ejecutar `Main`.

Desde la línea de comandos:

```bash
javac -encoding UTF-8 -d out/production/SpeedFast src/cl/speedfast/interfaces/*.java src/cl/speedfast/modelo/*.java src/cl/speedfast/gestores/*.java src/cl/speedfast/concurrencia/*.java src/cl/speedfast/vista/*.java src/cl/speedfast/main/*.java
```

```bash
java -cp out/production/SpeedFast cl.speedfast.main.Main
```

## Escenarios de prueba

Los pedidos se ingresan desde la interfaz, de modo que los escenarios se recorren operando las ventanas.

| Escenario | Recorrido | Resultado esperado |
|---|---|---|
| Registro válido | Completar el formulario con datos correctos para cada tipo | El pedido se confirma y aparece en la tabla en estado `PENDIENTE` |
| Identificador repetido | Escribir el ID de un pedido ya registrado | El campo se marca en rojo mientras se escribe, indicando el ID en conflicto |
| Dato no numérico | Escribir texto en distancia o peso | El campo se marca en rojo al teclear, indicando que el dato debe ser un número |
| Cantidad fuera de rango | Escribir cero, un negativo o un valor sobre el máximo | El campo se marca en rojo indicando el rango admitido |
| Identificador mal formado | Escribir un ID con espacios, símbolos o menos de tres caracteres | El campo se marca en rojo señalando el formato esperado |
| Dirección sin calle | Escribir solo números en la dirección | El campo se marca en rojo pidiendo el nombre de la calle |
| Encomienda con sobrepeso | Escribir un peso mayor a veinte kilos | El campo se destaca en ámbar avisando que requerirá vehículo de carga, sin impedir el registro |
| Sobrepeso al asignar | Asignar repartidor a esa encomienda | La asignación se rechaza y el pedido queda derivado a revisión |
| Corrección de un dato | Reemplazar un valor rechazado por uno válido | El borde y el mensaje desaparecen sin necesidad de guardar |
| Formulario incompleto | Pulsar *Guardar* con campos sin completar | Los campos exigibles se marcan y el foco va al primero pendiente |
| Campos del tipo | Alternar el tipo en el combo | El formulario muestra los campos propios del tipo y descarta las advertencias de los que ya no aplican |
| Asignación automática | Asignar repartidor dejando el nombre en blanco | El pedido queda `ASIGNADO` con el repartidor que determina su tipo |
| Asignación manual | Asignar repartidor indicando un nombre | El pedido queda `ASIGNADO` con el nombre ingresado |
| Asignación rechazada | Asignar una encomienda que excede el peso máximo | El pedido se deriva a revisión y permanece `PENDIENTE` |
| Entrega de un pedido sin asignar | Iniciar entrega sobre un pedido `PENDIENTE` | La operación se rechaza informando el estado vigente |
| Entrega de un pedido asignado | Iniciar entrega sobre un pedido `ASIGNADO` | El pedido queda `DESPACHADO` con su tiempo estimado |
| Operación sin selección | Pulsar una acción sin elegir fila | La ventana pide seleccionar un pedido de la tabla |
| Alcance del listado | Abrir el listado completo y el de gestión con pedidos en distintos estados | El primero los muestra todos; el segundo omite los despachados y cancelados |
| Modo de consulta | Abrir el listado completo | La ventana no ofrece botones para asignar ni despachar |

Los tres tipos de pedido calculan tiempos distintos para una misma distancia, y la columna de tiempo estimado lo refleja: un trayecto de cinco kilómetros da veinticinco minutos en comida, veintiocho en encomienda y diez en express, que a esa distancia aún no aplica su recargo.

## Autora

Kamila Villablanca

## Contexto

Proyecto desarrollado para la asignatura Desarrollo Orientado a Objetos II, Duoc UC.
