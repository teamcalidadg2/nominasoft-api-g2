package nomina.soft.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.ContratoModel;

@Repository
public interface ContratoRepository extends JpaRepository<ContratoModel,Integer>{
        
}
