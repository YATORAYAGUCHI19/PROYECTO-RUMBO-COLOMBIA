package empresa.org.modelo;

import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.modelo.grafo.Grafo;
import empresa.org.modelo.grafo.Vertice;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrafoCiudades implements Serializable {
    private final Grafo<Ciudad> grafo;

    public GrafoCiudades() {
        grafo = new Grafo<>();
    }

    public boolean agregarCiudad(Ciudad ciudad) {
        if (ciudad == null) {
            throw new IllegalArgumentException("La ciudad no puede ser nula");
        }
        return grafo.agregarVertice(ciudad);
    }

    public Ciudad buscarCiudad(String nombre) {
        if (nombre == null) {
            return null;
        }
        String nombreBuscado = nombre.trim();
        for (Vertice<Ciudad> vertice : grafo.obtenerVertices()) {
            if (vertice.dato.getNombre().equalsIgnoreCase(nombreBuscado)) {
                return vertice.dato;
            }
        }
        return null;
    }

    public List<Ciudad> obtenerCiudades() {
        List<Ciudad> ciudades = new ArrayList<>();
        for (Vertice<Ciudad> vertice : grafo.obtenerVertices()) {
            ciudades.add(vertice.dato);
        }
        return ciudades;
    }

    public boolean agregarConexion(String origen, String destino, double minutos) {
        Ciudad ciudadOrigen = buscarCiudad(origen);
        Ciudad ciudadDestino = buscarCiudad(destino);
        if (ciudadOrigen == null || ciudadDestino == null) {
            return false;
        }
        return grafo.agregarArista(ciudadOrigen, ciudadDestino, minutos);
    }

    public List<Ciudad> buscarRuta(String origen, String destino) {
        Ciudad ciudadOrigen = buscarCiudad(origen);
        Ciudad ciudadDestino = buscarCiudad(destino);
        List<Ciudad> ciudades = new ArrayList<>();
        if (ciudadOrigen == null || ciudadDestino == null) {
            return ciudades;
        }
        for (Vertice<Ciudad> vertice : grafo.buscarRuta(ciudadOrigen, ciudadDestino)) {
            ciudades.add(vertice.dato);
        }
        return ciudades;
    }

    public double calcularTiempoRuta(String origen, String destino) {
        Ciudad ciudadOrigen = buscarCiudad(origen);
        Ciudad ciudadDestino = buscarCiudad(destino);
        if (ciudadOrigen == null || ciudadDestino == null) {
            return -1;
        }
        List<Vertice<Ciudad>> ruta = grafo.buscarRuta(ciudadOrigen, ciudadDestino);
        if (ruta.isEmpty()) {
            return -1;
        }
        return grafo.calcularCostoRuta(ruta);
    }

}
