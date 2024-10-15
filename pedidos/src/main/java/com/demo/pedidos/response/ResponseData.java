package com.demo.pedidos.response;

import com.demo.pedidos.dto.PedidosDTO;
import lombok.Data;

@Data
public class ResponseData {
    private PedidosDTO pedido;
    private UsuarioResponse usuario;
}
