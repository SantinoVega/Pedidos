package com.demo.pedidos.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UsuariosDTO {
    private int  id;
    private String nombre;
    private String email;
    private Date registrationDate;
}
