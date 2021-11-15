package nomina.soft.backend.servicios.declaracion;
import java.util.Date;
import java.util.List;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.excepciones.clases.NominaNotFoundException;
import nomina.soft.backend.excepciones.clases.NominaNotValidException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.models.BoletaDePago;
import nomina.soft.backend.models.Nomina;


public interface ServicioNomina {
    
    public List<Nomina> obtenerTodas();
    public List<Nomina> obtenerTodasPorDescripcion(String descripcion) throws NominaNotFoundException;
    public List<BoletaDePago> guardarNuevaNomina(NominaDto nominaDto) throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException;
    public List<BoletaDePago> generarNuevaNomina(NominaDto nominaDto) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, PeriodoNominaNotFoundException, EmpleadoNotValidException;
    public Nomina buscarNominaPorId(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException;
    public void cerrarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException;
    public void eliminarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException;
    public List<BoletaDePago> generarNuevaNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException;
    public List<BoletaDePago> guardarNuevaNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException;

}
