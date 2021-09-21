package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.repositories.AfpRepository;
import nomina.soft.backend.repositories.ContratoRepository;
import nomina.soft.backend.repositories.EmpleadoRepository;

import nomina.soft.backend.services.ContratoService;

@Service
@Transactional
public class ContratoServiceImpl implements ContratoService {

	private ContratoRepository contratoRepository;

	private EmpleadoRepository empleadoRepository;
	private AfpRepository afpRepository;

	@Autowired
	public ContratoServiceImpl(ContratoRepository contratoRepository, EmpleadoRepository empleadoRepository, AfpRepository afpRepository) {
		super();
		this.contratoRepository = contratoRepository;
		this.empleadoRepository = empleadoRepository;
		this.afpRepository = afpRepository;
	}

	@Override
	public ContratoModel guardarContrato(ContratoDto contratoDto) {
		ContratoModel contrato = new ContratoModel();
		contrato.setNombres(contratoDto.getNombres());
		contrato.setFechaInicio(contratoDto.getFechaInicio());
		contrato.setFechaFin(contratoDto.getFechaFin());
		contrato.setTieneAsignacionFamiliar(contratoDto.getTieneAsignacionFamiliar());
		contrato.setHorasPorSemana(contratoDto.getHorasPorSemana());
		contrato.setPagoPorHora(contratoDto.getPagoPorHora());
		contrato.setPuesto(contratoDto.getPuesto());
		contrato.setCancelado(contratoDto.getCancelado());
		contrato.setEmpleado(empleadoRepository.getById(contratoDto.getEmpleado_id()));
		contrato.setAfp(afpRepository.getById(contratoDto.getAfp_id()));
		contrato.setIncidenciaLaborales(contratoDto.getIncidenciaLaborales());
		return contratoRepository.save(contrato);

	}
	
	
	
	
}
