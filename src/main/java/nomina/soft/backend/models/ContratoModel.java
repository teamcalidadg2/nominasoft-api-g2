package nomina.soft.backend.models;

import java.util.Date;
import java.util.Set;

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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contrato")
@AllArgsConstructor
@NoArgsConstructor
public class ContratoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter
    private int contrato_id;

    @Getter @Setter
    private String nombres;
    @Getter @Setter
    private Date fechaInicio;
    @Getter @Setter
    private Date fechaFin; 
    @Getter @Setter
    private Boolean tieneAsignacionFamiliar;
    @Getter @Setter
    private String horasPorSemana;
    @Getter @Setter
    private String pagoPorHora;
    @Getter @Setter
    private String puesto;
    @Getter @Setter
    private Boolean cancelado;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "empleado_id")
    @Getter @Setter
    private EmpleadoModel empleado;

    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "afp_id")
    @Getter @Setter
    private AfpModel afp;

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "contrato")
	@Getter @Setter
	private Set<IncidenciaLaboralModel> incidenciaLaborales;


}
