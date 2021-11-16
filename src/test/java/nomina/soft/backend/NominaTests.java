package nomina.soft.backend;

import static nomina.soft.backend.statics.NominaImplConstant.PERIODO_FECHA_FIN_NO_VALIDO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.entidades.Nomina;
import nomina.soft.backend.entidades.PeriodoNomina;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.NominaNotValidException;

@SpringBootTest
class NominaTests {
    @Test
    void ValidarContratoConNominaTest3() throws ParseException {

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Nomina nominaModel = new Nomina();
        PeriodoNomina periodoNominaModel = new PeriodoNomina();

        Date fechaInicioNomina = formatter.parse("01/10/2021");
        Date fechaFinNomina = formatter.parse("09/10/2021");
        Date fechaNomina = formatter.parse("04/10/2021");

        periodoNominaModel.setFechaInicio(fechaInicioNomina);
        periodoNominaModel.setFechaFin(fechaFinNomina);
        nominaModel.setFecha(fechaNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        try {
            assertEquals(false, nominaModel.validarNomina(nominaModel));
        } catch (Exception e) {
            String expectedMessage = PERIODO_FECHA_FIN_NO_VALIDO;
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void ValidarContratoConNominaTest4() throws ParseException, ContratoNotValidException, NominaNotValidException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Nomina nominaModel = new Nomina();
        PeriodoNomina periodoNominaModel = new PeriodoNomina();

        Date fechaInicioNomina = formatter.parse("01/10/2021");
        Date fechaFinNomina = formatter.parse("03/10/2021");
        Date fechaNomina = formatter.parse("04/10/2021");

        periodoNominaModel.setFechaInicio(fechaInicioNomina);
        periodoNominaModel.setFechaFin(fechaFinNomina);
        nominaModel.setFecha(fechaNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        assertEquals(true, nominaModel.validarNomina(nominaModel));
    }
}
