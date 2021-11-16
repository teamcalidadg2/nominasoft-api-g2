package nomina.soft.backend;

import static nomina.soft.backend.statics.PeriodoNominaImplConstant.FECHAS_NO_VALIDAS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.entidades.PeriodoNomina;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotValidException;

@SpringBootTest
class FechaNominaTest {

    @Test
    void ValidarFechas1() throws ParseException, PeriodoNominaNotValidException {
        PeriodoNomina periodoNominaModel = new PeriodoNomina();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaInicio = formatter.parse("3/10/2021");
        Date fechaFin = formatter.parse("24/10/2021");

        boolean resultado = periodoNominaModel.fechasValidas(fechaInicio, fechaFin);
        boolean exceptativa = true;

        assertEquals(exceptativa, resultado);
    }

    @Test
    void ValidarFechas2() throws ParseException, PeriodoNominaNotValidException {
        PeriodoNomina periodoNominaModel = new PeriodoNomina();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaInicio = formatter.parse("3/10/2021");
        Date fechaFin = formatter.parse("10/10/2021");

        try {
            assertEquals(false, periodoNominaModel.fechasValidas(fechaInicio, fechaFin));
        } catch (Exception e) {
            String expectedMessage = FECHAS_NO_VALIDAS;
            assertEquals(expectedMessage, e.getMessage());
        }
    }

}
