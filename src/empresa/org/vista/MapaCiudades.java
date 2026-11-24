package empresa.org.vista;

import empresa.org.modelo.ciudad.Ciudad;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import static empresa.org.vista.EstiloVista.*;

final class MapaCiudades extends JPanel implements Scrollable {
    private static final int ANCHO = 620;
    private static final int ALTO_BASE = 472;
    static final Color AGUA = new Color(0xEAF4F1);
    static final Color INICIO = new Color(0x347957);
    static final Color ESCALA = new Color(0x718C45);
    static final Color DESTINO = new Color(0xB86C3F);
    private static final Color MARCADOR = new Color(0x597B6B);
    private static final Color ESQUEMATICO = new Color(0x735886);
    private static final Color TOPOGRAFIA = new Color(0xDCE8D0);
    private static final Color TRAZADO_TOUR = new Color(0x72518E);
    static final String ESTADO_ANIMACION = "estadoAnimacion";

    enum EstadoAnimacion { SIN_RECORRIDO, EN_CURSO, PAUSADA, TERMINADA }

    // Silueta simplificada proyectada a partir del mapa de referencia.
    private static final int[] CONTORNO = {
        276, 349, 265, 343, 253, 335, 246, 339, 224, 336, 218, 325,
        214, 325, 189, 311, 185, 303, 195, 301, 193, 289, 199, 280,
        212, 278, 222, 262, 232, 249, 223, 243, 227, 229, 222, 206,
        227, 199, 223, 178, 213, 165, 216, 153, 224, 155, 229, 147,
        223, 132, 226, 129, 239, 130, 258, 112, 268, 109, 268, 101,
        273, 80, 287, 68, 303, 68, 305, 63, 325, 65, 344, 52,
        354, 47, 366, 35, 375, 36, 382, 43, 377, 51, 361, 55,
        354, 68, 345, 75, 337, 84, 334, 102, 327, 117, 340, 118,
        343, 130, 349, 135, 351, 145, 348, 155, 349, 160, 355, 162,
        361, 171, 393, 168, 408, 171, 425, 193, 435, 190, 453, 192,
        468, 189, 476, 193, 472, 207, 466, 215, 464, 233, 469, 250,
        477, 257, 477, 263, 465, 275, 474, 280, 480, 289, 488, 314,
        483, 317, 479, 303, 472, 295, 463, 303, 415, 303, 415, 318,
        430, 321, 429, 330, 424, 328, 410, 332, 409, 350, 421, 359,
        424, 374, 424, 384, 413, 453, 400, 440, 393, 439, 409, 414,
        390, 402, 375, 404, 366, 400, 352, 406, 333, 403, 319, 377,
        307, 371, 299, 359, 282, 347, 276, 349
    };
    private static final Path2D SILUETA = crearSilueta();

    private record Posicion(int x, int y, int dx, int dy) {
    }

    private static Path2D crearSilueta() {
        Path2D silueta = new Path2D.Double();
        silueta.moveTo(CONTORNO[0], CONTORNO[1]);
        for (int i = 2; i < CONTORNO.length; i += 2) {
            silueta.lineTo(CONTORNO[i], CONTORNO[i + 1]);
        }
        silueta.closePath();
        return silueta;
    }

    private static List<Point> crearPuntosEsquematicos(CatalogoGeografico catalogo) {
        List<Point> puntos = new ArrayList<>();
        List<Point> ocupados = new ArrayList<>();
        for (CatalogoGeografico.Ubicacion ubicacion : catalogo.ubicaciones()) {
            Posicion posicion = proyectar(ubicacion);
            ocupados.add(new Point(posicion.x(), posicion.y()));
        }
        int[][] claros = {
            {383, 247}, {397, 278}, {341, 312}, {364, 351},
            {398, 199}, {438, 253}, {302, 321}, {405, 340}
        };
        for (int[] punto : claros) {
            agregarPunto(puntos, ocupados, punto[0], punto[1], 22);
        }
        for (int paso : new int[]{24, 13, 7}) {
            for (int y = 45; y < ALTO_BASE; y += paso) {
                for (int x = 190; x < 490; x += paso) {
                    agregarPunto(puntos, ocupados, x, y, paso);
                }
            }
        }
        return List.copyOf(puntos);
    }

