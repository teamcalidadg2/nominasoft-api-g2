package nomina.soft.backend.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.BoletaDePagoModel;

@Repository
public interface BoletaDePagoRepository extends JpaRepository<BoletaDePagoModel,Long> {
        
}
