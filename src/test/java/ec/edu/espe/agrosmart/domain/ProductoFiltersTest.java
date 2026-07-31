package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoFiltersTest {

    @Test
    void isValid_conPrecioPositivoYCorreos_debeSerTrue() {
        Producto producto = new Producto(1L, "Cafe arabigo", "Cafe",
                new BigDecimal("18.50"), List.of("ventas@agrosmart.ec"));

        assertTrue(ProductoFilters.IS_VALID.test(producto));
    }

    @Test
    void isValid_conPrecioCero_debeSerFalse() {
        Producto producto = new Producto(1L, "Cafe borra", "Cafe",
                BigDecimal.ZERO, List.of("ventas@agrosmart.ec"));

        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }

    @Test
    void isValid_conCorreosVacios_debeSerFalse() {
        Producto producto = new Producto(1L, "Cafe verde", "Cafe",
                new BigDecimal("9.99"), Collections.emptyList());

        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }
}