package py.edu.uc.lp3.ja_taller_git_2026.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.ja_taller_git_2026.domain.Arma;
import py.edu.uc.lp3.ja_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.ja_taller_git_2026.repository.ArmaRepository;
import py.edu.uc.lp3.ja_taller_git_2026.repository.JugadorRepository;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Cs2Controller implements CommandLineRunner {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public Cs2Controller(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    @Override
    public void run(String... args) {
        if (armaRepository.count() == 0) {
            armaRepository.save(new Arma("AK-47", "Rifle", 2700, 36));
            armaRepository.save(new Arma("M4A1-S", "Rifle", 2900, 38));
            armaRepository.save(new Arma("AWP", "Sniper", 4750, 115));
            armaRepository.save(new Arma("Desert Eagle", "Pistola", 700, 53));
        }

        if (jugadorRepository.count() == 0) {
            jugadorRepository.save(new Jugador("s1mple", "TR", 16000, 100));
            jugadorRepository.save(new Jugador("ZywOo", "CT", 12500, 100));
            jugadorRepository.save(new Jugador("niko", "TR", 4200, 85));
        }
    }

    @GetMapping("/armas")
    public List<Arma> getArmas() {
        return armaRepository.findAll();
    }

    @GetMapping("/jugadores")
    public List<Jugador> getJugadores() {
        return jugadorRepository.findAll();
    }
}
