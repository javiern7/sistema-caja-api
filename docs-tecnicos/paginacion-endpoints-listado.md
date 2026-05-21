# Paginación Backend para Endpoints de Listado

## Objetivo

Incorporar paginación server-side consistente en endpoints de consulta masiva para evitar descargas completas, mantener compatibilidad con filtros actuales y habilitar ordenamiento controlado por backend.

## Contrato propuesto

Todos los listados paginados deben responder dentro del `data` estándar de `ApiResponse` con un objeto `PageResponse<T>`:

```json
{
  "success": true,
  "message": "Operaciones auditadas obtenidas correctamente.",
  "data": {
    "items": [],
    "page": 0,
    "size": 20,
    "totalElements": 0,
    "totalPages": 0,
    "first": true,
    "last": true,
    "sort": [
      "occurredAt,desc"
    ]
  },
  "meta": {},
  "error": null
}
```

## Query params estándar

- `page`: índice base 0. Default `0`.
- `size`: tamaño de página. Default `20`.
- `sort`: uno o varios criterios en formato `campo,direccion`.

Ejemplos:

- `?page=0&size=20`
- `?sort=occurredAt,desc`
- `?sort=module,asc&sort=occurredAt,desc`

## Reglas de validación

- `page >= 0`
- `1 <= size <= 100`
- solo se permiten campos de ordenamiento definidos por endpoint
- la dirección debe ser `asc` o `desc`

Los errores de paginación inválida deben responder con `VALIDATION_ERROR` y HTTP `400`.

## Infraestructura común implementada

- `common.pagination.PageResponse`
- `common.pagination.PageableFactory`

`PageableFactory` concentra:

- defaults de paginación
- validación de `page` y `size`
- parseo de `sort`
- whitelist de campos ordenables

## Estrategia de implementación por módulo

### 1. Mover filtros al repositorio

No paginar listas que hoy filtran en memoria. Primero migrar a `JpaSpecificationExecutor` o consultas paginadas específicas.

### 2. Separar listados de detalles

En módulos donde el listado devuelve DTOs pesados, crear DTO de resumen para el endpoint paginado.

Casos críticos:

- ventas
- compras
- cajas

### 3. Mantener filtros compatibles

Los filtros funcionales del endpoint se conservan y se combinan con `Pageable` en el repositorio.

### 4. Diferenciar listados vs reportes agregados

Para reportes, separar:

- endpoint de tabla paginada
- endpoint de resumen agregado
- endpoint de exportación

Esto evita inconsistencias entre una página parcial y los totales globales del reporte.

## Módulos priorizados

### Fase 1: migración directa

- auditoría
- historial de reportes
- egresos
- movimientos de stock
- catálogos administrativos

### Fase 2: migración con DTO resumen

- ventas
- compras
- historial de cajas

### Fase 3: migración con rediseño de consulta

- stock actual
- reportes JSON tabulares

## Estado actual del avance

### Implementado

- `GET /api/v1/auditoria/operaciones`
- `GET /api/v1/reportes/historial`
- `GET /api/v1/egresos`
- `GET /api/v1/productos`
- `GET /api/v1/stock`
- `GET /api/v1/stock/movimientos`
- `GET /api/v1/proveedores`
- `GET /api/v1/usuarios`
- `GET /api/v1/roles`
- `GET /api/v1/negocios-eventos`
- `GET /api/v1/ventas`
- `GET /api/v1/compras`
- `GET /api/v1/cajas`
- `GET /api/v1/reportes/ventas/detalle`
- `GET /api/v1/reportes/caja/detalle`
- `GET /api/v1/reportes/compras/detalle`
- `GET /api/v1/reportes/egresos/detalle`
- `GET /api/v1/reportes/stock/detalle`

Estos endpoints ya:

- aceptan `page`, `size`, `sort`
- conservan filtros actuales
- ejecutan filtros en base de datos
- devuelven `PageResponse<T>`

## Decisiones adicionales implementadas

### Ventas

- el listado ahora devuelve `SaleListResponse`
- el detalle completo con `items` y `payments` se mantiene en `GET /api/v1/ventas/{saleId}`

### Compras

- el listado ahora devuelve `PurchaseListResponse`
- el detalle completo con `items` se mantiene en `GET /api/v1/compras/{purchaseId}`

### Cajas

- el listado ahora devuelve `CashBoxListResponse`
- el detalle con `movements` se mantiene en `GET /api/v1/cajas/{cashBoxId}/resumen` y `GET /api/v1/cajas/activa`
- se eliminó el filtrado en memoria y el patrón N+1 del endpoint de listado

### Stock actual

- el listado ahora devuelve `PageResponse<StockCurrentResponse>`
- la paginación se resuelve sobre productos, y luego se enriquece solo la página solicitada con `stock_current`
- se evita cargar todos los productos y todo el stock actual en memoria para cada consulta

### Reportes tabulares

- los endpoints resumen existentes se mantienen sin cambios:
  - `/api/v1/reportes/ventas`
  - `/api/v1/reportes/caja`
  - `/api/v1/reportes/compras`
  - `/api/v1/reportes/egresos`
  - `/api/v1/reportes/stock`
- se agregan endpoints `*/detalle` paginados para las tablas
- esta separación evita mezclar totales globales del reporte con la página visible de la grilla
- las consultas paginadas no registran historial por cada cambio de página; el historial sigue asociado a la generación de reportes resumen o exportables

## Guía para próximos endpoints

### Auditoría / historial de reportes

Patrón de referencia:

1. extender repositorio con `JpaSpecificationExecutor`
2. mover filtros a `Specification`
3. aceptar `page`, `size`, `sort`
4. mapear `Page<Entity>` a `PageResponse<DTO>`

### Ventas / compras / cajas

Además del patrón anterior:

1. crear DTO de listado liviano
2. mantener endpoint de detalle por `id`
3. evitar incluir `items`, `payments` o `movements` en la grilla

### Reportes

No reutilizar directamente el endpoint agregado actual para paginación de filas cuando también devuelve totales globales. Conviene separar contrato.

## Riesgos conocidos

- romper frontend existente si se cambia la forma de `data` sin coordinar rollout
- paginar DTOs pesados sin separar detalle seguiría dejando respuestas costosas
- combinar joins complejos con `Page` puede requerir consultas específicas en algunos módulos

## Recomendación de rollout

1. migrar frontend endpoint por endpoint
2. publicar primero módulos administrativos y trazabilidad
3. luego ventas, compras y cajas con DTO de resumen
4. por último rediseñar reportes tabulares y stock actual
