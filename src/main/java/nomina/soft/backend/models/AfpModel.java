package nomina.soft.backend.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "afp")
@AllArgsConstructor
@NoArgsConstructor
public class AfpModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idAfp;

    @Getter @Setter private String nombre;
    @Getter @Setter private float porcentajeDescuento;  

}
