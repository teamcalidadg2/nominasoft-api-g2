package nomina.soft.backend.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.BoletaDePago;

@Repository
public interface BoletaDePagoDao extends JpaRepository<BoletaDePago,Long> {
        
}
