package nomina.soft.backend.services;
import java.util.List;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotFoundException;
import nomina.soft.backend.models.BoletaDePagoModel;
import nomina.soft.backend.models.NominaModel;


public interface NominaService {
    
    public List<NominaModel> getAll();
    public List<NominaModel> getAllByDescripcion(String descripcion) throws NominaNotFoundException;
    public List<BoletaDePagoModel> guardarNomina(NominaDto nominaDto) throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException;
    public List<BoletaDePagoModel> generarNomina(NominaDto nominaDto) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, PeriodoNominaNotFoundException, EmpleadoNotValidException;
    public NominaModel buscarPorId(Long idNomina) throws NominaNotFoundException;
    public NominaModel cerrarNomina(Long idNomina) throws NominaNotFoundException;
    public void eliminarNomina(Long idNomina) throws NominaNotFoundException;

}
