package nomina.soft.backend;


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
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.services.impl.ContratoServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ContratoTests {

	//Regla 1
	@Test 
	void validarVigenciaTestCasoValido() throws ParseException {
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
	void validarVigenciaTestEstaCancelado() throws ParseException {
		
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
	void validarVigenciaTestFechaNoValida() throws ParseException {
		
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

}
