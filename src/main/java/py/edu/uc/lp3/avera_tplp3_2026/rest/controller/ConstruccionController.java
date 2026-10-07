package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;

import java.util.Map;

@RestController
@RequestMapping("/minecraft")
public class ConstruccionController {

    // Constructor completo: la clase valida y el controller solo informa
    @GetMapping("/crear-jugador")
    public ResponseEntity<?> crearJugador(
            @RequestParam(defaultValue = "Steve") String nombre,
            @RequestParam(defaultValue = "20") int vida,
            @RequestParam(defaultValue = "1.8") double altura,
            @RequestParam(defaultValue = "20") int hambre,
            @RequestParam(defaultValue = "true") boolean controlable) {
        try {
            return ResponseEntity.ok(new PersonajeJugable(vida, nombre, altura, hambre, controlable));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // Constructor simple: solo el nombre; el resto lo decide la clase
    @GetMapping("/crear-monstruo")
    public ResponseEntity<?> crearMonstruo(@RequestParam(defaultValue = "Creeper") String nombre) {
        try {
            return ResponseEntity.ok(new Monstruo(nombre));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
