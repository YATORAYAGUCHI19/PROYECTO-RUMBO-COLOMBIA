package empresa.org.modelo.grafo;

import java.util.ArrayList;

public interface Grafo<E> {

    void insVertice(E x);

    E obtVertice(int pos);

    void insArista(int vi, int vf, double costo);
    
    void elimArista(int vi, int vf);

    double costoArista(int vi, int vf);

    int orden();// size

    ArrayList<E> sucesores(int pos);

    String mostrar();

    //  double inf=999999;
    Double inf = Double.POSITIVE_INFINITY;
}
