package cl.duoc.pedidos360.backend;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Service
public class MensajeRabbitService {

    private static final Logger logger =
            Logger.getLogger(MensajeRabbitService.class.getName());

    public void procesar(
            Message message,
            Channel channel,
            String tipo,
            Consumer<Long> operacion) throws IOException {

        long tag = message.getMessageProperties().getDeliveryTag();
        String contenido = new String(
                message.getBody(),
                StandardCharsets.UTF_8
        );

        Long pedidoId;

        try {
            pedidoId = Long.parseLong(contenido);

            if (pedidoId <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }

        } catch (IllegalArgumentException e) {
            logger.severe(tipo + " | Mensaje inválido: " + contenido);
            channel.basicNack(tag, false, false);
            return;
        }

        for (int intento = 1; intento <= 3; intento++) {
            try {
                operacion.accept(pedidoId);

                logger.info(tipo + " | Pedido " + pedidoId
                        + " procesado correctamente en intento " + intento);

                channel.basicAck(tag, false);
                return;

            } catch (Exception e) {
                logger.warning(tipo + " | Intento " + intento
                        + " fallido para pedido " + pedidoId
                        + ": " + e.getMessage());

                if (intento == 3) {
                    logger.severe(tipo
                            + " | Reintentos agotados. Enviando a DLQ");
                    channel.basicNack(tag, false, false);
                }
            }
        }
    }
}

