package empresa.org.modelo.grafo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertice<E> implements Serializable {
    public E dato;
    public List<Arista<E>> aristas;

    public Vertice(E dato) {
        this.dato = dato;
        this.aristas = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "" + dato;
    }
}
