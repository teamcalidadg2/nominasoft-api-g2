package nomina.soft.backend.constantes;

public class ContratoImplConstant {

    public static final String CONTRATO_VIGENTE_YA_EXISTE = "El empleado indicado ya cuenta con un contrato vigente.";
    public static final String CONTRATO_VIGENTE_NO_ENCONTRADO = "No se encontró ningún contrato vigente asociado con el empleado indicado.";
    public static final String CONTRATO_NO_ENCONTRADO_POR_ID = "No se encontró ningún contrato el ID indicado.";
    public static final String CONTRATO_NO_VIGENTE = "El contrato indicado ya no se encuentra vigente";
    public static final String CONTRATO_CANCELADO = "Contrato cancelado.";
    public static final String FECHA_INICIO_NOT_VALID = "La fecha de inicio debe ser mayor o igual a la fecha actual.";
    public static final String FECHA_FIN_3_MESES_NO_VALIDA = "La fecha de fin debe ser mayor o igual a 3 meses de la fecha de inicio.";
    public static final String FECHA_FIN_12_MESES_NO_VALIDA = "La fecha de fin debe ser menor o igual a 12 meses de la fecha de inicio.";
    public static final String FECHAS_NO_VALIDAS = "La fecha fin del contrato debe ser superior a la fecha de inicio.";
    public static final String HORAS_CONTRATADAS_NO_ENTERO = "El número de horas contratadas debe ser número entero.";
    public static final String HORAS_CONTRATADAS_NO_MULTIPLO_DE_4 = "El número de horas contratadas debe ser múltiplo de 4";
    public static final String HORAS_CONTRATADAS_MENOR_8 = "El número de horas contratadas debe ser mayor o igual a 8.";
    public static final String HORAS_CONTRATADAS_MAYOR_40 = "El número de horas contratadas debe ser menor o igual a 40.";
    public static final String HORAS_CONTRATADAS_VACIO = "No se ingresó el número de horas por semana.";
    public static final String PAGO_POR_HORA_NO_ENTERO = "El pago por hora deben ser un número entero.";
    public static final String PAGO_POR_HORA_MENOR_10 = "El pago por hora debe ser un número mayor o igual a 10.";
    public static final String PAGO_POR_HORA_MAYOR_60 = "El pago por hora debe ser un número menor o igual a 60.";
    public static final String PAGO_POR_HORA_VACIO = "No se ingresó pago por hora.";
    public static final String PUESTO_NO_VALIDO = "No se ingresó puesto.";
    public static final String ASIGNACION_FAMILIAR_NO_VALIDO = "No se ingresó asignación familiar.";
    public static final String ID_AFP_NO_VALIDO = "No se ingresó ID AFP.";
    public static final String ID_AFP_NO_NUMERICO = "El ID AFP solo debe contener caracteres numéricos.";
    public static final String ID_EMPLEADO_NO_VALIDO = "No se ingresó ID Empleado.";
    public static final String ID_EMPLEADO_NO_NUMERICO = "El ID Empleado solo debe contener caracteres numéricos.";
    public static final String ID_CONTRATO_VACIO = "No se ingresó ID Contrato.";
    public static final String FECHA_INICIO_VACIA = "No se ingresó fecha de inicio.";
    public static final String FECHA_FIN_VACIA = "No se ingresó fecha de fin.";
    public static final String ID_CONTRATO_NO_ENTERO = "El ID Contrato solo debe contener caracteres numéricos.";
    public static final String CONTRATO_FECHA_FIN_NOT_VALID = "Se encontró un contrato con fecha fin menor a la fecha de inicio del periodo de nómina del empleado: ";

}
