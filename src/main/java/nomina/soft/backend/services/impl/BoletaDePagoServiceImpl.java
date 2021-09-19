package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.repositories.BoletaDePagoRepository;
import nomina.soft.backend.services.BoletaDePagoService;


@Service
@Transactional
public class BoletaDePagoServiceImpl implements BoletaDePagoService{

	private BoletaDePagoRepository boletaDePagoRepository;

	@Autowired
	public BoletaDePagoServiceImpl(BoletaDePagoRepository boletaDePagoRepository) {
		super();
		this.boletaDePagoRepository = boletaDePagoRepository;
	}
	
	
	
}
