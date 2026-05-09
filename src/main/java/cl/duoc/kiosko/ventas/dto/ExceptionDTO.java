package cl.duoc.kiosko.ventas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ExceptionDTO {
    @NotNull
    private int code;
    @NotBlank
    private String type;
    @NotBlank
    private String date;
    @NotBlank
    private String message;
    //Funciona como plantilla de errores, muestra el codigo, el porqué, la fecha y un mensaje
    public ExceptionDTO(HttpStatus httpStatus, Exception exception) {
        this.code = httpStatus.value();
        this.type = httpStatus.getReasonPhrase();
        this.date = (new Date()).toString();
        this.message = exception.getMessage();
    }

}
