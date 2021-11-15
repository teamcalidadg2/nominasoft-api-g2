package nomina.soft.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Entidades.Empleado;
import nomina.soft.backend.Entidades.Nomina;
import nomina.soft.backend.Entidades.PeriodoNomina;
import nomina.soft.backend.Excepciones.Clases.ContratoNotValidException;
import nomina.soft.backend.Excepciones.Clases.NominaNotValidException;

@SpringBootTest
public class NominaTests {
    @Test
    void ValidarContratoConNominaTest1() throws ParseException, ContratoNotValidException, NominaNotValidException {
        Empleado empleadomodel = new Empleado();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        Contrato contratomodel = new Contrato();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("01/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(false);

        Nomina nominaModel = new Nomina();
        PeriodoNomina periodoNominaModel = new PeriodoNomina();

        Date fechaInicioNomina = formatter.parse("10/10/2021");
        periodoNominaModel.setFechaInicio(fechaInicioNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        try {
            // assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel,
            // nominaModel));
        } catch (Exception e) {
            String expectedMessage = "Se encontró un contrato con fecha fin menor a la fecha de inicio del periodo de nómina del empleado: Elvis Tek";
            assertEquals(expectedMessage, e.getMessage());
        }

    }

    @Test
    void ValidarContratoConNominaTest2() throws ParseException {
        Empleado empleadomodel = new Empleado();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        Contrato contratomodel = new Contrato();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("10/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(true);

        Nomina nominaModel = new Nomina();
        PeriodoNomina periodoNominaModel = new PeriodoNomina();

        Date fechaInicioNomina = formatter.parse("01/10/2021");
        periodoNominaModel.setFechaInicio(fechaInicioNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        try {
            // assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel,
            // nominaModel));
        } catch (Exception e) {
            String expectedMessage = "Se encontró un contrato cancelado del empleado: Elvis Tek";
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void ValidarContratoConNominaTest3() throws ParseException {
        Empleado empleadomodel = new Empleado();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        Contrato contratomodel = new Contrato();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("10/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(false);

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
            // assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel,
            // nominaModel));
        } catch (Exception e) {
            String expectedMessage = "La fecha fin del periodo de Nómina debe ser menor a la fecha de generación de la Nómina.";
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void ValidarContratoConNominaTest4() throws ParseException, ContratoNotValidException, NominaNotValidException {
        Empleado empleadomodel = new Empleado();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        Contrato contratomodel = new Contrato();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("10/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(false);

        Nomina nominaModel = new Nomina();
        PeriodoNomina periodoNominaModel = new PeriodoNomina();

        Date fechaInicioNomina = formatter.parse("01/10/2021");
        Date fechaFinNomina = formatter.parse("03/10/2021");
        Date fechaNomina = formatter.parse("04/10/2021");

        periodoNominaModel.setFechaInicio(fechaInicioNomina);
        periodoNominaModel.setFechaFin(fechaFinNomina);
        nominaModel.setFecha(fechaNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);


        // assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel,
        // nominaModel));
    }
}
