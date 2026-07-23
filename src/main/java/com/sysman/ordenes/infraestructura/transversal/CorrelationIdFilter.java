package com.sysman.ordenes.infraestructura.transversal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Genera (o propaga) un Correlation ID por cada petición HTTP y lo publica en el MDC
 * para que aparezca en toda la traza de logs estructurados (ver logback-spring.xml).
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            log.info("{} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            log.info("{} {} -> {}", request.getMethod(), request.getRequestURI(), response.getStatus());
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
