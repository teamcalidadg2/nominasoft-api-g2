package nomina.soft.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.EmpleadoModel;


@Repository
public interface EmpleadoRepository extends JpaRepository<EmpleadoModel,Integer>{
    public EmpleadoModel findByDni(String dni);
    public EmpleadoModel findByTelefono(String telefono);
    public EmpleadoModel findByCorreo(String correo);
    public abstract boolean existsByCorreo(String correo);

}

