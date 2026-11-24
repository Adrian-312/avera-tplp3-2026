package py.edu.uc.lp3.avera_tplp3_2026.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Explosivo;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.service.ComportamientoService;

@Service
public class ComportamientoServiceImpl implements ComportamientoService {

    private final EntidadVivaRepository repository;

    public ComportamientoServiceImpl(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    // Sobreescritura + polimorfismo: se habla con el tipo padre.
    // Una entidad muerta no realiza ninguna acción: no figura en la lista.
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> obtenerAcciones() {
        return repository.findAll().stream()
                .filter(EntidadViva::isVivo)
                .map(e -> Map.of(
                        "tipo", e.getClass().getSimpleName(),
                        "nombre", e.getNombre(),
                        "accion", e.realizarAccionEspecial()))
                .toList();
    }

    // Polimorfismo por interfaz: dos familias distintas hablan por el tipo Explosivo.
    // Un explosivo muerto (por ejemplo un Creeper que ya explotó) ya no cuenta.
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerExplosivos() {
        List<Explosivo> explosivos = repository.findAll().stream()
                .filter(EntidadViva::isVivo)
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
}
