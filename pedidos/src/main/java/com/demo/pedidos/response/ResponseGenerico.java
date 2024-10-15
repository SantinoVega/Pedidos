package com.demo.pedidos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Clase funciona como un response generico para el API Pedidos.
 *
 * @param <T> Tipo generico de datos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseGenerico<T> {
    private String message;
    private boolean success;
    private T data;

    public ResponseEntity<ResponseGenerico<T>> success(){
        return new ResponseEntity<>(this, HttpStatus.OK);
    }

    public ResponseEntity<ResponseGenerico<T>> success(HttpStatus status) {
        return new ResponseEntity<>(this, status);
    }
}
