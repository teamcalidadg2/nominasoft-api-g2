package nomina.soft.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.PeriodoNominaModel;

@Repository
public interface PeriodoNominaRepository extends JpaRepository<PeriodoNominaModel,Integer>{
    
}
