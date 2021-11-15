package nomina.soft.backend.Servicios.Implementacion;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.Data_Access_Object.DAO_BoletaDePago;
import nomina.soft.backend.Entidades.BoletaDePago;
import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Entidades.Nomina;
import nomina.soft.backend.Servicios.Declaracion.Servicio_BoletaDePago;

@Service
@Transactional
public class Impl_Servicio_BoletaDePago implements Servicio_BoletaDePago {

	private DAO_BoletaDePago repositorioBoletaDePago;

	@Autowired
	public Impl_Servicio_BoletaDePago(DAO_BoletaDePago repositorioBoletaDePago) {
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
