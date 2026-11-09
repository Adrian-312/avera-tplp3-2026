package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Explosivo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;

@RestController
@RequestMapping("/minecraft")
public class ComportamientoController {

    private final EntidadVivaRepository repository;

    public ComportamientoController(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    // Sobreescritura + polimorfismo: el controller habla con el tipo padre.
    @GetMapping("/acciones")
    public List<Map<String, String>> obtenerAcciones() {
        return repository.findAll().stream()
                .map(e -> Map.of(
                        "tipo", e.getClass().getSimpleName(),
                        "nombre", e.getNombre(),
                        "accion", e.realizarAccionEspecial()))
                .toList();
    }

    // Polimorfismo por interfaz: dos familias distintas hablan por el tipo Explosivo.
    @GetMapping("/explosivos")
    public List<Map<String, Object>> obtenerExplosivos() {
        List<Explosivo> explosivos = repository.findAll().stream()
                .filter(Explosivo.class::isInstance)
                .map(Explosivo.class::cast)
                .toList();

        return explosivos.stream()
                .map(e -> Map.<String, Object>of(
                        "clase", e.getClass().getSimpleName(),
                        "poderExplosivo", e.poderExplosivo(),
                        "sonido", e.sonidoDeExplosion()))
                .toList();
    }

    // Sobrecarga: con "origen" se llama a recibirDanio(int, String); sin él, a recibirDanio(int).
    // Es una demostración de constructores y sobrecarga; no persiste.
    @GetMapping("/danio")
    public Map<String, Object> danio(
            @RequestParam(defaultValue = "Creeper") String nombre,
            @RequestParam int puntos,
            @RequestParam(required = false) String origen) {
        Monstruo monstruo = new Monstruo(nombre);
        String firma;
        if (origen == null) {
            monstruo.recibirDanio(puntos);
            firma = "recibirDanio(int)";
        } else {
            monstruo.recibirDanio(puntos, origen);
            firma = "recibirDanio(int, String)";
        }
        return Map.of(
                "nombre", monstruo.getNombre(),
                "vida", monstruo.getVida(),
                "vivo", monstruo.isVivo(),
                "ultimoOrigenDanio", monstruo.getUltimoOrigenDanio(),
                "metodoUsado", firma);
    }
}
