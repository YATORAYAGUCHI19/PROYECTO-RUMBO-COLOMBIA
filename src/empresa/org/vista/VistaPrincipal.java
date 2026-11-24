package empresa.org.vista;

import empresa.org.controlador.Controlador;
import empresa.org.modelo.ciudad.Ciudad;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import static empresa.org.vista.EstiloVista.*;

public final class VistaPrincipal extends JFrame {
    private static final Locale ESPANOL_COLOMBIA = Locale.forLanguageTag("es-CO");
    private static final String PLANIFICAR = "Planificar";
    private static final String TOUR = "Tour";
    private static final String FICHA = "Ficha";

    private final Controlador controlador;
    private final JComboBox<Ciudad> origen = new JComboBox<>();
    private final JComboBox<Ciudad> destino = new JComboBox<>();
    private final JComboBox<Ciudad> origenTour = new JComboBox<>();
    private final JComboBox<Ciudad> ciudadConsultada = new JComboBox<>();
    private final CardLayout cambioPanel = new CardLayout();
    private final JPanel paneles = new JPanel(cambioPanel);
    private final JToggleButton elegirPlan = pestana(PLANIFICAR);
    private final JToggleButton elegirTour = pestana(TOUR);
    private final JToggleButton elegirFicha = pestana(FICHA);
    private final JTextArea resultado = areaLectura();
    private final JTextArea resultadoTour = areaLectura();
    private final FichaDestino ficha = new FichaDestino();
    private final JLabel totalCiudades = new JLabel();
    private final JLabel ayudaMapa = etiqueta("", sans(Font.PLAIN, 12), SECUNDARIO);
    private final MapaCiudades mapa = new MapaCiudades(this::abrirFicha);
    private final JScrollPane desplazamientoMapa = new JScrollPane(mapa);
    private final JPanel controlesAnimacion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JButton pausa = boton("Pausar", false, mapa::alternarPausa);
    private final JButton repetir = boton("Repetir", false, mapa::repetirAnimacion);
    private List<Ciudad> rutaActual = List.of();
    private List<Ciudad> tourActual = List.of();

    public VistaPrincipal() {
        super("Rumbo Colombia");
        controlador = new Controlador();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        prepararSelector(origen, "Elige una ciudad");
        prepararSelector(destino, "Elige una ciudad");
        prepararSelector(origenTour, "Elige una ciudad");
        prepararSelector(ciudadConsultada, "Elige una ciudad");
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);

        actualizarCiudades();
        Ciudad primeraCiudad = origen.getItemAt(0);
        origen.setSelectedItem(primeraCiudad);
        ciudadConsultada.setSelectedItem(primeraCiudad);
        mostrarFicha(primeraCiudad);
        limpiarResultado();
        calcularTour();
        origen.addActionListener(evento -> limpiarResultado());
        destino.addActionListener(evento -> limpiarResultado());
        origenTour.addActionListener(evento -> calcularTour());
        ciudadConsultada.addActionListener(evento -> mostrarFicha(seleccion(ciudadConsultada)));

