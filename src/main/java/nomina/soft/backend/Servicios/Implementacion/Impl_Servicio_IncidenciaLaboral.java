package nomina.soft.backend.Servicios.Implementacion;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.Data_Access_Object.DAO_IncidenciaLaboral;
import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Entidades.IncidenciaLaboral;
import nomina.soft.backend.Entidades.PeriodoNomina;
import nomina.soft.backend.Excepciones.Clases.ContratoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_IncidenciaLaboral;

@Service
@Transactional
public class Impl_Servicio_IncidenciaLaboral implements Servicio_IncidenciaLaboral {

	private DAO_IncidenciaLaboral repositorioIncidenciaLaboral;
	private Impl_Servicio_Contrato servicioContrato;

	@Autowired
	public Impl_Servicio_IncidenciaLaboral(DAO_IncidenciaLaboral repositorioIncidenciaLaboral,
			Impl_Servicio_Contrato servicioContrato) {
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
