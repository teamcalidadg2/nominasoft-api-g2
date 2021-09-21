package nomina.soft.backend.services;


import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.models.ContratoModel;

public interface ContratoService {
    public ContratoModel guardarContrato(ContratoDto contratoDto);

}
