
package cl.duoc.pedidos360.backend;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoEventProducer pedidoEventProducer;

    public PedidoController(
            PedidoRepository pedidoRepository,
            PedidoEventProducer pedidoEventProducer) {

        this.pedidoRepository = pedidoRepository;
        this.pedidoEventProducer = pedidoEventProducer;
    }

    @GetMapping
    public List<Pedido> obtenerPedidos(@AuthenticationPrincipal Jwt jwt) {
        String usuario = jwt.getSubject();
        return pedidoRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido crearPedido(
            @RequestBody Pedido nuevoPedido,
            @AuthenticationPrincipal Jwt jwt) {

        if (nuevoPedido.getProducto() == null ||
            nuevoPedido.getProducto().isBlank() ||
            nuevoPedido.getCantidad() == null ||
            nuevoPedido.getCantidad() <= 0 ||
            nuevoPedido.getTotal() == null ||
            nuevoPedido.getTotal() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Los datos del pedido no son válidos"
            );
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(jwt.getSubject());
        pedido.setProducto(nuevoPedido.getProducto());
        pedido.setCantidad(nuevoPedido.getCantidad());
        pedido.setTotal(nuevoPedido.getTotal());
        pedido.setEstado("Pendiente");
        pedido.setFecha(LocalDateTime.now());

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        String mensaje = "Pedido registrado - Producto: "
                + pedidoGuardado.getProducto()
                + " - Cantidad: "
                + pedidoGuardado.getCantidad()
                + " - Total: "
                + pedidoGuardado.getTotal();

        pedidoEventProducer.enviarNotificacion(pedidoGuardado.getId());

        pedidoEventProducer.generarTicket(pedidoGuardado.getId());

        pedidoEventProducer.registrarAuditoria(pedidoGuardado.getId());

        return pedidoGuardado;
    }
}
