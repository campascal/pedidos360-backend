package cl.duoc.pedidos360.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @GetMapping
    public Map<String, Object> obtenerPedidos() {

        return Map.of(
            "mensaje", "API de Pedidos360 funcionando correctamente",
            "estado", "OK"
        );
    }
}