package nomina.soft.backend;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import nomina.soft.backend.constant.ContratoImplConstant;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.services.impl.ContratoServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ContratoTests {

	//Regla 1
	@Test 
	void validarVigenciaTest1() throws ParseException {
		ContratoModel contrato = new ContratoModel();
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
		
		ContratoModel contrato = new ContratoModel();
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
		
		ContratoModel contrato = new ContratoModel();
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
		ContratoImplConstant exceptionMsg = new ContratoImplConstant();
		ContratoModel contrato = new ContratoModel();
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
			assertEquals(exceptionMsg.FECHA_INICIO_NOT_VALID,resultado);
		}
		
	}

	@Test
	void fechasValidasTest2() throws ParseException, ContratoNotValidException {
		ContratoImplConstant exceptionMsg = new ContratoImplConstant();
		ContratoModel contrato = new ContratoModel();
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
			assertEquals(exceptionMsg.FECHA_FIN_NOT_VALID,resultado);
		}
	}

	@Test
	void fechasValidasTest3() throws ParseException, ContratoNotValidException {
		ContratoImplConstant exceptionMsg = new ContratoImplConstant();
		ContratoModel contrato = new ContratoModel();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse("15/10/2021");
		Date fechaFin = formatter.parse("15/05/2022");
		contrato.setFechaInicio(fechaInicio);
		contrato.setFechaFin(fechaFin);

		//contrato.fechasValidas(fechaInicio,fechaFin)
		String resultado = new String();
		assertTrue(contrato.fechasValidas(fechaInicio,fechaFin));

	}

	@Test
	void pagoPorHoraValidoTest1() throws ContratoNotValidException {
		String pagoPorHoraEntrada = new String();
		ContratoModel contrato = new ContratoModel();
		pagoPorHoraEntrada="15";
		assertEquals(true,contrato.pagoPorHoraValido(pagoPorHoraEntrada));
	}

	@Test
	void pagoPorHoraValidoTest2() throws ContratoNotValidException {
		ContratoImplConstant exceptionMsg = new ContratoImplConstant();
		String pagoPorHoraEntrada = new String();
		ContratoModel contrato = new ContratoModel();
		pagoPorHoraEntrada="90";
		String resultado = new String();
		try{
			assertEquals(true,contrato.pagoPorHoraValido(pagoPorHoraEntrada));
		}catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(exceptionMsg.PAGO_POR_HORA_RANGO_NOT_VALID,resultado);
		}
	}

	@Test
	void pagoPorHoraValidoTest3() throws ContratoNotValidException {
		ContratoImplConstant exceptionMsg = new ContratoImplConstant();
		String pagoPorHoraEntrada = new String();
		ContratoModel contrato = new ContratoModel();
		pagoPorHoraEntrada="50asd";
		String resultado = new String();
		try{
			assertEquals(true,contrato.pagoPorHoraValido(pagoPorHoraEntrada));
		}catch (Exception e) {
			resultado = e.getMessage();
			assertEquals(exceptionMsg.PAGO_POR_HORA_NOT_INTEGER,resultado);
		}
	}

}
