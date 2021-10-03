package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.models.BoletaDePagoModel;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.repositories.BoletaDePagoRepository;
import nomina.soft.backend.services.BoletaDePagoService;

@Service
@Transactional
public class BoletaDePagoServiceImpl implements BoletaDePagoService {

	private BoletaDePagoRepository boletaDePagoRepository;

	@Autowired
	public BoletaDePagoServiceImpl(BoletaDePagoRepository boletaDePagoRepository) {
		super();
		this.boletaDePagoRepository = boletaDePagoRepository;
	}

	public BoletaDePagoModel generarBoletaDePago(ContratoModel contrato, NominaModel nomina) {
		BoletaDePagoModel boletaDePago = new BoletaDePagoModel();
		float totalIngresos = boletaDePago.calcularTotalIngresos(contrato, nomina, boletaDePago);
		float totalRetenciones = boletaDePago.calcularTotalRetenciones(contrato, nomina, boletaDePago);
		float netoAPagar = boletaDePago.calcularNetoAPagar(totalIngresos, totalRetenciones); 
		boletaDePago.setNetoPorPagar(netoAPagar);
		boletaDePago.setContrato(contrato);
		boletaDePago.setNomina(nomina);
		return boletaDePago;
	}

	public BoletaDePagoModel guardarBoletaDePago(ContratoModel contrato, NominaModel nomina) {
		BoletaDePagoModel boletaDePago = new BoletaDePagoModel();
		float totalIngresos = boletaDePago.calcularTotalIngresos(contrato, nomina, boletaDePago);
		float totalRetenciones = boletaDePago.calcularTotalRetenciones(contrato, nomina, boletaDePago);
		float netoAPagar = totalIngresos - totalRetenciones;
		boletaDePago.setNetoPorPagar(netoAPagar);
		boletaDePago.setContrato(contrato);
		boletaDePago.setNomina(nomina);
		this.boletaDePagoRepository.save(boletaDePago);
		return boletaDePago;
	}

}
