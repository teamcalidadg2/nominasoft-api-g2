package nomina.soft.backend.servicios.implementacion;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dao.BoletaDePagoDao;
import nomina.soft.backend.entidades.BoletaDePago;
import nomina.soft.backend.entidades.Contrato;
import nomina.soft.backend.entidades.Nomina;
import nomina.soft.backend.servicios.declaracion.ServicioBoletaDePago;

@Service
@Transactional
public class ImplServicioBoletaDePago implements ServicioBoletaDePago {

	private BoletaDePagoDao repositorioBoletaDePago;

	@Autowired
	public ImplServicioBoletaDePago(BoletaDePagoDao repositorioBoletaDePago) {
		super();
		this.repositorioBoletaDePago = repositorioBoletaDePago;
	}

	public BoletaDePago generarBoletaDePago(Contrato contrato, Nomina nuevaNomina) {
		BoletaDePago boletaDePago = new BoletaDePago();
		float totalIngresos = boletaDePago.calcularTotalIngresos(contrato, nuevaNomina, boletaDePago);
		float totalRetenciones = boletaDePago.calcularTotalRetenciones(contrato, nuevaNomina, boletaDePago);
		float netoAPagar = boletaDePago.calcularNetoAPagar(totalIngresos, totalRetenciones); 
		boletaDePago.setTotalIngresos(totalIngresos);
		boletaDePago.setTotalRetenciones(totalRetenciones);
		boletaDePago.setSueldoNeto(netoAPagar);
		boletaDePago.setContrato(contrato);
		boletaDePago.setNomina(nuevaNomina);
		return boletaDePago;
	}

	public BoletaDePago guardarBoletaDePago(Contrato contrato, Nomina nuevaNomina) {
		BoletaDePago boletaDePago = new BoletaDePago();
		float totalIngresos = boletaDePago.calcularTotalIngresos(contrato, nuevaNomina, boletaDePago);
		float totalRetenciones = boletaDePago.calcularTotalRetenciones(contrato, nuevaNomina, boletaDePago);
		float netoAPagar = boletaDePago.calcularNetoAPagar(totalIngresos, totalRetenciones); 
		boletaDePago.setTotalIngresos(totalIngresos);
		boletaDePago.setTotalRetenciones(totalRetenciones);
		boletaDePago.setSueldoNeto(netoAPagar);
		boletaDePago.setContrato(contrato);
		boletaDePago.setNomina(nuevaNomina);
		this.repositorioBoletaDePago.save(boletaDePago);
		return boletaDePago;
	}

}
