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

}
