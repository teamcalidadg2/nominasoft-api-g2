package nomina.soft.backend.controllers;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.services.EmpleadoService;
@Controller
@RequestMapping("/empleado")
public class EmpleadoController {
    @Autowired
    EmpleadoService empleadoService;

    @GetMapping("/listar")
    public ResponseEntity<EmpleadoModel> obtenerVentas(){
        //return (ArrayList<EmpleadoModel>) empleadoService.getAll();
        List<EmpleadoModel> lista = empleadoService.getAll();
        return new ResponseEntity(lista, HttpStatus.OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> create(@RequestBody EmpleadoDto empleadoDto) {
        empleadoService.guardarEmpleado(empleadoDto);
        return new ResponseEntity(HttpStatus.OK);
    }
}
