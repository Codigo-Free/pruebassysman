package com.sysman.ordenes.infraestructura.entrada.rest.documentacion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ordenesOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API de Gestión de Órdenes Operativas")
                .version("v1")
                .description("Sistema de gestión de órdenes operativas para una empresa de servicios públicos: "
                        + "creación, consulta, actualización de estado con control de concurrencia y listado "
                        + "con filtros."));
    }
}
