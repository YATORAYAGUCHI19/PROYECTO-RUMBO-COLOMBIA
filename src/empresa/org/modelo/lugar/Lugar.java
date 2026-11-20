package empresa.org.modelo.lugar;

import java.io.Serializable;

public class Lugar implements Serializable {

    private final String nombre;
    private final String direccion;

    public Lugar(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public String toString() {
        return nombre + " - " + direccion;
    }
}
