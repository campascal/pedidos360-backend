
package cl.duoc.pedidos360.backend;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public PedidoEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarNotificacion(Long pedidoId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DIRECT_EXCHANGE,
                RabbitMQConfig.NOTIFICACION_KEY,
                pedidoId.toString()
        );
    }

    public void generarTicket(Long pedidoId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DIRECT_EXCHANGE,
                RabbitMQConfig.TICKET_KEY,
                pedidoId.toString()
        );
    }

    public void registrarAuditoria(Long pedidoId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                "pedido.creado",
                pedidoId.toString()
        );
    }
}
