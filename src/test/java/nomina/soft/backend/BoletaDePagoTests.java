package nomina.soft.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.models.BoletaDePagoModel;

@SpringBootTest
public class BoletaDePagoTests {
    @Test
    void CalcularHorasSemanaTest1() throws ParseException {
        BoletaDePagoModel boletaDePagoModel = new BoletaDePagoModel();

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
        Date fechaFin = formatter.parse("24/10/2021");
        int horasSemana = 32;

        int resultado = boletaDePagoModel.calcularTotalDeHoras(fechaInicio, fechaFin, horasSemana);
        int exceptativa=96;
        assertEquals(exceptativa ,resultado);
    }    

    
}
