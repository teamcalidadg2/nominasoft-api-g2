package nomina.soft.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.entidades.BoletaDePago;


@SpringBootTest
public class BoletaDePagoTests {
    @Test
    void calcularSueldoBasicoTest(){
        BoletaDePago boleta = new BoletaDePago();
        int totalDeHoras, pagoPorHoraContrato;
        totalDeHoras=16;
        pagoPorHoraContrato=20;
        int result=boleta.calcularSueldoBasico(totalDeHoras, pagoPorHoraContrato);
        int expectativa=16*20;
        assertEquals(result, expectativa);
    }

    @Test
    void CalcularMontoPorAsignacionFamiliarTest1(){
        BoletaDePago boleta = new BoletaDePago();
        boolean tieneAsignacionFamiliar=true;
        int sueldoBasico=2000;
        float result=boleta.calcularMontoPorAsignacionFamiliar(tieneAsignacionFamiliar,sueldoBasico);
        assertEquals(200,result);
    }

    @Test
    void CalcularMontoPorAsignacionFamiliarTest2(){
        BoletaDePago boleta = new BoletaDePago();
        boolean tieneAsignacionFamiliar=false;
        int sueldoBasico=2500;
        float result=boleta.calcularMontoPorAsignacionFamiliar(tieneAsignacionFamiliar,sueldoBasico);
        assertEquals(0,result);
    }

    @Test
    void calcularRegimenPensionarioTest(){
        BoletaDePago boleta = new BoletaDePago();
        int sueldoBasico=1500;
        int porcentajeAFP=20;
        float result =boleta.calcularRegimenPensionario(sueldoBasico,porcentajeAFP);
        assertEquals(300,result);
    }

    @Test
    void calcularMontonPorHoraDeFalta(){
        BoletaDePago boleta = new BoletaDePago();
        int totalHorasDeFaltaIncidenciaLaboral= 10;
        int pagoPorHoraContrato = 20;
        float result = boleta.calcularMontoPorHorasDeFalta(totalHorasDeFaltaIncidenciaLaboral, pagoPorHoraContrato);
        assertEquals(200,result);
    }

    @Test
    void calcularNetoAPagartest(){
        BoletaDePago boleta = new BoletaDePago();
        float totalIngresos=5000;
        float totalRetenciones=400;
        float result = boleta.calcularNetoAPagar(totalIngresos, totalRetenciones);
        assertEquals(4600,result);
    }

    @Test
    void CalcularHorasSemanaTest1() throws ParseException {
        BoletaDePago boletaDePagoModel = new BoletaDePago();

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
        BoletaDePago boletaDePago = new BoletaDePago();
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
        
        BoletaDePago boletaDePago = new BoletaDePago();
        float resultado = boletaDePago.sumaTotalDeRetenciones(regimenPensionarioTMP, montoPorHorasDeFaltaTMP, adelantosTMP,  otrosDescuentosTMP);

        float expectativa = 268;
        assertEquals(expectativa ,resultado);
	}
}
