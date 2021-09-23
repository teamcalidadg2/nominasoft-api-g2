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
import nomina.soft.backend.exception.domain.EmpleadoExistsException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.NominaExistsException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import nomina.soft.backend.models.HttpResponse;

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

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<NominaModel> getNomina(@PathVariable("id") int id) throws NominaNotFoundException {
        NominaModel nomina = nominaService.buscarPorId(id);
        return new ResponseEntity<>(nomina,OK);

    }

    @PostMapping("/guardar")
    public ResponseEntity<NominaModel> create(@RequestBody NominaDto nominaDto, int contrato_id) throws NominaNotFoundException, NominaNotValidException, NominaExistsException, ContratoNotFoundException, ContratoNotValidException {
        NominaModel nomina = nominaService.guardarNomina(nominaDto,contrato_id);
        return new ResponseEntity<>(nomina,OK);
    }
}  
