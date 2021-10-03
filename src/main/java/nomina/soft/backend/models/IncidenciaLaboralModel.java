package nomina.soft.backend.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incidencia_laboral")
@AllArgsConstructor
@NoArgsConstructor
public class IncidenciaLaboralModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idIncidenciaLaboral;

    @Getter @Setter private int totalHorasDeFalta;
    @Getter @Setter private int totalHorasExtras;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato")
    @JsonIgnore
    @Getter @Setter private ContratoModel contrato;

    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodo_nomina")
    @JsonIgnore
    @Getter @Setter private PeriodoNominaModel periodoNomina;

    public void reportarHoraFaltante(){
        this.totalHorasDeFalta++;
    }

    public void reportarHoraExtra(){
        this.totalHorasExtras++;
    }

    public void addTotalHorasDeFalta(int horasDeFalta){
        this.totalHorasDeFalta+=horasDeFalta;
    }

    
}
