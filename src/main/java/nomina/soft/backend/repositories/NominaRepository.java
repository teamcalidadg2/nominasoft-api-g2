package nomina.soft.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.NominaModel;

@Repository
public interface NominaRepository extends JpaRepository<NominaModel,Integer>{
    public NominaModel findById(int id);
}
