package com.demo.pedidos.entity;

import com.demo.pedidos.enums.EstadoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Esta clase representa la entidad PedidosEntity en la base de datos. Es utilizada para guardar la informacion de los pedidos.
 *
 * @author Daniel Ivan Martinez R.
 * @since 1.0
 */
@Entity
@Table(name = "pedidos")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PedidosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoEnum estado;

    @Column(name = "total")
    private Double total;
}
