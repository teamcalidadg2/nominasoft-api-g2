package nomina.soft.backend.Servicios.Implementacion;

import static nomina.soft.backend.Constantes.NominaImplConstant.CONTRATOS_NO_ENCONTRADOS_PARA_PERIODO;
import static nomina.soft.backend.Constantes.NominaImplConstant.NOMINAS_NO_ENCONTRADAS_POR_DESCRIPCION;
import static nomina.soft.backend.Constantes.NominaImplConstant.NOMINA_NO_ENCONTRADA_POR_ID;
import static nomina.soft.backend.Constantes.NominaImplConstant.NOMINA_YA_CERRADA;
import static nomina.soft.backend.Constantes.PeriodoNominaImplConstant.PERIODO_NOMINA_NO_ENCONTRADO_POR_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.Data_Access_Object.DAO_Contrato;
import nomina.soft.backend.Data_Access_Object.DAO_Nomina;
import nomina.soft.backend.Data_Access_Object.DAO_PeriodoNomina;
import nomina.soft.backend.Data_Transfer_Object.DTO_Nomina;
import nomina.soft.backend.Entidades.BoletaDePago;
import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Entidades.IncidenciaLaboral;
import nomina.soft.backend.Entidades.Nomina;
import nomina.soft.backend.Entidades.PeriodoNomina;
import nomina.soft.backend.Excepciones.Clases.ContratoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.ContratoNotValidException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;
import nomina.soft.backend.Excepciones.Clases.NominaNotFoundException;
import nomina.soft.backend.Excepciones.Clases.NominaNotValidException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_Nomina;

@Service
@Transactional
public class Impl_Servicio_Nomina implements Servicio_Nomina {

	private DAO_Nomina repositorioNomina;
	private DAO_PeriodoNomina repositorioPeriodoNomina;
	private Impl_Servicio_BoletaDePago servicioBoletaDePago;
	private DAO_Contrato repositorioContrato;

	@Autowired
	public Impl_Servicio_Nomina(DAO_Nomina repositorioNomina, DAO_PeriodoNomina repositorioPeriodoNomina,
			Impl_Servicio_BoletaDePago servicioBoletaDePago, DAO_Contrato repositorioContrato) {
		super();
		this.repositorioNomina = repositorioNomina;
		this.repositorioPeriodoNomina = repositorioPeriodoNomina;
		this.servicioBoletaDePago = servicioBoletaDePago;
		this.repositorioContrato = repositorioContrato;
	}

	@Override
	public List<Nomina> obtenerTodas() {
		return this.repositorioNomina.findAll();
	}

	@Override
	public List<Nomina> obtenerTodasPorDescripcion(String descripcion) throws NominaNotFoundException {
		List<Nomina> lista = this.repositorioNomina.findByDescripcionContains(descripcion);
		if (lista == null)
			throw new NominaNotFoundException(NOMINAS_NO_ENCONTRADAS_POR_DESCRIPCION);
		return lista;
	}

	@Override
	public Nomina buscarNominaPorId(String idNomina)
			throws NominaNotFoundException, NumberFormatException, NominaNotValidException {
		Nomina nominaEncontrada = new Nomina();
		if (nominaEncontrada.identificadorValido(idNomina)) {
			nominaEncontrada = this.repositorioNomina.findByIdNomina(Long.parseLong(idNomina));
			if (nominaEncontrada == null)
				throw new NominaNotFoundException(NOMINA_NO_ENCONTRADA_POR_ID);
			else
				return nominaEncontrada;
		}
		return null;
	}

	@Override
	public List<BoletaDePago> guardarNuevaNomina(DTO_Nomina nominaDto)
			throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException,
			ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException {
		Nomina nuevaNomina = new Nomina();
		PeriodoNomina periodoNominaEncontrado = obtenerPeriodoNominaValido(nominaDto.getIdPeriodoNomina());
		nuevaNomina.setPeriodoNomina(periodoNominaEncontrado);
		nuevaNomina.setFecha(this.arreglarZonaHorariaFechaGeneracion(nominaDto.getFecha()));
		nuevaNomina.setDescripcion(nominaDto.getDescripcion());
		if (periodoNominaEncontrado != null && nuevaNomina.validarNomina(nuevaNomina)
				&& nuevaNomina.validarDescripcion(nuevaNomina.getDescripcion())) {
			this.repositorioNomina.save(nuevaNomina);
			nuevaNomina.setEstaCerrada(false);
			nuevaNomina.setBoletasDePago(guardarBoletasDePago(nuevaNomina));
			this.repositorioNomina.save(nuevaNomina);
			return nuevaNomina.getBoletasDePago();
		}
		return Collections.emptyList();
	}

