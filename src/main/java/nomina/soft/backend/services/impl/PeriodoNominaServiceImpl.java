package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.repositories.PeriodoNominaRepository;
import nomina.soft.backend.services.PeriodoNominaService;

@Service
@Transactional
public class PeriodoNominaServiceImpl implements PeriodoNominaService{

	private PeriodoNominaRepository periodoNominaRepository;

	
	@Autowired
	public PeriodoNominaServiceImpl(PeriodoNominaRepository periodoNominaRepository) {
		super();
		this.periodoNominaRepository = periodoNominaRepository;
	}
	
	
	
}
