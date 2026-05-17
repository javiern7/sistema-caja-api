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

Todavía no está lista para prueba funcional completa la parte de reportes.

## Entorno local

- PostgreSQL: `localhost:5433`
- base de datos: `sistema_caja`
- usuario: `cajaapi`
- password: `cajaapi`

## Usuarios seed para entorno local

- `admin` / `Admin123*`
- `cajero` / `Cajero123*`

Estos usuarios se crean automáticamente en base de datos si el esquema está vacío.

## Estructura

- `src/main/java`: código fuente principal
- `src/main/resources`: configuraciones por perfil
- `src/test/java`: pruebas base del proyecto
- `database`: migraciones, seeds y referencias SQL
- `tests`: pruebas por nivel
- `docs-tecnicos`: notas técnicas específicas del backend
