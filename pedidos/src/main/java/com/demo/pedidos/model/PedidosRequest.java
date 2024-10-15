package com.demo.pedidos.model;

import com.demo.pedidos.enums.EstadoEnum;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Digits;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Esta clase requesenta el request para crear o actualizar un Pedido.
 * Contiene los atributos y algunas validaciones para un Pedido.
 */
@Data
public class PedidosRequest {
    private Integer id;
    @NotNull(message = "El usuarioId NO puede ser nulo y debe ser numerico sin decimales")
    @Min(0)
    @Digits(integer = 8, fraction = 0)
    private Integer usuarioId;
    @Enumerated
    private EstadoEnum estado;
    @NotNull(message = "El Total no puede ser nulo y debe ser numerico con dos decimales")
    @Digits(integer = 8, fraction = 2)
    private Double total;
}
