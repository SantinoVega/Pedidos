package com.demo.pedidos.exception;

import com.demo.pedidos.response.ResponseGenerico;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLDataException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseGenerico<?>> validacionDatos(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(getErrorsMap(errors), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseGenerico<?>> validacionDatos(HttpMessageNotReadableException e) {
        String errorDetails = "";
        ResponseGenerico<?> response = null;
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ifx = (InvalidFormatException) e.getCause();
            if (ifx.getTargetType()!=null && ifx.getTargetType().isEnum()) {
                errorDetails = String.format("Valor del Enum invalido: '%s' para el campo: '%s'. El valor debe ser alguno de los siguientes: %s.",
                        ifx.getValue(), ifx.getPath().get(ifx.getPath().size()-1).getFieldName(), Arrays.toString(ifx.getTargetType().getEnumConstants()));
            }
            else if(ifx.getTargetType() != null && ifx.getTargetType().equals(Integer.class)){
                errorDetails = String.format("Valor invalido: '%s' para el campo: '%s'. El valor debe ser numerico",
                        ifx.getValue(), ifx.getPath().get(ifx.getPath().size()-1).getFieldName());

            }
            response = new ResponseGenerico<>(errorDetails,Boolean.FALSE,null);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ResponseGenerico<?> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("atributos", errors);
        return new ResponseGenerico<>("Favor de validar los datos.",Boolean.FALSE,errorResponse);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> integridadDatos(HttpRequestMethodNotSupportedException e) {
        log.error("Error en la aplicacion: {}", e.getMessage(), e);
        ResponseGenerico<?> response = new ResponseGenerico<>(e.getMessage(),Boolean.FALSE,null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(SQLDataException.class)
    public ResponseEntity<?> sqlData(SQLDataException e) {
        log.error("Error en la aplicacion: {}", e.getMessage(), e);
        ResponseGenerico<?> response = new ResponseGenerico<>(e.getMessage(),Boolean.TRUE,null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<?> all(Exception e, WebRequest request) {
        log.error("Error en la aplicacion: {}", e.getMessage(), e);
        ResponseGenerico<?> response = new ResponseGenerico<>("Internal Server Error",Boolean.FALSE,null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
