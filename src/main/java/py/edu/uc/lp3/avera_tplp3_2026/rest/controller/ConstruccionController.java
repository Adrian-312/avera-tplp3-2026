package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;

@RestController
@RequestMapping("/minecraft")
public class ConstruccionController {

    // Constructor completo: la clase valida y el handler convierte el error.
    // Es una demostración; el CRUD persistente está en /minecraft/entidades.
    @GetMapping("/crear-jugador")
    public PersonajeJugable crearJugador(
            @RequestParam(defaultValue = "Steve") String nombre,
            @RequestParam(defaultValue = "20") int vida,
            @RequestParam(defaultValue = "1.8") double altura,
            @RequestParam(defaultValue = "20") int hambre,
            @RequestParam(defaultValue = "true") boolean controlable) {
        return new PersonajeJugable(vida, nombre, altura, hambre, controlable);
    }

    // Constructor simple: solo el nombre; el resto lo decide la clase.
    @GetMapping("/crear-monstruo")
    public Monstruo crearMonstruo(@RequestParam(defaultValue = "Creeper") String nombre) {
        return new Monstruo(nombre);
    }
}
