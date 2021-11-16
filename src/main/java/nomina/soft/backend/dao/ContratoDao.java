package nomina.soft.backend.dao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.entidades.Contrato;
import nomina.soft.backend.entidades.Empleado;

@Repository
public interface ContratoDao extends JpaRepository<Contrato,Long>{
    public Contrato findByIdContrato(Long idContrato);
	public List<Contrato> findAllByEmpleado(Empleado empleado);


}
