# Sistema Caja API

Base técnica backend del proyecto usando Java 21, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL y Flyway.

## Estado actual

Esta base ya deja preparado:

- estructura modular backend por dominio
- seguridad stateless con JWT
- autenticación persistente en base de datos
- seed automático de permisos, roles y usuarios base
- endpoints base de `auth`, `usuarios`, `roles`, `contextos-operativos`, `negocios-eventos`, `productos` y `proveedores`
- consultas base de `stock` actual y movimientos
- apertura, consulta activa, resumen y cierre de `cajas`
- registro, consulta, listado y anulacion de `ventas`
- registro, consulta, listado y anulacion de `compras`
- registro y consulta de `egresos`
- consulta de `auditoria` operativa y listado administrativo de `cajas`
- reportes operativos base y exportaciones iniciales
- respuesta y error estándar para API
- endpoint técnico de salud
- configuración de perfiles y carga de migraciones desde `database/`

## Endpoints disponibles en esta etapa

- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`
- `POST /api/v1/auth/logout`
- `GET /api/v1/system/health`
- `GET /api/v1/contextos-operativos`
- `GET /api/v1/negocios-eventos`
- `POST /api/v1/negocios-eventos`
- `PUT /api/v1/negocios-eventos/{operationalContextId}`
- `GET /api/v1/usuarios`
- `POST /api/v1/usuarios`
- `PUT /api/v1/usuarios/{userId}`
- `PATCH /api/v1/usuarios/{userId}/estado`
- `GET /api/v1/roles`
- `POST /api/v1/roles`
- `PUT /api/v1/roles/{roleId}/permisos`
- `GET /api/v1/productos`
- `POST /api/v1/productos`
- `PUT /api/v1/productos/{productId}`
- `PATCH /api/v1/productos/{productId}/estado`
- `GET /api/v1/proveedores`
- `POST /api/v1/proveedores`
- `PUT /api/v1/proveedores/{providerId}`
- `GET /api/v1/stock`
- `GET /api/v1/stock/movimientos`
- `POST /api/v1/cajas/aperturas`
- `GET /api/v1/cajas/activa`
- `GET /api/v1/cajas/{cajaId}/resumen`
- `POST /api/v1/cajas/{cajaId}/cierres`
- `POST /api/v1/ventas`
- `GET /api/v1/ventas`
- `GET /api/v1/ventas/{ventaId}`
- `POST /api/v1/ventas/{ventaId}/anulacion`
- `POST /api/v1/compras`
- `GET /api/v1/compras`
- `GET /api/v1/compras/{compraId}`
- `POST /api/v1/compras/{compraId}/anulacion`
- `POST /api/v1/egresos`
- `GET /api/v1/egresos`
- `GET /api/v1/egresos/{egresoId}`
- `GET /api/v1/cajas`
- `GET /api/v1/auditoria/operaciones`
- `GET /api/v1/reportes/ventas`
- `GET /api/v1/reportes/caja`
- `GET /api/v1/reportes/compras`
- `GET /api/v1/reportes/egresos`
- `GET /api/v1/reportes/stock`
- `GET /api/v1/reportes/utilidad`
- `GET /api/v1/reportes/ventas/exportar?formato=xlsx`
- `GET /api/v1/reportes/caja/exportar?formato=pdf`
- `GET /api/v1/reportes/historial`

## Alcance ya revisable por Swagger o Postman

En este punto ya puedes revisar de forma funcional:

- Fase 1: base técnica, seguridad inicial y autenticación
- Fase 2: usuarios, roles, permisos y contexto operativo base
- Fase 3: catálogos base de productos y proveedores
- Fase 4 parcial: consulta de stock actual y movimientos base
- Fase 4 completa: stock base y flujo de caja con apertura, consulta y cierre
- Fase 5 base: ventas con detalle, pagos, comprobante interno y anulacion
- Fase 6 base: compras con impacto en stock y egresos administrativos o de caja
- Fase 7 base: auditoria operativa minima y consulta administrativa de cajas
- Fase 8 base: reportes operativos, exportaciones iniciales e historial de reportes

## Entorno local

- PostgreSQL: `localhost:5433`
- base de datos: `sistema_caja`
- usuario: `cajaapi`
- password: `cajaapi`

## Flujo comodo en IntelliJ

El proyecto ya incluye `spring-boot-devtools` como dependencia `runtime` opcional para facilitar reinicio automatico durante desarrollo local.

- si ejecutas desde IntelliJ, activa `Build project automatically`
- para reinicio mas fluido, usa el perfil `local` o `demo`
- los cambios en clases y recursos deberian reflejarse con reinicio rapido del backend

## Seed real disponible en entorno local

Con el perfil `local` el backend solo siembra seguridad base. No crea contextos, productos, proveedores, compras, ventas ni cajas.

Roles base garantizados:

- `ADMIN`
- `CAJERO`
- `SUPERVISOR`
- `COMPRAS`
- `REPORTES`

Usuarios base garantizados:

- `admin` / `Admin123*`
- `cajero` / `Cajero123*`
- `supervisor` / `Supervisor123*`
- `compras` / `Compras123*`
- `reportes` / `Reportes123*`

Permisos operativos por perfil:

- `ADMIN`: acceso total a usuarios, roles, contextos, catalogos, caja, ventas, compras, egresos, stock, auditoria y reportes.
- `CAJERO`: `venta.registrar`, `egreso.registrar`, `caja.abrir`, `caja.cerrar`.
- `SUPERVISOR`: caja, ventas, anulacion de ventas, egresos, stock y reportes operativos de ventas/caja/egresos.
- `COMPRAS`: compras, proveedores, stock y reportes de compras/stock.
- `REPORTES`: auditoria y reportes con exportacion.

Si necesitas un flujo operativo listo para QA sin preparar catalogos ni contexto manualmente, levanta el perfil `demo`.

## Perfil demo opcional

Si quieres levantar el backend con datos demostrativos para revisar Swagger, Postman o el cliente HTTP de IntelliJ, activa tambien el perfil `demo`.

- IntelliJ o variable de entorno: `SPRING_PROFILES_ACTIVE=demo`
- Maven: `mvn spring-boot:run -Dspring-boot.run.profiles=demo`

El perfil `demo` deja cargado automaticamente:

- `DEMO-NEG-001` como contexto operativo en curso
- productos demo `DEMO-PROD-CAFE` y `DEMO-PROD-SAND`
- proveedor demo `Distribuidora Demo SAC`
- una compra con ingreso de stock
- una caja demo abierta y cerrada sin diferencias
- una venta demo y un egreso demo
- historial inicial de reportes para consultas operativas

La carga demo es idempotente: si el contexto `DEMO-NEG-001` ya existe, no vuelve a sembrar los datos.

## Perfil QA opcional

Si quieres una base neutra y repetible para QA del MVP, activa el perfil `qa`.

- IntelliJ o variable de entorno: `SPRING_PROFILES_ACTIVE=qa`
- Maven: `mvn spring-boot:run -Dspring-boot.run.profiles=qa`

El perfil `qa` deja cargado automaticamente:

- contexto `QA-BASE-001` en estado `EN_CURSO`
- producto `PROD-QA-001`
- proveedor `Proveedor QA Base`
- compra semilla `QA-SEED-COMPRA-001` para poblar stock inicial

Por defecto no deja:

- caja abierta
- ventas
- egresos
- reportes generados

Si necesitas que la semilla deje una caja abierta desde el arranque, puedes sobrescribir:

- `APP_QA_SEED_OPEN_CASH=true`

La carga QA es idempotente por piezas: reusa contexto, producto, proveedor y compra semilla si ya existen.

## Reinicio limpio y resembrado para QA

Si quieres repetir pruebas sin destruir roles ni usuarios base:

1. detén la aplicación
2. ejecuta `database/scripts/reset-operational-data.sql`
3. vuelve a levantar el backend

Alternativa desde API para perfiles locales:

- `POST /api/v1/system/operational-data/reset`
- requiere autenticacion y permiso `usuario.gestionar`
- disponible para entornos `local`, `qa` y `demo`
- limpia datos operativos y reejecuta la semilla activa del perfil cuando corresponda

Resultado esperado:

- con perfil `local`: quedas con usuarios, roles y permisos seed, pero sin contexto ni datos operativos
- con perfil `qa`: quedas con base operativa minima neutra para QA, sin caja consumida ni transacciones de venta/egreso
- con perfil `demo`: al reiniciar se vuelve a sembrar `DEMO-NEG-001`, catalogos demo, compra, venta, egreso, caja cerrada e historial de reportes

Notas operativas:

- el reset borra solo datos operativos y administrativos generados durante QA; no elimina roles, permisos ni usuarios seed
- si QA necesita una caja abierta de verdad, el perfil `demo` no la deja abierta: crea una nueva apertura manual sobre `DEMO-NEG-001`
- en perfil `local`, la preparacion minima real es: crear contexto `EN_CURSO`, crear producto, crear proveedor, registrar compra para poblar stock y luego abrir caja
- el reporte `stock` expone `stockScope=GLOBAL_MVP` y marca si el `operationalContextId` solicitado fue aplicado; en esta etapa el filtro por contexto aun no aplica al stock

## Estructura

- `src/main/java`: código fuente principal
- `src/main/resources`: configuraciones por perfil
- `src/test/java`: pruebas base del proyecto
- `database`: migraciones, seeds y referencias SQL
- `tests`: pruebas por nivel
- `docs-tecnicos`: notas técnicas específicas del backend
- `requests/sistema-caja-smoke.http`: pruebas rápidas para IntelliJ HTTP Client
- `requests/sistema-caja-demo.http`: recorrido base de datos demo para IntelliJ HTTP Client
- `postman/Sistema-Caja-MVP.postman_collection.json`: colección base para Postman
