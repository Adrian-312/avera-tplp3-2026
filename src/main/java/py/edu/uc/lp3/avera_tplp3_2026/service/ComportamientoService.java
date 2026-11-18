package py.edu.uc.lp3.avera_tplp3_2026.service;

import java.util.List;
import java.util.Map;

public interface ComportamientoService {

    List<Map<String, String>> obtenerAcciones();

    List<Map<String, Object>> obtenerExplosivos();
}
