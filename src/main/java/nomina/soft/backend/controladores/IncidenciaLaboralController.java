package nomina.soft.backend.controladores;

import static org.springframework.http.HttpStatus.OK;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import nomina.soft.backend.entidades.IncidenciaLaboral;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.servicios.declaracion.ServicioIncidenciaLaboral;

@Controller
@RequestMapping("/incidenciaLaboral")
public class IncidenciaLaboralController {

	private ServicioIncidenciaLaboral incidenciaLaboralService;

    @Autowired
    public IncidenciaLaboralController(ServicioIncidenciaLaboral incidenciaLaboralService) {
		super();
		this.incidenciaLaboralService = incidenciaLaboralService;
	}
    
	@GetMapping("/listar/{dni}")
    public ResponseEntity<List<IncidenciaLaboral>> getIncidenciasLaboralByDni(@PathVariable("dni") String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException, ParseException {
		List<IncidenciaLaboral> incidenciasLaborales = incidenciaLaboralService.buscarIncidenciasPorDni(dni);
        return new ResponseEntity<>(incidenciasLaborales, OK);
    }

    @PostMapping("/reportarHoraFaltante/{dni}")
    public ResponseEntity<IncidenciaLaboral> saveHoraFaltante(@PathVariable("dni") String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException, ParseException{
    	IncidenciaLaboral horaFaltante = incidenciaLaboralService.reportarHoraFaltante(dni);
        return new ResponseEntity<>(horaFaltante, OK);
    }
    
    @PostMapping("/reportarHoraExtra/{dni}")
    public ResponseEntity<IncidenciaLaboral> saveHoraExtra(@PathVariable("dni") String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException, ParseException{
    	IncidenciaLaboral horaExtra = incidenciaLaboralService.reportarHoraExtra(dni);
        return new ResponseEntity<>(horaExtra, OK);
    }
	
}
