package com.sysman.ordenes.infraestructura.transversal;

import com.sysman.ordenes.dominio.excepcion.ClienteNoEncontradoException;
import com.sysman.ordenes.dominio.excepcion.ConflictoVersionOrdenException;
import com.sysman.ordenes.dominio.excepcion.OrdenNoEncontradaException;
import com.sysman.ordenes.dominio.excepcion.TransicionEstadoInvalidaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones: traduce las excepciones de dominio y de validación
 * a respuestas {@link ProblemDetail} (RFC 7807), incluyendo el Correlation ID activo
 * para facilitar la trazabilidad (ver {@link CorrelationIdFilter}).
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrdenNoEncontradaException.class)
    public ProblemDetail manejarOrdenNoEncontrada(OrdenNoEncontradaException ex) {
        return problema(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ProblemDetail manejarClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ProblemDetail manejarTransicionInvalida(TransicionEstadoInvalidaException ex) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler({ConflictoVersionOrdenException.class, OptimisticLockingFailureException.class})
    public ProblemDetail manejarConflictoVersion(Exception ex) {
        return problema(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail manejarErrorNoEsperado(Exception ex) {
        log.error("Error no esperado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado, intente nuevamente");
    }

    @Override
    protected org.springframework.http.ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos");
        problema.setProperty("errores", errores);

        return org.springframework.http.ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
    }

    private ProblemDetail problema(HttpStatus status, String mensaje) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, mensaje);
        String correlationId = org.slf4j.MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY);
        if (correlationId != null) {
            problema.setProperty("correlationId", correlationId);
        }
        return problema;
    }
}
