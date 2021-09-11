package nomina.soft.backend.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empleado")
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter
    private int empleado_id;
    @Getter @Setter
    private String nombres;
    @Getter @Setter
    private String apellidos;
    @Getter @Setter
    private String dni;
    @Getter @Setter
    private Date fechaNacimiento;
    @Getter @Setter
    private String telefono;
    @Getter @Setter
    private String correo;
    @Getter @Setter
    private String direccion;
    @OneToMany(fetch =FetchType.LAZY,mappedBy = "empleado")
	@Getter @Setter
	private Set<ContratoModel> contratos;


}
