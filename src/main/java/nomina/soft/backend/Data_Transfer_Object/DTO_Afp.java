package nomina.soft.backend.Data_Transfer_Object;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class DTO_Afp {
  
    @Getter @Setter private Long idAfp;
    @Getter @Setter private String nombre;
    @Getter @Setter private float porcentajeDescuento;

}
