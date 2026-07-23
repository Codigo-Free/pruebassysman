package com.sysman.ordenes.infraestructura.salida.persistencia;

import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.Orden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.adaptador.OrdenRepositorioAdaptador;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.OrdenJpaEntity;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.mapper.OrdenPersistenciaMapper;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.repositorio.OrdenJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({OrdenPersistenciaMapper.class, OrdenRepositorioAdaptador.class})
class OrdenRepositorioAdaptadorTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrdenJpaRepository repositorio;

    @Autowired
    private OrdenRepositorioAdaptador adaptador;

    @Test
    void guardaYReconstruyeUnaOrdenDeDominio() {
        Orden nueva = Orden.crear(1L, TipoOrden.INSTALACION, "Instalar medidor", "Calle 1 # 2-3", "test");

        Orden guardada = adaptador.guardar(nueva);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getEstado()).isEqualTo(EstadoOrden.CREADA);
        assertThat(guardada.getVersion()).isZero();
    }

    @Test
    void buscaPorIdDevuelveVacioSiNoExiste() {
        assertThat(adaptador.buscarPorId(999L)).isEmpty();
    }

    @Test
    void filtraPorEstadoYRangoDeFechas() {
        Instant ahora = Instant.now();
        persistir(1L, TipoOrden.INSTALACION, EstadoOrden.CREADA, ahora.minus(10, ChronoUnit.DAYS));
        persistir(1L, TipoOrden.MANTENIMIENTO, EstadoOrden.ASIGNADA, ahora.minus(5, ChronoUnit.DAYS));
        persistir(1L, TipoOrden.MANTENIMIENTO, EstadoOrden.ASIGNADA, ahora.minus(1, ChronoUnit.DAYS));
        entityManager.flush();
        // Limpia el contexto de persistencia: la fecha real se fijó con una actualización JPQL
        // en bloque, y el mapa de identidad de Hibernate aún conserva el valor previo en memoria.
        entityManager.getEntityManager().clear();

        PaginaResultado<Orden> resultado = adaptador.buscar(EstadoOrden.ASIGNADA,
                ahora.minus(7, ChronoUnit.DAYS), ahora, 0, 10);

        // Las dos órdenes ASIGNADA (-5d y -1d) caen dentro del rango; la CREADA (-10d) queda excluida
        // tanto por estado como por fecha.
        assertThat(resultado.contenido()).hasSize(2);
        assertThat(resultado.totalElementos()).isEqualTo(2);
        assertThat(resultado.contenido()).allMatch(orden -> orden.getEstado() == EstadoOrden.ASIGNADA);
    }

    private void persistir(Long idCliente, TipoOrden tipo, EstadoOrden estado, Instant fechaCreacion) {
        OrdenJpaEntity entidad = new OrdenJpaEntity(idCliente, tipo, estado, "desc", "dir", "test", "test");
        entityManager.persist(entidad);
        entityManager.getEntityManager()
                .createQuery("UPDATE OrdenJpaEntity o SET o.fechaCreacion = :fecha WHERE o.id = :id")
                .setParameter("fecha", fechaCreacion)
                .setParameter("id", entidad.getId())
                .executeUpdate();
    }
}