    private static void agregarPunto(List<Point> puntos, List<Point> ocupados, int x, int y, int separacion) {
        if (SILUETA.contains(x - 5, y - 5, 10, 10)
                && ocupados.stream().noneMatch(punto -> punto.distanceSq(x, y) < separacion * separacion)) {
            Point punto = new Point(x, y);
            puntos.add(punto);
            ocupados.add(punto);
        }
    }

    private static Posicion proyectar(CatalogoGeografico.Ubicacion ubicacion) {
        int x = (int) Math.round(185 + (ubicacion.longitud() + 79) * 25);
        int y = (int) Math.round(33 + (12.5 - ubicacion.latitud()) * 25);
        return new Posicion(x, y, ubicacion.desplazamientoX(), ubicacion.desplazamientoY());
    }

    private Posicion posicionEsquematica(String nombre, Set<Integer> usados) {
        // La semilla del nombre permite reproducir el punto al cargar la misma lista de ciudades.
        int cantidad = puntosEsquematicos.size();
        int inicio = new Random(CatalogoGeografico.normalizar(nombre).hashCode()).nextInt(cantidad);
        int indice = inicio;
        while (!usados.add(indice) && usados.size() < cantidad) {
            indice = (indice + 1) % cantidad;
        }
        Point punto = puntosEsquematicos.get(indice);
        return new Posicion(punto.x, punto.y, punto.x < 410 ? 12 : -12, 0);
    }

    private final CatalogoGeografico catalogo = new CatalogoGeografico();
    private final List<Point> puntosEsquematicos = crearPuntosEsquematicos(catalogo);
    private final Map<String, Marcador> marcadores = new LinkedHashMap<>();
    private final Consumer<Ciudad> alElegirCiudad;
    private final Timer reloj = new Timer(16, evento -> avanzarAnimacion());
    private List<Ciudad> resultado = List.of();
    private boolean esRuta;
    private RecorridoMapa recorrido = new RecorridoMapa(List.of());
    private EstadoAnimacion estadoAnimacion = EstadoAnimacion.SIN_RECORRIDO;
    private double segundosAnimados;
    private long ultimoFotograma;

    MapaCiudades(Consumer<Ciudad> alElegirCiudad) {
        this.alElegirCiudad = alElegirCiudad;
        setLayout(null);
        setBackground(AGUA);
        getAccessibleContext().setAccessibleName("Mapa de Colombia y ciudades agregadas");
    }

    void actualizarCiudades(List<Ciudad> ciudades) {
        removeAll();
        marcadores.clear();
        ButtonGroup seleccion = new ButtonGroup();
        Set<Integer> puntosUsados = new HashSet<>();
        for (Ciudad ciudad : ciudades) {
            String nombre = ciudad.getNombre();
            CatalogoGeografico.Ubicacion ubicacion = catalogo.buscar(nombre);
            Posicion posicion = ubicacion == null ? posicionEsquematica(nombre, puntosUsados) : proyectar(ubicacion);
            Marcador marcador = new Marcador(nombre, posicion, ubicacion == null);
            marcador.addActionListener(evento -> alElegirCiudad.accept(ciudad));
            seleccion.add(marcador);
            marcadores.put(nombre, marcador);
            add(marcador);
        }
        prepararRecorrido();
        actualizarMarcadores();
    }

    void seleccionarCiudad(Ciudad ciudad) {
        Marcador marcador = ciudad == null ? null : marcadores.get(ciudad.getNombre());
        if (marcador != null) {
            marcador.setSelected(true);
            scrollRectToVisible(marcador.getBounds());
            SwingUtilities.invokeLater(() -> {
                if (marcador.getParent() == this && marcador.isSelected()) {
                    scrollRectToVisible(marcador.getBounds());
                }
            });
        }
    }

    void mostrarResultado(List<Ciudad> ciudades, boolean ruta) {
        resultado = ciudades;
        esRuta = ruta;
        prepararRecorrido();
        actualizarMarcadores();
        repetirAnimacion();
    }

    private void prepararRecorrido() {
        List<Point> puntos = new ArrayList<>();
        for (Ciudad ciudad : resultado) {
            Marcador marcador = marcadores.get(ciudad.getNombre());
            if (marcador != null) {
                puntos.add(new Point(marcador.posicion.x(), marcador.posicion.y()));
            }
        }
        recorrido = new RecorridoMapa(puntos);
        if (!recorrido.tieneTramos() && estadoAnimacion != EstadoAnimacion.SIN_RECORRIDO) {
            reloj.stop();
            cambiarEstado(EstadoAnimacion.SIN_RECORRIDO);
        }
    }

