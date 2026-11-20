package empresa.org.modelo.grafo;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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
        Deque<Vertice<E>> cola = new ArrayDeque<>();
        visitados.add(inicio);
        cola.addLast(inicio);
        while (!cola.isEmpty()) {
            Vertice<E> actual = cola.removeFirst();
            for (Arista<E> arista : actual.aristas) {
                if (!visitados.contains(arista.destino)) {
                    visitados.add(arista.destino);
                    cola.addLast(arista.destino);
                }
            }
        }
        return visitados;
    }

    public List<Vertice<E>> buscarRuta(E origen, E destino) {
        List<Vertice<E>> ruta = new ArrayList<>();
        Vertice<E> inicio = buscarVertice(origen);
        Vertice<E> fin = buscarVertice(destino);
        if (inicio == null || fin == null) {
            return ruta;
        }

        double[] costos = new double[vertices.size()];
        boolean[] visitados = new boolean[vertices.size()];
        int[] anteriores = new int[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            costos[i] = Double.POSITIVE_INFINITY;
            anteriores[i] = -1;
        }
        costos[vertices.indexOf(inicio)] = 0;

        while (true) {

            int actual = -1;
            for (int i = 0; i < vertices.size(); i++) {
                if (!visitados[i] && costos[i] < Double.POSITIVE_INFINITY
                        && (actual == -1 || costos[i] < costos[actual])) {
                    actual = i;
                }
            }
            if (actual == -1 || vertices.get(actual) == fin) {
                break;
            }
            visitados[actual] = true;

            for (Arista<E> arista : vertices.get(actual).aristas) {
                int vecino = vertices.indexOf(arista.destino);
                double nuevoCosto = costos[actual] + arista.costo;
                if (!visitados[vecino] && nuevoCosto < costos[vecino]) {
                    costos[vecino] = nuevoCosto;
                    anteriores[vecino] = actual;
                }
            }
        }

        int posicion = vertices.indexOf(fin);
        if (costos[posicion] == Double.POSITIVE_INFINITY) {
            return ruta;
        }
        while (posicion != -1) {
            ruta.add(0, vertices.get(posicion));
            posicion = anteriores[posicion];
        }
        return ruta;
    }

    public double calcularCostoRuta(List<Vertice<E>> ruta) {
        if (ruta == null || ruta.isEmpty()) {
            throw new IllegalArgumentException("La ruta no puede estar vacía");
        }
        double total = 0;
        for (Vertice<E> vertice : ruta) {
            if (!vertices.contains(vertice)) {
                throw new IllegalArgumentException("La ruta contiene un vértice ajeno al grafo");
            }
        }
        for (int i = 0; i < ruta.size() - 1; i++) {
            Arista<E> conexion = null;
            for (Arista<E> arista : ruta.get(i).aristas) {
                if (arista.destino == ruta.get(i + 1)) {
                    conexion = arista;
                    break;
                }
            }
            if (conexion == null) {
                throw new IllegalArgumentException("La ruta contiene una conexión inexistente");
            }
            total += conexion.costo;
        }
        return total;
    }
}
