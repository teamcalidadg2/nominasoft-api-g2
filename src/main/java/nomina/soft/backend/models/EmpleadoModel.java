package nomina.soft.backend.models;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import static nomina.soft.backend.constant.EmpleadoImplConstant.*;
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "empleado")
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idEmpleado;

    @Getter @Setter private String nombres;
    @Getter @Setter private String apellidos;
    @Getter @Setter private String dni;
    @Getter @Setter private Date fechaNacimiento;
    @Getter @Setter private String telefono;
    @Getter @Setter private String correo;
    @Getter @Setter private String direccion;
    @Getter @Setter private Boolean estaActivo;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="empleado")
    @JsonIgnore
    @Getter @Setter private List<ContratoModel> contratos;
    
    public void addContrato(ContratoModel contrato) {
    	contratos.add(contrato);
    }

    public boolean validarDni(String dniCad) throws EmpleadoNotValidException{
        int dniTMP = 0;
        if(dniCad.length()==0) throw new EmpleadoNotValidException(DNI_VACIO);
        if(dniCad.length()<8) throw new EmpleadoNotValidException(DNI_MENOS_DE_8_DIGITOS);
        if(dniCad.length()>8) throw new EmpleadoNotValidException(DNI_MAS_DE_8_DIGITOS);
        try {
			dniTMP = Integer.parseInt(dniCad);
		} catch (NumberFormatException nfe){
		    throw new EmpleadoNotValidException(DNI_CARACTERES_NO_NUMERICOS);
		}
        return true;
    }

}
