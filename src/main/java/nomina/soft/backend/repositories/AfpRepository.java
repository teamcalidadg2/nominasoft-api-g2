package nomina.soft.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.AfpModel;

@Repository
public interface AfpRepository extends JpaRepository<AfpModel,Integer>{
    
	public AfpModel findById(int idAfp);
	public AfpModel findByNombre(String nombre);
	public AfpModel findByPorcentajeDescuento(Double porcentajeDescuento);
	
}
