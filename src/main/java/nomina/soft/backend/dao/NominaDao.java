package nomina.soft.backend.dao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.Nomina;

@Repository
public interface NominaDao extends JpaRepository<Nomina,Long>{
    public Nomina findByIdNomina(Long idNomina);
    public List<Nomina> findAllByDescripcion(String descripcion);
    public List<Nomina> findByDescripcionContains(String descripcion);
}
