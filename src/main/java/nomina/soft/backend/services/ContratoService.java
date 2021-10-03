package nomina.soft.backend.services;

import java.util.List;

import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.exception.domain.ContratoExistsException;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.ContratoModel;

public interface ContratoService {
    
	public List<ContratoModel> getAll(String dniEmpleado) throws EmpleadoNotFoundException;
	
	public ContratoModel buscarContratoPorDni(String dniEmpleado) throws ContratoNotFoundException, EmpleadoNotFoundException;
	
	public ContratoModel guardarContrato(ContratoDto contratoDto) throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException;

    public ContratoModel updateContrato(Long idContrato, String puesto, String horasPorSemana, Long idAfp,
            								Boolean tieneAsignacionFamiliar, String pagoPorHora) throws ContratoNotValidException, AfpNotFoundException, ContratoNotFoundException;

    public ContratoModel cancelarContrato(Long idContrato) throws ContratoNotFoundException;
	
	
	
}
