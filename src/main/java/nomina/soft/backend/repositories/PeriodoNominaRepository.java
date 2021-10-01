package nomina.soft.backend.repositories;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nomina.soft.backend.models.PeriodoNominaModel;

@Repository
public interface PeriodoNominaRepository extends JpaRepository<PeriodoNominaModel,Long>{
    
	public PeriodoNominaModel findByIdPeriodoNomina(Long idPeriodoNomina);
	public PeriodoNominaModel findByFechaInicio(Date fechaInicio);
	public PeriodoNominaModel findByFechaFin(Date fechaFin);
	
}
