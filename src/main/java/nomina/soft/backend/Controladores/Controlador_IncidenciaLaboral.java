package nomina.soft.backend.Controladores;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import nomina.soft.backend.Entidades.IncidenciaLaboral;
import nomina.soft.backend.Excepciones.Clases.ContratoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_IncidenciaLaboral;

@Controller
@RequestMapping("/incidenciaLaboral")
public class Controlador_IncidenciaLaboral {

	private Servicio_IncidenciaLaboral incidenciaLaboralService;

    @Autowired
    public Controlador_IncidenciaLaboral(Servicio_IncidenciaLaboral incidenciaLaboralService) {
		super();
		this.incidenciaLaboralService = incidenciaLaboralService;
	}
    
	@GetMapping("/listar/{dni}")
    public ResponseEntity<List<IncidenciaLaboral>> getIncidenciasLaboralByDni(@PathVariable("dni") String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException {
		List<IncidenciaLaboral> incidenciasLaborales = incidenciaLaboralService.buscarIncidenciasPorDni(dni);
        return new ResponseEntity<>(incidenciasLaborales, OK);
    }

    @PostMapping("/reportarHoraFaltante/{dni}")
    public ResponseEntity<IncidenciaLaboral> saveHoraFaltante(@PathVariable("dni") String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException{
    	IncidenciaLaboral horaFaltante = incidenciaLaboralService.reportarHoraFaltante(dni);
        return new ResponseEntity<>(horaFaltante, OK);
    }
    
    @PostMapping("/reportarHoraExtra/{dni}")
    public ResponseEntity<IncidenciaLaboral> saveHoraExtra(@PathVariable("dni") String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException{
    	IncidenciaLaboral horaExtra = incidenciaLaboralService.reportarHoraExtra(dni);
        return new ResponseEntity<>(horaExtra, OK);
    }
	
}
