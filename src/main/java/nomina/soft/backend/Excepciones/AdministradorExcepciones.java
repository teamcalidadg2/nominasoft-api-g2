package nomina.soft.backend.excepciones;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.IOException;
import java.util.Objects;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nomina.soft.backend.excepciones.clases.AfpExistsException;
import nomina.soft.backend.excepciones.clases.AfpNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoExistsException;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoExistsException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.excepciones.clases.NominaExistsException;
import nomina.soft.backend.excepciones.clases.NominaNotFoundException;
import nomina.soft.backend.excepciones.clases.NominaNotValidException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaExistsException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotValidException;
import nomina.soft.backend.models.HttpResponse;


@RestControllerAdvice
public class AdministradorExcepciones implements ErrorController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String METHOD_IS_NOT_ALLOWED = "Este método de solicitud no está permitido en este punto final. Envíe una solicitud '% s'";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Se produjo un error al procesar la solicitud";
    private static final String ERROR_PROCESSING_FILE = "Se produjo un error al procesar el archivo";
    public static final String ERROR_PATH = "/error";

    
    @ExceptionHandler(EmpleadoExistsException.class)
    public ResponseEntity<HttpResponse> empleadoExistsException(EmpleadoExistsException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(EmpleadoNotFoundException.class)
    public ResponseEntity<HttpResponse> empleadoNotFoundException(EmpleadoNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmpleadoNotValidException.class)
    public ResponseEntity<HttpResponse> empleadoNotValidException(EmpleadoNotValidException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(AfpExistsException.class)
    public ResponseEntity<HttpResponse> afpExistsException(AfpExistsException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(AfpNotFoundException.class)
    public ResponseEntity<HttpResponse> afpNotFoundException(AfpNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(ContratoExistsException.class)
    public ResponseEntity<HttpResponse> contratoExistsException(ContratoExistsException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(ContratoNotFoundException.class)
    public ResponseEntity<HttpResponse> contratoNotFoundException(ContratoNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(ContratoNotValidException.class)
    public ResponseEntity<HttpResponse> contratoNotValidException(ContratoNotValidException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(PeriodoNominaExistsException.class)
    public ResponseEntity<HttpResponse> periodoNominaExistsException(PeriodoNominaExistsException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(PeriodoNominaNotFoundException.class)
    public ResponseEntity<HttpResponse> periodoNominaNotFoundException(PeriodoNominaNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(PeriodoNominaNotValidException.class)
    public ResponseEntity<HttpResponse> periodoNominaNotValidException(PeriodoNominaNotValidException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NominaExistsException.class)
    public ResponseEntity<HttpResponse> nominaExistsException(NominaExistsException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(NominaNotFoundException.class)
    public ResponseEntity<HttpResponse> nominaNotFoundException(NominaNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(NominaNotValidException.class)
    public ResponseEntity<HttpResponse> nominaNotValidException(NominaNotValidException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }


    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }


    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, "No hay mapeo para esta URL");
    }


    public String getErrorPath() {
        return ERROR_PATH;
    }
}