    EstadoAnimacion estadoAnimacion() {
        return estadoAnimacion;
    }

    boolean muestraTour() {
        return !esRuta;
    }

    void repetirAnimacion() {
        reloj.stop();
        segundosAnimados = 0;
        if (recorrido.tieneTramos()) {
            iniciarReloj();
        } else {
            cambiarEstado(EstadoAnimacion.SIN_RECORRIDO);
        }
    }

    void alternarPausa() {
        if (estadoAnimacion == EstadoAnimacion.EN_CURSO) {
            reloj.stop();
            cambiarEstado(EstadoAnimacion.PAUSADA);
        } else if (estadoAnimacion == EstadoAnimacion.PAUSADA) {
            iniciarReloj();
        }
    }

    private void iniciarReloj() {
        ultimoFotograma = System.nanoTime();
        cambiarEstado(EstadoAnimacion.EN_CURSO);
        reloj.start();
    }

    private void avanzarAnimacion() {
        long ahora = System.nanoTime();
        double duracion = recorrido.duracionSegundos();
        segundosAnimados = Math.min(duracion, segundosAnimados + Math.min(0.1, (ahora - ultimoFotograma) / 1e9));
        ultimoFotograma = ahora;
        if (isShowing()) {
            RecorridoMapa.Fotograma punto = recorrido.fotograma(segundosAnimados / duracion);
            int x = (int) Math.round(margenX() + punto.x() * escalaMapa());
            int y = (int) Math.round(margenY() + punto.y() * escalaMapa());
            scrollRectToVisible(new Rectangle(x - 16, y - 16, 32, 32));
        }
        if (segundosAnimados >= duracion) {
            reloj.stop();
            cambiarEstado(EstadoAnimacion.TERMINADA);
        } else {
            repaint();
        }
    }

    private void cambiarEstado(EstadoAnimacion estado) {
        estadoAnimacion = estado;
        firePropertyChange(ESTADO_ANIMACION, null, estado);
        repaint();
    }

    @Override
    public void removeNotify() {
        if (reloj.isRunning()) {
            reloj.stop();
            cambiarEstado(EstadoAnimacion.PAUSADA);
        }
        super.removeNotify();
    }

