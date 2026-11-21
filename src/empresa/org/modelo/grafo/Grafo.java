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

    private int posicion(E dato) {
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).dato.equals(dato)) {
                return i;
            }
        }
        return -1;
    }

    private void floyd(double[][] f, int[][] r) {
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                r[i][j] = -1;
                if (i == j) {
                    f[i][j] = 0;
                } else {
                    f[i][j] = Double.POSITIVE_INFINITY;
                }
            }
            for (Arista<E> arista : vertices.get(i).aristas) {
                f[i][posicion(arista.destino.dato)] = arista.costo;
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (f[i][k] + f[k][j] < f[i][j]) {
                        f[i][j] = f[i][k] + f[k][j];
                        r[i][j] = k;
                    }
                }
            }
        }
    }

    public double costoMinimo(E origen, E destino) {
        int a = posicion(origen);
        int b = posicion(destino);
        if (a == -1 || b == -1) {
            return Double.POSITIVE_INFINITY;
        }
        int n = vertices.size();
        double[][] f = new double[n][n];
        int[][] r = new int[n][n];
        floyd(f, r);
        return f[a][b];
    }

    public List<E> buscarRuta(E origen, E destino) {
        List<E> ruta = new ArrayList<>();
        int a = posicion(origen);
        int b = posicion(destino);
        if (a == -1 || b == -1) {
            return ruta;
        }
        int n = vertices.size();
        double[][] f = new double[n][n];
        int[][] r = new int[n][n];
        floyd(f, r);
        if (f[a][b] == Double.POSITIVE_INFINITY) {
            return ruta;
        }
        ruta.add(vertices.get(a).dato);
        if (a != b) {
            agregarIntermedios(a, b, r, ruta);
            ruta.add(vertices.get(b).dato);
        }
        return ruta;
    }

    private void agregarIntermedios(int i, int j, int[][] r, List<E> ruta) {
        int k = r[i][j];
        if (k != -1) {
            agregarIntermedios(i, k, r, ruta);
            ruta.add(vertices.get(k).dato);
            agregarIntermedios(k, j, r, ruta);
        }
    }
}