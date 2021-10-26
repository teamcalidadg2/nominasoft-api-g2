package nomina.soft.backend.models;

import static nomina.soft.backend.constant.ContratoImplConstant.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.SourceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "contrato")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContratoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idContrato;

    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin; 
    @Getter @Setter private Boolean tieneAsignacionFamiliar;
    @Getter @Setter private String horasPorSemana;
    @Getter @Setter private String pagoPorHora;
    @Getter @Setter private String puesto;
    @Getter @Setter private Boolean estaCancelado;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    @Getter @Setter private EmpleadoModel empleado;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_afp")
    @Getter @Setter private AfpModel afp;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="contrato")
    @JsonIgnore
	@Getter @Setter private List<IncidenciaLaboralModel> incidenciasLaborales;

    public void addIncidenciaLaboral(IncidenciaLaboralModel nuevaIncidenciaLaboral) {
        incidenciasLaborales.add(nuevaIncidenciaLaboral);
    }


    public boolean fechasValidas(Date fechaInicio, Date fechaFin) throws ContratoNotValidException {
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		if(fechaInicio.before(tiempoActual)) throw new ContratoNotValidException(FECHA_INICIO_NOT_VALID);
		if(fechaFin.after(fechaInicio)) {			//REGLA03
			double mesesDeDiferencia = (fechaFin.getTime() - fechaInicio.getTime()) / (1000D*60*60*24*30.4167);
			if(mesesDeDiferencia<3) throw new ContratoNotValidException(FECHA_FIN_3_MESES_NOT_VALID);
			if(mesesDeDiferencia>12) throw new ContratoNotValidException(FECHA_FIN_12_MESES_NOT_VALID);
		}else throw new ContratoNotValidException(FECHAS_NOT_VALID);
		return true;
	}

    public boolean horasContratadasValidas(String horasContratadasCad) throws ContratoNotValidException { //REGLA 04
		int horasContratadas = 0;
		try {
			horasContratadas = Integer.parseInt(horasContratadasCad);
		} catch (NumberFormatException nfe){
			throw new ContratoNotValidException(HORAS_CONTRATADAS_NOT_INTEGER);
		}
		if(horasContratadas>=8 && horasContratadas<=40) {
			if(horasContratadas % 4 != 0) throw new ContratoNotValidException(HORAS_CONTRATADAS_NOT_MULTIPLO_4);
		}else{
			if(horasContratadas<8) throw new ContratoNotValidException(HORAS_CONTRATADAS_MENOR_8);
			if(horasContratadas>40) throw new ContratoNotValidException(HORAS_CONTRATADAS_MAYOR_40);
		}
		return true;
	}

    public boolean pagoPorHoraValido(String pagoPorHoraCad) throws ContratoNotValidException { //REGLA05
		int pagoPorHoraTemporal = 0;
		try {
			pagoPorHoraTemporal = Integer.parseInt(pagoPorHoraCad);
		} catch (NumberFormatException nfe){
			throw new ContratoNotValidException(PAGO_POR_HORA_NOT_INTEGER);
		}
		if(pagoPorHoraTemporal<10) throw new ContratoNotValidException(PAGO_POR_HORA_MENOR_10);
		if(pagoPorHoraTemporal>60) throw new ContratoNotValidException(PAGO_POR_HORA_MAYOR_60);
		return true;
	}

    public boolean vigenciaValida(ContratoModel contratoModel) {				//REGLA01
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		if(contratoModel.getFechaFin().before(tiempoActual) || contratoModel.getEstaCancelado()) return false;
		return true;
	}

	public boolean puestoValido(String puestoCad) throws ContratoNotValidException{
		if(puestoCad.length()==0) throw new ContratoNotValidException(PUESTO_NOT_VALID);
		return true;
	}


	public boolean asignacionFamiliarValido(Boolean tieneAsignacionFamiliar) throws ContratoNotValidException {
		if(tieneAsignacionFamiliar==null) throw new ContratoNotValidException(ASIGNACION_FAMILIAR_NOT_VALID);
		return true;
	}


	public boolean identificadorValido(String idContrato) throws ContratoNotValidException {
		if(idContrato.length()==0) throw new ContratoNotValidException(ID_CONTRATO_VACIO);
		try {
			int idContratoTMP = Integer.parseInt(idContrato);
		} catch (NumberFormatException nfe){
			throw new ContratoNotValidException(ID_CONTRATO_NOT_INTEGER);
		}
		return true;
	}


	public boolean afpValido(String idAfp) throws ContratoNotValidException {
		if(idAfp.length()==0) throw new ContratoNotValidException(ID_AFP_NOT_VALID);
		try {
			int idAFPTMP = Integer.parseInt(idAfp);
		} catch (NumberFormatException nfe){
			throw new ContratoNotValidException(ID_AFP_NOT_NUMBER);
		}
		return true;
	}


	public boolean empleadoValido(String idEmpleado) throws ContratoNotValidException {
		if(idEmpleado.length()==0) throw new ContratoNotValidException(ID_EMPLEADO_NOT_VALID);
		try {
			int idEmpleadoTMP = Integer.parseInt(idEmpleado);
		} catch (NumberFormatException nfe){
			throw new ContratoNotValidException(ID_EMPLEADO_NOT_NUMBER);
		}
		return true;
	}


}
