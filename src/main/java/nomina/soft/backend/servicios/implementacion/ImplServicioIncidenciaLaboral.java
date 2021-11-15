package nomina.soft.backend.servicios.implementacion;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dao.IncidenciaLaboralDao;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.models.Contrato;
import nomina.soft.backend.models.IncidenciaLaboral;
import nomina.soft.backend.models.PeriodoNomina;
import nomina.soft.backend.servicios.declaracion.ServicioIncidenciaLaboral;

@Service
@Transactional
public class ImplServicioIncidenciaLaboral implements ServicioIncidenciaLaboral {

	private IncidenciaLaboralDao repositorioIncidenciaLaboral;
	private ImplServicioContrato servicioContrato;

	@Autowired
	public ImplServicioIncidenciaLaboral(IncidenciaLaboralDao repositorioIncidenciaLaboral,
			ImplServicioContrato servicioContrato) {
		super();
		this.repositorioIncidenciaLaboral = repositorioIncidenciaLaboral;
		this.servicioContrato = servicioContrato;
	}

	@Override
	public List<IncidenciaLaboral> buscarIncidenciasPorDni(String dniEmpleado)
			throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException {
		Contrato contratoVigente = this.servicioContrato.buscarContratoVigentePorDni(dniEmpleado);
		if (contratoVigente != null)
			return contratoVigente.getIncidenciasLaborales();
		return Collections.emptyList();
	}

	@Override
	public IncidenciaLaboral reportarHoraFaltante(String dniEmpleado)
			throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException {
		Contrato contratoVigente = this.servicioContrato.buscarContratoVigentePorDni(dniEmpleado);
		if (contratoVigente != null) {
			IncidenciaLaboral incidenciaLaboralVigente = obtenerIncidenciaLaboralVigente(contratoVigente);
			if (incidenciaLaboralVigente != null) {
				incidenciaLaboralVigente.reportarHoraFaltante();
				this.repositorioIncidenciaLaboral.save(incidenciaLaboralVigente);
				return incidenciaLaboralVigente;
			}
		}
		return null;
	}

	private IncidenciaLaboral obtenerIncidenciaLaboralVigente(Contrato contrato) {
		List<IncidenciaLaboral> incidenciasLaborales = contrato.getIncidenciasLaborales();
		for (IncidenciaLaboral incidenciaLaboral : incidenciasLaborales) {
			if (validarPeriodoNomina(incidenciaLaboral.getPeriodoNomina()))
				return incidenciaLaboral;
		}
		return null;
	}

	private boolean validarPeriodoNomina(PeriodoNomina periodoNomina) {
		return periodoNomina == this.servicioContrato.obtenerPeriodoNominaActual();
	}

	@Override
	public IncidenciaLaboral reportarHoraExtra(String dniEmpleado)
			throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException {
		Contrato contratoVigente = this.servicioContrato.buscarContratoVigentePorDni(dniEmpleado);
		if (contratoVigente != null) {
			IncidenciaLaboral incidenciaLaboralVigente = obtenerIncidenciaLaboralVigente(contratoVigente);
			if (incidenciaLaboralVigente != null) {
				incidenciaLaboralVigente.reportarHoraExtra();
				this.repositorioIncidenciaLaboral.save(incidenciaLaboralVigente);
				return incidenciaLaboralVigente;
			}
		}
		return null;
	}

}
