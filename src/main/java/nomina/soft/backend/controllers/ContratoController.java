package nomina.soft.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.services.ContratoService;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    ContratoService contratoService;

    @PostMapping("/guardar")
    public ResponseEntity<ContratoModel> create(@RequestBody ContratoDto contratoDto) {
        ContratoModel contrato =  contratoService.guardarContrato(contratoDto);
        return new ResponseEntity<>(contrato, OK);
    }

    
}
