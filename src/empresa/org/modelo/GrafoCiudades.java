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
}