	@Override
	public List<BoletaDePago> guardarNuevaNomina(Date fecha, String descripcion, String idPeriodoNomina)
			throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException,
			ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException {
		Nomina nuevaNomina = new Nomina();
		PeriodoNomina periodoNominaEncontrado = obtenerPeriodoNominaValido(idPeriodoNomina);
		nuevaNomina.setPeriodoNomina(periodoNominaEncontrado);
		nuevaNomina.setFecha(arreglarZonaHorariaFechaGeneracion(fecha));
		nuevaNomina.setDescripcion(descripcion);
		if (periodoNominaEncontrado != null && this.validarNuevaNomina(nuevaNomina)) {
			this.repositorioNomina.save(nuevaNomina);
			nuevaNomina.setEstaCerrada(false);
			nuevaNomina.setBoletasDePago(guardarBoletasDePago(nuevaNomina));
			this.repositorioNomina.save(nuevaNomina);
			return nuevaNomina.getBoletasDePago();
		}
		return Collections.emptyList();
	}

	private boolean validarNuevaNomina(Nomina nuevaNomina) throws ContratoNotValidException, NominaNotValidException {
		return nuevaNomina.validarNomina(nuevaNomina) && nuevaNomina.validarDescripcion(nuevaNomina.getDescripcion());
	}

	public Date arreglarZonaHorariaFechaGeneracion(Date fechaGeneracion) {
		fechaGeneracion.setMinutes(fechaGeneracion.getMinutes() + fechaGeneracion.getTimezoneOffset());
		return fechaGeneracion;
	}

	private List<BoletaDePago> guardarBoletasDePago(Nomina nuevaNomina)
			throws ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException {
		List<BoletaDePago> boletasDePagoGeneradas = new ArrayList<>();
		boolean noHayNingunContrato = true;
		if (nuevaNomina.validarNomina(nuevaNomina)) {
			List<Contrato> listaDeContratos = this.repositorioContrato.findAll();
			for (Contrato contrato : listaDeContratos) {
				List<IncidenciaLaboral> listaDeIncidenciasLaborales = contrato.getIncidenciasLaborales();
				IncidenciaLaboral incidenciaLaboralDelPeriodo = null;
				for (IncidenciaLaboral incidenciaLaboral : listaDeIncidenciasLaborales) {
					if (incidenciaLaboral.getPeriodoNomina() == nuevaNomina.getPeriodoNomina()) {
						incidenciaLaboralDelPeriodo = incidenciaLaboral;
						noHayNingunContrato = false;
					}
				}
				if (incidenciaLaboralDelPeriodo != null) {
					boletasDePagoGeneradas.add(this.servicioBoletaDePago.guardarBoletaDePago(contrato, nuevaNomina));
				}
			}
		}
		if (noHayNingunContrato)
			throw new NominaNotValidException(CONTRATOS_NO_ENCONTRADOS_PARA_PERIODO);
		return boletasDePagoGeneradas;
	}

	@Override
	public List<BoletaDePago> generarNuevaNomina(DTO_Nomina nominaDto)
			throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException,
			NominaNotValidException, PeriodoNominaNotFoundException, EmpleadoNotValidException {
		Nomina nuevaNomina = new Nomina();
		PeriodoNomina periodoNominaEncontrado = obtenerPeriodoNominaValido(nominaDto.getIdPeriodoNomina());
		nuevaNomina.setPeriodoNomina(periodoNominaEncontrado);
		nuevaNomina.setFecha(this.arreglarZonaHorariaFechaGeneracion(nominaDto.getFecha()));
		nuevaNomina.setDescripcion(nominaDto.getDescripcion());
		if (periodoNominaEncontrado != null && nuevaNomina.validarNomina(nuevaNomina)
				&& nuevaNomina.validarDescripcion(nuevaNomina.getDescripcion())) {
			nuevaNomina.setEstaCerrada(false);
			nuevaNomina.setBoletasDePago(generarBoletasDePago(nuevaNomina));
			return nuevaNomina.getBoletasDePago();
		}
		return Collections.emptyList();
	}

