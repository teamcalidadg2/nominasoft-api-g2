package nomina.soft.backend.controllers;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.services.IncidenciaLaboralService;

@Controller
@RequestMapping("/incidenciaLaboral")
public class IncidenciaLaboralController {

	private IncidenciaLaboralService incidenciaLaboralService;

    @Autowired
    public IncidenciaLaboralController(IncidenciaLaboralService incidenciaLaboralService) {
		super();
		this.incidenciaLaboralService = incidenciaLaboralService;
	}
    
	@GetMapping("/listar/{dni}")
    public ResponseEntity<List<IncidenciaLaboralModel>> getIncidenciasLaboralByDni(@PathVariable("dni") String dni) throws EmpleadoNotFoundException {
		List<IncidenciaLaboralModel> incidenciasLaborales = incidenciaLaboralService.buscarIncidenciasPorDni(dni);
        return new ResponseEntity<>(incidenciasLaborales, OK);
    }

    @PostMapping("/reportarHoraFaltante/{dni}")
    public ResponseEntity<IncidenciaLaboralModel> saveHoraFaltante(@PathVariable("dni") String dni) throws EmpleadoNotFoundException{
    	IncidenciaLaboralModel horaFaltante = incidenciaLaboralService.reportarHoraFaltante(dni);
        return new ResponseEntity<>(horaFaltante, OK);
    }
    
    @PostMapping("/reportarHoraExtra/{dni}")
    public ResponseEntity<IncidenciaLaboralModel> saveHoraExtra(@PathVariable("dni") String dni) throws EmpleadoNotFoundException{
    	IncidenciaLaboralModel horaExtra = incidenciaLaboralService.reportarHoraExtra(dni);
        return new ResponseEntity<>(horaExtra, OK);
    }
	
}
