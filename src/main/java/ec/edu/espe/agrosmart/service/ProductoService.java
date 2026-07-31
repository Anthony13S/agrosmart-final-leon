package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    // Producto que se emite si, tras filtrar, no queda ningun comercializable
    private static final Producto PRODUCTO_GENERICO = new Producto(
            0L, "Sin productos comercializables", "N/A", BigDecimal.ZERO, Collections.emptyList());

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Flux<Producto> obtenerProductosComercializables() {
        // fromCallable difiere la consulta bloqueante: no se ejecuta hasta que
        // alguien se suscriba al flujo
        return Mono.fromCallable(repository::findAll)
                // JPA/Hibernate bloquea el hilo. Si esto corriera en el event loop
                // de Netty, un solo hilo bloqueado degradaria TODAS las peticiones
                .subscribeOn(Schedulers.boundedElastic())
                // convierto el Mono<List<ProductoEntity>> materializado en un Flux
                .flatMapMany(Flux::fromIterable)
                .map(ProductoMapper::toDominio)
                .map(ProductoFilters.A_MAYUSCULAS)
                // descarto lo no comercializable segun la regla de negocio
                .filter(ProductoFilters.IS_VALID)
                // efecto de trazabilidad, no transforma el elemento
                .doOnNext(ProductoFilters.LOG_PRODUCTO)
                // si el filtro dejo el flujo vacio, emito un valor por defecto
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }

    public Mono<Producto> buscarPorId(Long id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                // Optional vacio -> Mono vacio
                .flatMap(Mono::justOrEmpty)
                .map(ProductoMapper::toDominio)
                // switchIfEmpty resuelve el "no encontrado" DENTRO del flujo
                // reactivo, sin sacar el valor con block() para revisarlo con un if
                .switchIfEmpty(Mono.error(new ProductoNoEncontradoException(id)));
    }
}