package modelos;

import java.io.Serializable;

public class Contacto implements Serializable {

    private int id;
    private String nombre;
    private String telefono;
    private String nota;
    private String pais;
    private String imagen;

    public Contacto(int id, String nombre, String telefono, String nota, String pais, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.pais = pais;
        this.imagen = imagen;
    }

    public Contacto(){}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTerminacion(){
        String terminacion=""+
                pais.charAt(pais.length()-4)+
                pais.charAt(pais.length()-3)+
                pais.charAt(pais.length()-2)
                ;

        return terminacion;
    }

    public String getContactoString() {
        return nombre+"   "+"+"+getTerminacion()+telefono;
    }


    public String getTelCall() {
        return "tel:+"+getTerminacion()+" "+telefono;
    }

    @Override
    public String toString() {
        return  nombre + " | " + telefono;
    }
}
