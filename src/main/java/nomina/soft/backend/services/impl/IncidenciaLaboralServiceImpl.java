package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND_BY_DNI;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.repositories.IncidenciaLaboralRepository;
import nomina.soft.backend.services.IncidenciaLaboralService;

@Service
@Transactional
public class IncidenciaLaboralServiceImpl implements IncidenciaLaboralService{

	private IncidenciaLaboralRepository incidenciaLaboralRepository;
	private EmpleadoRepository empleadoRepository;

	@Autowired
	public IncidenciaLaboralServiceImpl(IncidenciaLaboralRepository incidenciaLaboralRepository, EmpleadoRepository empleadoRepository) {
		super();
		this.incidenciaLaboralRepository = incidenciaLaboralRepository;
		this.empleadoRepository = empleadoRepository;
	}

	@Override
	public List<IncidenciaLaboralModel> buscarIncidenciasPorDni(String dni) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = this.empleadoRepository.findByDni(dni);
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + dni);
		}
		
		//List<IncidenciaLaboralModel> lista = incidenciaLaboralRepository.findAllByEmpleado(empleado);
		return null;
	}

	@Override
	public IncidenciaLaboralModel reportarHoraFaltante(String dni) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = this.empleadoRepository.findByDni(dni);
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + dni);
		}
		/*IncidenciaLaboralModel incidenciaLaboral = incidenciaLaboralRepository.findByEmpleado(empleado);
		incidenciaLaboral.reportarHoraFaltante();
		this.incidenciaLaboralRepository.save(incidenciaLaboral);*/
		return null;
	}

	@Override
	public IncidenciaLaboralModel reportarHoraExtra(String dni) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = this.empleadoRepository.findByDni(dni);
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + dni);
		}
		/*IncidenciaLaboralModel incidenciaLaboral = incidenciaLaboralRepository.findByEmpleado(empleado);
		incidenciaLaboral.reportarHoraExtra();
		this.incidenciaLaboralRepository.save(incidenciaLaboral);*/
		return null;
	}
	
	
	
}
