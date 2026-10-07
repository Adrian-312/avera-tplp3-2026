package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Animal;
import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/minecraft")
public class ComportamientoController {

    // Sobreescritura + polimorfismo: el controller habla con el tipo padre
    @GetMapping("/acciones")
    public List<Map<String, String>> obtenerAcciones() {
        List<EntidadViva> entidades = List.of(
                new Animal("Oveja"),
                new Monstruo("Creeper"));

        return entidades.stream()
                .map(e -> Map.of(
                        "nombre", e.getNombre(),
                        "accion", e.realizarAccionEspecial()))
                .toList();
    }

    // Sobrecarga: con "origen" se llama a recibirDanio(int, String); sin él, a recibirDanio(int)
    @GetMapping("/danio")
    public ResponseEntity<?> danio(
            @RequestParam(defaultValue = "Creeper") String nombre,
            @RequestParam int puntos,
            @RequestParam(required = false) String origen) {
        try {
            Monstruo monstruo = new Monstruo(nombre);
            String firma;
            if (origen == null) {
                monstruo.recibirDanio(puntos);
                firma = "recibirDanio(int)";
            } else {
                monstruo.recibirDanio(puntos, origen);
                firma = "recibirDanio(int, String)";
            }
            return ResponseEntity.ok(Map.of(
                    "nombre", monstruo.getNombre(),
                    "vida", monstruo.getVida(),
                    "vivo", monstruo.isVivo(),
                    "ultimoOrigenDanio", monstruo.getUltimoOrigenDanio(),
                    "metodoUsado", firma));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
