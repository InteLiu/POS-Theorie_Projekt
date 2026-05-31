package at.spengergasse;

import at.spengergasse.GraphAnalyzer;
import at.spengergasse.GraphVisualizer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class GraphGUI extends JFrame {

    private final JTextArea output = new JTextArea();
    private int[][] adj;

    public GraphGUI() {
        super("Graphen-Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 4, 5, 5));
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton loadBtn   = new JButton("CSV laden");
        JButton distBtn   = new JButton("Distanzen");
        JButton eccBtn    = new JButton("Exzentrizität");
        JButton bfsBtn    = new JButton("BFS");
        JButton dfsBtn    = new JButton("DFS");
        JButton centerBtn = new JButton("Zentrum");
        JButton drawBtn   = new JButton("Graph anzeigen");
        JButton clearBtn  = new JButton("Clear");

        buttons.add(loadBtn);
        buttons.add(distBtn);
        buttons.add(eccBtn);
        buttons.add(bfsBtn);
        buttons.add(dfsBtn);
        buttons.add(centerBtn);
        buttons.add(drawBtn);
        buttons.add(clearBtn);

        add(buttons, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadCSV());
        distBtn.addActionListener(e -> requireGraph(this::showDistances));
        eccBtn.addActionListener(e -> requireGraph(this::showEccentricities));
        bfsBtn.addActionListener(e -> requireGraph(this::runBFS));
        dfsBtn.addActionListener(e -> requireGraph(this::runDFS));
        centerBtn.addActionListener(e -> requireGraph(this::showCenter));
        drawBtn.addActionListener(e -> requireGraph(this::showGraph));
        clearBtn.addActionListener(e -> output.setText(""));
    }

    private void loadCSV() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            File f = chooser.getSelectedFile();
            adj = GraphAnalyzer.readAdjacencyMatrix(f.getAbsolutePath());
            output.setText("CSV geladen.\n\nAdjazenzmatrix:\n\n");

            for (int[] row : adj) {
                for (int v : row) output.append(v + " ");
                output.append("\n");
            }

        } catch (Exception ex) {
            showError("Fehler beim Lesen der Datei:\n" + ex.getMessage());
        }
    }

    private void requireGraph(Runnable action) {
        if (adj == null) {
            showError("Bitte zuerst eine CSV-Datei laden!");
            return;
        }
        action.run();
    }

    private void showDistances() {
        int[][] dist = GraphAnalyzer.computeDistances(adj);
        output.setText("Distanzenmatrix:\n\n");
        for (int[] row : dist) {
            for (int v : row) output.append((v >= 1_000_000_000 ? "INF" : v) + " ");
            output.append("\n");
        }
    }

    private void showEccentricities() {
        int[][] dist = GraphAnalyzer.computeDistances(adj);
        int[] ecc = GraphAnalyzer.computeEccentricities(dist);

        output.setText("Exzentrizitäten:\n\n");
        for (int i = 0; i < ecc.length; i++)
            output.append("Knoten " + i + ": " + ecc[i] + "\n");

        output.append("\nRadius: " + GraphAnalyzer.computeRadius(ecc));
        output.append("\nDurchmesser: " + GraphAnalyzer.computeDiameter(ecc));
    }

    private void runBFS() {
        int start = askNode("BFS Startknoten:");
        if (start == -1) return;
        ArrayList<Integer> order = GraphAnalyzer.bfs(adj, start);
        output.setText("BFS-Reihenfolge:\n" + order);
    }

    private void runDFS() {
        int start = askNode("DFS Startknoten:");
        if (start == -1) return;
        ArrayList<Integer> order = GraphAnalyzer.dfs(adj, start);
        output.setText("DFS-Reihenfolge:\n" + order);
    }

    private int askNode(String msg) {
        String s = JOptionPane.showInputDialog(this, msg);
        if (s == null) return -1;
        try {
            int n = Integer.parseInt(s.trim());
            if (n < 0 || n >= adj.length) throw new Exception();
            return n;
        } catch (Exception e) {
            showError("Ungültiger Knoten! Gültig: 0 bis " + (adj.length - 1));
            return -1;
        }
    }

    private void showCenter() {
        int[][] dist = GraphAnalyzer.computeDistances(adj);
        int[] ecc = GraphAnalyzer.computeEccentricities(dist);
        int radius = GraphAnalyzer.computeRadius(ecc);
        var center = GraphAnalyzer.computeCenter(ecc, radius);
        output.setText("Zentrum:\n" + center);
    }

    private void showGraph() {
        JFrame f = new JFrame("Graph-Darstellung");
        f.add(new GraphVisualizer(adj));
        f.pack();
        f.setLocationRelativeTo(this);
        f.setVisible(true);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}