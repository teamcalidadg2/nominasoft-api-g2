package nomina.soft.backend.Data_Access_Object;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Entidades.Empleado;

@Repository
public interface DAO_Contrato extends JpaRepository<Contrato,Long>{
    public Contrato findByIdContrato(Long idContrato);
	public List<Contrato> findAllByEmpleado(Empleado empleado);


}
