package nomina.soft.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;

@Repository
public interface ContratoRepository extends JpaRepository<ContratoModel,Integer>{
        
	public List<ContratoModel> findAllByEmpleado(EmpleadoModel empleado);
	
}
