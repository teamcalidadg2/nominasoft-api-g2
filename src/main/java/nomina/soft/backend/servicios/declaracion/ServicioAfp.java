package nomina.soft.backend.servicios.declaracion;

import java.util.List;

import nomina.soft.backend.dto.AfpDto;
import nomina.soft.backend.entidades.Afp;
import nomina.soft.backend.excepciones.clases.AfpExistsException;
import nomina.soft.backend.excepciones.clases.AfpNotFoundException;

public interface ServicioAfp {

	public List<Afp> obtenerListaAfp();

	public Afp buscarAfpPorId(String idAfp) throws AfpNotFoundException;

	public Afp buscarAfpPorNombre(String nombreAfp) throws AfpNotFoundException;

	public Afp guardarNuevaAfp(AfpDto dtoAfp) throws AfpNotFoundException, AfpExistsException;

	public Afp actualizarAfp(String nombreActual, String nuevoNombre, float descuentoActual, float nuevoDescuento)
			throws AfpNotFoundException, AfpExistsException;

	public void eliminarAfp(String nombreAfp) throws AfpNotFoundException;

}
