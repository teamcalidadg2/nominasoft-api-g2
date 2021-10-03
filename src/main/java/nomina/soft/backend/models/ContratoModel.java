package nomina.soft.backend.models;

import static nomina.soft.backend.constant.ContratoImplConstant.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
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
import lombok.ToString;
import nomina.soft.backend.exception.domain.ContratoNotValidException;

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
		boolean fechasValidas = true;
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		if(!(fechaInicio.after(tiempoActual) || fechaInicio.equals(tiempoActual))) {//REGLA02
			fechasValidas = false;
			throw new ContratoNotValidException(FECHA_INICIO_NOT_VALID);
		}
		
		if((fechaFin.after(fechaInicio))) {			//REGLA03
			int mesesDeDiferencia = Period.between(LocalDate.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()),
													LocalDate.ofInstant(fechaFin.toInstant(), ZoneId.systemDefault()))
													.getMonths();
			if(!(mesesDeDiferencia>=3 && mesesDeDiferencia<=12)) {
				fechasValidas = false;
				throw new ContratoNotValidException(FECHA_FIN_NOT_VALID);
			}
		}else {
			fechasValidas = false;
			throw new ContratoNotValidException(FECHAS_NOT_VALID);
		}
		return fechasValidas;
	}

    public boolean horasContratadasValidas(String horasContratadasCad) throws ContratoNotValidException { //REGLA 04
		int horasContratadas = 0;
		boolean horasContratadasValidas = true;
		try {
			horasContratadas = Integer.parseInt(horasContratadasCad);
		} catch (NumberFormatException nfe){
			horasContratadasValidas = false;
			throw new ContratoNotValidException(HORAS_CONTRATADAS_NOT_INTEGER);
		}
		if(horasContratadas>=8 && horasContratadas<=40) {
			if(horasContratadas % 4 != 0) {
				horasContratadasValidas = false;
				throw new ContratoNotValidException(HORAS_CONTRATADAS_NOT_VALID);
			}
		}else {
			horasContratadasValidas = false;
			throw new ContratoNotValidException(HORAS_CONTRATADAS_RANGO_NOT_VALID);
		}
		return horasContratadasValidas;
	}

    public boolean pagoPorHoraValido(String pagoPorHoraCad) throws ContratoNotValidException { //REGLA05
		int pagoPorHoraTemporal = 0;
		boolean pagoPorHoraValido = true;
		try {
			pagoPorHoraTemporal = Integer.parseInt(pagoPorHoraCad);
		} catch (NumberFormatException nfe){
			pagoPorHoraValido = false;
			throw new ContratoNotValidException(PAGO_POR_HORA_NOT_INTEGER);
		}
		if(!(pagoPorHoraTemporal>=10 && pagoPorHoraTemporal<=60)) {
			pagoPorHoraValido = false;
			throw new ContratoNotValidException(PAGO_POR_HORA_RANGO_NOT_VALID);
		}
		return pagoPorHoraValido;
	}

    public boolean vigenciaValida(ContratoModel contratoModel) {				//REGLA01
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		boolean vigenciaValida = true;
		if(!((contratoModel.getFechaFin().after(tiempoActual) || 
				contratoModel.getFechaFin().equals(tiempoActual)) &&
				!contratoModel.getEstaCancelado())) {
			vigenciaValida = false;
		}
		return vigenciaValida;
	}


}
