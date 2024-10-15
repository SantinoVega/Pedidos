package com.demo.pedidos.controller;

import com.demo.pedidos.dto.PedidosDTO;
import com.demo.pedidos.model.PedidosRequest;
import com.demo.pedidos.response.ResponseData;
import com.demo.pedidos.response.ResponseGenerico;
import com.demo.pedidos.service.IPedidosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.util.List;

/**
 * La clase pedidosController contiene los
 * metodos para hacer CRUD con datos del pedido.
 *
 * @author Daniel Ivan Martinez R.
 */
@RestController
@RequestMapping("/pedidos")
public class PedidosController {

    @Autowired
    IPedidosService pedidosService;

    /**
     * Guardar un pedido en la base de datos.
     * @param pedido Es un Objeto que contiene los atributos que se intentaran guardar en la BD, {@link PedidosRequest}
     * @return Devuelve el objeto Request si se guardo con exito en la BD.
     * @author Daniel Ivan Martinez R.
     */
    @Operation(summary = "Guardar un pedido en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido guardado correctamente en base de datos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class)) ),
            @ApiResponse(responseCode = "500", description = "Error: Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class))) })
    @PostMapping
    public ResponseEntity<ResponseGenerico<PedidosRequest>> savePedido(@Valid @RequestBody PedidosRequest pedido) throws Exception {
        PedidosRequest response = pedidosService.save(pedido);
        return new ResponseGenerico<>("Pedido guardado exitosamente en BD.",Boolean.TRUE,response).success(HttpStatus.CREATED);
    }

    /**
     * Busca un pedido en BD por id
     * @param id valod del id del pedido
     * @return Devuelve el objeto {@link ResponseData} que contiene los datos del pedido.
     * @author Daniel Ivan Martinez R.
     */
    @Operation(summary = "Obtener pedido por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success!!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class)) ),
            @ApiResponse(responseCode = "404", description = "No se encontraron datos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class))),
            @ApiResponse(responseCode = "500", description = "Error: Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class))) })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) throws SQLDataException {
        PedidosDTO response = pedidosService.pedidosGetById(id);
        if(response == null){
            return new ResponseGenerico<>("Pedido no encontrado",Boolean.TRUE,null).success(HttpStatus.NOT_FOUND);
        }
        return new ResponseGenerico<>("Consulta correcta",Boolean.TRUE,response).success(HttpStatus.OK);
    }

    /**
     * Obtener todos los pedidos almacenados en la base de datos.
     * @return Devuelve una lista de objetos {@link PedidosDTO} que contiene todos los pedidos guardados en BD.
     * @author Daniel Ivan Martinez R.
     */
    @Operation(summary = "Obtener todos los pedidos almacenados en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success!!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class)) ),
            @ApiResponse(responseCode = "404", description = "No se encontraron datos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class))),
            @ApiResponse(responseCode = "500", description = "Error: Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class))) })
    @GetMapping("/all")
    public ResponseEntity<?> getTodos(){
        List<PedidosDTO> pedidosList = pedidosService.getAll();
        if(pedidosList.isEmpty()){
            return new ResponseGenerico<>("No se encontraron datos",Boolean.TRUE,null).success(HttpStatus.NOT_FOUND);
        }
        return new ResponseGenerico<>("Consulta exitosa",Boolean.TRUE,pedidosList).success(HttpStatus.OK);
    }

    /**
     * Modificar un pedido existente en la base de datos
     * @param pedido Objeto que se pretende actualizar en BD {@link PedidosRequest}
     * @return Devuelve un objeto {@link PedidosRequest} que contiene los pedidos actualizados en BD.
     * @author Daniel Ivan Martinez R.
     */
    @Operation(summary = "Modificar un pedido existente en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido modificado correctamente en base de datos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class)) ),
            @ApiResponse(responseCode = "500", description = "Error: Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class))) })
    @PutMapping
    public ResponseEntity<ResponseGenerico<PedidosRequest>> updatePedidos(@Valid @RequestBody PedidosRequest pedido) throws SQLDataException {
        PedidosRequest response = pedidosService.update(pedido);
        if(response == null){
            return new ResponseGenerico<>("No se encontro el pedido",Boolean.TRUE,new PedidosRequest()).success(HttpStatus.OK);
        }
        return new ResponseGenerico<>("Pedido modificado exitosamente",Boolean.TRUE,response).success(HttpStatus.OK);
    }

    /**
     * Eliminar un pedido existente en la base de datos
     * @param id valor del id del pedido que se pretende actualizar en BD.
     * @return Devuelve 1 si se elimino correctamente en BD, 0 si no lo encontro en la BD.
     * @author Daniel Ivan Martinez R.
     */
    @Operation(summary = "Eliminar un pedido existente en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido eliminado correctamente en base de datos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class)) ),
            @ApiResponse(responseCode = "400", description = "Error pedido no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class)) ),
            @ApiResponse(responseCode = "500", description = "Error: Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseGenerico.class))) })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGenerico<Integer>> deletePedido(@PathVariable("id") int id) {
        Integer response = pedidosService.deleteById(id);

        if(response == 0){
            return new ResponseGenerico<>("No se encontro el pedido",Boolean.TRUE,0).success(HttpStatus.OK);
        }
        return new ResponseGenerico<>("Pedido Eliminado exitosamente",Boolean.TRUE,response).success(HttpStatus.OK);
    }
}
