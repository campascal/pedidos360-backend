
package cl.duoc.pedidos360.backend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.logging.Logger;

@Service
public class PedidoProcesamientoService {

    private static final Logger logger =
            Logger.getLogger(PedidoProcesamientoService.class.getName());

    private final PedidoRepository pedidoRepository;

    public PedidoProcesamientoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public void procesarNotificacion(Long pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);

        logger.info("NOTIFICACION PROCESADA | Pedido: " + pedido.getId()
                + " | Producto: " + pedido.getProducto()
                + " | Estado: " + pedido.getEstado());
    }

    @Transactional(readOnly = true)
    public void procesarTicket(Long pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);

        String ticket = """
                ==========================
                     TICKET PEDIDOS360
                ==========================
                Pedido: %s
                Producto: %s
                Cantidad: %s
                Total: $%s
                Estado: %s
                Fecha: %s
                ==========================
                """.formatted(
                pedido.getId(),
                pedido.getProducto(),
                pedido.getCantidad(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getFecha()
        );

        logger.info(ticket);
    }

    @Transactional(readOnly = true)
    public void procesarAuditoria(Long pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);

        logger.info("AUDITORIA | Pedido: " + pedido.getId()
                + " | Usuario: " + pedido.getUsuario()
                + " | Fecha: " + pedido.getFecha()
                + " | Estado: " + pedido.getEstado());
    }

    private Pedido buscarPedido(Long pedidoId) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("ID de pedido inválido");
        }

        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Pedido no encontrado: " + pedidoId
                        ));
    }
}
