package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void getters_devuelvenLoQueRecibioElConstructor() {
        // Arrange
        List<String> correos = List.of("ventas@agrosmart.ec");

        // Act
        Producto producto = new Producto(1L, "Cafe arabigo", "Cafe",
                new BigDecimal("18.50"), correos);

        // Assert
        assertEquals(1L, producto.getId());
        assertEquals("Cafe arabigo", producto.getNombre());
        assertEquals(new BigDecimal("18.50"), producto.getPrecioUsd());
    }

    @Test
    void constructor_alMutarLaListaOriginalDespues_noDebeAfectarAlProducto() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");
        Producto producto = new Producto(1L, "Cafe arabigo", "Cafe",
                new BigDecimal("18.50"), correos);

        // Act
        correos.add("intruso@mail.com");

        // Assert
        assertEquals(1, producto.getCorreosNotificacion().size());
    }

    @Test
    void getCorreosNotificacion_devuelveListaDeSoloLecturaYDistintaDeLaOriginal() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");
        Producto producto = new Producto(1L, "Cafe arabigo", "Cafe",
                new BigDecimal("18.50"), correos);

        // Act
        List<String> obtenidos = producto.getCorreosNotificacion();

        // Assert
        assertNotSame(correos, obtenidos);
        assertThrows(UnsupportedOperationException.class, () -> obtenidos.add("otro@mail.com"));
    }
}