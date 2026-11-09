package py.edu.uc.lp3.avera_tplp3_2026.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;

public interface EntidadVivaRepository extends JpaRepository<EntidadViva, Long> {

    List<EntidadViva> findByNombreContainingIgnoreCase(String nombre);

    List<EntidadViva> findByVidaGreaterThan(int vida);

    List<EntidadViva> findByUltimoOrigenDanio(String origen);
}
