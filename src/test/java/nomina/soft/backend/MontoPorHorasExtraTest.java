package nomina.soft.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.models.BoletaDePagoModel;

@SpringBootTest
public class MontoPorHorasExtraTest {
    
    @Test
    void CalcularMontoHorasExtraTest1 () {

        BoletaDePagoModel boletaDePagoModel = new BoletaDePagoModel();
        int totalHorasExtrasIncidenciaLaboral = 3;
        int pagoPorHoraContrato = 40;

        int resultado =boletaDePagoModel.calcularMontoPorHorasExtras(totalHorasExtrasIncidenciaLaboral, pagoPorHoraContrato);
        int exceptativa = 120;

        assertEquals(exceptativa ,resultado);


    }
}
