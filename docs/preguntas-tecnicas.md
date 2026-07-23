# Preguntas técnicas (prueba técnica)

## 1. ¿Cómo optimizarías una consulta que tarda 12 segundos con 10 millones de registros?


Primero revisaría el plan de ejecución para identificar el cuello de botella. Después validaría los índices, evitaría traer datos innecesarios y optimizaría la consulta para que solo lea la información que realmente necesita.

## 2. ¿Qué tipo de índices usarías?

Depende de la consulta. Generalmente usaría índices en las columnas que más se utilizan para búsquedas, filtros o JOIN. Si hay varias columnas relacionadas, evaluaría un índice compuesto.

## 3. ¿Particionarías la tabla? ¿Cómo?

Sí, si la tabla es muy grande y las consultas suelen hacerse por fechas o por algún criterio específico. La particionaría por ese campo para que la base de datos consulte solo la información necesaria.

## 4. ¿Cómo manejarías concurrencia y transacciones?

Usaría transacciones para garantizar la integridad de los datos y procuraría que fueran lo más cortas posible para evitar bloqueos. También manejaría correctamente los errores y los posibles conflictos entre usuarios.

## 5. ¿Cómo aseguras calidad técnica en el equipo?

Con revisiones de código, pruebas automatizadas, estándares de desarrollo y buena comunicación. La idea es detectar problemas antes de que lleguen a producción y mantener un código fácil de entender y mantener.