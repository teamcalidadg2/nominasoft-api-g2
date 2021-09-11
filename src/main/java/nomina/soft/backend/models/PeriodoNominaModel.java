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
@Table(name = "periodo_nomina")
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoNominaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter
    private int periodo_nomina_id;
    @Getter @Setter
    private String descripcion;
    @Getter @Setter
    private LocalDateTime fechaInicio;
    @Getter @Setter
    private LocalDateTime fechaFin;

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "periodo_nomina")
	@Getter @Setter
	private Set<IncidenciaLaboralModel> incidenciaLaboral;

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "periodo_nomina")
	@Getter @Setter
	private Set<NominaModel> nominas;

}
