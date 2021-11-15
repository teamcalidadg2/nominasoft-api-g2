package nomina.soft.backend;


import static nomina.soft.backend.constantes.ContratoImplConstant.FECHA_FIN_3_MESES_NO_VALIDA;
import static nomina.soft.backend.constantes.ContratoImplConstant.FECHA_INICIO_NOT_VALID;
import static nomina.soft.backend.constantes.ContratoImplConstant.PAGO_POR_HORA_MAYOR_60;
import static nomina.soft.backend.constantes.ContratoImplConstant.PAGO_POR_HORA_NO_ENTERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.models.Contrato;

@SpringBootTest
public class ContratoTests {

	//Regla 1
	@Test 
	void validarVigenciaTest1() throws ParseException {
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
		Date fechaFin = formatter.parse("3/01/2022");
		//TODOS LOS ARGUMENTOS SON VALIDOS
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);
		contrato.setEstaCancelado(false);
		assertTrue(contrato.vigenciaValida(contrato));
	}
	
	@Test
	void validarVigenciaTest2() throws ParseException {
		
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
		Date fechaFin = formatter.parse("3/01/2022");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);
		//CASO DE PRUEBA DONDE CONTRATO YA ESTA CANCELADO
		contrato.setEstaCancelado(true);
		assertFalse(contrato.vigenciaValida(contrato));
	}

	@Test
	void validarVigenciaTest3() throws ParseException {
		
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/9/2021");
		//CASO DE PRUEBA DONDE FECHA FIN ES ANTERIOR A LA ACTUAL
		Date fechaFin = formatter.parse("2/10/2021");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);
		contrato.setEstaCancelado(true);
		assertFalse(contrato.vigenciaValida(contrato));
	}

	@Test
	void fechasValidasTest1() throws ParseException, ContratoNotValidException {
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/9/2021");
		Date fechaFin = formatter.parse("3/12/2021");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);

		//contrato.fechasValidas(fechaInicio,fechaFin)
		String resultado = new String();
		try {
			assertTrue(contrato.fechasValidas(fechaInicio,fechaFin));
		} catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(FECHA_INICIO_NOT_VALID,resultado);
		}
		
	}

	@Test
	void fechasValidasTest2() throws ParseException, ContratoNotValidException {
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("4/12/2021");
		Date fechaFin = formatter.parse("5/12/2021");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);

		//contrato.fechasValidas(fechaInicio,fechaFin)
		String resultado = new String();
		try {
			assertTrue(contrato.fechasValidas(fechaInicio,fechaFin));
		} catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(FECHA_FIN_3_MESES_NO_VALIDA,resultado);
		}
	}

	@Test
	void fechasValidasTest3() throws ParseException, ContratoNotValidException {
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("15/10/2021");
		Date fechaFin = formatter.parse("15/05/2022");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);

		//contrato.fechasValidas(fechaInicio,fechaFin)
		assertTrue(contrato.fechasValidas(fechaInicio,fechaFin));

	}

	@Test
	void pagoPorHoraValidoTest1() throws ContratoNotValidException {
		String pagoPorHoraEntrada = new String();
		Contrato contrato = new Contrato();
		pagoPorHoraEntrada="15";
		assertEquals(true,contrato.pagoPorHoraValido(pagoPorHoraEntrada));
	}

	@Test
	void pagoPorHoraValidoTest2() throws ContratoNotValidException {
		String pagoPorHoraEntrada = new String();
		Contrato contrato = new Contrato();
		pagoPorHoraEntrada="90";
		String resultado = new String();
		try{
			assertEquals(true,contrato.pagoPorHoraValido(pagoPorHoraEntrada));
		}catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(PAGO_POR_HORA_MAYOR_60,resultado);
		}
	}

	@Test
	void pagoPorHoraValidoTest3() throws ContratoNotValidException {
		String pagoPorHoraEntrada = new String();
		Contrato contrato = new Contrato();
		pagoPorHoraEntrada="50asd";
		String resultado = new String();
		try{
			assertEquals(true,contrato.pagoPorHoraValido(pagoPorHoraEntrada));
		}catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(PAGO_POR_HORA_NO_ENTERO,resultado);
		}
	}

	@Test
	void horasContratadasValidasTest1(){
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "10.3";
		try {
            assertEquals(false, contratoTemporal.horasContratadasValidas(horasContratadas));
        } 
        catch (Exception e) {
            String expectedMessage = "Las horas contratadas por semana deben ser números enteros.";
            assertEquals( expectedMessage, e.getMessage() );
        }
	}

	@Test
	void horasContratadasValidasTest2(){
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "50";
		try {
            assertEquals(false, contratoTemporal.horasContratadasValidas(horasContratadas));
        } 
        catch (Exception e) {
            String expectedMessage = "Las horas contratadas por semana deben estar en un rango de 8 a 40 horas.";
            assertEquals( expectedMessage, e.getMessage() );
        }
	}

	@Test
	void horasContratadasValidasTest3(){
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "14";
		try {
            assertEquals(false, contratoTemporal.horasContratadasValidas(horasContratadas));
        } 
        catch (Exception e) {
            String expectedMessage = "La horas contratadas por semana deben ser múltiplos de 4.";
            assertEquals( expectedMessage, e.getMessage() );
        }
	}

	@Test
	void horasContratadasValidasTest4() throws ContratoNotValidException{
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "24";
        assertEquals(true, contratoTemporal.horasContratadasValidas(horasContratadas));
	}


	

}
