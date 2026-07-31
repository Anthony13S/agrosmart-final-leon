package ec.edu.espe.agrosmart.mapper;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProductoMapper {

    public static Producto toDominio(ProductoEntity entity) {
        String correosCrudo = entity.getCorreosNotificacion();
        List<String> correos = (correosCrudo == null || correosCrudo.isBlank())
                ? Collections.emptyList()
                : Arrays.asList(correosCrudo.split(","));

        return new Producto(
                entity.getIdProducto(),
                entity.getNombreProducto(),
                entity.getCategoria(),
                entity.getPrecioUsd(),
                correos
        );
    }

    private ProductoMapper() {
    }
}