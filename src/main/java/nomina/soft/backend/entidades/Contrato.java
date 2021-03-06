package nomina.soft.backend.entidades;

import static nomina.soft.backend.statics.ContratoImplConstant.ASIGNACION_FAMILIAR_NO_VALIDO;
import static nomina.soft.backend.statics.ContratoImplConstant.FECHAS_NO_VALIDAS;
import static nomina.soft.backend.statics.ContratoImplConstant.FECHA_FIN_12_MESES_NO_VALIDA;
import static nomina.soft.backend.statics.ContratoImplConstant.FECHA_FIN_3_MESES_NO_VALIDA;
import static nomina.soft.backend.statics.ContratoImplConstant.FECHA_FIN_VACIA;
import static nomina.soft.backend.statics.ContratoImplConstant.FECHA_INICIO_NOT_VALID;
import static nomina.soft.backend.statics.ContratoImplConstant.FECHA_INICIO_VACIA;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_MAYOR_40;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_MENOR_8;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_NO_ENTERO;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_NO_MULTIPLO_DE_4;
import static nomina.soft.backend.statics.ContratoImplConstant.HORAS_CONTRATADAS_VACIO;
import static nomina.soft.backend.statics.ContratoImplConstant.ID_AFP_NO_NUMERICO;
import static nomina.soft.backend.statics.ContratoImplConstant.ID_AFP_NO_VALIDO;
import static nomina.soft.backend.statics.ContratoImplConstant.ID_CONTRATO_NO_ENTERO;
import static nomina.soft.backend.statics.ContratoImplConstant.ID_CONTRATO_VACIO;
import static nomina.soft.backend.statics.ContratoImplConstant.ID_EMPLEADO_NO_NUMERICO;
import static nomina.soft.backend.statics.ContratoImplConstant.ID_EMPLEADO_NO_VALIDO;
import static nomina.soft.backend.statics.ContratoImplConstant.PAGO_POR_HORA_MAYOR_60;
import static nomina.soft.backend.statics.ContratoImplConstant.PAGO_POR_HORA_MENOR_10;
import static nomina.soft.backend.statics.ContratoImplConstant.PAGO_POR_HORA_NO_ENTERO;
import static nomina.soft.backend.statics.ContratoImplConstant.PAGO_POR_HORA_VACIO;
import static nomina.soft.backend.statics.ContratoImplConstant.PUESTO_NO_VALIDO;

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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;

