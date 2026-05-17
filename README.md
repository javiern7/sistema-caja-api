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

## Alcance ya revisable por Swagger o Postman

En este punto ya puedes revisar de forma funcional:

- Fase 1: base técnica, seguridad inicial y autenticación
- Fase 2: usuarios, roles, permisos y contexto operativo base
- Fase 3: catálogos base de productos y proveedores
- Fase 4 parcial: consulta de stock actual y movimientos base

Todavía no está lista para prueba funcional completa la parte de caja, ventas, compras, egresos, anulaciones, cierre ni reportes.

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
