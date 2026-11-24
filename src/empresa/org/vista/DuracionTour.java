package empresa.org.vista;

import empresa.org.controlador.Controlador;
import empresa.org.modelo.GrafoCiudades;
import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.modelo.grafo.Grafo;
<<<<<<< Updated upstream
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
=======
import empresa.org.modelo.grafo.GrafoLista;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
>>>>>>> Stashed changes

final class DuracionTour {
    private DuracionTour() {
    }

    static OptionalDouble calcular(Controlador controlador, List<Ciudad> visitas) {
        if (visitas.size() < 2) {
            return OptionalDouble.of(0);
        }
<<<<<<< Updated upstream
        Grafo<Ciudad> grafo;
=======
        GrafoLista<Ciudad> grafo;
>>>>>>> Stashed changes
        try {
            grafo = leerGrafo(controlador);
        } catch (ReflectiveOperationException | SecurityException error) {
            return OptionalDouble.empty();
        }

        double total = 0;
        for (int i = 1; i < visitas.size(); i++) {
            double minutos = duracionTraslado(grafo, visitas.get(i - 1), visitas.get(i));
            if (!Double.isFinite(minutos)) {
                return OptionalDouble.empty();
            }
            total += minutos;
        }
        return Double.isFinite(total) ? OptionalDouble.of(total) : OptionalDouble.empty();
    }

<<<<<<< Updated upstream
    private record Paso(Ciudad ciudad, double minutos) {
    }

    private static double duracionTraslado(Grafo<Ciudad> grafo, Ciudad origen, Ciudad destino) {
        ArrayDeque<Paso> pendientes = new ArrayDeque<>();
        Set<String> visitadas = new HashSet<>();
        pendientes.push(new Paso(origen, 0));
        while (!pendientes.isEmpty()) {
            Paso paso = pendientes.pop();
            if (!visitadas.add(paso.ciudad().getNombre())) {
                continue;
            }
            var directa = grafo.buscarArista(paso.ciudad(), destino);
            if (directa != null) {
                return paso.minutos() + directa.costo;
            }

            // El orden de registro decide el camino en profundidad. Los minutos solo se suman.
            var aristas = grafo.obtenerAristas(paso.ciudad());
            for (int i = aristas.size() - 1; i >= 0; i--) {
                var arista = aristas.get(i);
                if (!visitadas.contains(arista.destino.dato.getNombre())) {
                    pendientes.push(new Paso(arista.destino.dato, paso.minutos() + arista.costo));
=======
    private record Paso(int vertice, double minutos) {
    }

    private static double duracionTraslado(GrafoLista<Ciudad> grafo, Ciudad origen, Ciudad destino) {
        int inicio = grafo.posicion(origen);
        int fin = grafo.posicion(destino);
        if (inicio == -1 || fin == -1) {
            return Double.NaN;
        }
        ArrayDeque<Paso> pendientes = new ArrayDeque<>();
        boolean[] visitado = new boolean[grafo.orden()];
        pendientes.push(new Paso(inicio, 0));
        while (!pendientes.isEmpty()) {
            Paso paso = pendientes.pop();
            if (visitado[paso.vertice()]) {
                continue;
            }
            visitado[paso.vertice()] = true;
            double directo = grafo.costoArista(paso.vertice(), fin);
            if (directo != Grafo.inf) {
                return paso.minutos() + directo;
            }

            // El orden de registro decide el camino en profundidad. Los minutos solo se suman.
            ArrayList<Ciudad> sucesores = grafo.sucesores(paso.vertice());
            for (int i = sucesores.size() - 1; i >= 0; i--) {
                int v = grafo.posicion(sucesores.get(i));
                if (!visitado[v]) {
                    double costo = grafo.costoArista(paso.vertice(), v);
                    pendientes.push(new Paso(v, paso.minutos() + costo));
>>>>>>> Stashed changes
                }
            }
        }
        return Double.NaN;
    }

    @SuppressWarnings("unchecked")
<<<<<<< Updated upstream
    private static Grafo<Ciudad> leerGrafo(Controlador controlador) throws ReflectiveOperationException {
        // Las API públicas solo exponen el orden del tour y el costo mínimo.
        // Se leen las aristas de la red activa para sumar tiempos directos desde la vista.
=======
    private static GrafoLista<Ciudad> leerGrafo(Controlador controlador) throws ReflectiveOperationException {
>>>>>>> Stashed changes
        var campoRed = Controlador.class.getDeclaredField("red");
        campoRed.setAccessible(true);
        var campoGrafo = GrafoCiudades.class.getDeclaredField("grafo");
        campoGrafo.setAccessible(true);
<<<<<<< Updated upstream
        return (Grafo<Ciudad>) campoGrafo.get(campoRed.get(controlador));
    }
}
=======
        return (GrafoLista<Ciudad>) campoGrafo.get(campoRed.get(controlador));
    }
}
>>>>>>> Stashed changes
