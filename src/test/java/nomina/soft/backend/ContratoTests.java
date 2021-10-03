package nomina.soft.backend;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.models.ContratoModel;

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

	@Test
	void horasContratadasValidasTest1(){
		ContratoModel contratoTemporal = new ContratoModel();
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
		ContratoModel contratoTemporal = new ContratoModel();
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
		ContratoModel contratoTemporal = new ContratoModel();
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
		ContratoModel contratoTemporal = new ContratoModel();
		String horasContratadas = "24";
        assertEquals(true, contratoTemporal.horasContratadasValidas(horasContratadas));
	}

}
