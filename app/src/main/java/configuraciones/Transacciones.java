package configuraciones;

public class Transacciones {


    //Nombre de la base de datos
    public static final String NAME_DATABASE = "PM1E0280";

    //Creacion de la tabla persona en la base de datos
    public static final String TABLA_CONTACTOS = "contactos";

    //Creacion de los atributos de la tabla
    public static final String ID = "id";
    public static final String NOMBRE = "nombre";
    public static final String TELEFONO = "telefono";
    public static final String NOTA = "nota";
    public static final String PAIS = "pais";
    public static final String IMAGEN = "imagen";


    //Creacion y eliminacion de la tabla

    public static final String CREATE_TABLE_CONTACTOS = "CREATE TABLE " + TABLA_CONTACTOS +
            "("+
            ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            NOMBRE +" TEXT, "+
            TELEFONO +" TEXT, "+
            NOTA +" TEXT, "+
            PAIS +" TEXT, "+
            IMAGEN +" TEXT"+
            ")";
    public static final String DROP_TABLE_CONTACTOS = "DROP TABLE IF EXIST " + TABLA_CONTACTOS;

    //Seleccionar todas las personas
    public static final String SELECT_TABLE_CONTACTOS = "SELECT * FROM " + TABLA_CONTACTOS;

}
