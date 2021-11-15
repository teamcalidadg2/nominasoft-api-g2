package nomina.soft.backend.Servicios.Declaracion;

import java.util.List;

import nomina.soft.backend.Data_Transfer_Object.DTO_Afp;
import nomina.soft.backend.Entidades.Afp;
import nomina.soft.backend.Excepciones.Clases.AfpExistsException;
import nomina.soft.backend.Excepciones.Clases.AfpNotFoundException;

public interface Servicio_Afp {

	public List<Afp> obtenerListaAfp();

	public Afp buscarAfpPorId(String idAfp) throws AfpNotFoundException;

	public Afp buscarAfpPorNombre(String nombreAfp) throws AfpNotFoundException;

	public Afp guardarNuevaAfp(DTO_Afp dtoAfp) throws AfpNotFoundException, AfpExistsException;

	public Afp actualizarAfp(String nombreActual, String nuevoNombre, float descuentoActual, float nuevoDescuento)
			throws AfpNotFoundException, AfpExistsException;

	public void eliminarAfp(String nombreAfp) throws AfpNotFoundException;

}
