package empresa.org.vista;

import empresa.org.controlador.Controlador;
import empresa.org.modelo.GrafoCiudades;
import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.modelo.grafo.Grafo;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;

final class DuracionTour {
    private DuracionTour() {
    }

    static OptionalDouble calcular(Controlador controlador, List<Ciudad> visitas) {
        if (visitas.size() < 2) {
            return OptionalDouble.of(0);
        }
        Grafo<Ciudad> grafo;
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
                }
            }
        }
        return Double.NaN;
    }

    @SuppressWarnings("unchecked")
    private static Grafo<Ciudad> leerGrafo(Controlador controlador) throws ReflectiveOperationException {
        // Las API públicas solo exponen el orden del tour y el costo mínimo.
        // Se leen las aristas de la red activa para sumar tiempos directos desde la vista.
        var campoRed = Controlador.class.getDeclaredField("red");
        campoRed.setAccessible(true);
        var campoGrafo = GrafoCiudades.class.getDeclaredField("grafo");
        campoGrafo.setAccessible(true);
        return (Grafo<Ciudad>) campoGrafo.get(campoRed.get(controlador));
    }
}
