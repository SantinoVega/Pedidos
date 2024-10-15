package com.demo.pedidos.service;

import com.demo.pedidos.dto.PedidosDTO;
import com.demo.pedidos.model.PedidosRequest;

import java.sql.SQLDataException;
import java.util.List;

public interface IPedidosService {
    PedidosRequest save(PedidosRequest request) throws Exception;
    PedidosDTO pedidosGetById(Integer id) throws SQLDataException;
    List<PedidosDTO> getAll();
    PedidosRequest update(PedidosRequest pedido) throws SQLDataException;
    Integer deleteById(Integer id);
}
