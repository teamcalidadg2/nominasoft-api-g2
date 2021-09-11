package nomina.soft.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nomina.soft.backend.models.EmpleadoModel;


@Repository
public interface EmpleadoRepository extends JpaRepository<EmpleadoModel,Integer>{
    public abstract Optional<EmpleadoModel> findByDni(String dni);
    public abstract Optional<EmpleadoModel> findByCorreo(String correo);
    public abstract boolean existsByCorreo(String correo);
}
