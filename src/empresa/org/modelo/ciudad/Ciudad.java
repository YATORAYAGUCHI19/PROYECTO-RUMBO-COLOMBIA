package empresa.org.modelo.ciudad;

import empresa.org.modelo.lugar.Lugar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ciudad implements Serializable {

    private final String nombre;
    private String informacionGeneral;
    private final List<Lugar> hoteles;
    private final List<Lugar> restaurantes;
    private final List<Lugar> sitiosTuristicos;
    private final List<Lugar> centrosComerciales;
    private final List<String> platosTipicos;

    public Ciudad(String nombre, String informacionGeneral) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio");
        }
        this.nombre = nombre.trim();
        this.informacionGeneral = informacionGeneral;
        hoteles = new ArrayList<>();
        restaurantes = new ArrayList<>();
        sitiosTuristicos = new ArrayList<>();
        centrosComerciales = new ArrayList<>();
        platosTipicos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getInformacionGeneral() {
        return informacionGeneral;
    }

    public void setInformacionGeneral(String informacionGeneral) {
        this.informacionGeneral = informacionGeneral;
    }

    public void agregarHotel(Lugar hotel) {
        hoteles.add(hotel);
    }

    public void agregarRestaurante(Lugar restaurante) {
        restaurantes.add(restaurante);
    }

    public void agregarSitioTuristico(Lugar sitio) {
        sitiosTuristicos.add(sitio);
    }

    public void agregarCentroComercial(Lugar centro) {
        centrosComerciales.add(centro);
    }

    public void agregarPlatoTipico(String plato) {
        platosTipicos.add(plato);
    }

    public List<Lugar> getHoteles() {
        return new ArrayList<>(hoteles);
    }

    public List<Lugar> getRestaurantes() {
        return new ArrayList<>(restaurantes);
    }

    public List<Lugar> getSitiosTuristicos() {
        return new ArrayList<>(sitiosTuristicos);
    }

    public List<Lugar> getCentrosComerciales() {
        return new ArrayList<>(centrosComerciales);
    }

    public List<String> getPlatosTipicos() {
        return new ArrayList<>(platosTipicos);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Ciudad)) {
            return false;
        }
        Ciudad otra = (Ciudad) objeto;
        return nombre.equalsIgnoreCase(otra.nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
