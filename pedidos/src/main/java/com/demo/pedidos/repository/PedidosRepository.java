package com.demo.pedidos.repository;

import com.demo.pedidos.entity.PedidosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidosRepository extends JpaRepository<PedidosEntity,Integer> {
}
