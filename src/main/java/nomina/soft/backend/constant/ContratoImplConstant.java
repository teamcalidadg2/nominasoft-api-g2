package nomina.soft.backend.constant;

public class ContratoImplConstant {

	public static final String CONTRATO_ALREADY_EXISTS = "El empleado indicado ya cuenta con un contrato vigente.";
    public static final String NO_CONTRATO_FOUND_BY_DNI = "No se encontró ningún contrato vigente asociado con el empleado con DNI: ";
    public static final String NO_CONTRATO_FOUND_BY_EMPLEADO = "No se encontró ningún contrato vigente asociado con el empleado: ";
    public static final String CONTRATO_VIGENTE_NOT_FOUND = "No se encontró ningún contrato vigente asociado con el empleado: ";
    public static final String FOUND_CONTRATO_BY_DNI = "Se encontró un contrato vigente para el empleado con DNI:  ";
    public static final String FECHA_INICIO_NOT_VALID = "La fecha de inicio debe ser mayor o igual a la fecha actual.";
    public static final String FECHA_FIN_3_MESES_NOT_VALID = "La fecha de fin debe ser mayor o igual a 3 meses de la fecha de inicio.";
    public static final String FECHA_FIN_12_MESES_NOT_VALID = "La fecha de fin debe ser menor o igual a 12 meses de la fecha de inicio.";
    public static final String FECHAS_NOT_VALID = "La fecha fin del contrato debe ser superior a la fecha de inicio.";
    public static final String HORAS_CONTRATADAS_NOT_INTEGER = "El número de horas contratadas debe ser número entero.";
    public static final String HORAS_CONTRATADAS_NOT_MULTIPLO_4 = "El número de horas contratadas debe ser múltiplo de 4";
    public static final String HORAS_CONTRATADAS_MENOR_8 = "El número de horas contratadas debe ser mayor o igual a 8.";
    public static final String HORAS_CONTRATADAS_MAYOR_40 = "El número de horas contratadas debe ser menor o igual a 40.";
    public static final String PAGO_POR_HORA_NOT_INTEGER = "El pago por hora deben ser un número entero.";
    public static final String PAGO_POR_HORA_MENOR_10 = "El pago por hora debe ser un número mayor o igual a 10.";
    public static final String PAGO_POR_HORA_MAYOR_60 = "El pago por hora debe ser un número menor o igual a 60.";
    public static final String CONTRATO_NOT_FOUND = "El contrato indicado no existe.";
	public static final String CONTRATO_CERRADO = "El contrato se encuentra cerrado.";
    public static final String CONTRATO_CANCELADO = "Se encontró un contrato cancelado del empleado: ";
    public static final String PUESTO_NOT_VALID = "No se ingresó puesto.";
    public static final String ID_AFP_NOT_VALID = "No se ingresó ID AFP.";
    public static final String ID_AFP_NOT_NUMBER = "El ID AFP solo debe contener caracteres numéricos.";
    public static final String ID_EMPLEADO_NOT_VALID = "No se ingresó ID Empleado.";
    public static final String ID_EMPLEADO_NOT_NUMBER = "El ID Empleado solo debe contener caracteres numéricos.";
    public static final String ID_CONTRATO_VACIO = "No se ingresó ID Contrato.";
    public static final String ID_CONTRATO_NOT_INTEGER = "El ID Contrato solo debe contener caracteres numéricos.";
    public static final String ASIGNACION_FAMILIAR_NOT_VALID = "No se ingresó asignación familiar.";
    public static final String CONTRATO_NOT_VIGENTE = "El contrato indicado no se encuentra vigente.";
    public static final String CONTRATO_FECHA_FIN_NOT_VALID = "Se encontró un contrato con fecha fin menor a la fecha de inicio del periodo de nómina del empleado: ";
}
