package de.idigath.project;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

/**
 * Architecture rules for the layered structure of this service. Plain JUnit 5 without a Quarkus
 * context: the rules analyze the bytecode of the main classes only, so they run as fast unit tests
 * in surefire during {@code mvn test} and fail the build on violation.
 *
 * <p>Layers (top-down): {@code web} (REST endpoints) may use {@code service} and {@code model}
 * (DTOs only, never entities); {@code service} may use {@code mapper}, {@code entity} and {@code
 * model}; {@code mapper} may use {@code entity} and {@code model}; {@code entity} and {@code model}
 * are the innermost layers and must not access any other application layer.
 */
public class ArchitectureTest {

    private static final JavaClasses MAIN_CLASSES =
            new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages("de.idigath");

    @Test
    void packageSlices_shouldBeFreeOfCycles() {
        slices().matching("de.idigath.(*)..").should().beFreeOfCycles().check(MAIN_CLASSES);
    }

    @Test
    void layeredArchitecture_shouldBeRespected() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Web")
                .definedBy("de.idigath.web")
                .layer("Service")
                .definedBy("de.idigath.service")
                .layer("Mapper")
                .definedBy("de.idigath.mapper")
                .layer("Entity")
                .definedBy("de.idigath.entity")
                .layer("Model")
                .definedBy("de.idigath.model")
                .whereLayer("Web")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("Web")
                .mayOnlyAccessLayers("Service", "Model")
                .whereLayer("Service")
                .mayOnlyBeAccessedByLayers("Web")
                .whereLayer("Service")
                .mayOnlyAccessLayers("Mapper", "Entity", "Model")
                .whereLayer("Mapper")
                .mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Mapper")
                .mayOnlyAccessLayers("Entity", "Model")
                .whereLayer("Entity")
                .mayOnlyBeAccessedByLayers("Service", "Mapper")
                .whereLayer("Entity")
                .mayNotAccessAnyLayer()
                .whereLayer("Model")
                .mayOnlyBeAccessedByLayers("Web", "Service", "Mapper")
                .whereLayer("Model")
                .mayNotAccessAnyLayer()
                .check(MAIN_CLASSES);
    }

    @Test
    void webClasses_shouldBeSuffixedWithEndpoint() {
        classes()
                .that()
                .resideInAPackage("de.idigath.web")
                .should()
                .haveSimpleNameEndingWith("Endpoint")
                .check(MAIN_CLASSES);
    }

    @Test
    void mapperClasses_shouldBeSuffixedWithMapper() {
        // MapStruct generates *Impl classes into the same package; the convention applies to
        // the hand-written mapper interfaces only.
        classes()
                .that()
                .resideInAPackage("de.idigath.mapper")
                .and()
                .haveSimpleNameNotEndingWith("Impl")
                .should()
                .haveSimpleNameEndingWith("Mapper")
                .check(MAIN_CLASSES);
    }
}
