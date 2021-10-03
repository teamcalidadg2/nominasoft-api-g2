package nomina.soft.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.models.BoletaDePagoModel;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.services.impl.ContratoServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class BoletaDePagoTests {
    @Test
    void calcularSueldoBasicoTest(){
        BoletaDePagoModel boleta = new BoletaDePagoModel();
        int totalDeHoras, pagoPorHoraContrato;
        totalDeHoras=16;
        pagoPorHoraContrato=20;
        int result=boleta.calcularSueldoBasico(totalDeHoras, pagoPorHoraContrato);
        int expectativa=16*20;
        assertEquals(result, expectativa);
    }

    @Test
    void CalcularMontoPorAsignacionFamiliarTest1(){
        BoletaDePagoModel boleta = new BoletaDePagoModel();
        boolean tieneAsignacionFamiliar=true;
        int sueldoBasico=2000;
        float result=boleta.calcularMontoPorAsignacionFamiliar(tieneAsignacionFamiliar,sueldoBasico);
        assertEquals(result,200);
    }

    @Test
    void CalcularMontoPorAsignacionFamiliarTest2(){
        BoletaDePagoModel boleta = new BoletaDePagoModel();
        boolean tieneAsignacionFamiliar=false;
        int sueldoBasico=2500;
        float result=boleta.calcularMontoPorAsignacionFamiliar(tieneAsignacionFamiliar,sueldoBasico);
        assertEquals(result,0);
    }

    @Test
    void calcularRegimenPensionarioTest(){
        BoletaDePagoModel boleta = new BoletaDePagoModel();
        int sueldoBasico=1500;
        int porcentajeAFP=20;
        float result =boleta.calcularRegimenPensionario(sueldoBasico,porcentajeAFP);
        assertEquals(result,300);
    }

    @Test
    void calcularMontonPorHoraDeFalta(){
        BoletaDePagoModel boleta = new BoletaDePagoModel();
        int totalHorasDeFaltaIncidenciaLaboral= 10;
        int pagoPorHoraContrato = 20;
        float result = boleta.calcularMontoPorHorasDeFalta(totalHorasDeFaltaIncidenciaLaboral, pagoPorHoraContrato);
        assertEquals(result,200);
    }

    @Test
    void calcularNetoAPagartest(){
        BoletaDePagoModel boleta = new BoletaDePagoModel();
        float totalIngresos=5000;
        float totalRetenciones=400;
        float result = boleta.calcularNetoAPagar(totalIngresos, totalRetenciones);
        assertEquals(result,4600);
    }

    @Test
    void CalcularHorasSemanaTest1() throws ParseException {
        BoletaDePagoModel boletaDePagoModel = new BoletaDePagoModel();

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
        Date fechaFin = formatter.parse("24/10/2021");
        int horasSemana = 32;

        int resultado = boletaDePagoModel.calcularTotalDeHoras(fechaInicio, fechaFin, horasSemana);
        int expectativa=96;
        assertEquals(expectativa ,resultado);
    }

    @Test
	void calcularTotalDeIngresosTest(){
		int sueldoBasico = 1800;
		float montoPorAsignacionFamiliar = 180;
		int montoPorHorasExtras = 20;
		float reintegros = 0;
		float movilidad = 100;
		float otrosIngresos = 50;
        BoletaDePagoModel boletaDePago = new BoletaDePagoModel();
        float resultado = boletaDePago.sumaTotalIngresos(sueldoBasico, montoPorAsignacionFamiliar, montoPorHorasExtras, reintegros, movilidad, otrosIngresos);

        float expectativa = 2150;
        assertEquals(expectativa ,resultado);
	}

    @Test
	void calcularTotalDeRetencionesTest1(){
        float regimenPensionarioTMP = 100f;
        int montoPorHorasDeFaltaTMP = 8;
        float adelantosTMP = 150;
        float otrosDescuentosTMP = 10;
        
        BoletaDePagoModel boletaDePago = new BoletaDePagoModel();
        float resultado = boletaDePago.sumaTotalDeRetenciones(regimenPensionarioTMP, montoPorHorasDeFaltaTMP, adelantosTMP,  otrosDescuentosTMP);

        float expectativa = 268;
        assertEquals(expectativa ,resultado);
	}
}
