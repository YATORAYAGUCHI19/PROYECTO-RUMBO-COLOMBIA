package empresa.org.vista;

import empresa.org.controlador.Controlador;
import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.modelo.lugar.Lugar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import static empresa.org.vista.EstiloVista.*;

final class GestionDestinos extends JDialog {
    private final Controlador controlador;
    private final JComboBox<Ciudad> destinos;
    private final JTextField nombre = new JTextField();
    private final JTextArea informacion = new JTextArea();
    private final JTabbedPane secciones = new JTabbedPane();
    private final EnumMap<Categoria, ListaLugares> listas = new EnumMap<>(Categoria.class);
    private Ciudad destinoActual;
    private Ciudad destinoGuardado;
    private boolean actualizandoSelector;

    GestionDestinos(VistaPrincipal propietario, Controlador controlador, List<Ciudad> ciudades, Ciudad inicial) {
        super(propietario, "Gestionar destinos", true);
        this.controlador = controlador;
        destinos = new JComboBox<>(ciudades.toArray(new Ciudad[0]));
        prepararSelector(destinos, "Nuevo destino");
        destinos.setSelectedItem(inicial);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBackground(BLANCO);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));
        contenido.add(crearCabecera(), BorderLayout.NORTH);
        contenido.add(crearSecciones(), BorderLayout.CENTER);
        contenido.add(crearAcciones(), BorderLayout.SOUTH);
        setContentPane(contenido);

        destinos.addActionListener(evento -> cambiarDestino((Ciudad) destinos.getSelectedItem()));
        cargarDestino(inicial);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                cerrar();
            }
        });
        setMinimumSize(new Dimension(620, 510));
        setPreferredSize(new Dimension(730, 590));
        pack();
        setLocationRelativeTo(propietario);
    }

    Ciudad mostrar() {
        setVisible(true);
        return destinoGuardado;
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(0, 12));
        cabecera.setOpaque(false);
        cabecera.add(etiqueta("Gestionar destinos", serif(30), TINTA), BorderLayout.NORTH);
        JPanel selector = new JPanel(new BorderLayout(9, 0));
        selector.setOpaque(false);
        JLabel titulo = etiqueta("Destino", sans(Font.BOLD, 14), TINTA);
        titulo.setLabelFor(destinos);
        selector.add(titulo, BorderLayout.WEST);
        selector.add(destinos, BorderLayout.CENTER);
        selector.add(boton("Nuevo destino", false, this::nuevoDestino), BorderLayout.EAST);
        cabecera.add(selector, BorderLayout.CENTER);
        return cabecera;
    }

    private JTabbedPane crearSecciones() {
        JPanel general = new JPanel(new BorderLayout(0, 12));
        general.setBackground(BLANCO);
        general.setBorder(BorderFactory.createEmptyBorder(14, 12, 10, 12));

        nombre.setFont(sans(Font.PLAIN, 15));
        general.add(campo("Nombre de la ciudad", nombre), BorderLayout.NORTH);
        informacion.setFont(sans(Font.PLAIN, 15));
        informacion.setLineWrap(true);
        informacion.setWrapStyleWord(true);
        informacion.setBorder(BorderFactory.createEmptyBorder(8, 9, 8, 9));
        JScrollPane desplazamiento = new JScrollPane(informacion);
        prepararDesplazamiento(desplazamiento, BLANCO);
        desplazamiento.setBorder(BorderFactory.createLineBorder(LINEA, 1, true));
        JLabel titulo = etiqueta("Información general", sans(Font.BOLD, 14), TINTA);
        titulo.setLabelFor(informacion);
        JPanel descripcion = new JPanel(new BorderLayout(0, 5));
        descripcion.setOpaque(false);
        descripcion.add(titulo, BorderLayout.NORTH);
        descripcion.add(desplazamiento, BorderLayout.CENTER);
        general.add(descripcion, BorderLayout.CENTER);
        secciones.setFont(sans(Font.BOLD, 13));
        secciones.setForeground(TINTA);
        secciones.addTab("General", general);

        for (Categoria categoria : Categoria.values()) {
            ListaLugares lista = new ListaLugares(categoria);
            listas.put(categoria, lista);
            secciones.addTab(categoria.titulo, lista);
        }
        return secciones;
    }

    private static JPanel campo(String texto, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        JLabel titulo = etiqueta(texto, sans(Font.BOLD, 14), TINTA);
        titulo.setLabelFor(componente);
        panel.add(titulo, BorderLayout.NORTH);
        componente.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINEA, 1, true),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)));
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(boton("Cancelar", false, this::cerrar));
        var guardar = boton("Guardar destino", true, this::guardar);
        acciones.add(guardar);
        getRootPane().setDefaultButton(guardar);
        return acciones;
    }

    private void nuevoDestino() {
        if (destinoActual == null) {
            secciones.setSelectedIndex(0);
            nombre.requestFocusInWindow();
        } else {
            destinos.setSelectedItem(null);
        }
    }

    private void cambiarDestino(Ciudad ciudad) {
        if (actualizandoSelector || ciudad == destinoActual) {
            return;
        }
        if (confirmarDescartar()) {
            cargarDestino(ciudad);
            if (ciudad == null) {
                nombre.requestFocusInWindow();
            }
        } else {
            seleccionar(destinoActual);
        }
    }

    private void seleccionar(Ciudad ciudad) {
        actualizandoSelector = true;
        destinos.setSelectedItem(ciudad);
        actualizandoSelector = false;
    }

    private void cargarDestino(Ciudad ciudad) {
        destinoActual = ciudad;
        nombre.setText(ciudad == null ? "" : ciudad.getNombre());
        nombre.setEditable(ciudad == null);
        informacion.setText(ciudad == null ? "" : ciudad.getInformacionGeneral());
        informacion.setCaretPosition(0);
        listas.forEach((categoria, lista) -> lista.cargar(ciudad));
        secciones.setSelectedIndex(0);
    }

    private boolean confirmarDescartar() {
        String generalGuardada = destinoActual == null ? "" : destinoActual.getInformacionGeneral();
        boolean cambioGeneral = !informacion.getText().equals(generalGuardada == null ? "" : generalGuardada);
        boolean nuevoNombre = destinoActual == null && !nombre.getText().isBlank();
        boolean nuevasFilas = listas.values().stream().anyMatch(ListaLugares::tienePendientes);
        return !(cambioGeneral || nuevoNombre || nuevasFilas) || JOptionPane.showConfirmDialog(this,
                "Hay cambios sin guardar. ¿Quieres descartarlos?", "Cambios sin guardar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void cerrar() {
        if (confirmarDescartar()) {
            dispose();
        }
    }

    private void guardar() {
        boolean nuevo = destinoActual == null;
        String nombreCiudad = nombre.getText().strip();
        if (nuevo && (nombreCiudad.isEmpty() || controlador.buscarCiudad(nombreCiudad) != null)) {
            secciones.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this, nombreCiudad.isEmpty() ? "Escribe el nombre de la ciudad."
                    : "Ya existe un destino con ese nombre. Selecciónalo para completar su ficha.",
                    "Revisa el nombre", JOptionPane.WARNING_MESSAGE);
            nombre.requestFocusInWindow();
            return;
        }

        EnumMap<Categoria, List<Lugar>> cambios = new EnumMap<>(Categoria.class);
        for (ListaLugares lista : listas.values()) {
            if (!lista.validar()) {
                return;
            }
            cambios.put(lista.categoria, lista.leerPendientes());
        }
        Ciudad ciudad = nuevo ? new Ciudad(nombreCiudad, "") : destinoActual;
        ciudad.setInformacionGeneral(informacion.getText().strip());
        cambios.forEach((categoria, lugares) -> lugares.forEach(lugar -> categoria.agregar(ciudad, lugar)));
        destinoGuardado = ciudad;
        try {
            if (nuevo) {
                controlador.agregarCiudad(ciudad);
            } else {
                controlador.guardarCambios();
            }
            dispose();
        } catch (IllegalStateException error) {
            if (nuevo) {
                destinos.addItem(ciudad);
            }
            seleccionar(ciudad);
            cargarDestino(ciudad);
            JOptionPane.showMessageDialog(this, error.getMessage()
                    + ". Los cambios están disponibles en esta sesión. Pulsa Guardar destino para reintentar.",
                    "Error de guardado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private enum Categoria {
        HOTELES("Hoteles"),
        RESTAURANTES("Restaurantes"),
        SITIOS("Sitios turísticos"),
        CENTROS("Centros comerciales"),
        PLATOS("Platos típicos");

        final String titulo;

        Categoria(String titulo) {
            this.titulo = titulo;
        }

        List<Lugar> obtener(Ciudad ciudad) {
            return switch (this) {
                case HOTELES -> ciudad.getHoteles();
                case RESTAURANTES -> ciudad.getRestaurantes();
                case SITIOS -> ciudad.getSitiosTuristicos();
                case CENTROS -> ciudad.getCentrosComerciales();
                case PLATOS -> ciudad.getPlatosTipicos().stream().map(plato -> new Lugar(plato, "")).toList();
            };
        }

        void agregar(Ciudad ciudad, Lugar lugar) {
            switch (this) {
                case HOTELES -> ciudad.agregarHotel(lugar);
                case RESTAURANTES -> ciudad.agregarRestaurante(lugar);
                case SITIOS -> ciudad.agregarSitioTuristico(lugar);
                case CENTROS -> ciudad.agregarCentroComercial(lugar);
                case PLATOS -> ciudad.agregarPlatoTipico(lugar.getNombre());
            }
        }
    }

    private final class ListaLugares extends JPanel {
        private final Categoria categoria;
        private final DefaultTableModel datos;
        private final JTable tabla;
        private int filasGuardadas;

        ListaLugares(Categoria categoria) {
            super(new BorderLayout(0, 10));
            this.categoria = categoria;
            String[] columnas = categoria == Categoria.PLATOS ? new String[]{"Nombre"}
                    : new String[]{"Nombre", "Dirección"};
            datos = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return fila >= filasGuardadas;
                }
            };
            tabla = new JTable(datos);
            tabla.getAccessibleContext().setAccessibleName(categoria.titulo);
            tabla.setFont(sans(Font.PLAIN, 14));
            tabla.getTableHeader().setFont(sans(Font.BOLD, 13));
            tabla.setRowHeight(29);
            tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabla.putClientProperty("terminateEditOnFocusLost", true);
            tabla.getTableHeader().setReorderingAllowed(false);

            setBackground(BLANCO);
            setBorder(BorderFactory.createEmptyBorder(14, 12, 10, 12));
            JLabel ayuda = etiqueta("Añade una fila y completa sus datos. Las entradas guardadas se muestran arriba.",
                    sans(Font.PLAIN, 13), SECUNDARIO);
            add(ayuda, BorderLayout.NORTH);
            JScrollPane desplazamiento = new JScrollPane(tabla);
            prepararDesplazamiento(desplazamiento, BLANCO);
            desplazamiento.setBorder(BorderFactory.createLineBorder(LINEA, 1, true));
            add(desplazamiento, BorderLayout.CENTER);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            acciones.setOpaque(false);
            acciones.add(boton("Añadir fila", false, this::anadirFila));
            var quitar = boton("Quitar fila nueva", false, this::quitarFila);
            quitar.setEnabled(false);
            tabla.getSelectionModel().addListSelectionListener(evento ->
                    quitar.setEnabled(tabla.getSelectedRow() >= filasGuardadas));
            acciones.add(quitar);
            add(acciones, BorderLayout.SOUTH);
        }

        void cargar(Ciudad ciudad) {
            if (tabla.isEditing()) {
                tabla.getCellEditor().cancelCellEditing();
            }
            datos.setRowCount(0);
            List<Lugar> guardados = ciudad == null ? List.of() : categoria.obtener(ciudad);
            filasGuardadas = guardados.size();
            for (Lugar lugar : guardados) {
                datos.addRow(new Object[]{lugar.getNombre(), lugar.getDireccion()});
            }
            tabla.clearSelection();
        }

        private boolean confirmarCelda() {
            return !tabla.isEditing() || tabla.getCellEditor().stopCellEditing();
        }

        private void anadirFila() {
            if (confirmarCelda()) {
                int nueva = datos.getRowCount();
                datos.addRow(new Object[datos.getColumnCount()]);
                tabla.changeSelection(nueva, 0, false, false);
                tabla.scrollRectToVisible(tabla.getCellRect(nueva, 0, true));
                if (tabla.editCellAt(nueva, 0)) {
                    tabla.getEditorComponent().requestFocusInWindow();
                }
            }
        }

        private void quitarFila() {
            int fila = tabla.getSelectedRow();
            if (fila >= filasGuardadas && confirmarCelda()) {
                datos.removeRow(fila);
            }
        }

        boolean tienePendientes() {
            confirmarCelda();
            for (int fila = filasGuardadas; fila < datos.getRowCount(); fila++) {
                if (!valor(fila, 0).isEmpty() || !direccion(fila).isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        boolean validar() {
            if (!confirmarCelda()) {
                return false;
            }
            for (int fila = filasGuardadas; fila < datos.getRowCount(); fila++) {
                if (valor(fila, 0).isEmpty() && !direccion(fila).isEmpty()) {
                    secciones.setSelectedComponent(this);
                    tabla.changeSelection(fila, 0, false, false);
                    JOptionPane.showMessageDialog(GestionDestinos.this, "Escribe el nombre de la fila seleccionada.",
                            categoria.titulo, JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
            return true;
        }

        List<Lugar> leerPendientes() {
            List<Lugar> lugares = new ArrayList<>();
            for (int fila = filasGuardadas; fila < datos.getRowCount(); fila++) {
                String nombreLugar = valor(fila, 0);
                if (!nombreLugar.isEmpty()) {
                    lugares.add(new Lugar(nombreLugar, direccion(fila)));
                }
            }
            return lugares;
        }

        private String direccion(int fila) {
            return categoria == Categoria.PLATOS ? "" : valor(fila, 1);
        }

        private String valor(int fila, int columna) {
            Object valor = datos.getValueAt(fila, columna);
            return valor == null ? "" : valor.toString().strip();
        }
    }
}
