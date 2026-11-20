package empresa.org.vista;

import empresa.org.modelo.ciudad.Ciudad;

import java.awt.*;

import java.awt.geom.*;

import java.util.*;

import java.util.List;

import javax.swing.*;

public class MapaColombia extends JPanel {

    private final Map<String, double[]> posiciones = new HashMap<>();

    private final Map<String, int[]> etiquetas = new HashMap<>();

    private final double[][] contorno = {

        {-75.373223, -0.152032},

        {-75.801466, 0.084801},

        {-76.292314, 0.416047},

        {-76.576380, 0.256936},

        {-77.424984, 0.395687},

        {-77.668613, 0.825893},

        {-77.855061, 0.809925},

        {-78.855259, 1.380924},

        {-78.990935, 1.691370},

        {-78.617831, 1.766404},

        {-78.662118, 2.267355},

        {-78.427610, 2.629556},

        {-77.931543, 2.696606},

        {-77.510431, 3.325017},

        {-77.127690, 3.849636},

        {-77.496272, 4.087606},

        {-77.307601, 4.667984},

        {-77.533221, 5.582812},

        {-77.318815, 5.845354},

        {-77.476661, 6.691116},

        {-77.881571, 7.223771},

        {-77.753414, 7.709840},

        {-77.431108, 7.638061},

        {-77.242566, 7.935278},

        {-77.474723, 8.524286},

        {-77.353361, 8.670505},

        {-76.836674, 8.638749},

        {-76.086384, 9.336821},

        {-75.674600, 9.443248},

        {-75.664704, 9.774003},

        {-75.480426, 10.618990},

        {-74.906895, 11.083045},

        {-74.276753, 11.102036},

        {-74.197223, 11.310473},

        {-73.414764, 11.227015},

        {-72.627835, 11.731972},

        {-72.238195, 11.955550},

        {-71.754090, 12.437303},

        {-71.399822, 12.376041},

        {-71.137461, 12.112982},

        {-71.331584, 11.776284},

        {-71.973922, 11.608672},

        {-72.227575, 11.108702},

        {-72.614658, 10.821975},

        {-72.905286, 10.450344},

        {-73.027604, 9.736770},

        {-73.304952, 9.152000},

        {-72.788730, 9.085027},

        {-72.660495, 8.625288},

        {-72.439862, 8.405275},

        {-72.360901, 8.002638},

        {-72.479679, 7.632506},

        {-72.444487, 7.423785},

        {-72.198352, 7.340431},

        {-71.960176, 6.991615},

        {-70.674234, 7.087785},

        {-70.093313, 6.960376},

        {-69.389480, 6.099861},

        {-68.985319, 6.206805},

        {-68.265052, 6.153268},

        {-67.695087, 6.267318},

        {-67.341440, 6.095468},

        {-67.521532, 5.556870},

        {-67.744697, 5.221129},

        {-67.823012, 4.503937},

        {-67.621836, 3.839482},

        {-67.337564, 3.542342},

        {-67.303173, 3.318454},

        {-67.809938, 2.820655},

        {-67.447092, 2.600281},

        {-67.181294, 2.250638},

        {-66.876326, 1.253361},

        {-67.065048, 1.130112},

        {-67.259998, 1.719999},

        {-67.537810, 2.037163},

        {-67.868565, 1.692455},

        {-69.816973, 1.714805},

        {-69.804597, 1.089081},

        {-69.218638, 0.985677},

        {-69.252434, 0.602651},

        {-69.452396, 0.706159},

        {-70.015566, 0.541414},

        {-70.020656, -0.185156},

        {-69.577065, -0.549992},

        {-69.420486, -1.122619},

        {-69.444102, -1.556287},

        {-69.893635, -4.298187},

        {-70.394044, -3.766591},

        {-70.692682, -3.742872},

        {-70.047709, -2.725156},

        {-70.813476, -2.256865},

        {-71.413646, -2.342802},

        {-71.774761, -2.169790},

        {-72.325787, -2.434218},

        {-73.070392, -2.308954},

        {-73.659504, -1.260491},

        {-74.122395, -1.002833},

        {-74.441601, -0.530820},

        {-75.106625, -0.057205},

        {-75.373223, -0.152032}

    };

