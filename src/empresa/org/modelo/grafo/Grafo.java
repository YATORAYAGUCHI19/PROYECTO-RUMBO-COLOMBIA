package empresa.org.modelo.grafo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Grafo<E> implements Serializable {

    private List<Vertice<E>> vertices;

    public Grafo() {
        vertices = new ArrayList<>();
    }

    public boolean agregarVertice(E dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
        if (buscarVertice(dato) != null) {
            return false;
        }
        vertices.add(new Vertice<>(dato));
        return true;
    }

    public Vertice<E> buscarVertice(E dato) {
        for (Vertice<E> vertice : vertices) {
            if (vertice.dato.equals(dato)) {
                return vertice;
            }
        }
        return null;
    }

    public List<Vertice<E>> obtenerVertices() {
        return new ArrayList<>(vertices);
    }

    public boolean agregarArista(E origen, E destino, double costo) {
        Vertice<E> desde = buscarVertice(origen);
        Vertice<E> hasta = buscarVertice(destino);
        if (desde == null || hasta == null || buscarArista(origen, destino) != null) {
            return false;
        }
        desde.aristas.add(new Arista<>(hasta, costo));
        return true;
    }

    public Arista<E> buscarArista(E origen, E destino) {
        Vertice<E> desde = buscarVertice(origen);
        Vertice<E> hasta = buscarVertice(destino);
        if (desde == null || hasta == null) {
            return null;
        }
        for (Arista<E> arista : desde.aristas) {
            if (arista.destino == hasta) {
                return arista;
            }
        }
        return null;
    }

    public List<Arista<E>> obtenerAristas(E origen) {
        Vertice<E> vertice = buscarVertice(origen);
        if (vertice == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(vertice.aristas);
    }

    public List<Vertice<E>> recorridoAnchura(E origen) {
        List<Vertice<E>> visitados = new ArrayList<>();
        Vertice<E> inicio = buscarVertice(origen);
        
        if (inicio == null) {
            return visitados;
        }
        Queue<Vertice<E>> cola = new LinkedList<>();
        visitados.add(inicio);
        cola.offer(inicio);
        while (!cola.isEmpty()) {
            Vertice<E> actual = cola.poll();
            for (Arista<E> arista : actual.aristas) {
                if (!visitados.contains(arista.destino)) {
                    visitados.add(arista.destino);
                    cola.offer(arista.destino);
                }
            }
        }
        return visitados;
    }
}
