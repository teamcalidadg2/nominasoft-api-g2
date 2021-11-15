package nomina.soft.backend.Data_Access_Object;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.Entidades.Nomina;

@Repository
public interface DAO_Nomina extends JpaRepository<Nomina,Long>{
    public Nomina findByIdNomina(Long idNomina);
    public List<Nomina> findAllByDescripcion(String descripcion);
    public List<Nomina> findByDescripcionContains(String descripcion);
}
