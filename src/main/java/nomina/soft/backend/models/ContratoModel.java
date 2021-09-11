package nomina.soft.backend.models;
import java.time.LocalDate;
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
    private LocalDateTime fechaInicio;
    @Getter @Setter
    private LocalDateTime fechaFin; 
    @Getter @Setter
    private Boolean tieneAsignacionFamiliar;
    @Getter @Setter
    private int horasPorSemana;
    @Getter @Setter
    private Double pagoPorHora;
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

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "contrato")
	@Getter @Setter
	private Set<BoletaDePagoModel> boletasDePagos;





}
