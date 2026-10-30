
package cl.duoc.pedidos360.backend;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuditoriaConsumer {

    private final MensajeRabbitService mensajeService;
    private final PedidoProcesamientoService procesamientoService;

    public AuditoriaConsumer(
            MensajeRabbitService mensajeService,
            PedidoProcesamientoService procesamientoService) {

        this.mensajeService = mensajeService;
        this.procesamientoService = procesamientoService;
    }

    @RabbitListener(queues = RabbitMQConfig.AUDITORIA_QUEUE)
    public void recibirAuditoria(Message message, Channel channel)
            throws IOException {

        mensajeService.procesar(
                message,
                channel,
                "AUDITORIA",
                procesamientoService::procesarAuditoria
        );
    }
}
