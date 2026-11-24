package empresa.org.modelo.grafo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GrafoLista<E> implements Grafo<E>, Serializable {

    private final List<Vertice<E>> vertices = new ArrayList<>();

    @Override
    public void insVertice(E x) {
        if (x == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
        if (posicion(x) == -1) {
            vertices.add(new Vertice<>(x));
        }
    }

    @Override
    public E obtVertice(int pos) {
        return vertices.get(pos).dato;
    }

    @Override
    public void insArista(int vi, int vf, double costo) {
        Vertice<E> desde = vertices.get(vi);
        Vertice<E> hasta = vertices.get(vf);
        if (buscar(desde, hasta) == null) {
            desde.aristas.add(new Arista<>(hasta, costo));
        }
    }

    @Override
    public void elimArista(int vi, int vf) {
        Vertice<E> hasta = vertices.get(vf);
        vertices.get(vi).aristas.removeIf(a -> a.destino == hasta);
    }

    @Override
    public double costoArista(int vi, int vf) {
        Arista<E> a = buscar(vertices.get(vi), vertices.get(vf));
        return a == null ? inf : a.costo;
    }

    @Override
    public int orden() {
        return vertices.size();
    }

    @Override
    public ArrayList<E> sucesores(int pos) {
        ArrayList<E> resultado = new ArrayList<>();
        for (Arista<E> a : vertices.get(pos).aristas) {
            resultado.add(a.destino.dato);
        }
        return resultado;
    }

    @Override
    public String mostrar() {
        StringBuilder sb = new StringBuilder();
        for (Vertice<E> v : vertices) {
            sb.append(v).append(" -> ").append(v.aristas).append('\n');
        }
        return sb.toString();
    }

    public int posicion(E dato) {         
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).dato.equals(dato)) {
                return i;
            }
        }
        return -1;
    }

    public List<Vertice<E>> obtenerVertices() {
        return new ArrayList<>(vertices);
    }

    private Arista<E> buscar(Vertice<E> desde, Vertice<E> hasta) {
        for (Arista<E> a : desde.aristas) {
            if (a.destino == hasta) {
                return a;
            }
        }
        return null;
    }

    public List<Vertice<E>> recorridoAnchura(E origen) {
        List<Vertice<E>> visitados = new ArrayList<>();
        int a = posicion(origen);
        if (a == -1) {
            return visitados;
        }
        Vertice<E> inicio = vertices.get(a);
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

    private void floyd(double[][] f, int[][] r) {
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                r[i][j] = -1;
                f[i][j] = (i == j) ? 0 : inf;
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
            return inf;
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
        if (f[a][b] == inf) {
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