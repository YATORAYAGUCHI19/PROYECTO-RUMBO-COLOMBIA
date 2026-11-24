package empresa.org.vista;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class CatalogoGeografico {
    record Ubicacion(double latitud, double longitud, int desplazamientoX, int desplazamientoY) {
    }

    private static final Map<String, Ubicacion> POSICIONES = crearIndice();

    private static Map<String, Ubicacion> crearIndice() {
        Map<String, Ubicacion> indice = new HashMap<>();
        // Coordenadas de referencia de las cabeceras DANE publicadas por IDEAM.
        registrar(indice, 1.212455, -77.278776, -15, 5, "Pasto", "San Juan de Pasto");
        registrar(indice, 2.459949, -76.598892, -15, 2, "Popayán");
        registrar(indice, 3.414419, -76.52156, -18, 5, "Cali", "Santiago de Cali");
        registrar(indice, 4.535987, -75.680755, -43, 12, "Armenia");
        registrar(indice, 4.805323, -75.719015, -37, 0, "Pereira");
        registrar(indice, 4.649937, -74.106715, 15, 1,
                "Bogotá", "Bogotá, D.C.", "Bogotá, Distrito Capital", "Bogotá DC");
        registrar(indice, 4.432809, -75.195368, 14, 19, "Ibagué");
        registrar(indice, 2.935559, -75.277731, 14, 6, "Neiva");
        registrar(indice, 5.057687, -75.491047, -24, -14, "Manizales");
        registrar(indice, 5.539952, -73.35549, 15, 0, "Tunja");
        registrar(indice, 6.248586, -75.576002, 13, -1, "Medellín");
        registrar(indice, 7.116268, -73.132598, 14, 4, "Bucaramanga");
        registrar(indice, 7.905584, -72.508065, 13, -3, "Cúcuta", "San José de Cúcuta");
        registrar(indice, 8.759815, -75.873257, -15, 5, "Montería");
        registrar(indice, 9.30233, -75.395482, -15, -5, "Sincelejo");
        registrar(indice, 10.384988, -75.496426, -14, -1, "Cartagena", "Cartagena de Indias");
        registrar(indice, 10.97832, -74.813217, -16, -13, "Barranquilla");
        registrar(indice, 11.204773, -74.200211, 6, -31, "Santa Marta");
        registrar(indice, 10.460481, -73.25939, 13, 7, "Valledupar");
        registrar(indice, 11.52875, -72.911487, 13, -2, "Riohacha");
        return Map.copyOf(indice);
    }

    private static void registrar(Map<String, Ubicacion> indice, double latitud, double longitud,
            int desplazamientoX, int desplazamientoY, String... nombres) {
        Ubicacion ubicacion = new Ubicacion(latitud, longitud, desplazamientoX, desplazamientoY);
        for (String nombre : nombres) {
            indice.put(normalizar(nombre), ubicacion);
        }
    }

    Ubicacion buscar(String nombre) {
        return POSICIONES.get(normalizar(nombre));
    }

    Set<Ubicacion> ubicaciones() {
        return new HashSet<>(POSICIONES.values());
    }

    static String normalizar(String nombre) {
        return Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .strip();
    }
}
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
