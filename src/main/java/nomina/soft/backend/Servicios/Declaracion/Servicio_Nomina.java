package nomina.soft.backend.Servicios.Declaracion;
import java.util.Date;
import java.util.List;

import nomina.soft.backend.Data_Transfer_Object.DTO_Nomina;
import nomina.soft.backend.Entidades.BoletaDePago;
import nomina.soft.backend.Entidades.Nomina;
import nomina.soft.backend.Excepciones.Clases.ContratoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.ContratoNotValidException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;
import nomina.soft.backend.Excepciones.Clases.NominaNotFoundException;
import nomina.soft.backend.Excepciones.Clases.NominaNotValidException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotFoundException;


public interface Servicio_Nomina {
    
    public List<Nomina> obtenerTodas();
    public List<Nomina> obtenerTodasPorDescripcion(String descripcion) throws NominaNotFoundException;
    public List<BoletaDePago> guardarNuevaNomina(DTO_Nomina nominaDto) throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException;
    public List<BoletaDePago> generarNuevaNomina(DTO_Nomina nominaDto) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, PeriodoNominaNotFoundException, EmpleadoNotValidException;
    public Nomina buscarNominaPorId(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException;
    public Nomina cerrarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException;
    public void eliminarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException;
    public List<BoletaDePago> generarNuevaNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException;
    public List<BoletaDePago> guardarNuevaNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException;

}
