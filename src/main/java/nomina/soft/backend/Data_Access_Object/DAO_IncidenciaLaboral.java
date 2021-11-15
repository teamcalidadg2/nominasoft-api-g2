package nomina.soft.backend.Data_Access_Object;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Entidades.IncidenciaLaboral;

@Repository
public interface DAO_IncidenciaLaboral extends JpaRepository<IncidenciaLaboral,Long>{
	List<IncidenciaLaboral> findAllByContrato(Contrato contrato);
}
