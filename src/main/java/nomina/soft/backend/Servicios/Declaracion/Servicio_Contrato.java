package nomina.soft.backend.Servicios.Declaracion;

import java.util.Date;
import java.util.List;

import nomina.soft.backend.Data_Transfer_Object.DTO_Contrato;
import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Excepciones.Clases.AfpNotFoundException;
import nomina.soft.backend.Excepciones.Clases.ContratoExistsException;
import nomina.soft.backend.Excepciones.Clases.ContratoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.ContratoNotValidException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;

public interface Servicio_Contrato {

        public List<Contrato> buscarListaDeContratosPorEmpleado(String dniEmpleado) throws EmpleadoNotFoundException;

        public Contrato buscarContratoVigentePorDni(String dniEmpleado)
                        throws ContratoNotFoundException, EmpleadoNotFoundException, EmpleadoNotValidException;

        public Contrato buscarContratoPorId(String idContrato)
                        throws NumberFormatException, ContratoNotValidException, ContratoNotFoundException;

        public Contrato guardarNuevoContrato(DTO_Contrato contratoDto) throws ContratoNotValidException,
                        AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException, NumberFormatException, ContratoNotFoundException;

        public Contrato guardarNuevoContrato(Date fechaInicio, Date fechaFin, String idEmpleado, String puesto,
                        String horasPorSemana, String idAfp, Boolean tieneAsignacionFamiliar, String pagoPorHora)
                        throws NumberFormatException, ContratoNotValidException, AfpNotFoundException,
                        EmpleadoNotFoundException, ContratoExistsException, ContratoNotFoundException;

        public Contrato actualizarContrato(String idContrato, String puesto, String horasPorSemana, String idAfp,
                        Boolean tieneAsignacionFamiliar, String pagoPorHora)
                        throws ContratoNotValidException, AfpNotFoundException, ContratoNotFoundException;

        public void cancelarContrato(String idContrato)
                        throws ContratoNotFoundException, NumberFormatException, ContratoNotValidException;
}
