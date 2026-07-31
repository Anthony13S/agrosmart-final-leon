package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

class ProductoServiceTest {

    @Test
    void obtenerProductosComercializables_conTresValidosYDosInvalidos_debeEmitirSoloLosValidos() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findAll()).thenReturn(List.of(
                new ProductoEntity("Cafe A", new BigDecimal("18.50"), 200, "Cafe", "a@x.ec"),
                new ProductoEntity("Cafe B", new BigDecimal("21.90"), 150, "Cafe", "b@x.ec"),
                new ProductoEntity("Cafe C", new BigDecimal("15.75"), 300, "Cafe", "c@x.ec"),
                new ProductoEntity("Cafe D", BigDecimal.ZERO, 0, "Cafe", "d@x.ec"),
                new ProductoEntity("Cafe E", new BigDecimal("9.99"), 500, "Cafe", "")
        ));
        ProductoService service = new ProductoService(repo);

        // Act
        Flux<ec.edu.espe.agrosmart.domain.Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void obtenerProductosComercializables_conTodosInvalidos_debeEmitirElGenerico() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findAll()).thenReturn(List.of(
                new ProductoEntity("Cafe D", BigDecimal.ZERO, 0, "Cafe", "d@x.ec")
        ));
        ProductoService service = new ProductoService(repo);

        // Act & Assert
        StepVerifier.create(service.obtenerProductosComercializables())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void buscarPorId_conIdInexistente_debeTerminarEnError() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findById(anyLong())).thenReturn(Optional.empty());
        ProductoService service = new ProductoService(repo);

        // Act & Assert
        StepVerifier.create(service.buscarPorId(999L))
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }
}