package py.edu.uc.lp3.avera_tplp3_2026.minecraft.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.avera_tplp3_2026.minecraft.Animal;
import py.edu.uc.lp3.avera_tplp3_2026.minecraft.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.minecraft.Monstruo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/minecraft")
public class ComportamientoController {

    @GetMapping("/acciones")
    public List<Map<String, String>> obtenerAccionesPolimorficas() {
        List<EntidadViva> entidades = new ArrayList<>();

        // Instanciación utilizando las firmas exactas de tus constructores
        entidades.add(new Animal(10, "Oveja", 1.0, true, true, true));
        entidades.add(new Monstruo(20, "Creeper", 1.5, false, true, false));

        List<Map<String, String>> respuesta = new ArrayList<>();

        for (EntidadViva entidad : entidades) {
            Map<String, String> item = new HashMap<>();
            item.put("nombre", entidad.getNombre());
            // Polimorfismo: invocación uniforme sin bloques if / switch
            item.put("accion", entidad.realizarAccionEspecial());
            respuesta.add(item);
        }

        return respuesta;
    }
}