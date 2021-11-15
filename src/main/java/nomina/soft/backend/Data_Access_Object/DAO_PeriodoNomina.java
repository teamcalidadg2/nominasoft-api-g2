package nomina.soft.backend.Data_Access_Object;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.Entidades.PeriodoNomina;

@Repository
public interface DAO_PeriodoNomina extends JpaRepository<PeriodoNomina,Long>{
    
	public PeriodoNomina findByIdPeriodoNomina(Long idPeriodoNomina);
	public PeriodoNomina findByFechaInicio(Date fechaInicio);
	public PeriodoNomina findByFechaFin(Date fechaFin);

	
}
