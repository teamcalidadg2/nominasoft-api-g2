package nomina.soft.backend.Servicios.Implementacion;

import static nomina.soft.backend.Constantes.AfpImplConstant.AFP_CON_DESCUENTO_YA_EXISTENTE;
import static nomina.soft.backend.Constantes.AfpImplConstant.AFP_CON_NOMBRE_YA_EXISTENTE;
import static nomina.soft.backend.Constantes.AfpImplConstant.AFP_NO_ENCONTRADO_POR_DESCUENTO;
import static nomina.soft.backend.Constantes.AfpImplConstant.AFP_NO_ENCONTRADO_POR_ID;
import static nomina.soft.backend.Constantes.AfpImplConstant.AFP_NO_ENCONTRADO_POR_NOMBRE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.Data_Access_Object.DAO_Afp;
import nomina.soft.backend.Data_Transfer_Object.DTO_Afp;
import nomina.soft.backend.Entidades.Afp;
import nomina.soft.backend.Excepciones.Clases.AfpExistsException;
import nomina.soft.backend.Excepciones.Clases.AfpNotFoundException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_Afp;

@Service
@Transactional
public class Impl_Servicio_Afp implements Servicio_Afp {

	private DAO_Afp respositorioAfp;

	@Autowired
	public Impl_Servicio_Afp(DAO_Afp respositorioAfp) {
		super();
		this.respositorioAfp = respositorioAfp;
	}

	@Override
	public List<Afp> obtenerListaAfp() {
		return respositorioAfp.findAll();
	}

	@Override
	public Afp buscarAfpPorNombre(String nombre) throws AfpNotFoundException {
		Afp afpEncontrado = respositorioAfp.findByNombre(nombre);
		if (afpEncontrado == null)
			throw new AfpNotFoundException(AFP_NO_ENCONTRADO_POR_NOMBRE);
		return afpEncontrado;
	}

	@Override
	public Afp buscarAfpPorId(String id) throws AfpNotFoundException {
		Afp afpEncontrado = respositorioAfp.findByIdAfp(Long.parseLong(id));
		if (afpEncontrado == null)
			throw new AfpNotFoundException(AFP_NO_ENCONTRADO_POR_ID);
		return afpEncontrado;
	}

	@Override
	public Afp guardarNuevaAfp(DTO_Afp dtoAfp) throws AfpNotFoundException, AfpExistsException {
		Afp nuevoAfp = new Afp();
		validarNuevoNombre(EMPTY, dtoAfp.getNombre());
		validarNuevoDescuento(0.0f, dtoAfp.getPorcentajeDescuento());

		nuevoAfp.setNombre(dtoAfp.getNombre());
		nuevoAfp.setPorcentajeDescuento(dtoAfp.getPorcentajeDescuento());
		respositorioAfp.save(nuevoAfp);
		return nuevoAfp;
	}

	@Override
	public Afp actualizarAfp(String actualNombre, String nuevoNombre, float actualDescuento, float nuevodescuento)
			throws AfpNotFoundException, AfpExistsException {
		Afp afpExistente = validarNuevoNombre(actualNombre, nuevoNombre);
		if (afpExistente != null) {
			validarNuevoDescuento(actualDescuento, nuevodescuento);
			afpExistente.setNombre(nuevoNombre);
			afpExistente.setPorcentajeDescuento(nuevodescuento);
			respositorioAfp.save(afpExistente);
		}
		return afpExistente;
	}

	@Override
	public void eliminarAfp(String nombreAfp) throws AfpNotFoundException {
		Afp afpEncontrado = respositorioAfp.findByNombre(nombreAfp);
		if (afpEncontrado == null) {
			throw new AfpNotFoundException(AFP_NO_ENCONTRADO_POR_NOMBRE);
		} else {
			respositorioAfp.deleteById(afpEncontrado.getIdAfp());
		}
	}

	private Afp validarNuevoNombre(String nombreActual, String nuevoNombre)
			throws AfpNotFoundException, AfpExistsException {
		Afp afpConNuevoNombre = respositorioAfp.findByNombre(nuevoNombre);
		if (StringUtils.isNotBlank(nombreActual)) {
			Afp afpExistente = respositorioAfp.findByNombre(nombreActual);
			if (afpExistente == null) {
				throw new AfpNotFoundException(AFP_NO_ENCONTRADO_POR_NOMBRE);
			}
			if (afpConNuevoNombre != null && (!(afpExistente.getIdAfp().equals(afpConNuevoNombre.getIdAfp())))) {
				throw new AfpExistsException(AFP_CON_NOMBRE_YA_EXISTENTE);
			}
			return afpExistente;
		} else {
			if (afpConNuevoNombre != null) {
				throw new AfpExistsException(AFP_CON_NOMBRE_YA_EXISTENTE);
			}
			return null;
		}
	}

	private Afp validarNuevoDescuento(float descuentoActual, float nuevoDescuento)
			throws AfpNotFoundException, AfpExistsException {
		Afp afpConNuevoDescuento = respositorioAfp.findByPorcentajeDescuento(nuevoDescuento);
		if (descuentoActual != 0.0f) {
			Afp afpExistente = respositorioAfp.findByPorcentajeDescuento(descuentoActual);
			if (afpExistente == null) {
				throw new AfpNotFoundException(AFP_NO_ENCONTRADO_POR_DESCUENTO);
			}
			if (afpConNuevoDescuento != null && (!(afpExistente.getIdAfp().equals(afpConNuevoDescuento.getIdAfp())))) {
				throw new AfpExistsException(AFP_CON_DESCUENTO_YA_EXISTENTE);
			}
			return afpExistente;
		} else {
			if (afpConNuevoDescuento != null) {
				throw new AfpExistsException(AFP_CON_DESCUENTO_YA_EXISTENTE);
			}
			return null;
		}
	}

}
