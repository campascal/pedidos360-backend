
package cl.duoc.pedidos360.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RabbitAdminController {

    private final RabbitAdminService service;

    public RabbitAdminController(RabbitAdminService service) {
        this.service = service;
    }

    public record ColaRequest(String nombre) {}

    public record ExchangeRequest(String nombre, String tipo) {}

    public record BindingRequest(
            String cola,
            String exchange,
            String routingKey
    ) {}

    @PostMapping("/queues")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> crearCola(@RequestBody ColaRequest request) {
        service.crearCola(request.nombre());
        return Map.of("mensaje", "Cola creada", "nombre", request.nombre());
    }

    @DeleteMapping("/queues/{name}")
    public Map<String, String> eliminarCola(@PathVariable String name) {
        service.eliminarCola(name);
        return Map.of("mensaje", "Cola eliminada", "nombre", name);
    }

    @PostMapping("/exchanges")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> crearExchange(
            @RequestBody ExchangeRequest request) {

        service.crearExchange(request.nombre(), request.tipo());

        return Map.of(
                "mensaje", "Exchange creado",
                "nombre", request.nombre()
        );
    }

    @DeleteMapping("/exchanges/{name}")
    public Map<String, String> eliminarExchange(@PathVariable String name) {
        service.eliminarExchange(name);
        return Map.of("mensaje", "Exchange eliminado", "nombre", name);
    }

    @PostMapping("/bindings")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> crearBinding(
            @RequestBody BindingRequest request) {

        service.crearBinding(
                request.cola(),
                request.exchange(),
                request.routingKey()
        );

        return Map.of("mensaje", "Binding creado");
    }

    @DeleteMapping("/bindings")
    public Map<String, String> eliminarBinding(
            @RequestBody BindingRequest request) {

        service.eliminarBinding(
                request.cola(),
                request.exchange(),
                request.routingKey()
        );

        return Map.of("mensaje", "Binding eliminado");
    }
}
