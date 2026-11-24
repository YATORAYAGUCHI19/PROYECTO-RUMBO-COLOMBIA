package empresa.org.modelo;

import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.modelo.grafo.Grafo;
import empresa.org.modelo.grafo.GrafoLista;
import empresa.org.modelo.grafo.Vertice;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrafoCiudades implements Serializable {

    private final GrafoLista<Ciudad> grafo;

    public GrafoCiudades() {
        grafo = new GrafoLista<>();
    }

    public boolean agregarCiudad(Ciudad ciudad) {
        if (ciudad == null) {
            throw new IllegalArgumentException("La ciudad no puede ser nula");
        }
        if (grafo.posicion(ciudad) != -1) {
            return false;
        }
        grafo.insVertice(ciudad);
        return true;
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
        int vi = grafo.posicion(ciudadOrigen);
        int vf = grafo.posicion(ciudadDestino);
        if (grafo.costoArista(vi, vf) != Grafo.inf) {
            return false;
        }
        grafo.insArista(vi, vf, minutos);
        return true;
    }

    // Costo mínimo entre dos ciudades (infinito si no hay camino)
    public double costoMinimo(String origen, String destino) {
        Ciudad ciudadOrigen = buscarCiudad(origen);
        Ciudad ciudadDestino = buscarCiudad(destino);
        if (ciudadOrigen == null || ciudadDestino == null) {
            return Double.POSITIVE_INFINITY;
        }
        return grafo.costoMinimo(ciudadOrigen, ciudadDestino);
    }

    // Ciudades a seguir, en orden (lista vacía si no hay ruta)
    public List<Ciudad> buscarRuta(String origen, String destino) {
        Ciudad ciudadOrigen = buscarCiudad(origen);
        Ciudad ciudadDestino = buscarCiudad(destino);
        if (ciudadOrigen == null || ciudadDestino == null) {
            return new ArrayList<>();
        }
        return grafo.buscarRuta(ciudadOrigen, ciudadDestino);
    }

    // Tour saliendo de una ciudad, sin importar el costo
    public List<Ciudad> recorrerTour(String origen) {
        List<Ciudad> tour = new ArrayList<>();
        Ciudad ciudadOrigen = buscarCiudad(origen);
        if (ciudadOrigen == null) {
            return tour;
        }
        for (Vertice<Ciudad> vertice : grafo.recorridoAnchura(ciudadOrigen)) {
            tour.add(vertice.dato);
        }
        return tour;
    }
}
