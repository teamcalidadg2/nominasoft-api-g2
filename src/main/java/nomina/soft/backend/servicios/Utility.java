package nomina.soft.backend.servicios;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

@Service
public final class Utility {

    private Utility() {
    }

    public static final String TIME_ZONE = "America/Lima";

    public Date arreglarZonaHorariaFecha(Date fecha) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(fecha);
        return isoFormat.parse(strDate);
    }

    public Date obtenerFechaActual() throws ParseException{
        return this.arreglarZonaHorariaFecha(java.sql.Timestamp.valueOf(LocalDateTime.now()));
    }
}
