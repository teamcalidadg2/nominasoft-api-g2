package nomina.soft.backend.servicios.declaracion;

import java.util.Date;
import java.util.List;

import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.excepciones.clases.AfpNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoExistsException;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.models.Contrato;

public interface ServicioContrato {

        public List<Contrato> buscarListaDeContratosPorEmpleado(String dniEmpleado) throws EmpleadoNotFoundException;

        public Contrato buscarContratoVigentePorDni(String dniEmpleado)
                        throws ContratoNotFoundException, EmpleadoNotFoundException, EmpleadoNotValidException;

        public Contrato buscarContratoPorId(String idContrato)
                        throws NumberFormatException, ContratoNotValidException, ContratoNotFoundException;

        public Contrato guardarNuevoContrato(ContratoDto contratoDto) throws ContratoNotValidException,
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
