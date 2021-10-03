package nomina.soft.backend.constant;

public class ContratoImplConstant {

	public static final String CONTRATO_ALREADY_EXISTS = "El empleado indicado ya cuenta con un contrato vigente.";
    public static final String NO_CONTRATO_FOUND_BY_DNI = "No se encontró ningún contrato vigente asociado con el empleado con DNI: ";
    public static final String NO_CONTRATO_FOUND_BY_EMPLEADO = "No se encontró ningún contrato vigente asociado con el empleado: ";
    public static final String CONTRATO_VIGENTE_NOT_FOUND = "No se encontró ningún contrato vigente asociado con el empleado: ";
    public static final String FOUND_CONTRATO_BY_DNI = "Se encontró un contrato vigente para el empleado con DNI:  ";
    public static final String FECHA_INICIO_NOT_VALID = "La fecha de inicio del contrato debe ser igual o mayor a la fecha actual.";
    public static final String FECHA_FIN_NOT_VALID = "La fecha fin del contrato debe ser superior a la fecha de inicio con una diferencia de tres meses como mínimo y 12 meses como máximo.";
    public static final String FECHAS_NOT_VALID = "La fecha fin del contrato debe ser superior a la fecha de inicio.";
    public static final String HORAS_CONTRATADAS_NOT_INTEGER = "Las horas contratadas por semana deben ser números enteros.";
    public static final String HORAS_CONTRATADAS_RANGO_NOT_VALID = "Las horas contratadas por semana deben estar en un rango de 8 a 40 horas.";
    public static final String HORAS_CONTRATADAS_NOT_VALID = "La horas contratadas por semana deben ser múltiplos de 4.";
    public static final String PAGO_POR_HORA_NOT_INTEGER = "El pago por hora deben ser un número entero.";
    public static final String PAGO_POR_HORA_RANGO_NOT_VALID = "El pago por hora debe estar en un rango de 10 a 60 soles.";
    public static final String CONTRATO_NOT_FOUND = "No se encontró ningún contrato con ID: ";
	public static final String CONTRATO_CERRADO = "El contrato se encuentra cerrado.";
    public static final String CONTRATO_CANCELADO = "Se encontró un contrato cancelado del empleado: ";
    public static final String CONTRATO_FECHA_FIN_NOT_VALID = "Se encontró un contrato con fecha fin menor a la fecha de inicio del periodo de nómina del empleado: ";

	
}
