package nomina.soft.backend.services;
import java.util.Date;
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
    public NominaModel buscarPorId(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException;
    public NominaModel cerrarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException;
    public void eliminarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException;
    public List<BoletaDePagoModel> generarNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException;
    public List<BoletaDePagoModel> guardarNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException;

}
