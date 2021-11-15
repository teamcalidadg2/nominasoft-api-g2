package nomina.soft.backend.Data_Access_Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.Entidades.Afp;

@Repository
public interface DAO_Afp extends JpaRepository<Afp,Long>{
    
	public Afp findByIdAfp(Long idAFP);
	public Afp findByNombre(String nombre);
	public Afp findByPorcentajeDescuento(float porcentajeDescuento);
	
}
