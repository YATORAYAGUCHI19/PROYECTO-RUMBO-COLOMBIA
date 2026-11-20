package empresa.org.modelo.grafo;

import java.io.Serializable;

public class Arista<E> implements Serializable {
    public Vertice<E> destino;
    public double costo;

    public Arista(Vertice<E> destino, double costo) {
        if (destino == null) {
            throw new IllegalArgumentException("El destino no puede ser nulo");
        }
        if (!Double.isFinite(costo) || costo < 0) {
            throw new IllegalArgumentException("El costo debe ser finito y no negativo");
        }
        this.destino = destino;
        this.costo = costo;
    }

    @Override
    public String toString() {
        return destino + " (" + costo + ")";
    }
}