    private List<Ciudad> ruta = new ArrayList<>();

    private List<Ciudad> ciudades = new ArrayList<>();

    private double progreso;

    private double zoom = 1;

    private boolean pausa;

    private boolean rutaDibujable;

    private final javax.swing.Timer reloj;

    private final JLabel estado;

    public MapaColombia(JLabel estado) {

        this.estado = estado;

        posiciones.put("Pasto", new double[]{-77.28, 1.21});

        posiciones.put("Popayán", new double[]{-76.61, 2.44});

        posiciones.put("Cali", new double[]{-76.53, 3.45});

        posiciones.put("Armenia", new double[]{-75.68, 4.53});

        posiciones.put("Pereira", new double[]{-75.69, 4.81});

        posiciones.put("Bogotá", new double[]{-74.07, 4.71});

        posiciones.put("Ibagué", new double[]{-75.23, 4.44});

        posiciones.put("Neiva", new double[]{-75.28, 2.93});

        posiciones.put("Manizales", new double[]{-75.52, 5.07});

        posiciones.put("Tunja", new double[]{-73.36, 5.54});

        posiciones.put("Medellín", new double[]{-75.58, 6.24});

        posiciones.put("Bucaramanga", new double[]{-73.12, 7.12});

        posiciones.put("Cúcuta", new double[]{-72.5, 7.89});

        posiciones.put("Montería", new double[]{-75.88, 8.75});

        posiciones.put("Sincelejo", new double[]{-75.4, 9.3});

        posiciones.put("Cartagena", new double[]{-75.48, 10.39});

        posiciones.put("Barranquilla", new double[]{-74.78, 10.98});

        posiciones.put("Santa Marta", new double[]{-74.2, 11.24});

        posiciones.put("Valledupar", new double[]{-73.25, 10.46});

        posiciones.put("Riohacha", new double[]{-72.91, 11.54});

        etiquetas.put("Pasto", new int[]{-9, 19}); etiquetas.put("Popayán", new int[]{-10, 13});

        etiquetas.put("Cali", new int[]{-11, -3}); etiquetas.put("Armenia", new int[]{-16, 27});

        etiquetas.put("Pereira", new int[]{-18, 7}); etiquetas.put("Manizales", new int[]{-10, -13});

        etiquetas.put("Ibagué", new int[]{13, 15}); etiquetas.put("Bogotá", new int[]{13, 9});

        etiquetas.put("Tunja", new int[]{13, -4}); etiquetas.put("Medellín", new int[]{-12, -12});

        etiquetas.put("Bucaramanga", new int[]{-10, -10}); etiquetas.put("Cúcuta", new int[]{12, 5});

        etiquetas.put("Montería", new int[]{-13, 7}); etiquetas.put("Sincelejo", new int[]{-13, -6});

        etiquetas.put("Cartagena", new int[]{-14, -3}); etiquetas.put("Barranquilla", new int[]{-13, -13});

        etiquetas.put("Santa Marta", new int[]{12, -25}); etiquetas.put("Valledupar", new int[]{13, 8});

        etiquetas.put("Riohacha", new int[]{12, 8});

        setBackground(new Color(235, 245, 240));

        setPreferredSize(new Dimension(600, 530));

        reloj = new javax.swing.Timer(30, e -> {

            if (!pausa && rutaDibujable && progreso < 1) {

                progreso = Math.min(1, progreso + 0.003);

                if (progreso == 1) estado.setText("Llegaste");

                repaint();

            }

        });

        reloj.start();

    }

    public void setCiudades(List<Ciudad> ciudades) { this.ciudades = ciudades; repaint(); }

    public void setRuta(List<Ciudad> ruta) { this.ruta = ruta; repetir(); }

    public void repetir() {

        progreso = 0; pausa = false; rutaDibujable = ruta.size() > 1;

        for (Ciudad ciudad : ruta) {

            if (punto(ciudad) == null) { rutaDibujable = false; estado.setText("Ruta sin coordenadas para el mapa"); repaint(); return; }

        }

        estado.setText(ruta.size() > 1 ? "En camino" : "Selecciona una ruta"); repaint();

    }

    public boolean pausar() { pausa = !pausa; return pausa; }

    public void acercar() { zoom = Math.min(2.5, zoom + 0.25); repaint(); }

