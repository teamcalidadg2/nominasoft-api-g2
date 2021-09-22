package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static nomina.soft.backend.constant.NominaImplConstant.NO_NOMINA_FOUND_BY_ID;
import java.util.ArrayList;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.exception.domain.NominaExistsException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.models.NominaModel;
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
	@Override
    public NominaModel guardarNomina (NominaDto nominaDto) throws NominaExistsException, NominaNotFoundException{
        NominaModel nomina = new NominaModel();
		// validateNewDni(EMPTY,nominaDto.getDni());
		// validateNewTelefono(EMPTY,nominaDto.getTelefono());
		// validateNewCorreo(EMPTY,nominaDto.getCorreo());
		
        nomina.setFecha(nominaDto.getFecha());
        nomina.setDescripcion(nominaDto.getDescripcion());
		nomina.setCerrada(nominaDto.getCerrada());

        nominaRepository.save(nomina);
        return nomina;
    }
	@Override
	public ArrayList<NominaModel> getAll() {
		return (ArrayList<NominaModel>)nominaRepository.findAll();
	}
	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NominaModel buscarPorId(int id) throws NominaNotFoundException {
		NominaModel nomina = this.nominaRepository.findById(id);
		if(nomina == null){
			throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID + id);
		}
		return nomina;
	}
	
	
}
