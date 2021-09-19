package nomina.soft.backend.models;
import java.time.LocalDateTime;

import java.util.Set;

import javax.persistence.*;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nomina")
@AllArgsConstructor
@NoArgsConstructor
public class NominaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter
    private int nomina_id;
    @Getter @Setter
    private String descripcion;
    @Getter @Setter
    private LocalDateTime fecha;
    @Getter @Setter
    private Boolean cerrada;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "periodo_nomina_id")
    @Getter @Setter
    private PeriodoNominaModel periodo_nomina;

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "nomina")
	@Getter @Setter
	private Set<BoletaDePagoModel> boletas;

    
}
