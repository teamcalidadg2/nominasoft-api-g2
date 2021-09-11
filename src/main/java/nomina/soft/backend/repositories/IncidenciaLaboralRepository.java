package nomina.soft.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.IncidenciaLaboralModel;


@Repository
public interface IncidenciaLaboralRepository extends JpaRepository<IncidenciaLaboralModel,Integer>{
        
}
