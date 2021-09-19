package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.repositories.AfpRepository;
import nomina.soft.backend.services.AfpService;

@Service
@Transactional
public class AfpServiceImpl implements AfpService{

	private AfpRepository afpRepository;

	@Autowired
	public AfpServiceImpl(AfpRepository afpRepository) {
		super();
		this.afpRepository = afpRepository;
	}
	
	
	
	
}
