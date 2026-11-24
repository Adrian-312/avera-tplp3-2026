package py.edu.uc.lp3.avera_tplp3_2026.service;

import java.util.List;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EntidadRequest;

public interface EntidadService {

    EntidadViva crear(EntidadRequest request);

    List<EntidadViva> listar(String nombre, Integer vidaMinima);

    EntidadViva buscar(Long id);

    EntidadViva reemplazar(Long id, EntidadRequest request);

    EntidadViva actualizar(Long id, EntidadRequest request);

    void eliminar(Long id);
}
