package nomina.soft.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class AfpDto {
  
    @Getter @Setter private Long idAfp;
    @Getter @Setter private String nombre;
    @Getter @Setter private float porcentajeDescuento;

}
