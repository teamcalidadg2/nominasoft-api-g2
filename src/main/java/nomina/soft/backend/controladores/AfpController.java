package nomina.soft.backend.controladores;

import static nomina.soft.backend.constantes.AfpImplConstant.AFP_ELIMINADA;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import nomina.soft.backend.dto.AfpDto;
import nomina.soft.backend.excepciones.clases.AfpExistsException;
import nomina.soft.backend.excepciones.clases.AfpNotFoundException;
import nomina.soft.backend.models.Afp;
import nomina.soft.backend.models.HttpResponse;
import nomina.soft.backend.servicios.declaracion.ServicioAfp;

@Controller
@RequestMapping("/afp")
public class AfpController {

    private ServicioAfp servicioAfp;

    @Autowired
    public AfpController(ServicioAfp servicioAfp) {
        super();
        this.servicioAfp = servicioAfp;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Afp>> obtenerTodo() {
        List<Afp> listaAfp = servicioAfp.obtenerListaAfp();
        return new ResponseEntity<>(listaAfp, OK);
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<Afp> obtenerAfpPorNombre(@PathVariable("nombreAfp") String nombreAfp)
            throws AfpNotFoundException {
        Afp afpEncontrada = servicioAfp.buscarAfpPorNombre(nombreAfp);
        return new ResponseEntity<>(afpEncontrada, OK);
    }

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<Afp> obtenerAfpPorId(@PathVariable("idAfp") String idAfp) throws AfpNotFoundException {
        Afp afpEncontrada = servicioAfp.buscarAfpPorId(idAfp);
        return new ResponseEntity<>(afpEncontrada, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<Afp> crearNuevaAfp(@RequestBody AfpDto dtoAfp)
            throws AfpNotFoundException, AfpExistsException {
        Afp nuevaAfp = servicioAfp.guardarNuevaAfp(dtoAfp);
        return new ResponseEntity<>(nuevaAfp, OK);
    }

    @PostMapping("/editar")
    public ResponseEntity<Afp> actualizarAfp(@RequestParam("nombreActual") String nombreActual,
            @RequestParam("nuevoNombre") String nuevoNombre, @RequestParam("descuentoActual") int descuentoActual,
            @RequestParam("nuevoDescuento") int nuevoDescuento) throws AfpNotFoundException, AfpExistsException {
        Afp afpActualizada = servicioAfp.actualizarAfp(nombreActual, nuevoNombre, descuentoActual, nuevoDescuento);
        return new ResponseEntity<>(afpActualizada, OK);
    }

    @DeleteMapping("/eliminar/{nombre}")
    public ResponseEntity<HttpResponse> eliminarAfp(@PathVariable("nombreAfp") String nombreAfp) throws AfpNotFoundException {
        servicioAfp.eliminarAfp(nombreAfp);
        return response(OK, AFP_ELIMINADA);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
                httpStatus);
    }

}
