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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import static nomina.soft.backend.constant.ContratoImplConstant.*;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.*;
import static nomina.soft.backend.constant.NominaImplConstant.*;

@Entity
@Table(name = "nomina")
@AllArgsConstructor
@NoArgsConstructor
public class NominaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idNomina;
    
    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fecha;
    @Getter @Setter private Boolean estaCerrada;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodo_nomina")
    @JsonIgnore
    @Getter @Setter private PeriodoNominaModel periodoNomina;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="nomina")
    @JsonIgnore
	@Getter @Setter private List<BoletaDePagoModel> boletasDePago;    



    public boolean validarContratoConNomina(ContratoModel contratoVigente,          //REGLA 06
                                            NominaModel nuevaNomina) throws ContratoNotValidException, NominaNotValidException{
		boolean contratoValido = true;
        String nombreEmpleado = contratoVigente.getEmpleado().getNombres() + " " + 
                                contratoVigente.getEmpleado().getApellidos();
		if(contratoVigente != null){
			if(contratoVigente.getFechaFin().after(nuevaNomina.getPeriodoNomina().getFechaInicio())){
				if(contratoVigente.getEstaCancelado()){
					contratoValido = false;
					throw new ContratoNotValidException(CONTRATO_CANCELADO + nombreEmpleado);
                                                        
				}
			}
			else{
				contratoValido = false;
				throw new ContratoNotValidException(CONTRATO_FECHA_FIN_NOT_VALID + nombreEmpleado);
			}
		}else contratoValido = false;
        
		// if(!(nuevaNomina.getPeriodoNomina().getFechaFin().before(nuevaNomina.getFecha()))){
		// 	contratoValido = false;
		// 	throw new NominaNotValidException(PERIODO_FECHA_FIN_NOT_VALID);
		// }
		return contratoValido;
	}


}
