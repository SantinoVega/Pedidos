package com.demo.pedidos.service.Impl;

import com.demo.pedidos.dto.PedidosDTO;
import com.demo.pedidos.dto.UsuariosDTO;
import com.demo.pedidos.entity.PedidosEntity;
import com.demo.pedidos.model.PedidosRequest;
import com.demo.pedidos.repository.PedidosRepository;
import com.demo.pedidos.response.ResponseData;
import com.demo.pedidos.response.ResponseGenerico;
import com.demo.pedidos.service.IPedidosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLDataException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * La clase PedidosService contiene los
 * metodos con la logica para hacer CRUD con datos del pedido.
 *
 * @version(1.0.0)
 * @author Daniel Ivan Martinez R.
 */
@Service
@Slf4j
public class PedidosService implements IPedidosService {

    @Value("${usuarios.api.url}")
    String usuariosApiUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PedidosRepository pedidosRepository;

    /**
     * Primero consulta al microservicio de "Usuarios" si existe el usuarioId, si es asi guarda un nuevo {@link PedidosEntity} en la base de datos, sino devuleve una exception.
     *
     * @param request El objeto {@link PedidosRequest} contiene la informacion necesaria para guaradar un nuevo pedido en BD.
     *
     * @return El mismo objeto {@link PedidosRequest} que es pasado como parametro. Con esto indicamos que se guardo con exito en BD.
     * @version(1.0.0)
     * @author Daniel Ivan Martinez R.
     */
    public PedidosRequest save(PedidosRequest request) throws SQLDataException {
        Boolean userExist = usuariosFindById(request.getUsuarioId());

        if( Boolean.TRUE.equals(userExist) ){
            PedidosEntity entity = PedidosEntity.builder()
                    .usuarioId(request.getUsuarioId())
                    .estado(request.getEstado())
                    .fechaCreacion(new Date())
                    .total(request.getTotal())
                    .build();
            pedidosRepository.save(entity);
        }else{
            throw new SQLDataException("No se encontro el usuario con id: " + request.getUsuarioId() + " en BD.");
        }

        return request;
    }

    /**
     * Este metodo regresa un obejto {@link PedidosDTO} con el {@code id} de la BD.
     *
     * @param id Es el identificador unico {@link PedidosEntity} que sera recibido.
     *
     * @return El objeto {@link PedidosDTO} contien los datos existentes en BD.
     * @version(1.0.0)
     * @author Daniel Ivan Martinez R.
     */
    public PedidosDTO pedidosGetById(Integer id) throws SQLDataException {
        Optional<PedidosEntity> entity = pedidosRepository.findById(id);
        if (entity.isPresent()) {
            return PedidosDTO.builder()
                    .id(entity.get().getId())
                    .usuarioId(entity.get().getUsuarioId())
                    .estado(entity.get().getEstado())
                    .fechaCreacion(entity.get().getFechaCreacion())
                    .total(entity.get().getTotal())
                    .build();
        }
        else{
            throw new SQLDataException("No se encontro el pedido con id: " + id + " en BD.");
        }
    }

    /**
     * Este metodo devuelve una lista de tipo {@link PedidosDTO} con todos los pedidos de la BD.
     *
     * @return devuelve una lista de tipo {@link PedidosDTO} con todos los pedidos de la BD.
     * @version(1.0.0)
     * @author Daniel Ivan Martinez R.
     */
    public List<PedidosDTO> getAll() {
        List<PedidosDTO> response;
        try{
            response = pedidosRepository.findAll().stream().map(x -> PedidosDTO.builder()
                    .id(x.getId())
                    .usuarioId(x.getUsuarioId())
                    .fechaCreacion(x.getFechaCreacion())
                    .estado(x.getEstado())
                    .total(x.getTotal())
                    .build()).toList();
            return  response;
        }
        catch(Exception e){
            log.error("Error al obtener los pedidos: ", e);
            throw e;
        }
    }

