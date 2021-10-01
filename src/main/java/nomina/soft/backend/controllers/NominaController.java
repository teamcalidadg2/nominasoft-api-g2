package nomina.soft.backend.controllers;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotFoundException;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.services.NominaService;

@Controller
@RequestMapping("/nomina")
public class NominaController { 
    private NominaService nominaService;

    @Autowired
    public  NominaController(NominaService nominaService){
        super();
        this.nominaService = nominaService;
    }

	@GetMapping("/listar")
    public ResponseEntity<List<NominaModel>> obtenerNominas(){
        List<NominaModel> lista = nominaService.getAll();
        return new ResponseEntity<>(lista,OK);
    }
	
	@GetMapping("/listar/descripcion/{descripcion}")
    public ResponseEntity<List<NominaModel>> obtenerNominasPorDescripcion(@PathVariable("descripcion") String descripcion) throws NominaNotFoundException{
        List<NominaModel> lista = nominaService.getAllByDescripcion(descripcion);
        return new ResponseEntity<>(lista,OK);
    }

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<NominaModel> getNomina(@PathVariable("id") Long id) throws NominaNotFoundException {
        NominaModel nomina = nominaService.buscarPorId(id);
        return new ResponseEntity<>(nomina,OK);

    }
    
    @PostMapping("/generar")
    public ResponseEntity<NominaModel> generate(@RequestBody NominaDto nominaDto) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, PeriodoNominaNotFoundException {
        NominaModel nomina = nominaService.generarNomina(nominaDto);
        return new ResponseEntity<>(nomina,OK);
    }
    

    @PostMapping("/guardar")
    public ResponseEntity<NominaModel> save(@RequestBody NominaDto nominaDto) {
        NominaModel nomina = nominaService.guardarNomina(nominaDto);
        return new ResponseEntity<>(nomina,OK);
    }
}  
