package nomina.soft.backend.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.entidades.Afp;

@Repository
public interface AfpDao extends JpaRepository<Afp,Long>{
    
	public Afp findByIdAfp(Long idAFP);
	public Afp findByNombre(String nombre);
	public Afp findByPorcentajeDescuento(float porcentajeDescuento);
	
}
