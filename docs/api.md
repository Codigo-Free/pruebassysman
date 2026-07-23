# Contrato de la API

Base URL: `/api/v1/orden`. Documentación interactiva en `/swagger-ui.html` (OpenAPI en `/api-docs`).

También hay una colección de Postman lista para usar en [`../postman/`](../postman/), con los flujos felices y los 4 códigos de error cubiertos por tests automáticos (ver el README para el comando de Newman).

## Crear orden

```
POST /api/v1/orden
Content-Type: application/json

{
  "idCliente": 1,
  "tipo": "INSTALACION",
  "descripcion": "Instalar medidor",
  "direccionServicio": "Calle 1 # 2-3",
  "usuarioCrea": "sysman"
}
```

`tipo` ∈ `INSTALACION | MANTENIMIENTO | REPARACION | CORTE | RECONEXION | INSPECCION`. Respuesta `201 Created`:

```json
{
  "id": 1,
  "idCliente": 1,
  "tipo": "INSTALACION",
  "estado": "CREADA",
  "descripcion": "Instalar medidor",
  "direccionServicio": "Calle 1 # 2-3",
  "version": 0,
  "fechaCreacion": "2026-07-23T02:36:07.292375Z",
  "fechaModificacion": null,
  "usuarioCrea": "sysman",
  "usuarioModifica": "sysman"
}
```

`404` si `idCliente` no existe.

```bash
curl -X POST http://localhost:8080/api/v1/orden \
  -H "Content-Type: application/json" \
  -d '{"idCliente":1,"tipo":"INSTALACION","descripcion":"Instalar medidor","direccionServicio":"Calle 1 # 2-3","usuarioCrea":"sysman"}'
```

## Consultar orden

```
GET /api/v1/orden/{id}
```

`200` con el mismo formato anterior; `404` si no existe.

```bash
curl http://localhost:8080/api/v1/orden/1
```

## Actualizar estado

```
PUT /api/v1/orden/{id}/estado
Content-Type: application/json

{
  "estadoNuevo": "ASIGNADA",
  "versionEsperada": 0,
  "usuarioModifica": "operador1",
  "observacion": "asignada a técnico"
}
```

`versionEsperada` debe coincidir con el `version` devuelto por la última lectura (optimistic locking). Respuestas:

| Código | Motivo |
|---|---|
| `200` | actualizado correctamente |
| `404` | la orden no existe |
| `422` | la transición de estado no es válida (ver matriz en [`modelo-datos.md`](modelo-datos.md)) |
| `409` | `versionEsperada` desactualizada — otro proceso modificó la orden primero |
| `400` | el cuerpo no pasa las validaciones (`estadoNuevo`/`versionEsperada`/`usuarioModifica` obligatorios) |

```bash
curl -X PUT http://localhost:8080/api/v1/orden/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estadoNuevo":"ASIGNADA","versionEsperada":0,"usuarioModifica":"operador1","observacion":"asignada a tecnico"}'
```

Ejemplo de respuesta `409`:

```json
{
  "detail": "La orden 1 fue modificada por otro proceso; consulte el estado actual e intente nuevamente",
  "instance": "/api/v1/orden/1/estado",
  "status": 409,
  "title": "Conflict",
  "correlationId": "34e1b60a-8afc-42b2-9f6e-e2a5f6196ff8"
}
```

## Listar con paginación y filtros

```
GET /api/v1/orden?estado=&fechaInicio=&fechaFin=&pagina=0&tamanoPagina=20
```

Todos los parámetros son opcionales excepto `pagina`/`tamanoPagina` (por defecto `0`/`20`). `fechaInicio`/`fechaFin` en formato ISO-8601 (`2026-07-01T00:00:00Z`).

```bash
curl "http://localhost:8080/api/v1/orden?estado=ASIGNADA&pagina=0&tamanoPagina=10"
```

Respuesta `200`:

```json
{
  "contenido": [ { "id": 1, "...": "..." } ],
  "pagina": 0,
  "tamanoPagina": 10,
  "totalElementos": 1,
  "totalPaginas": 1
}
```

## Correlation ID

Toda petición acepta (o genera si no viene) el header `X-Correlation-Id`, propagado en la respuesta y en cada línea de log JSON de esa petición.

```bash
curl -H "X-Correlation-Id: mi-id-de-seguimiento" http://localhost:8080/api/v1/orden/1
```
