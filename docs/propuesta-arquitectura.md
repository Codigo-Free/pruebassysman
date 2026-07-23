# Propuesta de Arquitectura — Sistema de Gestión de Órdenes Operativas

1. Arquitectura propuesta

Propuse una Arquitectura Hexagonal con Spring Boot, separando la aplicación en tres capas:

Dominio: contiene las reglas de negocio y no depende de ninguna tecnología.
Aplicación: implementa los casos de uso y se comunica mediante puertos e interfaces.
Infraestructura: aquí están los controladores REST, la persistencia con JPA y la comunicación con la base de datos.

Esta separación permite cambiar tecnologías o realizar pruebas sin afectar la lógica del negocio.

2. ¿Por qué elegí esta arquitectura?

Porque facilita el mantenimiento y el crecimiento de la aplicación.

También permite:

Hacer pruebas unitarias de forma sencilla.
Mantener desacoplada la lógica del negocio.
Cambiar la base de datos o la API sin modificar el dominio.
Mantener un código más limpio y organizado.

Además, aplicaría buenas prácticas como revisión de código, pruebas automatizadas y un pipeline de integración continua para asegurar la calidad antes de cada despliegue.

3. Manejo de transacciones

Utilizaría transacciones de Spring para garantizar que una operación se complete completamente o no se ejecute.

Cuando la lógica crítica está en un procedimiento almacenado de Oracle, dejaría que sea el mismo procedimiento quien controle la transacción para mantener la consistencia de los datos.

4. Manejo de concurrencia

Usaría bloqueo optimista mediante una columna de versión.

Así, si dos usuarios intentan modificar la misma orden al mismo tiempo, el sistema detecta el conflicto y evita sobrescribir información.

5. Estrategia de indexación

Crearía índices en:

Las llaves primarias.
Las llaves foráneas.
Las columnas más utilizadas en búsquedas y filtros.

Si el volumen de información crece mucho, particionaría las tablas por fecha para mejorar el rendimiento de las consultas históricas.

6. Versionamiento de la API

Versionaría la API por la URL, por ejemplo:

/api/v1/orden

Esto permite evolucionar el servicio sin afectar a los consumidores existentes.

7. Logging y auditoría

Implementaría un logging centralizado para facilitar el seguimiento de errores y las solicitudes.

Para la auditoría, registraría cada cambio importante de una orden, indicando quién realizó el cambio, cuándo se hizo y cuál fue el estado anterior y el nuevo. Esto facilita el soporte y la trazabilidad.
