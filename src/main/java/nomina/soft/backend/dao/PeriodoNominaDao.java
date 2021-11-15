package nomina.soft.backend.dao;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.PeriodoNomina;

@Repository
public interface PeriodoNominaDao extends JpaRepository<PeriodoNomina,Long>{
    
	public PeriodoNomina findByIdPeriodoNomina(Long idPeriodoNomina);
	public PeriodoNomina findByFechaInicio(Date fechaInicio);
	public PeriodoNomina findByFechaFin(Date fechaFin);

	
}