    /**
     * Este metodo actualiza un nuevo {@link PedidosRequest} en la base de datos.
     * Primero verifica que el pedido existe en BD, despues verifica que el usuario existe en BD.
     * a continuacion se procede a actualizar en BD. Devuelve mismo objeto request que indica que se actualizo correntamente en BD.
     *
     * @param pedido El objeto {@link PedidosRequest} que sera actualizado. Contiene la informacion necesaria para actualizar un pedido.
     *
     * @return El mismo objeto {@link PedidosRequest} que es pasado como parametro. Con esto indicamos que se actualizo con exito en BD.
     * @version(1.0.0)
     * @author Daniel Ivan Martinez R.
     */
    public PedidosRequest update(PedidosRequest pedido) throws SQLDataException {
        Boolean userExist;
        PedidosDTO pedidoExist = pedidosGetById(pedido.getId());

        if (pedidoExist != null ) {
            userExist = usuariosFindById(pedido.getUsuarioId());
        }else{
            throw new SQLDataException("Pedido no encontrado en BD.");
        }

        if( Boolean.TRUE.equals(userExist) ){
            PedidosEntity entity = setData(pedido, pedidoExist);
            pedidosRepository.save(entity);
        }else {
            throw new SQLDataException("Usuario no encontrado en BD.");
        }

        return pedido;
    }

    /**
     * Eliminar un pedido existente en la base de datos
     * @param id valor del id del pedido que se pretende eliminar en BD.
     *
     * @return Devuelve 1 si se elimino correctamente en BD, 0 si no lo encontro en la BD.
     * @version(1.0.0)
     * @author Daniel Ivan Martinez R.
     */
    public Integer deleteById(Integer id) {
        try {
            PedidosDTO pedido = pedidosGetById(id);
            if(pedido != null) {
                pedidosRepository.deleteById(id);
                return 1;
            }
            return 0;
        }catch (Exception e){
            log.error("Error al eliminar el pedido por Id: ", e);
            return 0;
        }
    }

    /**
     * Este metodo devuelve la informacion del usuario desde el miroservicio "Usuarios" utilizando el usuarioId.
     *
     * @param usuarioId EL identificador unico del usuario que se buscara en el microservicio "Usuario".
     *
     * @return A {@link UsuariosDTO} objeto que contiene la informacion del usuario.
     * @version(1.0.0)
     * @author Daniel Ivan Martinez R.
     */
    public Boolean usuariosFindById(Integer usuarioId) {
        String url = usuariosApiUrl + "/" + usuarioId;
        try {
            ResponseGenerico responseGenerico = restTemplate.getForEntity(url, ResponseGenerico.class).getBody();
            return responseGenerico != null && responseGenerico.getData() != null;
        }catch (ResourceAccessException e) {
            log.error("Error al conectar con el microservicio Usuarios: ", e);
            throw e;
        }
    }

    /**
     * Este metodo contiene un conjunto de datos de tipo {@link PedidosRequest} y {@link PedidosDTO} que se guardara
     * en un objeto de tipo {@link PedidosEntity}.
     *
     * @param update El objeto {@link PedidosRequest} contiene los datos con los que se actualizara el registro en la entidad {@link PedidosEntity}.
     * @param last El objeto {@link PedidosDTO} contiene la fecha de creacion de la primera vez que se guardo con {@link PedidosEntity}.
     *
     * @return Devuelve un objeto nuevo {@link PedidosEntity} con los datos actualizados de {@link PedidosRequest} y {@link PedidosDTO}.
     * @version(1.0.0)
     * @author Daniel Ivan Martinez R.
     */
    public PedidosEntity setData(PedidosRequest update, PedidosDTO last){
        return PedidosEntity.builder()
                .id(update.getId())
                .usuarioId(update.getUsuarioId())
                .estado(update.getEstado())
                .fechaCreacion(last.getFechaCreacion())
                .total(update.getTotal())
                .build();
    }

}
