package nomina.soft.backend.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.entidades.Empleado;

@Repository
public interface EmpleadoDao extends JpaRepository<Empleado,Long>{
	public Empleado findByIdEmpleado(Long idEmpleado);
    public Empleado findByDni(String dni);
    public Empleado findByTelefono(String telefono);
    public Empleado findByCorreo(String correo);
    public abstract boolean existsByCorreo(String correo);

}

