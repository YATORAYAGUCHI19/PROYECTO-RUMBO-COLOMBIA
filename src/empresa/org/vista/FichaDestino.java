package empresa.org.vista;

import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.modelo.lugar.Lugar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import static empresa.org.vista.EstiloVista.*;

final class FichaDestino extends JPanel {
    private static final String PENDIENTE = "Pendiente de investigación";
    private static final SimpleAttributeSet NOMBRE_CIUDAD = estilo(serif(27), TINTA, 0, 4);
    private static final SimpleAttributeSet TITULO_SECCION = estilo(serif(19), BOSQUE, 12, 5);
    private static final SimpleAttributeSet NOMBRE_LUGAR = estilo(sans(Font.BOLD, 14), TINTA, 8, 1);
    private static final SimpleAttributeSet DIRECCION = estilo(sans(Font.PLAIN, 13), SECUNDARIO, 0, 2);
    private static final SimpleAttributeSet DESCRIPCION = estilo(sans(Font.PLAIN, 14), SECUNDARIO, 0, 4);
    private static final SimpleAttributeSet ESTADO = estilo(sans(Font.ITALIC, 13), SECUNDARIO, 0, 5);

    private final JComboBox<Seccion> secciones = new JComboBox<>(Seccion.values());
    private final JTextPane contenido = new JTextPane();
    private Ciudad ciudad;

    FichaDestino() {
        super(new BorderLayout(0, 9));
        setOpaque(false);

        prepararSelector(secciones, "");
        JLabel titulo = etiqueta("Ver sección", sans(Font.BOLD, 13), TINTA);
        titulo.setLabelFor(secciones);
        JPanel filtro = new JPanel(new BorderLayout(0, 4));
        filtro.setOpaque(false);
        filtro.add(titulo, BorderLayout.NORTH);
        filtro.add(secciones, BorderLayout.CENTER);
        add(filtro, BorderLayout.NORTH);

        contenido.setEditable(false);
        contenido.setBackground(FONDO);
        contenido.setMargin(new Insets(10, 12, 9, 12));
        contenido.getAccessibleContext().setAccessibleName("Ficha turística");
        JScrollPane desplazamiento = new JScrollPane(contenido);
        prepararDesplazamiento(desplazamiento, FONDO);
        desplazamiento.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel marco = tarjeta(new BorderLayout(), FONDO, 14);
        marco.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        marco.add(desplazamiento);
        add(marco, BorderLayout.CENTER);

        secciones.addActionListener(evento -> actualizarContenido());
        mostrar(null);
    }

    void mostrar(Ciudad seleccionada) {
        boolean cambioDeCiudad = ciudad != seleccionada;
        ciudad = seleccionada;
        secciones.setEnabled(ciudad != null);
        if (cambioDeCiudad && secciones.getSelectedItem() != Seccion.COMPLETA) {
            secciones.setSelectedItem(Seccion.COMPLETA);
        } else {
            actualizarContenido();
        }
    }

    private void actualizarContenido() {
        DefaultStyledDocument documento = new DefaultStyledDocument();
        if (ciudad == null) {
            linea(documento, "Selecciona una ciudad para consultar su ficha.", DESCRIPCION);
        } else {
            linea(documento, ciudad.getNombre(), NOMBRE_CIUDAD);
            Seccion seleccion = (Seccion) secciones.getSelectedItem();
            for (Seccion seccion : Seccion.values()) {
                if (seccion != Seccion.COMPLETA && (seleccion == Seccion.COMPLETA || seleccion == seccion)) {
                    mostrarSeccion(documento, seccion);
                }
            }
        }
        contenido.setStyledDocument(documento);
        contenido.setCaretPosition(0);
    }

    private void mostrarSeccion(DefaultStyledDocument documento, Seccion seccion) {
        switch (seccion) {
            case GENERAL -> {
                linea(documento, seccion.toString(), TITULO_SECCION);
                String general = ciudad.getInformacionGeneral();
                if (general == null || general.isBlank()) {
                    linea(documento, PENDIENTE, ESTADO);
                } else {
                    linea(documento, general, DESCRIPCION);
                }
            }
            case HOTELES -> mostrarLugares(documento, seccion, ciudad.getHoteles());
            case RESTAURANTES -> mostrarLugares(documento, seccion, ciudad.getRestaurantes());
            case SITIOS -> mostrarLugares(documento, seccion, ciudad.getSitiosTuristicos());
            case CENTROS -> mostrarLugares(documento, seccion, ciudad.getCentrosComerciales());
            case PLATOS -> {
                List<String> platos = ciudad.getPlatosTipicos();
                tituloSeccion(documento, seccion, platos.size());
                for (String plato : platos) {
                    linea(documento, plato, NOMBRE_LUGAR);
                }
            }
            case COMPLETA -> throw new IllegalArgumentException("Selecciona una sección de la ficha.");
        }
    }

    private static void mostrarLugares(DefaultStyledDocument documento, Seccion seccion, List<Lugar> lugares) {
        tituloSeccion(documento, seccion, lugares.size());
        for (Lugar lugar : lugares) {
            linea(documento, lugar.getNombre(), NOMBRE_LUGAR);
            String direccion = lugar.getDireccion();
            if (direccion != null && !direccion.isBlank()) {
                linea(documento, direccion, DIRECCION);
            }
        }
    }

    private static void tituloSeccion(DefaultStyledDocument documento, Seccion seccion, int cantidad) {
        String titulo = cantidad > 0 ? seccion + " · " + cantidad : seccion.toString();
        linea(documento, titulo, TITULO_SECCION);
        if (cantidad == 0) {
            linea(documento, PENDIENTE, ESTADO);
        }
    }

    private static SimpleAttributeSet estilo(Font fuente, Color color, float antes, float despues) {
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setFontFamily(atributos, fuente.getFamily());
        StyleConstants.setFontSize(atributos, fuente.getSize());
        StyleConstants.setBold(atributos, fuente.isBold());
        StyleConstants.setItalic(atributos, fuente.isItalic());
        StyleConstants.setForeground(atributos, color);
        StyleConstants.setSpaceAbove(atributos, antes);
        StyleConstants.setSpaceBelow(atributos, despues);
        return atributos;
    }

    private static void linea(DefaultStyledDocument documento, String texto, SimpleAttributeSet estilo) {
        int inicio = documento.getLength();
        try {
            documento.insertString(inicio, texto + "\n", estilo);
            documento.setParagraphAttributes(inicio, texto.length(), estilo, true);
        } catch (BadLocationException error) {
            throw new IllegalStateException("No se pudo mostrar la ficha turística", error);
        }
    }

    private enum Seccion {
        COMPLETA("Ficha completa"),
        GENERAL("Información general"),
        HOTELES("Hoteles"),
        RESTAURANTES("Restaurantes"),
        SITIOS("Sitios turísticos"),
        CENTROS("Centros comerciales"),
        PLATOS("Platos típicos");

        private final String titulo;

        Seccion(String titulo) {
            this.titulo = titulo;
        }

        @Override
        public String toString() {
            return titulo;
        }
    }
}
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
