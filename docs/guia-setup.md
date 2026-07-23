# Guía de instalación y ejecución local

## Requisitos

- JDK 21+ (probado con JDK 26; el proyecto compila con `--release 21`)
- Maven 3.9+
- Docker (para Oracle local vía `docker compose`, y para Testcontainers en los tests de integración)

## 1. Levantar Oracle

```bash
docker compose up -d
```

Espera a que el contenedor esté `healthy` (la primera vez tarda ~1 minuto en inicializar):

```bash
docker inspect -f '{{.State.Health.Status}}' sysman-oracle
```

## 2. Compilar y correr las pruebas unitarias/slice

```bash
mvn verify
```

No requiere Oracle levantado — usa H2 en modo Oracle para las pruebas `@DataJpaTest`.

## 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

Flyway aplica las 5 migraciones automáticamente contra el Oracle de `docker compose` (`jdbc:oracle:thin:@//localhost:1521/FREEPDB1`, usuario `ordenes_app`). La API queda disponible en `http://localhost:8080`, y la documentación interactiva en `http://localhost:8080/swagger-ui.html`.

Variables de entorno soportadas (ver `application.yml`): `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_POOL_SIZE`, `SERVER_PORT`, `LOG_LEVEL`.

## 4. Probar los endpoints

```bash
curl -X POST http://localhost:8080/api/v1/orden \
  -H "Content-Type: application/json" \
  -d '{"idCliente":1,"tipo":"INSTALACION","descripcion":"Instalar medidor","direccionServicio":"Calle 1 # 2-3","usuarioCrea":"sysman"}'
```

Ver el contrato completo en [`api.md`](api.md). `V5__datos_semilla_clientes.sql` deja 3 clientes de ejemplo (`idCliente` 1-3) para probar sin necesidad de crearlos manualmente.

## 5. Pruebas de integración (Testcontainers + Oracle real)

```bash
mvn verify -Pintegration
```

No requiere el `docker compose` del paso 1 — Testcontainers arranca su propio contenedor Oracle efímero. Tarda 1-2 minutos (descarga/arranque de Oracle).

## 6. Apagar el entorno

```bash
docker compose down       # conserva el volumen de datos
docker compose down -v    # reinicia también el volumen (próximo arranque re-ejecuta Flyway desde cero)
```
