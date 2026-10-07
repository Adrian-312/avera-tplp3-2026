package py.edu.uc.lp3.avera_tplp3_2026.minecraft.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.avera_tplp3_2026.minecraft.PersonajeJugable;

@RestController
@RequestMapping("/minecraft")
public class ConstruccionController {

    @GetMapping("/crear-jugador")
    public PersonajeJugable crearJugador(
            @RequestParam(name = "nombre", defaultValue = "Steve") String nombre,
            @RequestParam(name = "vida", defaultValue = "20") int vida,
            @RequestParam(name = "altura", defaultValue = "1.8") double altura,
            @RequestParam(name = "hambre", defaultValue = "20") int hambre,
            @RequestParam(name = "controlable", defaultValue = "true") boolean controlable) {
        
        // Se invoca directamente tu constructor
        return new PersonajeJugable(vida, nombre, altura, hambre, controlable);
    }
}