        setMinimumSize(new Dimension(875, 640));
        setPreferredSize(new Dimension(1160, 803));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(20, 0));
        encabezado.setBackground(BLANCO);
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA),
                BorderFactory.createEmptyBorder(12, 28, 12, 28)));

        JPanel marca = new JPanel(new BorderLayout(10, 0));
        marca.setOpaque(false);
        JComponent icono = new JComponent() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D dibujo = suavizar(graphics);
                dibujo.setColor(BOSQUE);
                dibujo.fillRoundRect(0, 2, 40, 40, 13, 13);
                dibujo.setColor(LIMA);
                dibujo.fillPolygon(new int[]{29, 23, 18, 11}, new int[]{12, 33, 24, 21}, 4);
                dibujo.setColor(BLANCO);
                dibujo.fillOval(11, 29, 4, 4);
                dibujo.dispose();
            }
        };
        icono.setPreferredSize(new Dimension(40, 44));
        JPanel nombre = new JPanel(new BorderLayout(0, -2));
        nombre.setOpaque(false);
        nombre.add(etiqueta("rumbo", sans(Font.BOLD, 27), TINTA), BorderLayout.CENTER);
        nombre.add(etiqueta("C O L O M B I A", sans(Font.BOLD, 9), BOSQUE), BorderLayout.SOUTH);
        marca.add(icono, BorderLayout.WEST);
        marca.add(nombre, BorderLayout.CENTER);
        encabezado.add(marca, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(boton("Gestionar destinos", false, this::gestionarDestinos));
        acciones.add(boton("Registrar ruta", true, this::registrarRuta));
        encabezado.add(acciones, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 26, 18, 26));
        contenido.add(crearPresentacion(), BorderLayout.NORTH);

        JPanel espacio = new JPanel(new BorderLayout(16, 0));
        espacio.setOpaque(false);
        espacio.add(crearPanelLateral(), BorderLayout.WEST);
        espacio.add(crearPanelMapa(), BorderLayout.CENTER);
        contenido.add(espacio, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearPresentacion() {
        JPanel presentacion = tarjeta(new BorderLayout(0, 4), BLANCO, 22);
        presentacion.setBorder(BorderFactory.createEmptyBorder(12, 22, 14, 22));
        JLabel titulo = etiqueta("Planifica tu viaje", serif(40), TINTA);
        JLabel descripcion = etiqueta("Consulta un plan de viaje o elige desde dónde iniciar un tour.",
                sans(Font.PLAIN, 15), SECUNDARIO);
        presentacion.add(titulo, BorderLayout.CENTER);
        presentacion.add(descripcion, BorderLayout.SOUTH);
        return presentacion;
    }

    private JPanel crearPanelMapa() {
        JPanel tarjetaMapa = tarjeta(new BorderLayout(0, 10), BLANCO, 20);
        tarjetaMapa.setBorder(BorderFactory.createEmptyBorder(12, 14, 9, 14));
        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        titulo.add(etiqueta("Mapa de Colombia", serif(26), TINTA), BorderLayout.WEST);
        totalCiudades.setFont(sans(Font.BOLD, 13));
        totalCiudades.setForeground(BOSQUE);
        JPanel cuenta = tarjeta(new FlowLayout(FlowLayout.CENTER, 12, 5), new Color(0xECF3E5), 23);
        cuenta.add(totalCiudades);
        titulo.add(cuenta, BorderLayout.EAST);
        tarjetaMapa.add(titulo, BorderLayout.NORTH);

        prepararDesplazamiento(desplazamientoMapa, MapaCiudades.AGUA);
        tarjetaMapa.add(desplazamientoMapa, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(10, 0));
        pie.setOpaque(false);
        pie.add(ayudaMapa, BorderLayout.WEST);
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        leyenda.setOpaque(false);
        leyenda.add(leyenda("Origen", MapaCiudades.INICIO));
        leyenda.add(leyenda("Visita", MapaCiudades.ESCALA));
        leyenda.add(leyenda("Destino", MapaCiudades.DESTINO));
        pie.add(leyenda, BorderLayout.EAST);
        JPanel pieMapa = new JPanel(new BorderLayout(0, 6));
        pieMapa.setOpaque(false);
        controlesAnimacion.setOpaque(false);
        pausa.getAccessibleContext().setAccessibleDescription("Pausa o reanuda el movimiento del carrito");
        repetir.getAccessibleContext().setAccessibleDescription("Reproduce el recorrido desde el inicio");
        controlesAnimacion.add(pausa);
        controlesAnimacion.add(repetir);
        pieMapa.add(controlesAnimacion, BorderLayout.NORTH);
        pieMapa.add(pie, BorderLayout.SOUTH);
        tarjetaMapa.add(pieMapa, BorderLayout.SOUTH);
        mapa.addPropertyChangeListener(MapaCiudades.ESTADO_ANIMACION, evento -> actualizarControlesMapa());
        actualizarControlesMapa();
        return tarjetaMapa;
    }

    private void actualizarControlesMapa() {
        MapaCiudades.EstadoAnimacion estado = mapa.estadoAnimacion();
        boolean tieneRecorrido = estado != MapaCiudades.EstadoAnimacion.SIN_RECORRIDO;
        boolean pausada = estado == MapaCiudades.EstadoAnimacion.PAUSADA;
        controlesAnimacion.setVisible(tieneRecorrido);
        pausa.setText(pausada ? "Reanudar" : "Pausar");
        pausa.setEnabled(estado == MapaCiudades.EstadoAnimacion.EN_CURSO || pausada);
        if (tieneRecorrido && mapa.muestraTour()) {
            ayudaMapa.setText("Línea punteada · Orden de visita");
            ayudaMapa.setToolTipText("La línea muestra el orden de visita. El tiempo total incluye "
                    + "los traslados intermedios entre visitas.");
        } else {
            ayudaMapa.setText(ciudadConsultada.getItemCount() == 0 ? "Agrega un destino para verlo aquí."
                    : "Los puntos sin coordenadas son esquemáticos.");
            ayudaMapa.setToolTipText(null);
        }
    }

    private static JLabel leyenda(String texto, Color color) {
        JLabel etiqueta = etiqueta(texto, sans(Font.PLAIN, 11), SECUNDARIO);
        etiqueta.setIcon(new javax.swing.Icon() {
            @Override
            public int getIconWidth() {
                return 8;
            }

            @Override
            public int getIconHeight() {
                return 8;
            }

            @Override
            public void paintIcon(java.awt.Component componente, Graphics graphics, int x, int y) {
                Graphics2D dibujo = suavizar(graphics);
                dibujo.setColor(color);
                dibujo.fillOval(x, y, 8, 8);
                dibujo.dispose();
            }
        });
        return etiqueta;
    }

    private JPanel crearPanelLateral() {
        JPanel panel = tarjeta(new BorderLayout(0, 13), BLANCO, 20);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 16, 13, 16));
        panel.setPreferredSize(new Dimension(312, 400));

        JPanel opciones = tarjeta(new GridLayout(1, 3, 4, 0), FONDO, 12);
        opciones.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(elegirPlan);
        grupo.add(elegirTour);
        grupo.add(elegirFicha);
        elegirPlan.addActionListener(evento -> mostrarPanel(PLANIFICAR));
        elegirTour.addActionListener(evento -> mostrarPanel(TOUR));
        elegirFicha.addActionListener(evento -> mostrarPanel(FICHA));
        opciones.add(elegirPlan);
        opciones.add(elegirTour);
        opciones.add(elegirFicha);
        panel.add(opciones, BorderLayout.NORTH);

        paneles.setOpaque(false);
        paneles.add(crearPlanificador(), PLANIFICAR);
        paneles.add(crearTour(), TOUR);
        paneles.add(crearFicha(), FICHA);
        panel.add(paneles, BorderLayout.CENTER);
        mostrarPanel(PLANIFICAR);
        return panel;
    }

    private void mostrarPanel(String pagina) {
        cambioPanel.show(paneles, pagina);
        elegirPlan.setSelected(PLANIFICAR.equals(pagina));
        elegirTour.setSelected(TOUR.equals(pagina));
        elegirFicha.setSelected(FICHA.equals(pagina));
        if (elegirPlan.isSelected()) {
            mapa.mostrarResultado(rutaActual, true);
        } else if (elegirTour.isSelected()) {
            mapa.mostrarResultado(tourActual, false);
        }
    }

    private JPanel crearPlanificador() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        JPanel consultas = new JPanel(new GridLayout(0, 1, 0, 8));
        consultas.setOpaque(false);
        consultas.add(campo("Origen", origen));
        consultas.add(campo("Destino", destino));
        JPanel formulario = new JPanel(new BorderLayout(0, 12));
        formulario.setOpaque(false);
        formulario.add(consultas, BorderLayout.NORTH);
        formulario.add(boton("Ver plan de viaje", true, this::calcularRuta), BorderLayout.SOUTH);
        panel.add(formulario, BorderLayout.NORTH);
        panel.add(tarjetaLectura("Tu recorrido", resultado), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTour() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.add(campo("Origen del tour", origenTour), BorderLayout.NORTH);
        panel.add(tarjetaLectura("Orden del tour", resultadoTour), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFicha() {
        JPanel panel = new JPanel(new BorderLayout(0, 9));
        panel.setOpaque(false);
        panel.add(campo("Ciudad", ciudadConsultada), BorderLayout.NORTH);
        panel.add(ficha, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel tarjetaLectura(String titulo, JTextArea area) {
        JPanel tarjeta = tarjeta(new BorderLayout(0, 5), FONDO, 14);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(11, 12, 8, 12));
        tarjeta.add(etiqueta(titulo, serif(21), TINTA), BorderLayout.NORTH);
        area.getAccessibleContext().setAccessibleName(titulo);
        JScrollPane desplazamiento = new JScrollPane(area);
        prepararDesplazamiento(desplazamiento, FONDO);
        tarjeta.add(desplazamiento, BorderLayout.CENTER);
        return tarjeta;
    }

    private static JPanel campo(String texto, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel etiqueta = etiqueta(texto, sans(Font.BOLD, 13), TINTA);
        etiqueta.setLabelFor(componente);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    private static JTextArea areaLectura() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(SECUNDARIO);
        area.setBackground(FONDO);
        area.setFont(sans(Font.PLAIN, 14));
        return area;
    }

    private List<Ciudad> ciudadesOrdenadas() {
        List<Ciudad> ciudades = controlador.obtenerCiudades();
        Collator orden = Collator.getInstance(ESPANOL_COLOMBIA);
        ciudades.sort((a, b) -> orden.compare(a.getNombre(), b.getNombre()));
        return ciudades;
    }

    private void actualizarCiudades() {
        mapa.actualizarCiudades(controlador.obtenerCiudades());
        List<Ciudad> ciudades = ciudadesOrdenadas();
        totalCiudades.setText(ciudades.size() + " ciudades");
        actualizarSelector(origen, ciudades);
        actualizarSelector(destino, ciudades);
        actualizarSelector(origenTour, ciudades);
        actualizarSelector(ciudadConsultada, ciudades);
        actualizarControlesMapa();
    }

    private static void actualizarSelector(JComboBox<Ciudad> selector, List<Ciudad> ciudades) {
        Ciudad anterior = seleccion(selector);
        DefaultComboBoxModel<Ciudad> modelo = new DefaultComboBoxModel<>(ciudades.toArray(new Ciudad[0]));
        modelo.setSelectedItem(anterior);
        selector.setModel(modelo);
    }

    private static Ciudad seleccion(JComboBox<Ciudad> selector) {
        return (Ciudad) selector.getSelectedItem();
    }

    private void abrirFicha(Ciudad ciudad) {
        if (ciudad.equals(seleccion(ciudadConsultada))) {
            mostrarFicha(ciudad);
        } else {
            ciudadConsultada.setSelectedItem(ciudad);
        }
        mostrarPanel(FICHA);
    }

    private void mostrarFicha(Ciudad ciudad) {
        mapa.seleccionarCiudad(ciudad);
        ficha.mostrar(ciudad);
    }

    private void limpiarResultado() {
        rutaActual = List.of();
        if (elegirPlan.isSelected()) {
            mapa.mostrarResultado(rutaActual, true);
        }
        mostrarTexto(resultado, "Elige un origen y un destino para consultar tu plan de viaje.");
    }

    private void calcularRuta() {
        Ciudad salida = seleccion(origen);
        Ciudad llegada = seleccion(destino);
        if (salida == null || llegada == null) {
            aviso("Selecciona el origen y el destino.");
            return;
        }
        rutaActual = controlador.buscarRuta(salida.getNombre(), llegada.getNombre());
        mapa.mostrarResultado(rutaActual, true);
        if (rutaActual.isEmpty()) {
            mostrarTexto(resultado, "No hay una ruta registrada entre " + salida + " y " + llegada + ".");
            return;
        }
        double tiempo = controlador.costoMinimo(salida.getNombre(), llegada.getNombre());
        mostrarTexto(resultado, "Tiempo total · " + formatearDuracion(tiempo) + "\n\n" + enumerar(rutaActual));
        mapa.seleccionarCiudad(salida);
    }

    private void calcularTour() {
        Ciudad salida = seleccion(origenTour);
        tourActual = salida == null ? List.of() : controlador.recorrerTour(salida.getNombre());
        if (elegirTour.isSelected()) {
            mapa.mostrarResultado(tourActual, false);
            mapa.seleccionarCiudad(salida);
        }
        if (salida == null) {
            mostrarTexto(resultadoTour, "Elige la ciudad desde donde quieres iniciar el tour.");
            return;
        }
        String titulo = tourActual.size() == 1
                ? "Desde " + salida + " no se llega a otras ciudades con las conexiones registradas."
                : tourActual.size() + " ciudades";
        var tiempo = DuracionTour.calcular(controlador, tourActual);
        String duracion = tiempo.isPresent() ? formatearDuracion(tiempo.getAsDouble())
                : "No disponible para este orden de visita";
        mostrarTexto(resultadoTour, "Tiempo total · " + duracion + "\n" + titulo + "\n\n" + enumerar(tourActual));
    }

    private static String formatearDuracion(double minutos) {
        BigDecimal segundos = BigDecimal.valueOf(minutos).multiply(BigDecimal.valueOf(60))
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal[] tiempo = segundos.divideAndRemainder(BigDecimal.valueOf(3600));
        int segundosRestantes = tiempo[1].intValueExact();
        int minutosRestantes = segundosRestantes / 60;
        segundosRestantes %= 60;
        List<String> partes = new ArrayList<>(3);
        if (tiempo[0].signum() > 0) {
            partes.add(tiempo[0].toPlainString() + " h");
        }
        if (minutosRestantes > 0) {
            partes.add(minutosRestantes + " min");
        }
        if (segundosRestantes > 0) {
            partes.add(segundosRestantes + " s");
        }
        return partes.isEmpty() ? "0 min" : String.join(" ", partes);
    }

    private static String enumerar(List<Ciudad> ciudades) {
        List<String> pasos = new ArrayList<>();
        for (int i = 0; i < ciudades.size(); i++) {
            pasos.add((i + 1) + ". " + ciudades.get(i).getNombre());
        }
        return String.join("\n", pasos);
    }

    private static void mostrarTexto(JTextArea area, String texto) {
        area.setText(texto);
        area.setCaretPosition(0);
    }

    private void gestionarDestinos() {
        Ciudad actual = seleccion(ciudadConsultada);
        Ciudad guardado = new GestionDestinos(this, controlador, ciudadesOrdenadas(), actual).mostrar();
        if (guardado == null) {
            return;
        }
        actualizarCiudades();
        abrirFicha(guardado);
    }

    private void registrarRuta() {
        Ciudad[] ciudades = ciudadesOrdenadas().toArray(new Ciudad[0]);
        if (ciudades.length < 2) {
            aviso("Registra al menos dos destinos para crear una ruta.");
            return;
        }
        JComboBox<Ciudad> desde = new JComboBox<>(ciudades);
        JComboBox<Ciudad> hasta = new JComboBox<>(ciudades);
        prepararSelector(desde, "Elige una ciudad");
        prepararSelector(hasta, "Elige una ciudad");
        desde.setSelectedItem(seleccion(origen));
        hasta.setSelectedItem(seleccion(destino));
        JTextField tiempo = new JTextField(12);
        tiempo.setFont(sans(Font.PLAIN, 15));
        tiempo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINEA, 1, true),
                BorderFactory.createEmptyBorder(5, 11, 5, 11)));
        JPanel formulario = new JPanel(new GridLayout(0, 1, 0, 8));
        formulario.add(new JLabel("Registra un sentido de viaje por vez."));
        formulario.add(campo("Origen", desde));
        formulario.add(campo("Destino", hasta));
        formulario.add(campo("Tiempo terrestre en minutos", tiempo));

        while (JOptionPane.showConfirmDialog(this, formulario, "Registrar ruta",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            Ciudad salida = seleccion(desde);
            Ciudad llegada = seleccion(hasta);
            if (salida == null || llegada == null || salida.equals(llegada)) {
                aviso("Selecciona dos ciudades distintas.");
                continue;
            }
            double minutos;
            try {
                minutos = Double.parseDouble(tiempo.getText().trim().replace(',', '.'));
            } catch (NumberFormatException error) {
                minutos = Double.NaN;
            }
            if (!Double.isFinite(minutos) || minutos <= 0) {
                aviso("Escribe un tiempo en minutos mayor que cero.");
                continue;
            }
            try {
                if (!controlador.agregarConexion(salida.getNombre(), llegada.getNombre(), minutos)) {
                    aviso("Ya hay una conexión registrada en ese sentido.");
                    continue;
                }
            } catch (IllegalStateException error) {
                errorGuardado(error);
            }
            mostrarPanel(PLANIFICAR);
            origen.setSelectedItem(salida);
            destino.setSelectedItem(llegada);
            calcularRuta();
            calcularTour();
            return;
        }
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Rumbo Colombia", JOptionPane.WARNING_MESSAGE);
    }

    private void errorGuardado(IllegalStateException error) {
        JOptionPane.showMessageDialog(this, error.getMessage() + ". El cambio está disponible en esta sesión.",
                "Error de guardado", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(() -> {
            try {
                new VistaPrincipal().setVisible(true);
            } catch (IllegalStateException error) {
                JOptionPane.showMessageDialog(null, error.getMessage(), "Rumbo Colombia", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
