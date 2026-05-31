package at.spengergasse;

import javax.swing.*;
import java.awt.*;

public class GraphVisualizer extends JPanel {

    private final int[][] adj;

    public GraphVisualizer(int[][] adj) {
        this.adj = adj;
        setPreferredSize(new Dimension(700, 700));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (adj == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = adj.length;
        int radius = 260;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        Point[] pos = new Point[n];
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = (int) (cx + radius * Math.cos(angle));
            int y = (int) (cy + radius * Math.sin(angle));
            pos[i] = new Point(x, y);
        }

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(180, 180, 180));

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (adj[i][j] != 0) {
                    g2.drawLine(pos[i].x, pos[i].y, pos[j].x, pos[j].y);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            int r = 20;

            g2.setColor(new Color(70, 140, 255));
            g2.fillOval(pos[i].x - r, pos[i].y - r, 2 * r, 2 * r);

            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(pos[i].x - r, pos[i].y - r, 2 * r, 2 * r);

            String label = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            int tx = pos[i].x - fm.stringWidth(label) / 2;
            int ty = pos[i].y + fm.getAscent() / 2;

            g2.setColor(Color.WHITE);
            g2.drawString(label, tx, ty);
        }
    }
}
