package cl.duoc.kiosko.ventas.Exception;

import cl.duoc.kiosko.ventas.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //NOT FOUND
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionDTO> handleNotFound(NoSuchElementException ex) {
        ExceptionDTO exceptionDTO= new ExceptionDTO(HttpStatus.NOT_FOUND,ex);
        return new ResponseEntity<>(exceptionDTO,HttpStatus.NOT_FOUND);
    }
    //BAD REQUEST (cuando falla una validación @Valid del request)
    //Sin este handler, el catch-all de abajo respondía 500 en vez de 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidation(MethodArgumentNotValidException ex) {
        //Juntamos todos los mensajes de los campos que fallaron en un solo texto legible
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(" | "));
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST, mensaje);
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }
    //CONFLICT (Ej: intentar borrar una venta que tiene detalles si no hay cascada)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ExceptionDTO> handleConflict(SQLIntegrityConstraintViolationException ex) {
        ExceptionDTO exceptionDTO= new ExceptionDTO(HttpStatus.CONFLICT,ex);
        return new ResponseEntity<>(exceptionDTO,HttpStatus.CONFLICT);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex){
        //si el parámetro a evaluar es parte de la configuración de springdoc, ignóralo
        if(ex.getParameter()!= null && ex.getParameter().getParameterType().getName().contains("springdoc") ){
            return null;
        }
        //Es un error del cliente (ej: mandar "abc" donde va un número), corresponde 400 y no 500
        ExceptionDTO exceptionDTO= new ExceptionDTO(HttpStatus.BAD_REQUEST,ex);
        return new ResponseEntity<>(exceptionDTO,HttpStatus.BAD_REQUEST);

    }

    //Es un catch all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleAllException(Exception ex) {
        ExceptionDTO exceptionDTO= new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR,ex);
        return new ResponseEntity<>(exceptionDTO,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
