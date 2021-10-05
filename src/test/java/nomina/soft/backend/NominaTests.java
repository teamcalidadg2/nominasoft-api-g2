package nomina.soft.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.models.PeriodoNominaModel;

@SpringBootTest
public class NominaTests {
    @Test
    void ValidarContratoConNominaTest1() throws ParseException, ContratoNotValidException, NominaNotValidException
    {
        EmpleadoModel empleadomodel = new EmpleadoModel();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        ContratoModel contratomodel = new ContratoModel();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("01/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(false);

        NominaModel nominaModel = new NominaModel();
        PeriodoNominaModel periodoNominaModel = new PeriodoNominaModel();
       
        Date fechaInicioNomina = formatter.parse("10/10/2021");
        periodoNominaModel.setFechaInicio(fechaInicioNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        boolean exceptativa = false;

        try {
            assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel, nominaModel));
        } 
        catch (Exception e) {
            String expectedMessage = "Se encontró un contrato con fecha fin menor a la fecha de inicio del periodo de nómina del empleado: Elvis Tek";
            assertEquals( expectedMessage, e.getMessage() );
        }

    }

    @Test
    void ValidarContratoConNominaTest2() throws ParseException
    {
        EmpleadoModel empleadomodel = new EmpleadoModel();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        ContratoModel contratomodel = new ContratoModel();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("10/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(true);

        NominaModel nominaModel = new NominaModel();
        PeriodoNominaModel periodoNominaModel = new PeriodoNominaModel();
       
        Date fechaInicioNomina = formatter.parse("01/10/2021");
        periodoNominaModel.setFechaInicio(fechaInicioNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        boolean exceptativa = false;

        try {
            assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel, nominaModel));
        } 
        catch (Exception e) {
            String expectedMessage = "Se encontró un contrato cancelado del empleado: Elvis Tek";
            assertEquals( expectedMessage, e.getMessage() );
        }
    }

    @Test
    void ValidarContratoConNominaTest3() throws ParseException
    {
        EmpleadoModel empleadomodel = new EmpleadoModel();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        ContratoModel contratomodel = new ContratoModel();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("10/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(false);

        NominaModel nominaModel = new NominaModel();
        PeriodoNominaModel periodoNominaModel = new PeriodoNominaModel();
       
        Date fechaInicioNomina = formatter.parse("01/10/2021");
        Date fechaFinNomina = formatter.parse("09/10/2021");
        Date fechaNomina = formatter.parse("04/10/2021");

        periodoNominaModel.setFechaInicio(fechaInicioNomina);
        periodoNominaModel.setFechaFin(fechaFinNomina);
        nominaModel.setFecha(fechaNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        boolean exceptativa = false;

        try {
            assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel, nominaModel));
        } 
        catch (Exception e) {
            String expectedMessage = "La fecha fin del periodo de Nómina debe ser menor a la fecha de generación de la Nómina."            ;
            assertEquals( expectedMessage, e.getMessage() );
        }
    }


    @Test
    void ValidarContratoConNominaTest4() throws ParseException, ContratoNotValidException, NominaNotValidException
    {
        EmpleadoModel empleadomodel = new EmpleadoModel();
        empleadomodel.setNombres("Elvis");
        empleadomodel.setApellidos("Tek");

        ContratoModel contratomodel = new ContratoModel();
        contratomodel.setEmpleado(empleadomodel);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaContratoFin = formatter.parse("10/10/2021");
        contratomodel.setFechaFin(fechaContratoFin);
        contratomodel.setEstaCancelado(false);

        NominaModel nominaModel = new NominaModel();
        PeriodoNominaModel periodoNominaModel = new PeriodoNominaModel();
       
        Date fechaInicioNomina = formatter.parse("01/10/2021");
        Date fechaFinNomina = formatter.parse("03/10/2021");
        Date fechaNomina = formatter.parse("04/10/2021");

        periodoNominaModel.setFechaInicio(fechaInicioNomina);
        periodoNominaModel.setFechaFin(fechaFinNomina);
        nominaModel.setFecha(fechaNomina);

        nominaModel.setPeriodoNomina(periodoNominaModel);

        boolean exceptativa = true;
        
            assertEquals(exceptativa, nominaModel.validarContratoConNomina(contratomodel, nominaModel));
        }
}