    public void alejar() { zoom = Math.max(1, zoom - 0.25); repaint(); }

    public void reiniciarZoom() { zoom = 1; repaint(); }

    private Point2D punto(double lon, double lat) { return new Point2D.Double((lon + 80) * 39 + 35, (13 - lat) * 39 + 15); }

    private Point2D punto(Ciudad ciudad) {

        double[] posicion = posiciones.get(ciudad.getNombre());

        return posicion == null ? null : punto(posicion[0], posicion[1]);

    }

    @Override protected void paintComponent(Graphics grafico) {

        super.paintComponent(grafico);

        Graphics2D g = (Graphics2D) grafico.create();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double escala = Math.min(getWidth() / 720.0, getHeight() / 720.0) * zoom;

        g.translate((getWidth() - 720 * escala) / 2, (getHeight() - 720 * escala) / 2);

        g.scale(escala, escala);

        g.setColor(new Color(207, 227, 217));

        for (int x = 15; x < 720; x += 32) for (int y = 15; y < 720; y += 32) g.fillOval(x, y, 2, 2);

        Path2D pais = new Path2D.Double();

        for (int i = 0; i < contorno.length; i++) {

            Point2D p = punto(contorno[i][0], contorno[i][1]);

            if (i == 0) pais.moveTo(p.getX(), p.getY()); else pais.lineTo(p.getX(), p.getY());

        }

        pais.closePath(); g.setColor(new Color(250, 252, 245)); g.fill(pais);

        g.setColor(new Color(175, 201, 179)); g.setStroke(new BasicStroke(2)); g.draw(pais);

        g.setColor(new Color(158, 187, 169)); g.setFont(new Font("SansSerif", Font.BOLD, 13));

        g.drawString("MAR CARIBE", 130, 52); g.drawString("OCÉANO PACÍFICO", 23, 270);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16)); g.drawString("C O L O M B I A", 330, 420);

        List<Point2D> puntos = new ArrayList<>();

        for (Ciudad ciudad : ruta) { Point2D p = punto(ciudad); if (p == null) { puntos.clear(); break; } puntos.add(p); }

        if (puntos.size() > 1) {

            g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            double longitud = 0; for (int i = 1; i < puntos.size(); i++) longitud += puntos.get(i - 1).distance(puntos.get(i));

            double restante = longitud * progreso; Point2D coche = puntos.get(0);

            for (int i = 1; i < puntos.size(); i++) {

                Point2D a = puntos.get(i - 1), b = puntos.get(i); double largo = a.distance(b);

                double fraccion = largo == 0 ? 1 : Math.min(1, restante / largo);

                if (fraccion <= 0) break;

                coche = new Point2D.Double(a.getX() + (b.getX() - a.getX()) * fraccion, a.getY() + (b.getY() - a.getY()) * fraccion);

                g.setColor(new Color(47, 113, 87)); g.draw(new Line2D.Double(a, coche)); restante -= largo;

                if (fraccion < 1) break;

            }

            g.setColor(Color.WHITE); g.fill(new Ellipse2D.Double(coche.getX()-12,coche.getY()-12,24,24));

            g.setColor(new Color(220, 151, 51)); g.fillRoundRect((int)coche.getX()-6,(int)coche.getY()-5,12,10,5,5);

        }

        g.setFont(new Font("SansSerif", Font.PLAIN, Math.max(13, Math.min(20, (int) (10.5 / escala)))));

        for (Ciudad ciudad : ciudades) {

            Point2D p = punto(ciudad); if (p == null) continue;

            int x = (int)p.getX(), y = (int)p.getY();

            g.setColor(ruta.contains(ciudad) ? new Color(27, 107, 76) : new Color(109, 149, 126));

            g.fillOval(x-5, y-5, 10, 10);

            int[] desplazamiento = etiquetas.getOrDefault(ciudad.getNombre(), new int[]{8, -6});

            int textoX = x + desplazamiento[0];

            if (desplazamiento[0] < 0) textoX -= g.getFontMetrics().stringWidth(ciudad.getNombre());

            g.setColor(new Color(43, 79, 61)); g.drawString(ciudad.getNombre(), textoX, y + desplazamiento[1]);

        }

        g.dispose();

    }

}