	@Override
	public List<BoletaDePago> generarNuevaNomina(Date fecha, String descripcion, String idPeriodoNomina)
			throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException,
			ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException {
		Nomina nuevaNomina = new Nomina();
		PeriodoNomina periodoNominaEncontrado = obtenerPeriodoNominaValido(idPeriodoNomina);
		nuevaNomina.setPeriodoNomina(periodoNominaEncontrado);
		nuevaNomina.setFecha(arreglarZonaHorariaFechaGeneracion(fecha));
		nuevaNomina.setDescripcion(descripcion);
		if (periodoNominaEncontrado != null && this.validarNuevaNomina(nuevaNomina)) {
			nuevaNomina.setEstaCerrada(false);
			nuevaNomina.setBoletasDePago(generarBoletasDePago(nuevaNomina));
			return nuevaNomina.getBoletasDePago();
		}
		return Collections.emptyList();
	}

	private List<BoletaDePago> generarBoletasDePago(Nomina nuevaNomina)
			throws NominaNotValidException, ContratoNotValidException {
		List<BoletaDePago> boletasDePagoGeneradas = new ArrayList<>();
		boolean noHayNingunContrato = true;
		if (nuevaNomina.validarNomina(nuevaNomina)) {
			List<Contrato> listaDeContratos = this.repositorioContrato.findAll();
			for (Contrato contrato : listaDeContratos) {
				List<IncidenciaLaboral> listaDeIncidenciasLaborales = contrato.getIncidenciasLaborales();
				IncidenciaLaboral incidenciaLaboralDelPeriodo = null;
				for (IncidenciaLaboral incidenciaLaboral : listaDeIncidenciasLaborales) {
					if (incidenciaLaboral.getPeriodoNomina() == nuevaNomina.getPeriodoNomina()) {
						incidenciaLaboralDelPeriodo = incidenciaLaboral;
						noHayNingunContrato = false;
					}
				}
				if (incidenciaLaboralDelPeriodo != null) {
					boletasDePagoGeneradas.add(this.servicioBoletaDePago.generarBoletaDePago(contrato, nuevaNomina));
				}
			}
		}
		if (noHayNingunContrato)
			throw new NominaNotValidException(CONTRATOS_NO_ENCONTRADOS_PARA_PERIODO);
		return boletasDePagoGeneradas;
	}

	@Override
	public Nomina cerrarNomina(String idNomina)
			throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException {
		Nomina nominaAEditar = obtenerNominaValida(idNomina);
		if (nominaAEditar != null) {
			if (Boolean.TRUE.equals(nominaAEditar.getEstaCerrada())) {
				throw new NominaNotValidException(NOMINA_YA_CERRADA);
			} else {
				nominaAEditar.setEstaCerrada(true);
				this.repositorioNomina.save(nominaAEditar);
			}
		}
		return nominaAEditar;
	}

	@Override
	public void eliminarNomina(String idNomina)
			throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException {
		Nomina nominaAEliminar = obtenerNominaValida(idNomina);
		if (nominaAEliminar != null) {
			if (Boolean.FALSE.equals(nominaAEliminar.getEstaCerrada())) {
				this.repositorioNomina.delete(nominaAEliminar);
			} else
				throw new NominaNotValidException(NOMINA_YA_CERRADA);
		}
	}

	private PeriodoNomina obtenerPeriodoNominaValido(String idPeriodoNomina)
			throws NumberFormatException, ContratoNotFoundException, NominaNotValidException {
		Nomina nominaTemporal = new Nomina();
		if (nominaTemporal.periodoDeNominaValido(idPeriodoNomina)) {
			PeriodoNomina periodoNominaEncontrado = this.repositorioPeriodoNomina
					.findByIdPeriodoNomina(Long.parseLong(idPeriodoNomina));
			if (periodoNominaEncontrado != null)
				return periodoNominaEncontrado;
			else
				throw new ContratoNotFoundException(PERIODO_NOMINA_NO_ENCONTRADO_POR_ID);
		}
		return null;
	}

	private Nomina obtenerNominaValida(String idNomina)
			throws NumberFormatException, ContratoNotFoundException, NominaNotValidException {
		Nomina nominaTemporal = new Nomina();
		if (nominaTemporal.identificadorValido(idNomina)) {
			nominaTemporal = this.repositorioNomina.findByIdNomina(Long.parseLong(idNomina));
			if (nominaTemporal != null)
				return nominaTemporal;
			else
				throw new ContratoNotFoundException(NOMINA_NO_ENCONTRADA_POR_ID);
		}
		return null;
	}

}
