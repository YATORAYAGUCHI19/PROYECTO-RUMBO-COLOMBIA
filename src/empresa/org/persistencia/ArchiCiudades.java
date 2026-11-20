package empresa.org.persistencia;

import empresa.org.modelo.GrafoCiudades;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ArchiCiudades {
    private static final String ARCHIVO = "ciudades.obj";

    public void guardarGrafo(GrafoCiudades red) throws IOException {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            salida.writeObject(red);
        }
    }

    public GrafoCiudades cargarGrafo() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            Object datos = entrada.readObject();
            if (!(datos instanceof GrafoCiudades)) {
                throw new IOException("El archivo no contiene una red de ciudades");
            }
            return (GrafoCiudades) datos;
        }
    }
}
