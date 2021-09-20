package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.repositories.ContratoRepository;
import nomina.soft.backend.services.ContratoService;

@Service
@Transactional
public class ContratoServiceImpl implements ContratoService {

	private ContratoRepository contratoRepository;

	@Autowired
	public ContratoServiceImpl(ContratoRepository contratoRepository) {
		super();
		this.contratoRepository = contratoRepository;
	}
	
	
	
	
}
