package empresa.org.vista;

import empresa.org.controlador.Controlador;
import empresa.org.modelo.ciudad.Ciudad;
import empresa.org.modelo.lugar.Lugar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PanelAdministracion extends JPanel {
    private final Controlador controlador;
    private final Color verde = new Color(23, 75, 60);
    private final Color fondo = new Color(245, 248, 244);
    private final JComboBox<Ciudad> ciudadEditar = new JComboBox<>();
    private final JComboBox<Ciudad> conexionOrigen = new JComboBox<>();
    private final JComboBox<Ciudad> conexionDestino = new JComboBox<>();

    public PanelAdministracion(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        for (JComboBox<Ciudad> lista : Arrays.asList(ciudadEditar, conexionOrigen, conexionDestino)) {
            ajustarCampo(lista);
        }
        add(crearAdministracion(), BorderLayout.CENTER);
        actualizarCiudades();
    }

    public JPanel crearAdministracion() {
        JPanel panel = pagina("Administrar ciudades", "Registra información y conexiones de viaje.");
        JTabbedPane opciones = new JTabbedPane();
        opciones.addTab("Ciudades", formularioCiudad());
        opciones.addTab("Lugares y platos", formularioLugar());
        opciones.addTab("Conexiones", formularioConexion());
        panel.add(opciones, BorderLayout.CENTER);
        return panel;
    }

    private JPanel formularioCiudad() {
        JPanel panel = tarjeta();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField nombre = new JTextField();
        ajustarCampo(nombre);
        JTextArea informacion = new JTextArea(4, 25);
        informacion.setLineWrap(true);
        informacion.setWrapStyleWord(true);
        panel.add(subtitulo("Agregar ciudad"));
        panel.add(espacio(14));
        panel.add(new JLabel("Nombre"));
        panel.add(nombre);
        panel.add(espacio(10));
        panel.add(new JLabel("Información general"));
        JScrollPane campo = new JScrollPane(informacion);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        panel.add(campo);
        panel.add(espacio(14));
        JButton agregar = boton("Guardar ciudad");
        agregar.addActionListener(e -> ejecutar(() -> {
            if (!controlador.agregarCiudad(new Ciudad(nombre.getText(), informacion.getText().trim()))) {
                throw new IllegalArgumentException("Ya existe una ciudad con ese nombre.");
            }
            nombre.setText(""); informacion.setText(""); actualizarCiudades();
            aviso("Ciudad agregada correctamente.");
        }));
        panel.add(agregar);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel formularioLugar() {
        JPanel panel = tarjeta();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Hotel", "Restaurante", "Sitio turístico", "Centro comercial", "Plato típico", "Información general"});
        JTextField nombre = new JTextField();
        JTextField direccion = new JTextField();
        ajustarCampo(nombre);
        ajustarCampo(direccion);
        ajustarCampo(tipo);
        panel.add(subtitulo("Agregar información a una ciudad"));
        panel.add(espacio(12));
        panel.add(new JLabel("Ciudad")); panel.add(ciudadEditar);
        panel.add(espacio(10)); panel.add(new JLabel("Categoría")); panel.add(tipo);
        panel.add(espacio(10)); panel.add(new JLabel("Nombre o información general")); panel.add(nombre);
        panel.add(espacio(10)); panel.add(new JLabel("Dirección (para lugares)")); panel.add(direccion);
        panel.add(espacio(14));
        JButton agregar = boton("Guardar información");
        agregar.addActionListener(e -> ejecutar(() -> {
            Ciudad ciudad = seleccion(ciudadEditar);
            String valor = nombre.getText().trim();
            String dir = direccion.getText().trim();
            if (valor.isEmpty()) throw new IllegalArgumentException("Escribe el nombre o la información general.");
            int categoria = tipo.getSelectedIndex();
            if (categoria < 4 && dir.isEmpty()) throw new IllegalArgumentException("Escribe la dirección del lugar.");
            switch (categoria) {
                case 0: ciudad.agregarHotel(new Lugar(valor, dir)); break;
                case 1: ciudad.agregarRestaurante(new Lugar(valor, dir)); break;
                case 2: ciudad.agregarSitioTuristico(new Lugar(valor, dir)); break;
                case 3: ciudad.agregarCentroComercial(new Lugar(valor, dir)); break;
                case 4: ciudad.agregarPlatoTipico(valor); break;
                default: ciudad.setInformacionGeneral(valor);
            }
            controlador.guardarCambios(); nombre.setText(""); direccion.setText("");
            aviso("Información guardada correctamente.");
        }));
        panel.add(agregar); panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel formularioConexion() {
        JPanel panel = tarjeta();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField minutos = new JTextField();
        ajustarCampo(minutos);
        panel.add(subtitulo("Agregar conexión terrestre")); panel.add(espacio(12));
        panel.add(new JLabel("Origen")); panel.add(conexionOrigen); panel.add(espacio(10));
        panel.add(new JLabel("Destino")); panel.add(conexionDestino); panel.add(espacio(10));
        panel.add(new JLabel("Tiempo de viaje en minutos")); panel.add(minutos); panel.add(espacio(14));
        JButton agregar = boton("Guardar conexión");
        agregar.addActionListener(e -> ejecutar(() -> {
            Ciudad a = seleccion(conexionOrigen), b = seleccion(conexionDestino);
            if (a.equals(b)) throw new IllegalArgumentException("Selecciona ciudades diferentes.");
            double tiempo;
            try { tiempo = Double.parseDouble(minutos.getText().trim()); }
            catch (NumberFormatException ex) { throw new IllegalArgumentException("Escribe un tiempo válido en minutos."); }
            if (!Double.isFinite(tiempo) || tiempo <= 0) throw new IllegalArgumentException("El tiempo debe ser mayor que cero.");
            if (!controlador.agregarConexion(a.getNombre(), b.getNombre(), tiempo)) throw new IllegalArgumentException("La conexión ya existe.");
            minutos.setText(""); aviso("Conexión guardada. Registra el regreso por separado si existe.");
        }));
        panel.add(agregar); panel.add(espacio(12));
        panel.add(new JLabel("La ida y el regreso se registran por separado."));
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    public void actualizarCiudades() {
        for (JComboBox<Ciudad> lista : Arrays.asList(ciudadEditar, conexionOrigen, conexionDestino)) {
            Ciudad anterior = (Ciudad) lista.getSelectedItem();
            lista.removeAllItems();
            for (Ciudad ciudad : controlador.obtenerCiudades()) {
                lista.addItem(ciudad);
            }
            if (anterior != null) {
                lista.setSelectedItem(anterior);
            }
        }
    }

    private Ciudad seleccion(JComboBox<Ciudad> lista) {
        Ciudad ciudad = (Ciudad) lista.getSelectedItem();
        if (ciudad == null) {
            throw new IllegalArgumentException("Primero agrega una ciudad en Administrar información.");
        }
        return ciudad;
    }

    private void ejecutar(Runnable accion) {
        try { accion.run(); }
        catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Rumbo Colombia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void aviso(String mensaje) { JOptionPane.showMessageDialog(this, mensaje, "Rumbo Colombia", JOptionPane.INFORMATION_MESSAGE); }
    private JPanel pagina(String titulo, String descripcion) {
        JPanel pagina = new JPanel(new BorderLayout(0, 20)); pagina.setBackground(fondo); pagina.setBorder(new EmptyBorder(24, 28, 24, 28));
        JPanel cabecera = new JPanel(); cabecera.setOpaque(false); cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel(titulo); nombre.setFont(new Font("Serif", Font.PLAIN, 30)); nombre.setForeground(verde);
        JLabel detalle = new JLabel(descripcion); detalle.setForeground(new Color(100, 118, 106));
        cabecera.add(nombre); cabecera.add(espacio(5)); cabecera.add(detalle); pagina.add(cabecera, BorderLayout.NORTH); return pagina;
    }
    private JPanel tarjeta() { JPanel panel = new JPanel(); panel.setBackground(Color.WHITE); panel.setBorder(new EmptyBorder(22, 22, 22, 22)); return panel; }
    private JLabel subtitulo(String texto) { JLabel label = new JLabel(texto); label.setFont(new Font("SansSerif", Font.BOLD, 17)); label.setForeground(verde); return label; }
    private JButton boton(String texto) { JButton boton = new BotonPrincipal(texto); boton.setAlignmentX(LEFT_ALIGNMENT); boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); return boton; }
    private void ajustarCampo(JComponent campo) {
        campo.setPreferredSize(new Dimension(300, 40));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }
    private java.awt.Component espacio(int alto) { return Box.createRigidArea(new Dimension(0, alto)); }

}
