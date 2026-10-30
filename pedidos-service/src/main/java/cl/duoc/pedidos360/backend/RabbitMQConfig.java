
package cl.duoc.pedidos360.backend;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DIRECT_EXCHANGE = "pedidos.direct";
    public static final String TOPIC_EXCHANGE = "pedidos.topic";
    public static final String DEAD_LETTER_EXCHANGE = "pedidos.dlx";

    public static final String NOTIFICACIONES_QUEUE = "pedidos.notificaciones";
    public static final String TICKETS_QUEUE = "pedidos.tickets";
    public static final String AUDITORIA_QUEUE = "pedidos.auditoria";

    public static final String NOTIFICACIONES_DLQ = "pedidos.notificaciones.dlq";
    public static final String TICKETS_DLQ = "pedidos.tickets.dlq";
    public static final String AUDITORIA_DLQ = "pedidos.auditoria.dlq";

    public static final String NOTIFICACION_KEY = "pedido.notificacion";
    public static final String TICKET_KEY = "pedido.ticket";
    public static final String AUDITORIA_KEY = "pedido.#";

    public static final String NOTIFICACION_ERROR_KEY = "pedido.notificacion.error";
    public static final String TICKET_ERROR_KEY = "pedido.ticket.error";
    public static final String AUDITORIA_ERROR_KEY = "pedido.auditoria.error";

    @Bean
    public DirectExchange pedidosDirectExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange pedidosTopicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange pedidosDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(NOTIFICACIONES_QUEUE).quorum().build();
    }

    @Bean
    public Queue ticketsQueue() {
        return QueueBuilder.durable(TICKETS_QUEUE).quorum().build();
    }

    @Bean
    public Queue auditoriaQueue() {
        return QueueBuilder.durable(AUDITORIA_QUEUE).quorum().build();
    }

    @Bean
    public Queue notificacionesDlq() {
        return QueueBuilder.durable(NOTIFICACIONES_DLQ).quorum().build();
    }

    @Bean
    public Queue ticketsDlq() {
        return QueueBuilder.durable(TICKETS_DLQ).quorum().build();
    }

    @Bean
    public Queue auditoriaDlq() {
        return QueueBuilder.durable(AUDITORIA_DLQ).quorum().build();
    }

    @Bean
    public Binding notificacionesBinding() {
        return BindingBuilder.bind(notificacionesQueue())
                .to(pedidosDirectExchange())
                .with(NOTIFICACION_KEY);
    }

    @Bean
    public Binding ticketsBinding() {
        return BindingBuilder.bind(ticketsQueue())
                .to(pedidosDirectExchange())
                .with(TICKET_KEY);
    }

    @Bean
    public Binding auditoriaBinding() {
        return BindingBuilder.bind(auditoriaQueue())
                .to(pedidosTopicExchange())
                .with(AUDITORIA_KEY);
    }

    @Bean
    public Binding notificacionesDlqBinding() {
        return BindingBuilder.bind(notificacionesDlq())
                .to(pedidosDeadLetterExchange())
                .with(NOTIFICACION_ERROR_KEY);
    }

    @Bean
    public Binding ticketsDlqBinding() {
        return BindingBuilder.bind(ticketsDlq())
                .to(pedidosDeadLetterExchange())
                .with(TICKET_ERROR_KEY);
    }

    @Bean
    public Binding auditoriaDlqBinding() {
        return BindingBuilder.bind(auditoriaDlq())
                .to(pedidosDeadLetterExchange())
                .with(AUDITORIA_ERROR_KEY);
    }
}
