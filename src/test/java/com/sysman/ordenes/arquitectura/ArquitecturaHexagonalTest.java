package com.sysman.ordenes.arquitectura;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Verifica automáticamente los límites de la arquitectura hexagonal:
 * el dominio no puede depender de frameworks ni de la infraestructura,
 * y la aplicación no puede depender de detalles de infraestructura.
 */
class ArquitecturaHexagonalTest {

    private static final String BASE = "com.sysman.ordenes";

    private static JavaClasses clases;

    @BeforeAll
    static void importarClases() {
        clases = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE);
    }

    @Test
    void elDominioNoDependeDeSpring() {
        ArchRule regla = noClasses().that().resideInAPackage(BASE + ".dominio..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..");

        regla.check(clases);
    }

    @Test
    void elDominioNoDependeDeJpaNiJdbc() {
        ArchRule regla = noClasses().that().resideInAPackage(BASE + ".dominio..")
                .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "java.sql..");

        regla.check(clases);
    }

    @Test
    void elDominioNoDependeDeInfraestructura() {
        ArchRule regla = noClasses().that().resideInAPackage(BASE + ".dominio..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".infraestructura..");

        regla.check(clases);
    }

    @Test
    void laAplicacionNoDependeDeInfraestructura() {
        // allowEmptyShould: en fases tempranas (solo dominio) el paquete "aplicacion" aún no
        // tiene clases; la regla se activa igualmente en cuanto existan (fase 4 en adelante).
        ArchRule regla = noClasses().that().resideInAPackage(BASE + ".aplicacion..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".infraestructura..")
                .allowEmptyShould(true);

        regla.check(clases);
    }

    @Test
    void lasCapasRespetanElFlujoHexagonal() {
        ArchRule regla = classes().that().resideInAPackage(BASE + ".infraestructura..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        BASE + ".infraestructura..",
                        BASE + ".aplicacion..",
                        BASE + ".dominio..",
                        "java..", "javax..", "jakarta..", "org.springframework..", "org.slf4j..",
                        "com.fasterxml..", "io.swagger..", "org.mapstruct..", "lombok..",
                        "oracle.jdbc..", "io.micrometer.."
                )
                .allowEmptyShould(true);

        regla.check(clases);
    }
}
