package py.edu.uc.lp3.avera_tplp3_2026.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;

// Interface: Spring Data genera la implementación en tiempo de ejecución.
// El nombre de cada método le dice a Spring qué consulta armar.
public interface EntidadVivaRepository extends JpaRepository<EntidadViva, Long> {

    List<EntidadViva> findByNombreContainingIgnoreCase(String nombre);

    List<EntidadViva> findByVidaGreaterThanEqual(int vidaMinima);

    List<EntidadViva> findByNombreContainingIgnoreCaseAndVidaGreaterThanEqual(String nombre, int vidaMinima);
}
