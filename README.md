# SpeedFast

Sistema de asignación de repartidores para una empresa de reparto a domicilio.

Cada tipo de servicio aplica un criterio distinto de asignación. El sistema resuelve esa diferencia mediante una jerarquía de clases con sobrescritura de métodos, sin condicionales por tipo en el código cliente.

## Requisitos

- JDK 21
- IntelliJ IDEA

## Estructura

```
SpeedFast/
├── src/
│   └── cl/
│       └── speedfast/
│           ├── model/
│           │   ├── Pedido.java
│           │   ├── PedidoComida.java
│           │   ├── PedidoEncomienda.java
│           │   ├── PedidoExpress.java
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
| `cl.speedfast.model` | Modelo de dominio: la jerarquía de pedidos |
| `cl.speedfast.main` | Punto de entrada de la aplicación |

## Modelo de clases

`PedidoComida`, `PedidoEncomienda` y `PedidoExpress` heredan de `Pedido`.

| Clase | Responsabilidad | Criterio de asignación |
|---|---|---|
| `Pedido` | Clase base. Datos comunes y flujo de asignación. | Criterio general, sin restricciones |
| `PedidoComida` | Pedidos de restaurante. | El repartidor debe contar con mochila térmica |
| `PedidoEncomienda` | Documentos y paquetes. | Peso dentro del límite de 20 kg |
| `PedidoExpress` | Compras de supermercado o farmacia. | Repartidor cercano con disponibilidad inmediata |
| `Main` | Punto de entrada. Ejecuta los escenarios de prueba. | — |

## Pedido

Estado encapsulado. Los tres atributos se reciben en el constructor y se exponen mediante *getters*; solo `direccionEntrega` admite modificación posterior.

| Atributo | Tipo | Acceso |
|---|---|---|
| `idPedido` | `String` | lectura |
| `direccionEntrega` | `String` | lectura y escritura |
| `tipoPedido` | `String` | lectura |

### Métodos

| Firma | Visibilidad | Descripción |
|---|---|---|
| `asignarRepartidor()` | `public` | Aplica y reporta el criterio de asignación. Sobrescrito por cada subclase. |
| `asignarRepartidor(String)` | `public` | Ejecuta la validación anterior y confirma el repartidor si esta se cumple. |
| `cumpleRequisitos()` | `protected` | Condición de asignación del pedido. Devuelve `true` en la clase base. |
| `mostrarEncabezado()` | `protected` | Imprime el identificador, tipo y dirección del pedido. |

`asignarRepartidor(String)` se define una sola vez en `Pedido` y opera correctamente sobre toda la jerarquía: la llamada interna a `asignarRepartidor()` se despacha según el tipo real del objeto.

## Subclases

Cada subclase agrega el estado que su criterio requiere y sobrescribe `asignarRepartidor()`. `PedidoEncomienda` y `PedidoExpress` sobrescriben además `cumpleRequisitos()`; `PedidoComida` hereda el comportamiento de la clase base.

| Clase | Atributos propios |
|---|---|
| `PedidoComida` | `requiereMochilaTermica: boolean` |
| `PedidoEncomienda` | `pesoKg: double`, `embalaje: String`, `PESO_MAXIMO_KG = 20.0` |
| `PedidoExpress` | `distanciaKm: double`, `disponibilidadInmediata: boolean` |

## Ejecución

Desde IntelliJ IDEA, ejecutar `Main`.

Desde la línea de comandos:

```bash
javac -encoding UTF-8 -d out/production/SpeedFast src/cl/speedfast/model/*.java src/cl/speedfast/main/*.java
```

```bash
java -cp out/production/SpeedFast cl.speedfast.main.Main
```

## Escenarios de prueba

`Main` ejecuta cuatro bloques:

1. `asignarRepartidor()` sobre un arreglo `Pedido[]` con una instancia de cada subclase.
2. `asignarRepartidor(String)` sobre las mismas instancias.
3. Una instancia de `Pedido` sin especializar.
4. Una encomienda de 35 kg y una compra express sin disponibilidad, que no superan la validación y no se asignan.

## Autora

Kamila Villablanca

## Contexto

Actividad Formativa 1 — PRY2203 Desarrollo Orientado a Objetos II, Duoc UC.
