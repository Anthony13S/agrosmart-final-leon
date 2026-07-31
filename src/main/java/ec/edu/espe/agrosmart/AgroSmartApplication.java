package ec.edu.espe.agrosmart;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class AgroSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroSmartApplication.class, args);
    }

    @Bean
    CommandLineRunner sembrarProductos(ProductoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new ProductoEntity(
                        "Cafe arabigo de altura", new BigDecimal("18.50"), 200,
                        "Cafe", "ventas@agrosmart.ec"));
                repository.save(new ProductoEntity(
                        "Cafe organico Sierra Norte", new BigDecimal("21.90"), 150,
                        "Cafe", "ventas@agrosmart.ec,exportaciones@agrosmart.ec"));
                repository.save(new ProductoEntity(
                        "Cafe tostado premium", new BigDecimal("15.75"), 300,
                        "Cafe", "contacto@agrosmart.ec"));
                repository.save(new ProductoEntity(
                        "Cafe borra descartado", BigDecimal.ZERO, 0,
                        "Cafe", "ventas@agrosmart.ec"));
                repository.save(new ProductoEntity(
                        "Cafe verde sin procesar", new BigDecimal("9.99"), 500,
                        "Cafe", ""));
            }
        };
    }
}