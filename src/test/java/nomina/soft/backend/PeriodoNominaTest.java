package nomina.soft.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.Entidades.PeriodoNomina;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotValidException;


@SpringBootTest
public class PeriodoNominaTest {

    @Test
    void ValidarFechas1() throws ParseException, PeriodoNominaNotValidException{
        PeriodoNomina periodoNominaModel = new PeriodoNomina();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
        Date fechaFin = formatter.parse("24/10/2021");

        boolean resultado = periodoNominaModel.fechasValidas(fechaInicio, fechaFin);
        boolean exceptativa = true;

        assertEquals(exceptativa ,resultado); 
    }

    @Test
    void ValidarFechas2() throws ParseException, PeriodoNominaNotValidException{
        PeriodoNomina periodoNominaModel = new PeriodoNomina();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
        Date fechaFin = formatter.parse("10/10/2021");

        try {
            assertEquals(false, periodoNominaModel.fechasValidas(fechaInicio, fechaFin));
        } 
        catch (Exception e) {
            String expectedMessage = "La fecha fin del periodo de nómina debe ser mayor en 15 días "+
            "y no mayor a 30 días a su fecha de inicio.";
            assertEquals( expectedMessage, e.getMessage() );
        }
    }
    
}
