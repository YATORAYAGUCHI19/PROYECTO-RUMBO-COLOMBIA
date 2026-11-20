package empresa.org.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class BotonPrincipal extends JButton {
    private static final Color VERDE = new Color(23, 75, 60);

    public BotonPrincipal(String texto) {
        super(texto);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(12, 20, 12, 20));
        setMinimumSize(new Dimension(100, 44));
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        Graphics2D g = (Graphics2D) grafico.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = !isEnabled() ? new Color(152, 172, 159)
                : getModel().isPressed() ? VERDE.darker()
                : getModel().isRollover() ? new Color(36, 99, 78) : VERDE;
        g.setColor(color);
        g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
        if (hasFocus()) {
            g.setColor(new Color(214, 244, 157));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(4, 4, getWidth() - 9, getHeight() - 9, 9, 9);
        }
        g.dispose();
        super.paintComponent(grafico);
    }
}
