# Preguntas del cargo (Líder Técnico)

> **Nota:** estas son respuestas de referencia sobre buenas prácticas generales de liderazgo técnico, redactadas como punto de partida. Al ser preguntas sobre experiencia y estilo personal de gestión, se recomienda **ajustarlas con ejemplos reales propios** antes de usarlas en la entrevista — no deben tomarse como una descripción de la experiencia real del candidato.

## ¿Cómo manejas deuda técnica?

Haciéndola visible y priorizable como cualquier otro ítem de trabajo, no como algo que se resuelve "cuando haya tiempo" (nunca lo hay). En la práctica: registrar la deuda con el mismo contexto que un bug (qué es, por qué se aceptó en su momento, qué riesgo concreto genera si no se atiende), y negociar explícitamente con el negocio un porcentaje fijo de cada ciclo dedicado a pagarla, en vez de pedir permiso cada vez. La deuda que bloquea entregas futuras o multiplica el costo de cambio se prioriza por encima de deuda cosmética.

## ¿Cómo tomas decisiones cuando el equipo no está de acuerdo?

Primero asegurarse de que el desacuerdo es sobre la decisión y no sobre información distinta que cada quien tiene — muchas veces alinear los hechos disuelve el desacuerdo. Si el desacuerdo persiste con la misma información sobre la mesa, priorizar criterios objetivos ya acordados de antemano (impacto en el usuario, costo de reversar la decisión, tiempo disponible) por encima de preferencia personal. Como líder técnico, la decisión final es mía cuando el equipo no converge, pero se documenta el porqué y se deja explícito qué evidencia haría reconsiderarla — evita que la misma discusión se repita sin nueva información.

## ¿Cómo manejas un desarrollador de bajo desempeño?

Conversación directa y temprana, en privado, con ejemplos concretos y específicos (no "tu código tiene muchos bugs" sino instancias reales) — la sorpresa tardía es peor que el problema mismo. Diagnosticar la causa antes de actuar: puede ser falta de contexto/mentoring, un problema de alcance mal definido, o algo personal ajeno al trabajo, y cada una requiere una respuesta distinta. Definir expectativas claras y medibles con un plazo concreto de seguimiento, y ser honesto si después de ese acompañamiento no hay mejora — no es justo para el resto del equipo prolongar indefinidamente una situación que todos ya notaron.

## ¿Cómo defines estándares de desarrollo?

Partiendo de problemas reales ya vividos por el equipo, no de una lista genérica copiada de otro lado — un estándar que nadie entiende por qué existe se ignora. Documentarlos donde el equipo ya trabaja (README, plantillas de PR, linters/CI) en vez de en un documento que nadie vuelve a abrir, y automatizar todo lo que se pueda automatizar (formato, análisis estático, arquitectura vía ArchUnit) para que la conversación humana se enfoque en lo que sí requiere criterio. Revisarlos periódicamente: un estándar que ya no aplica y sigue "vigente" solo en el papel erosiona la credibilidad de todos los demás.

## ¿Cómo asegurarías calidad en entregas críticas?

Reduciendo el tamaño del cambio: una entrega crítica grande es más riesgosa que varias pequeñas verificables una por una. Pruebas automatizadas en las rutas críticas como requisito no negociable antes de mergear (no "lo probamos manualmente después"), plan de rollback explícito y probado antes de desplegar (no improvisado en el momento de un incidente), y una ventana de despliegue con monitoreo activo, no un "desplegar y desaparecer". Para el código en sí, aplico lo mismo que en cualquier entrega: revisión enfocada en los casos límite y de error, no solo el camino feliz.
