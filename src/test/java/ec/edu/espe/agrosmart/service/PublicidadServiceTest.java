package ec.edu.espe.agrosmart.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

class PublicidadServiceTest {

    @Test
    void generarPublicidad_caminoFeliz_emiteElTextoDelModelo() {
        // Arrange
        AgroSmartAIService ia = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(ia.generarPublicidad(any(), any())).thenReturn("Cafe fresco de altura");
        PublicidadService service = new PublicidadService(ia);

        // Act & Assert
        StepVerifier.create(service.generarPublicidad("Cafe", "cafeterias"))
                .expectNext("Cafe fresco de altura")
                .verifyComplete();
    }

    @Test
    void generarPublicidad_cuandoElProveedorFalla_debeEmitirMensajeDeRespaldo() {
        // Arrange
        AgroSmartAIService ia = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(ia.generarPublicidad(any(), any()))
                .thenThrow(new RuntimeException("429 Too Many Requests"));
        PublicidadService service = new PublicidadService(ia);

        // Act & Assert
        StepVerifier.create(service.generarPublicidad("Cafe", "cafeterias"))
                .expectNextMatches(texto -> texto.contains("no disponible"))
                .verifyComplete();
    }
}