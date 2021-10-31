package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.AfpImplConstant.DESCUENTO_ALREADY_EXISTS;
import static nomina.soft.backend.constant.AfpImplConstant.NOMBRE_ALREADY_EXISTS;
import static nomina.soft.backend.constant.AfpImplConstant.NO_AFP_FOUND_BY_DESCUENTO;
import static nomina.soft.backend.constant.AfpImplConstant.NO_AFP_FOUND_BY_NOMBRE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.AfpDto;
import nomina.soft.backend.exception.domain.AfpExistsException;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.models.AfpModel;
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

	@Override
	public List<AfpModel> getAll() {
		return afpRepository.findAll();
	}

	@Override
	public AfpModel buscarAfpPorNombre(String nombre) throws AfpNotFoundException {
		AfpModel afpEncontrado = afpRepository.findByNombre(nombre);
		if(afpEncontrado==null) {
			throw new AfpNotFoundException(NO_AFP_FOUND_BY_NOMBRE + nombre);
		}
		return afpEncontrado;
	}

	@Override
	public AfpModel buscarAfpPorId(String id) throws AfpNotFoundException {
		AfpModel afpEncontrado = afpRepository.findByIdAfp(Long.parseLong(id));
		if(afpEncontrado==null) {
			throw new AfpNotFoundException(NO_AFP_FOUND_BY_NOMBRE + id);
		}
		return afpEncontrado;
	}
	

	@Override
	public AfpModel guardarAFP(AfpDto afpDto) throws AfpNotFoundException, AfpExistsException {
		AfpModel afp = new AfpModel();
		validateNewNombre(EMPTY,afpDto.getNombre());
		validateNewDescuento(0.0f,afpDto.getPorcentajeDescuento());
		
        afp.setNombre(afpDto.getNombre());
        afp.setPorcentajeDescuento(afpDto.getPorcentajeDescuento());
        afpRepository.save(afp);
        return afp;
	}

	@Override
	public AfpModel updateAfp(String actualNombre, String nombre, int actualDescuento, int descuento) throws AfpNotFoundException, AfpExistsException {
		AfpModel currentAfp = validateNewNombre(actualNombre, nombre);
		currentAfp = validateNewDescuento(actualDescuento, descuento);
		currentAfp.setNombre(nombre);
		currentAfp.setPorcentajeDescuento(descuento);
        afpRepository.save(currentAfp);
        return currentAfp;
	}

	@Override
	public void deleteAfp(String nombre) {
		AfpModel afp = afpRepository.findByNombre(nombre);
		afpRepository.deleteById(afp.getIdAfp());
	}
	
	
	private AfpModel validateNewNombre(String actualNombre, String nuevoNombre) throws AfpNotFoundException, AfpExistsException{
		AfpModel afpConNuevoNombre = afpRepository.findByNombre(nuevoNombre);
        if(StringUtils.isNotBlank(actualNombre)) {
        	AfpModel actualAfp = afpRepository.findByNombre(actualNombre);
            if(actualAfp == null) {
                throw new AfpNotFoundException(NO_AFP_FOUND_BY_NOMBRE + actualNombre);
            }
            if(afpConNuevoNombre != null && (!(actualAfp.getIdAfp().equals(afpConNuevoNombre.getIdAfp())))) {
                throw new AfpExistsException(NOMBRE_ALREADY_EXISTS);
            }
            return actualAfp;
        } else {
            if(afpConNuevoNombre != null) {
                throw new AfpExistsException(NOMBRE_ALREADY_EXISTS);
            }
            return null;
        }
    }
	
	private AfpModel validateNewDescuento(float actualDescuento, float nuevoDescuento) throws AfpNotFoundException, AfpExistsException{
		AfpModel afpConNuevoDescuento = afpRepository.findByPorcentajeDescuento(nuevoDescuento);
        if(actualDescuento!=0.0f) {
        	AfpModel actualAfp = afpRepository.findByPorcentajeDescuento(actualDescuento);
            if(actualAfp == null) {
                throw new AfpNotFoundException(NO_AFP_FOUND_BY_DESCUENTO + actualDescuento);
            }
            if(afpConNuevoDescuento != null && (!(actualAfp.getIdAfp().equals(afpConNuevoDescuento.getIdAfp())))) {
                throw new AfpExistsException(DESCUENTO_ALREADY_EXISTS);
            }
            return actualAfp;
        } else {
            if(afpConNuevoDescuento != null) {
                throw new AfpExistsException(DESCUENTO_ALREADY_EXISTS);
            }
            return null;
        }
    }


	
	
	
}