@JsonIgnoreProperties({ "hibernateLazyInitializer" })
@Entity
@Table(name = "contrato")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contrato {
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

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empleado")
	@Getter @Setter private Empleado empleado;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_afp")
	@Getter @Setter private Afp afp;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "contrato")
	@JsonIgnore @Getter @Setter private List<IncidenciaLaboral> incidenciasLaborales;

	public void addIncidenciaLaboral(IncidenciaLaboral nuevaIncidenciaLaboral) {
		incidenciasLaborales.add(nuevaIncidenciaLaboral);
	}

	public boolean fechasValidas(Date fechaInicio, Date fechaFin, Date fechaActual) throws ContratoNotValidException {
		if(fechaInicio.before(fechaActual)) throw new
			ContratoNotValidException(FECHA_INICIO_NOT_VALID);
		if (fechaFin.after(fechaInicio)) { // REGLA03
			double mesesDeDiferencia = (fechaFin.getTime() - fechaInicio.getTime()) / (1000D * 60 * 60 * 24 * 30.4167);
			if (mesesDeDiferencia < 3)
				throw new ContratoNotValidException(FECHA_FIN_3_MESES_NO_VALIDA);
			if (mesesDeDiferencia > 12)
				throw new ContratoNotValidException(FECHA_FIN_12_MESES_NO_VALIDA);
		} else
			throw new ContratoNotValidException(FECHAS_NO_VALIDAS);
		return true;
	}

	public boolean horasContratadasValidas(String horasContratadasCad) throws ContratoNotValidException { // REGLA 04
		if (horasContratadasCad.length() > 0) {
			int horasContratadas = convertirHorasContratadas(horasContratadasCad);
			if (horasContratadas >= 8 && horasContratadas <= 40) {
				if (horasContratadas % 4 != 0)
					throw new ContratoNotValidException(HORAS_CONTRATADAS_NO_MULTIPLO_DE_4);
			} else {
				if (horasContratadas < 8)
					throw new ContratoNotValidException(HORAS_CONTRATADAS_MENOR_8);
				if (horasContratadas > 40)
					throw new ContratoNotValidException(HORAS_CONTRATADAS_MAYOR_40);
			}
		} else {
			throw new ContratoNotValidException(HORAS_CONTRATADAS_VACIO);
		}

		return true;
	}

	private int convertirHorasContratadas(String cadena) throws ContratoNotValidException{
		int horasPorSemanaTemp;
		try {
			horasPorSemanaTemp = Integer.parseInt(cadena);
		} catch (NumberFormatException nfe) {
			throw new ContratoNotValidException(HORAS_CONTRATADAS_NO_ENTERO);
		}
		return horasPorSemanaTemp;
	}

	public boolean pagoPorHoraValido(String pagoPorHoraCad) throws ContratoNotValidException { // REGLA05
		int pagoPorHoraTemporal = 0;
		if (pagoPorHoraCad.length() > 0 && !pagoPorHoraCad.isEmpty()) {
			try {
				pagoPorHoraTemporal = Integer.parseInt(pagoPorHoraCad);
			} catch (NumberFormatException nfe) {
				throw new ContratoNotValidException(PAGO_POR_HORA_NO_ENTERO);
			}
			if (pagoPorHoraTemporal < 10)
				throw new ContratoNotValidException(PAGO_POR_HORA_MENOR_10);
			if (pagoPorHoraTemporal > 60)
				throw new ContratoNotValidException(PAGO_POR_HORA_MAYOR_60);
		} else {
			throw new ContratoNotValidException(PAGO_POR_HORA_VACIO);
		}
		return true;
	}

	public boolean vigenciaValida(Contrato contratoModel, Date fechaActual) { // REGLA01
		return contratoModel.getFechaFin().after(fechaActual) && !contratoModel.getEstaCancelado();
	}

	public boolean puestoValido(String puestoCad) throws ContratoNotValidException {
		if (puestoCad.length() == 0)
			throw new ContratoNotValidException(PUESTO_NO_VALIDO);
		return true;
	}

	public boolean asignacionFamiliarValido(Boolean tieneAsignacionFamiliar) throws ContratoNotValidException {
		if (tieneAsignacionFamiliar == null)
			throw new ContratoNotValidException(ASIGNACION_FAMILIAR_NO_VALIDO);
		return true;
	}

	public boolean afpValido(String idAfp) throws ContratoNotValidException {
		if (idAfp.length() == 0)
			throw new ContratoNotValidException(ID_AFP_NO_VALIDO);
		try {
			Integer.parseInt(idAfp);
		} catch (NumberFormatException nfe) {
			throw new ContratoNotValidException(ID_AFP_NO_NUMERICO);
		}
		return true;
	}

	public boolean empleadoValido(String idEmpleado) throws ContratoNotValidException {
		if (idEmpleado.length() == 0)
			throw new ContratoNotValidException(ID_EMPLEADO_NO_VALIDO);
		try {
			Integer.parseInt(idEmpleado);
		} catch (NumberFormatException nfe) {
			throw new ContratoNotValidException(ID_EMPLEADO_NO_NUMERICO);
		}
		return true;
	}

	public boolean validarIdentificador(String idContrato) throws ContratoNotValidException {
		if (idContrato.length() == 0)
			throw new ContratoNotValidException(ID_CONTRATO_VACIO);
		try {
			Integer.parseInt(idContrato);
		} catch (NumberFormatException nfe) {
			throw new ContratoNotValidException(ID_CONTRATO_NO_ENTERO);
		}
		return true;
	}

	public boolean validarFechas(Date fechaInicio, Date fechaFin) throws ContratoNotValidException {
		if (fechaInicio == null)
			throw new ContratoNotValidException(FECHA_INICIO_VACIA);
		if (fechaFin == null)
			throw new ContratoNotValidException(FECHA_FIN_VACIA);

		return true;
	}

}
