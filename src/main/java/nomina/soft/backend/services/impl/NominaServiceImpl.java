package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.repositories.NominaRepository;
import nomina.soft.backend.services.NominaService;

@Service
@Transactional
public class NominaServiceImpl implements NominaService{

	private NominaRepository nominaRepository;

	@Autowired
	public NominaServiceImpl(NominaRepository nominaRepository) {
		super();
		this.nominaRepository = nominaRepository;
	}
	
	
	
}
