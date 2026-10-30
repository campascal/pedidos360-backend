
package cl.duoc.pedidos360.backend;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotificacionConsumer {

    private final MensajeRabbitService mensajeService;
    private final PedidoProcesamientoService procesamientoService;

    public NotificacionConsumer(
            MensajeRabbitService mensajeService,
            PedidoProcesamientoService procesamientoService) {

        this.mensajeService = mensajeService;
        this.procesamientoService = procesamientoService;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICACIONES_QUEUE)
    public void recibirNotificacion(Message message, Channel channel)
            throws IOException {

        mensajeService.procesar(
                message,
                channel,
                "NOTIFICACION",
                procesamientoService::procesarNotificacion
        );
    }
}
