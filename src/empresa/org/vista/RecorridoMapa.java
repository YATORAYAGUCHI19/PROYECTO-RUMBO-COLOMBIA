package empresa.org.vista;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

final class RecorridoMapa {
    record Fotograma(double x, double y, double angulo) {
    }

    private record Tramo(Point inicio, Point fin, double longitud) {
    }

    private final Path2D linea = new Path2D.Double();
    private final List<Tramo> tramos = new ArrayList<>();
    private final double longitud;

    RecorridoMapa(List<Point> puntos) {
        double total = 0;
        if (!puntos.isEmpty()) {
            Point inicio = puntos.getFirst();
            linea.moveTo(inicio.x, inicio.y);
            for (int i = 1; i < puntos.size(); i++) {
                Point fin = puntos.get(i);
                double distancia = inicio.distance(fin);
                if (distancia > 0) {
                    tramos.add(new Tramo(inicio, fin, distancia));
                    linea.lineTo(fin.x, fin.y);
                    total += distancia;
                }
                inicio = fin;
            }
        }
        longitud = total;
    }

    boolean tieneTramos() {
        return !tramos.isEmpty();
    }

    Shape linea() {
        return linea;
    }

    double duracionSegundos() {
        return Math.clamp(longitud / 52.0, 2.5, 20.0);
    }

    Fotograma fotograma(double fraccion) {
        double distancia = Math.clamp(fraccion, 0.0, 1.0) * longitud;
        for (Tramo tramo : tramos) {
            if (distancia <= tramo.longitud()) {
                return interpolar(tramo, distancia / tramo.longitud());
            }
            distancia -= tramo.longitud();
        }
        return interpolar(tramos.getLast(), 1.0);
    }

    private static Fotograma interpolar(Tramo tramo, double fraccion) {
        double dx = tramo.fin().x - tramo.inicio().x;
        double dy = tramo.fin().y - tramo.inicio().y;
        return new Fotograma(tramo.inicio().x + fraccion * dx,
                tramo.inicio().y + fraccion * dy, Math.atan2(dy, dx));
    }
}
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
