
package cl.duoc.pedidos360.admin;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.regex.Pattern;

@Service
public class RabbitAdminService {

    private static final Pattern NOMBRE =
            Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._-]{0,99}");

    private static final Set<String> COLAS_PROTEGIDAS = Set.of(
            "pedidos.notificaciones",
            "pedidos.tickets",
            "pedidos.auditoria",
            "pedidos.notificaciones.dlq",
            "pedidos.tickets.dlq",
            "pedidos.auditoria.dlq"
    );

    private static final Set<String> EXCHANGES_PROTEGIDOS = Set.of(
            "pedidos.direct",
            "pedidos.topic",
            "pedidos.dlx"
    );

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public void crearCola(String nombre) {
        validarNombre(nombre);

        amqpAdmin.declareQueue(
                QueueBuilder.durable(nombre).quorum().build()
        );
    }

    public void eliminarCola(String nombre) {
        validarNombre(nombre);

        if (COLAS_PROTEGIDAS.contains(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se puede eliminar una cola principal del sistema"
            );
        }

        if (!amqpAdmin.deleteQueue(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La cola no existe"
            );
        }
    }

    public void crearExchange(String nombre, String tipo) {
        validarNombre(nombre);

        if (tipo == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de exchange es obligatorio"
            );
        }

        switch (tipo.toLowerCase()) {
            case "direct" -> amqpAdmin.declareExchange(
                    new DirectExchange(nombre, true, false)
            );
            case "topic" -> amqpAdmin.declareExchange(
                    new TopicExchange(nombre, true, false)
            );
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se permiten exchanges direct o topic"
            );
        }
    }

    public void eliminarExchange(String nombre) {
        validarNombre(nombre);

        if (EXCHANGES_PROTEGIDOS.contains(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se puede eliminar un exchange principal del sistema"
            );
        }

        if (!amqpAdmin.deleteExchange(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El exchange no existe"
            );
        }
    }

    public void crearBinding(
            String cola,
            String exchange,
            String routingKey) {

        validarNombre(cola);
        validarNombre(exchange);
        validarRoutingKey(routingKey);

        Binding binding = new Binding(
                cola,
                Binding.DestinationType.QUEUE,
                exchange,
                routingKey,
                null
        );

        amqpAdmin.declareBinding(binding);
    }

    public void eliminarBinding(
            String cola,
            String exchange,
            String routingKey) {

        validarNombre(cola);
        validarNombre(exchange);
        validarRoutingKey(routingKey);

        if (COLAS_PROTEGIDAS.contains(cola)
                || EXCHANGES_PROTEGIDOS.contains(exchange)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se pueden modificar bindings principales"
            );
        }

        Binding binding = new Binding(
                cola,
                Binding.DestinationType.QUEUE,
                exchange,
                routingKey,
                null
        );

        amqpAdmin.removeBinding(binding);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || !NOMBRE.matcher(nombre).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre inválido: utiliza entre 1 y 100 caracteres alfanuméricos, puntos, guiones o guiones bajos"
            );
        }

        if (nombre.startsWith("amq.")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El prefijo amq. está reservado"
            );
        }
    }

    private void validarRoutingKey(String routingKey) {
        if (routingKey == null
                || routingKey.isBlank()
                || routingKey.length() > 100
                || !routingKey.matches("[a-zA-Z0-9.*#_-]+")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Routing key inválida"
            );
        }
    }
}
