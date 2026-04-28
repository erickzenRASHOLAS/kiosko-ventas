package cl.duoc.kiosko.ventas.Exception;

import cl.duoc.kiosko.ventas.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //NOT FOUND
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionDTO> handleNotFound(NoSuchElementException ex) {
        ExceptionDTO exceptionDTO= new ExceptionDTO(HttpStatus.NOT_FOUND,ex);
        return new ResponseEntity<>(exceptionDTO,HttpStatus.NOT_FOUND);
    }
    //BAD REQUEST
    //CONFLICT (Ej: intentar borrar una venta que tiene detalles si no hay cascada)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ExceptionDTO> handleConflict(SQLIntegrityConstraintViolationException ex) {
        ExceptionDTO exceptionDTO= new ExceptionDTO(HttpStatus.CONFLICT,ex);
        return new ResponseEntity<>(exceptionDTO,HttpStatus.CONFLICT);
    }
    //Es un catch all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleAllException(Exception ex) {
        ExceptionDTO exceptionDTO= new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR,ex);
        return new ResponseEntity<>(exceptionDTO,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
