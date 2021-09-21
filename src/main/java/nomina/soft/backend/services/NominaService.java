package nomina.soft.backend.services;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.exception.domain.NominaExistsException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.models.NominaModel;


public interface NominaService {
    
    public ArrayList<NominaModel> getAll();
    
    public NominaModel guardarNomina(NominaDto nominaDto) throws NominaNotFoundException, NominaExistsException;
    public void delete (int id);


}
