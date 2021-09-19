package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.repositories.IncidenciaLaboralRepository;
import nomina.soft.backend.services.IncidenciaLaboralService;

@Service
@Transactional
public class IncidenciaLaboralServiceImpl implements IncidenciaLaboralService{

	private IncidenciaLaboralRepository incidenciaLaboralRepository;

	@Autowired
	public IncidenciaLaboralServiceImpl(IncidenciaLaboralRepository incidenciaLaboralRepository) {
		super();
		this.incidenciaLaboralRepository = incidenciaLaboralRepository;
	}
	
	
	
}
