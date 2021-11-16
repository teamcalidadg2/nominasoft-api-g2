package nomina.soft.backend.dao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.entidades.Contrato;
import nomina.soft.backend.entidades.IncidenciaLaboral;

@Repository
public interface IncidenciaLaboralDao extends JpaRepository<IncidenciaLaboral,Long>{
	List<IncidenciaLaboral> findAllByContrato(Contrato contrato);
}
