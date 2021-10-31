package nomina.soft.backend.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nomina.soft.backend.models.NominaModel;

@Repository
public interface NominaRepository extends JpaRepository<NominaModel,Long>{
    public NominaModel findByIdNomina(Long idNomina);
    public List<NominaModel> findAllByDescripcion(String descripcion);
    public List<NominaModel> findByDescripcionContains(String descripcion);
}
