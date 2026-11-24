package empresa.org.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

final class EstiloVista {
    static final Color BOSQUE = new Color(0x174B3C);
    static final Color TINTA = new Color(0x193D34);
    static final Color SECUNDARIO = new Color(0x536D62);
    static final Color LINEA = new Color(0xDCE6DD);
    static final Color FONDO = new Color(0xF5F8F3);
    static final Color LIMA = new Color(0xD6F49D);
    static final Color BLANCO = Color.WHITE;
    static final Color FOCO = new Color(0xAC7227);
    private static final String SANS = "Segoe UI";

    private EstiloVista() {
    }

    static Font sans(int estilo, int tamano) {
        return new Font(SANS, estilo, tamano);
    }

    static Font serif(int tamano) {
        return new Font("Georgia", Font.PLAIN, tamano);
    }

    static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);
        return etiqueta;
    }

    static Graphics2D suavizar(Graphics graphics) {
        Graphics2D dibujo = (Graphics2D) graphics.create();
        dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        dibujo.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return dibujo;
    }

    static JPanel tarjeta(LayoutManager layout, Color fondo, int radio) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics graphics) {
                superficie(this, graphics, fondo, LINEA, radio);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    static JButton boton(String texto, boolean principal, Runnable accion) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Color fondo = principal ? BOSQUE : BLANCO;
                if (getModel().isPressed() || getModel().isRollover()) {
                    fondo = principal ? new Color(0x21634F) : FONDO;
                }
                superficie(this, graphics, fondo, principal ? fondo : LINEA, 11);
                super.paintComponent(graphics);
            }
        };
        prepararBoton(boton, principal ? BLANCO : BOSQUE);
        boton.addActionListener(evento -> accion.run());
        return boton;
    }

    static JToggleButton pestana(String texto) {
        JToggleButton boton = new JToggleButton(texto) {
            @Override
            protected void paintComponent(Graphics graphics) {
                if (isSelected() || hasFocus()) {
                    superficie(this, graphics, BLANCO, LINEA, 9);
                }
                super.paintComponent(graphics);
            }
        };
        prepararBoton(boton, BOSQUE);
        boton.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        return boton;
    }

    private static void prepararBoton(javax.swing.AbstractButton boton, Color texto) {
        boton.setFont(sans(Font.BOLD, 14));
        boton.setForeground(texto);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static void superficie(JComponent componente, Graphics graphics, Color fondo, Color linea, int radio) {
        Graphics2D dibujo = suavizar(graphics);
        Shape forma = new RoundRectangle2D.Float(0.5f, 0.5f, componente.getWidth() - 1f,
                componente.getHeight() - 1f, radio, radio);
        dibujo.setColor(fondo);
        dibujo.fill(forma);
        dibujo.setColor(componente.hasFocus() ? FOCO : linea);
        dibujo.draw(forma);
        dibujo.dispose();
    }

    static void prepararSelector(JComboBox<?> selector, String marcador) {
        selector.setFont(sans(Font.PLAIN, 15));
        selector.setForeground(TINTA);
        selector.setBackground(BLANCO);
        selector.setOpaque(false);
        selector.setUI(new BasicComboBoxUI() {
            @Override
            public void paint(Graphics graphics, JComponent componente) {
                superficie(componente, graphics, BLANCO, LINEA, 10);
                super.paint(graphics, componente);
            }

            @Override
            public void paintCurrentValueBackground(Graphics graphics, Rectangle limites, boolean foco) {
                // El fondo redondeado ocupa todo el selector.
            }

            @Override
            protected JButton createArrowButton() {
                JButton flecha = new JButton() {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        Graphics2D dibujo = suavizar(graphics);
                        dibujo.setColor(BOSQUE);
                        int x = getWidth() / 2;
                        int y = getHeight() / 2;
                        dibujo.drawLine(x - 4, y - 2, x, y + 2);
                        dibujo.drawLine(x, y + 2, x + 4, y - 2);
                        dibujo.dispose();
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(32, 36);
                    }
                };
                flecha.setOpaque(false);
                flecha.setFocusable(false);
                flecha.setBorderPainted(false);
                flecha.setContentAreaFilled(false);
                flecha.setName("ComboBox.arrowButton");
                return flecha;
            }
        });
        selector.setBorder(BorderFactory.createEmptyBorder(4, 1, 4, 5));
        selector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor,
                    int indice, boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                setFont(selector.getFont());
                setBorder(BorderFactory.createEmptyBorder(indice < 0 ? 1 : 6, 11, indice < 0 ? 1 : 6, 8));
                setOpaque(indice >= 0);
                setForeground(valor == null ? SECUNDARIO : TINTA);
                setBackground(seleccionado ? new Color(0xE8F2DF) : BLANCO);
                if (valor == null) {
                    setText(marcador);
                }
                return this;
            }
        });
        selector.setPreferredSize(new Dimension(240, 41));
    }

    static void prepararDesplazamiento(JScrollPane panel, Color fondo) {
        panel.setBorder(BorderFactory.createEmptyBorder());
        panel.setViewportBorder(null);
        panel.setBackground(fondo);
        panel.getViewport().setBackground(fondo);
        panel.getVerticalScrollBar().setUnitIncrement(22);
        prepararBarra(panel.getVerticalScrollBar(), fondo);
        prepararBarra(panel.getHorizontalScrollBar(), fondo);
    }

    private static void prepararBarra(JScrollBar barra, Color fondo) {
        barra.setBackground(fondo);
        barra.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                scrollBarWidth = 13;
            }

            @Override
            protected JButton createDecreaseButton(int orientacion) {
                return botonInvisible();
            }

            @Override
            protected JButton createIncreaseButton(int orientacion) {
                return botonInvisible();
            }

            @Override
            protected void paintTrack(Graphics graphics, JComponent componente, Rectangle limites) {
                graphics.setColor(fondo);
                graphics.fillRect(limites.x, limites.y, limites.width, limites.height);
            }

            @Override
            protected void paintThumb(Graphics graphics, JComponent componente, Rectangle limites) {
                if (limites.isEmpty() || !componente.isEnabled()) {
                    return;
                }
                Graphics2D dibujo = suavizar(graphics);
                dibujo.setColor(isThumbRollover() || isDragging ? new Color(0x789984) : new Color(0xA3BBAC));
                dibujo.fillRoundRect(limites.x + 3, limites.y + 3, limites.width - 6, limites.height - 6, 7, 7);
                dibujo.dispose();
            }
        });
    }

    private static JButton botonInvisible() {
        JButton boton = new JButton();
        boton.setPreferredSize(new Dimension(0, 0));
        boton.setFocusable(false);
        return boton;
    }
}
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
