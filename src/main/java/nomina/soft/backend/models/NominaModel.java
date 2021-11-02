package nomina.soft.backend.models;
import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_CANCELADO;
import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_FECHA_FIN_NOT_VALID;
import static nomina.soft.backend.constant.NominaImplConstant.DESCRIPCION_NOT_VALID;
import static nomina.soft.backend.constant.NominaImplConstant.ID_NOMINA_NOT_NUMBER;
import static nomina.soft.backend.constant.NominaImplConstant.ID_NOMINA_NOT_VALID;
import static nomina.soft.backend.constant.NominaImplConstant.ID_PERIODO_NOMINA_NOT_NUMBER;
import static nomina.soft.backend.constant.NominaImplConstant.ID_PERIODO_NOMINA_NOT_VALID;
import static nomina.soft.backend.constant.NominaImplConstant.PERIODO_FECHA_FIN_NOT_VALID;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
@JsonIgnoreProperties({"hibernateLazyInitializer"})
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
    @Getter @Setter private PeriodoNominaModel periodoNomina;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="nomina")
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
        
		if(!(nuevaNomina.getPeriodoNomina().getFechaFin().before(nuevaNomina.getFecha()))){
			contratoValido = false;
			throw new NominaNotValidException(PERIODO_FECHA_FIN_NOT_VALID);
		}
		return contratoValido;
	}



    public boolean identificadorValido(String idNomina) throws NominaNotValidException {
        if(idNomina.length()==0) throw new NominaNotValidException(ID_NOMINA_NOT_VALID);
		try {
			int idNominaTMP = Integer.parseInt(idNomina);
		} catch (NumberFormatException nfe){
			throw new NominaNotValidException(ID_NOMINA_NOT_NUMBER);
		}
		return true;
    }



	public boolean periodoDeNominaValido(String idPeriodoNomina) throws NominaNotValidException {
		if(idPeriodoNomina.length()==0) throw new NominaNotValidException(ID_PERIODO_NOMINA_NOT_VALID);
		try {
			int idPeriodoNominaTMP = Integer.parseInt(idPeriodoNomina);
		} catch (NumberFormatException nfe){
			throw new NominaNotValidException(ID_PERIODO_NOMINA_NOT_NUMBER);
		}
		return true;
	}



	public boolean esPeriodoCerrado(List<NominaModel> nominasDePeriodo) {
		boolean estaCerrado = false;
		for(NominaModel nominaTemp : nominasDePeriodo){
			if(nominaTemp.estaCerrada) estaCerrado = true;
		}
		return estaCerrado;
	}



	public boolean descripcionValida(String descripcion) throws NominaNotValidException {
		if(descripcion.length()==0 || descripcion == null) throw new NominaNotValidException(DESCRIPCION_NOT_VALID);
		return true;
	}


}