    private void actualizarMarcadores() {
        marcadores.forEach((nombre, marcador) -> {
            marcador.setText(nombre);
            marcador.color = marcador.esquematico ? ESQUEMATICO : MARCADOR;
        });
        for (int i = 0; i < resultado.size(); i++) {
            Ciudad ciudad = resultado.get(i);
            Marcador marcador = marcadores.get(ciudad.getNombre());
            if (marcador != null) {
                marcador.setText((i + 1) + ". " + ciudad.getNombre());
                marcador.color = i == 0 ? INICIO : ESCALA;
                if (esRuta && i > 0 && i == resultado.size() - 1) {
                    marcador.color = DESTINO;
                }
            }
        }
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ANCHO, altoMapa());
    }

    private double escalaMapa() {
        int visible = getParent() == null ? 0 : getParent().getHeight();
        return visible == 0 ? 1 : Math.max(0.81, Math.min(1, (double) visible / ALTO_BASE));
    }

    private int altoMapa() {
        return (int) Math.floor(ALTO_BASE * escalaMapa());
    }

    private int margenX() {
        return (int) Math.round((getWidth() - ANCHO * escalaMapa()) / 2);
    }

    private int margenY() {
        return Math.max(0, (getHeight() - altoMapa()) / 2);
    }

    @Override
    public void doLayout() {
        double escala = escalaMapa();
        int margenX = margenX();
        int margenY = margenY();
        int alto = (int) Math.round(20 * escala);
        Font fuente = sans(Font.BOLD, escala >= 0.9 ? 12 : 11);
        for (Marcador marcador : marcadores.values()) {
            Posicion posicion = marcador.posicion;
            int x = (int) Math.round(posicion.x() * escala) + margenX;
            int y = (int) Math.round(posicion.y() * escala) + margenY;
            int dx = (int) Math.round(posicion.dx() * escala);
            int dy = (int) Math.round(posicion.dy() * escala);
            int anchoMaximo = Math.max(30, Math.min(145, dx < 0 ? x + dx - 4 : getWidth() - x - dx - 4));
            marcador.setFont(fuente);
            marcador.ubicar(x, y, dx, dy, anchoMaximo, alto);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D dibujo = suavizar(graphics);
        Rectangle visible = dibujo.getClipBounds();
        dibujo.setColor(new Color(0xCDE3DD));
        for (int x = 16; x < getWidth(); x += 22) {
            for (int y = Math.max(0, visible.y / 22) * 22 + 12; y < visible.y + visible.height; y += 22) {
                dibujo.fillOval(x, y, 2, 2);
            }
        }

        Graphics2D mapa = (Graphics2D) dibujo.create();
        mapa.translate(margenX(), margenY());
        mapa.scale(escalaMapa(), escalaMapa());
        dibujarPais(mapa);
        if (recorrido.tieneTramos()) {
            dibujarTrayecto(mapa);
        }
        mapa.dispose();
        dibujarBrujula(dibujo);
        dibujo.dispose();
    }

    private void dibujarPais(Graphics2D dibujo) {
        dibujo.translate(0, 4);
        dibujo.setColor(new Color(0xD5E7DE));
        dibujo.fill(SILUETA);
        dibujo.translate(0, -4);
        dibujo.setColor(new Color(0xF7FAEF));
        dibujo.fill(SILUETA);
        Shape clip = dibujo.getClip();
        dibujo.clip(SILUETA);
        dibujo.setColor(TOPOGRAFIA);
        dibujo.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++) {
            int desplazamiento = i * 27;
            Path2D cordillera = new Path2D.Double();
            cordillera.moveTo(210 + desplazamiento, 369);
            cordillera.curveTo(201 + desplazamiento, 285, 276 + desplazamiento, 232, 249 + desplazamiento, 169);
            cordillera.quadTo(235 + desplazamiento, 128, 281 + desplazamiento, 101);
            dibujo.draw(cordillera);
        }
        dibujo.setClip(clip);
        dibujo.setColor(new Color(0xABBFAA));
        dibujo.setStroke(new BasicStroke(1.3f));
        dibujo.draw(SILUETA);
        dibujo.setFont(sans(Font.BOLD, 11));
        dibujo.setColor(new Color(0x59786B));
        dibujo.drawString("MAR CARIBE", 142, 35);
        dibujo.drawString("OCÉANO", 98, 292);
        dibujo.drawString("PACÍFICO", 98, 308);
        dibujo.setFont(sans(Font.BOLD, 13));
        dibujo.setColor(new Color(0x67816D));
        dibujo.drawString("C O L O M B I A", 316, 370);
    }

    private void dibujarBrujula(Graphics2D dibujo) {
        int x = getWidth() - 28;
        int y = margenY() + 31;
        dibujo.setColor(BLANCO);
        dibujo.fillOval(x - 19, y - 10, 38, 52);
        dibujo.setColor(BOSQUE);
        dibujo.setFont(sans(Font.BOLD, 12));
        dibujo.drawString("N", x - 4, y + 6);
        dibujo.fillPolygon(new int[]{x, x - 7, x, x + 7}, new int[]{y + 12, y + 32, y + 27, y + 32}, 4);
    }

    private void dibujarTrayecto(Graphics2D dibujo) {
        float[] guiones = esRuta ? null : new float[]{5f, 8f};
        dibujo.setColor(BLANCO);
        dibujo.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, guiones, 0f));
        dibujo.draw(recorrido.linea());
        dibujo.setColor(esRuta ? BOSQUE : TRAZADO_TOUR);
        dibujo.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, guiones, 0f));
        dibujo.draw(recorrido.linea());
    }

    @Override
    protected void paintChildren(Graphics graphics) {
        super.paintChildren(graphics);
        if (!recorrido.tieneTramos()) {
            return;
        }
        Graphics2D dibujo = suavizar(graphics);
        RecorridoMapa.Fotograma punto = recorrido.fotograma(segundosAnimados / recorrido.duracionSegundos());
        dibujo.translate(margenX() + punto.x() * escalaMapa(), margenY() + punto.y() * escalaMapa());
        dibujo.setColor(new Color(0x33264033, true));
        dibujo.fillOval(-16, -14, 32, 32);
        dibujo.setColor(BLANCO);
        dibujo.fillOval(-16, -16, 32, 32);
        dibujo.rotate(punto.angulo());
        dibujo.setColor(TINTA);
        dibujo.fillRoundRect(-9, -8, 5, 16, 3, 3);
        dibujo.fillRoundRect(4, -8, 5, 16, 3, 3);
        dibujo.setColor(new Color(0xE9AE37));
        dibujo.fillRoundRect(-12, -6, 24, 12, 7, 7);
        dibujo.setColor(BOSQUE);
        dibujo.fillRoundRect(2, -4, 5, 8, 3, 3);
        dibujo.fillRoundRect(-7, -4, 4, 8, 3, 3);
        dibujo.setColor(new Color(0xFFF1A7));
        dibujo.fillOval(9, -4, 3, 2);
        dibujo.fillOval(9, 2, 3, 2);
        dibujo.dispose();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(ANCHO, ALTO_BASE);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getParent().getHeight() >= getPreferredSize().height;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion) {
        int dimension = orientacion == SwingConstants.VERTICAL ? visible.height : visible.width;
        return Math.max(24, dimension - 24);
    }

    private static final class Marcador extends JToggleButton {
        private final Point punto = new Point();
        private final Rectangle rotulo = new Rectangle();
        private final Posicion posicion;
        private final boolean esquematico;
        private Color color = MARCADOR;

        Marcador(String nombre, Posicion posicion, boolean esquematico) {
            super(nombre);
            this.posicion = posicion;
            this.esquematico = esquematico;
            setFont(sans(Font.BOLD, 12));
            setToolTipText(esquematico ? nombre + " · Ubicación esquemática" : nombre);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setOpaque(false);
            setBorderPainted(false);
            getAccessibleContext().setAccessibleName(nombre);
        }

        void ubicar(int x, int y, int dx, int dy, int anchoMaximo, int alto) {
            int ancho = Math.min(getFontMetrics(getFont()).stringWidth(getText()) + 14, anchoMaximo);
            int izquierda = x + dx - (dx < 0 ? ancho : 0);
            int arriba = y + dy - alto / 2;
            int baseX = Math.min(izquierda, x - 8);
            int baseY = Math.min(arriba, y - 8);
            int derecha = Math.max(izquierda + ancho, x + 8);
            int abajo = Math.max(arriba + alto, y + 8);
            setBounds(baseX, baseY, derecha - baseX, abajo - baseY);
            punto.setLocation(x - baseX, y - baseY);
            rotulo.setBounds(izquierda - baseX, arriba - baseY, ancho, alto);
        }

        @Override
        public boolean contains(int x, int y) {
            return rotulo.contains(x, y) || punto.distanceSq(x, y) <= 49;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D dibujo = suavizar(graphics);
            int extremo = punto.x < rotulo.x ? rotulo.x : rotulo.x + rotulo.width;
            dibujo.setColor(new Color(0x91AA98));
            dibujo.drawLine(punto.x, punto.y, extremo, rotulo.y + rotulo.height / 2);
            dibujo.setColor(BLANCO);
            dibujo.fillOval(punto.x - 5, punto.y - 5, 10, 10);
            dibujo.setColor(color);
            dibujo.fillOval(punto.x - 3, punto.y - 3, 6, 6);
            if (isSelected() || hasFocus()) {
                dibujo.setColor(hasFocus() ? FOCO : color);
                dibujo.drawOval(punto.x - 7, punto.y - 7, 14, 14);
            }

            boolean activo = isSelected() || getModel().isRollover();
            dibujo.setColor(activo ? BOSQUE : BLANCO);
            dibujo.fillRoundRect(rotulo.x, rotulo.y, rotulo.width, rotulo.height, 10, 10);
            dibujo.setColor(hasFocus() ? FOCO : activo ? BOSQUE : LINEA);
            dibujo.drawRoundRect(rotulo.x, rotulo.y, rotulo.width - 1, rotulo.height - 1, 10, 10);
            dibujo.setFont(getFont());
            dibujo.setColor(activo ? BLANCO : TINTA);
            FontMetrics metricas = dibujo.getFontMetrics();
            String texto = BasicGraphicsUtils.getClippedString(this, metricas, getText(), rotulo.width - 12);
            int lineaBase = rotulo.y + (rotulo.height - metricas.getHeight()) / 2 + metricas.getAscent();
            dibujo.drawString(texto, rotulo.x + 7, lineaBase);
            dibujo.dispose();
        }
    }
}
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
