package nomina.soft.backend.models;

import static nomina.soft.backend.constantes.NominaImplConstant.DESCRIPCION_NO_VALIDA;
import static nomina.soft.backend.constantes.NominaImplConstant.ID_NOMINA_NO_NUERICO;
import static nomina.soft.backend.constantes.NominaImplConstant.ID_NOMINA_NO_VALIDO;
import static nomina.soft.backend.constantes.NominaImplConstant.ID_PERIODO_NOMINA_NO_NUMERICO;
import static nomina.soft.backend.constantes.NominaImplConstant.ID_PERIODO_NOMINA_NO_VALIDO;
import static nomina.soft.backend.constantes.NominaImplConstant.PERIODO_FECHA_FIN_NO_VALIDO;

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
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.NominaNotValidException;

@JsonIgnoreProperties({ "hibernateLazyInitializer" })
@Entity
@Table(name = "nomina")
@AllArgsConstructor
@NoArgsConstructor
public class Nomina {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	@Getter
	@Setter
	private Long idNomina;

	@Getter
	@Setter
	private String descripcion;
	@Getter
	@Setter
	private Date fecha;
	@Getter
	@Setter
	private Boolean estaCerrada;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_periodo_nomina")
	@Getter
	@Setter
	private PeriodoNomina periodoNomina;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "nomina")
	@Getter
	@Setter
	private List<BoletaDePago> boletasDePago;

	public boolean validarNomina(Nomina nuevaNomina) throws ContratoNotValidException, NominaNotValidException {
		if (!(nuevaNomina.getPeriodoNomina().getFechaFin().before(nuevaNomina.getFecha())))
			throw new NominaNotValidException(PERIODO_FECHA_FIN_NO_VALIDO);
		return true;
	}

	public boolean identificadorValido(String idNomina) throws NominaNotValidException {
		if (idNomina.length() == 0)
			throw new NominaNotValidException(ID_NOMINA_NO_VALIDO);
		try {
			Integer.parseInt(idNomina);
		} catch (NumberFormatException nfe) {
			throw new NominaNotValidException(ID_NOMINA_NO_NUERICO);
		}
		return true;
	}

	public boolean periodoDeNominaValido(String idPeriodoNomina) throws NominaNotValidException {
		if (idPeriodoNomina.length() == 0)
			throw new NominaNotValidException(ID_PERIODO_NOMINA_NO_VALIDO);
		try {
			Integer.parseInt(idPeriodoNomina);
		} catch (NumberFormatException nfe) {
			throw new NominaNotValidException(ID_PERIODO_NOMINA_NO_NUMERICO);
		}
		return true;
	}

	public boolean esPeriodoCerrado(List<Nomina> nominasDePeriodo) {
		boolean estaCerrado = false;
		for (Nomina nominaTemp : nominasDePeriodo) {
			if (Boolean.TRUE.equals(nominaTemp.estaCerrada))
				estaCerrado = true;
		}
		return estaCerrado;
	}

	public boolean validarDescripcion(String descripcion) throws NominaNotValidException {
		if (descripcion.length() == 0 || descripcion.isEmpty())
			throw new NominaNotValidException(DESCRIPCION_NO_VALIDA);
		return true;
	}

}
