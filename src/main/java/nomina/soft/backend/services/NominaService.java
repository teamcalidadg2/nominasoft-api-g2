package nomina.soft.backend.services;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.NominaExistsException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import nomina.soft.backend.models.NominaModel;


public interface NominaService {
    
    public ArrayList<NominaModel> getAll();
    
    public NominaModel guardarNomina(NominaDto nominaDto, int contrato_id) throws NominaNotFoundException, NominaExistsException, NominaNotValidException, ContratoNotFoundException, ContratoNotValidException;
    public void delete (int id);
    public NominaModel buscarPorId(int id) throws NominaNotFoundException;

}
