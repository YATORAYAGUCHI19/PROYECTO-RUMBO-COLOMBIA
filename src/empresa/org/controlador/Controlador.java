package empresa.org.controlador;

import empresa.org.modelo.GrafoCiudades;
import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.persistencia.ArchiCiudades;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Controlador {
    private GrafoCiudades red;
    private final ArchiCiudades archivo = new ArchiCiudades();

    public Controlador() {
        try {
            red = archivo.cargarGrafo();
        } catch (FileNotFoundException e) {
            red = new GrafoCiudades();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo cargar la red de ciudades", e);
        }
    }

    public void guardarCambios() {
        try {
            archivo.guardarGrafo(red);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la red de ciudades", e);
        }
    }

    public boolean agregarCiudad(Ciudad ciudad) {
        boolean agregada = red.agregarCiudad(ciudad);
        if (agregada) {
            guardarCambios();
        }
        return agregada;
    }

    public Ciudad buscarCiudad(String nombre) {
        return red.buscarCiudad(nombre);
    }

    public List<Ciudad> obtenerCiudades() {
        return red.obtenerCiudades();
    }

    public boolean agregarConexion(String origen, String destino, double minutos) {
        boolean agregada = red.agregarConexion(origen, destino, minutos);
        if (agregada) {
            guardarCambios();
        }
        return agregada;
    }

    public double costoMinimo(String origen, String destino) {
        return red.costoMinimo(origen, destino);
    }

    public List<Ciudad> buscarRuta(String origen, String destino) {
        return red.buscarRuta(origen, destino);
    }

    public List<Ciudad> recorrerTour(String origen) {
        return red.recorrerTour(origen);
    }
    
}