package nomina.soft.backend;

import static nomina.soft.backend.statics.ContratoImplConstant.FECHA_FIN_3_MESES_NO_VALIDA;
import static nomina.soft.backend.statics.ContratoImplConstant.FECHA_INICIO_NOT_VALID;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_MAYOR_40;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_NO_ENTERO;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_NO_MULTIPLO_DE_4;
import static nomina.soft.backend.statics.ContratoImplConstant.PAGO_POR_HORA_MAYOR_60;
import static nomina.soft.backend.statics.ContratoImplConstant.PAGO_POR_HORA_NO_ENTERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.entidades.Contrato;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.servicios.Utility;

@SpringBootTest
class ContratoTests {

	@Autowired
	Utility utilidades;

	// Regla 1
	@Test
	void validarVigenciaTest1() throws ParseException {
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
		Date fechaFin = formatter.parse("3/01/2022");
		// TODOS LOS ARGUMENTOS SON VALIDOS
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);
		contrato.setEstaCancelado(false);
		Date tiempoActual = this.utilidades.obtenerFechaActual();
		assertTrue(contrato.vigenciaValida(contrato,tiempoActual));
	}

	@Test
	void validarVigenciaTest2() throws ParseException {

		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/10/2021");
		Date fechaFin = formatter.parse("3/01/2022");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);
		// CASO DE PRUEBA DONDE CONTRATO YA ESTA CANCELADO
		contrato.setEstaCancelado(true);
		Date tiempoActual = this.utilidades.obtenerFechaActual();
		assertFalse(contrato.vigenciaValida(contrato,tiempoActual));
	}

	@Test
	void validarVigenciaTest3() throws ParseException {

		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/9/2021");
		// CASO DE PRUEBA DONDE FECHA FIN ES ANTERIOR A LA ACTUAL
		Date fechaFin = formatter.parse("2/10/2021");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);
		contrato.setEstaCancelado(true);
		Date tiempoActual = this.utilidades.obtenerFechaActual();
		assertFalse(contrato.vigenciaValida(contrato,tiempoActual));
	}

	@Test
	void fechasValidasTest1() throws ParseException, ContratoNotValidException {
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("3/9/2021");
		Date fechaFin = formatter.parse("3/12/2021");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);

		String resultado = new String();
		try {
			Date tiempoActual = this.utilidades.obtenerFechaActual();
			assertTrue(contrato.fechasValidas(fechaInicio, fechaFin, tiempoActual));
		} catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(FECHA_INICIO_NOT_VALID, resultado);
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
		String resultado = new String();
		try {
			Date tiempoActual = this.utilidades.obtenerFechaActual();
			assertTrue(contrato.fechasValidas(fechaInicio, fechaFin, tiempoActual));
		} catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(FECHA_FIN_3_MESES_NO_VALIDA, resultado);
		}
	}

	@Test
	void fechasValidasTest3() throws ParseException, ContratoNotValidException {
		Contrato contrato = new Contrato();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("16/11/2021");
		Date fechaFin = formatter.parse("15/05/2022");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);
		Date tiempoActual = this.utilidades.obtenerFechaActual();
		assertTrue(contrato.fechasValidas(fechaInicio, fechaFin, tiempoActual));

	}

	@Test
	void pagoPorHoraValidoTest1() throws ContratoNotValidException {
		String pagoPorHoraEntrada = new String();
		Contrato contrato = new Contrato();
		pagoPorHoraEntrada = "15";
		assertEquals(true, contrato.pagoPorHoraValido(pagoPorHoraEntrada));
	}

	@Test
	void pagoPorHoraValidoTest2() throws ContratoNotValidException {
		String pagoPorHoraEntrada = new String();
		Contrato contrato = new Contrato();
		pagoPorHoraEntrada = "90";
		String resultado = new String();
		try {
			assertEquals(true, contrato.pagoPorHoraValido(pagoPorHoraEntrada));
		} catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(PAGO_POR_HORA_MAYOR_60, resultado);
		}
	}

	@Test
	void pagoPorHoraValidoTest3() throws ContratoNotValidException {
		String pagoPorHoraEntrada = new String();
		Contrato contrato = new Contrato();
		pagoPorHoraEntrada = "50asd";
		String resultado = new String();
		try {
			assertEquals(true, contrato.pagoPorHoraValido(pagoPorHoraEntrada));
		} catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(PAGO_POR_HORA_NO_ENTERO, resultado);
		}
	}

	@Test
	void horasContratadasValidasTest1() {
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "10.3";
		try {
			assertEquals(false, contratoTemporal.horasContratadasValidas(horasContratadas));
		} catch (Exception e) {
			String expectedMessage = HORAS_CONTRATADAS_NO_ENTERO;
			assertEquals(expectedMessage, e.getMessage());
		}
	}

	@Test
	void horasContratadasValidasTest2() {
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "50";
		try {
			assertEquals(false, contratoTemporal.horasContratadasValidas(horasContratadas));
		} catch (Exception e) {
			String expectedMessage = HORAS_CONTRATADAS_MAYOR_40;
			assertEquals(expectedMessage, e.getMessage());
		}
	}

	@Test
	void horasContratadasValidasTest3() {
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "14";
		try {
			assertEquals(false, contratoTemporal.horasContratadasValidas(horasContratadas));
		} catch (Exception e) {
			String expectedMessage = HORAS_CONTRATADAS_NO_MULTIPLO_DE_4;
			assertEquals(expectedMessage, e.getMessage());
		}
	}

	@Test
	void horasContratadasValidasTest4() throws ContratoNotValidException {
		Contrato contratoTemporal = new Contrato();
		String horasContratadas = "24";
		assertEquals(true, contratoTemporal.horasContratadasValidas(horasContratadas));
	}

}
