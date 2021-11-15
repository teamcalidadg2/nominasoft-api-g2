package nomina.soft.backend.Data_Access_Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.Entidades.BoletaDePago;

@Repository
public interface DAO_BoletaDePago extends JpaRepository<BoletaDePago,Long> {
        
}
