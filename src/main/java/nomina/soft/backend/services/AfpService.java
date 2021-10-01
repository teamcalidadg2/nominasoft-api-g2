package nomina.soft.backend.services;

import java.util.List;

import nomina.soft.backend.dto.AfpDto;
import nomina.soft.backend.exception.domain.AfpExistsException;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.models.AfpModel;

public interface AfpService {
    
	public List<AfpModel> getAll();
	
	public AfpModel buscarAfpPorNombre(String nombre) throws AfpNotFoundException;
	
	public AfpModel guardarAFP(AfpDto afpDto) throws AfpNotFoundException, AfpExistsException;
	
	public AfpModel updateAfp(String actualNombre, String nombre, float actualDescuento, float descuento) throws AfpNotFoundException, AfpExistsException;
	
	public void deleteAfp(String nombre);
	
}
