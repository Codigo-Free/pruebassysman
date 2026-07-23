package com.sysman.ordenes.infraestructura.integracion;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.ActualizarEstadoRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.CrearOrdenRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.response.OrdenResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración end-to-end contra un Oracle real (Testcontainers), incluyendo
 * las migraciones Flyway y el procedimiento PL/SQL. Se etiqueta "integration" y corre
 * aparte con {@code mvn verify -Pintegration} (ver pom.xml) dado el costo de arrancar
 * Oracle en cada ejecución.
 */
@Tag("integration")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class OrdenApiIntegrationTest {

    @Container
    @ServiceConnection
    static final OracleContainer ORACLE = new OracleContainer("gvenzl/oracle-free:23-slim");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void flujoCompletoDeUnaOrdenContraOracleReal() {
        CrearOrdenRequest crear = new CrearOrdenRequest(1L, TipoOrden.INSTALACION, "Instalar medidor",
                "Calle 1 # 2-3", "test");
        ResponseEntity<OrdenResponse> creado = restTemplate.postForEntity("/api/v1/orden", crear, OrdenResponse.class);
        assertThat(creado.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(creado.getBody()).isNotNull();
        assertThat(creado.getBody().estado()).isEqualTo(EstadoOrden.CREADA);
        Long id = creado.getBody().id();

        ActualizarEstadoRequest asignar = new ActualizarEstadoRequest(EstadoOrden.ASIGNADA, 0L, "test", "asignada a técnico");
        ResponseEntity<OrdenResponse> actualizado = restTemplate.exchange("/api/v1/orden/" + id + "/estado",
                HttpMethod.PUT, new HttpEntity<>(asignar), OrdenResponse.class);
        assertThat(actualizado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualizado.getBody()).isNotNull();
        assertThat(actualizado.getBody().estado()).isEqualTo(EstadoOrden.ASIGNADA);
        assertThat(actualizado.getBody().version()).isEqualTo(1L);

        ResponseEntity<OrdenResponse> consultado = restTemplate.getForEntity("/api/v1/orden/" + id, OrdenResponse.class);
        assertThat(consultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(consultado.getBody().estado()).isEqualTo(EstadoOrden.ASIGNADA);

        ActualizarEstadoRequest transicionInvalida = new ActualizarEstadoRequest(EstadoOrden.COMPLETADA, 1L, "test", null);
        ResponseEntity<String> rechazado = restTemplate.exchange("/api/v1/orden/" + id + "/estado",
                HttpMethod.PUT, new HttpEntity<>(transicionInvalida), String.class);
        assertThat(rechazado.getStatusCode().value()).isEqualTo(422);

        ActualizarEstadoRequest versionDesactualizada = new ActualizarEstadoRequest(EstadoOrden.EN_PROCESO, 0L, "test", null);
        ResponseEntity<String> conflicto = restTemplate.exchange("/api/v1/orden/" + id + "/estado",
                HttpMethod.PUT, new HttpEntity<>(versionDesactualizada), String.class);
        assertThat(conflicto.getStatusCode().value()).isEqualTo(409);

        ResponseEntity<String> noEncontrada = restTemplate.getForEntity("/api/v1/orden/999999", String.class);
        assertThat(noEncontrada.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listaOrdenesConFiltroDeEstado() {
        CrearOrdenRequest crear = new CrearOrdenRequest(2L, TipoOrden.MANTENIMIENTO, "Mantenimiento preventivo",
                "Carrera 5 # 10-20", "test");
        restTemplate.postForEntity("/api/v1/orden", crear, OrdenResponse.class);

        ResponseEntity<String> listado = restTemplate.getForEntity("/api/v1/orden?estado=CREADA&pagina=0&tamanoPagina=50",
                String.class);

        assertThat(listado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listado.getBody()).contains("\"estado\":\"CREADA\"");
    }
}
