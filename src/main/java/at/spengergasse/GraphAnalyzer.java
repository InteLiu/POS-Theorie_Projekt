package at.spengergasse;

import java.io.*;
import java.util.*;

public class GraphAnalyzer {

    private static final int INF = 1_000_000_000;

    // ------------------------------------------------------------
    // CSV EINLESEN
    // ------------------------------------------------------------
    public static int[][] readAdjacencyMatrix(String filename) throws IOException {
        List<int[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(";");
                int[] row = new int[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    try {
                        row[i] = Integer.parseInt(parts[i].trim());
                    } catch (NumberFormatException e) {
                        throw new IOException("Ungültiger Wert in der CSV: " + parts[i]);
                    }
                }

                rows.add(row);
            }
        }

        int n = rows.size();

        for (int[] row : rows) {
            if (row.length != n)
                throw new IOException("Die CSV-Datei ist nicht quadratisch!");
        }

        return rows.toArray(new int[n][n]);
    }

    // ------------------------------------------------------------
    // FLOYD-WARSHALL
    // ------------------------------------------------------------
    public static int[][] computeDistances(int[][] adj) {
        int n = adj.length;
        int[][] dist = new int[n][n];

        // Startmatrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = (i == j) ? 0 :
                        (adj[i][j] != 0 ? 1 : INF);
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);

        return dist;
    }

    // ------------------------------------------------------------
    // EXZENTRIZITÄTEN
    // ------------------------------------------------------------
    public static int[] computeEccentricities(int[][] dist) {
        int n = dist.length;
        int[] ecc = new int[n];

        for (int i = 0; i < n; i++) {
            int max = 0;
            for (int d : dist[i]) max = Math.max(max, d);
            ecc[i] = max;
        }

        return ecc;
    }

    public static int computeRadius(int[] ecc) {
        return Arrays.stream(ecc).min().orElse(0);
    }

    public static int computeDiameter(int[] ecc) {
        return Arrays.stream(ecc).max().orElse(0);
    }

    public static List<Integer> computeCenter(int[] ecc, int radius) {
        List<Integer> center = new ArrayList<>();
        for (int i = 0; i < ecc.length; i++)
            if (ecc[i] == radius) center.add(i);
        return center;
    }

    // ------------------------------------------------------------
    // BFS
    // ------------------------------------------------------------
    public static ArrayList<Integer> bfs(int[][] adj, int start) {
        int n = adj.length;
        boolean[] visited = new boolean[n];
        ArrayList<Integer> order = new ArrayList<>();
        Queue<Integer> q = new ArrayDeque<>();

        visited[start] = true;
        q.add(start);

        while (!q.isEmpty()) {
            int v = q.poll();
            order.add(v);

            for (int i = 0; i < n; i++) {
                if (adj[v][i] != 0 && !visited[i]) {
                    visited[i] = true;
                    q.add(i);
                }
            }
        }

        return order;
    }

    // ------------------------------------------------------------
    // DFS
    // ------------------------------------------------------------
    public static ArrayList<Integer> dfs(int[][] adj, int start) {
        ArrayList<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[adj.length];
        dfsRec(adj, start, visited, order);
        return order;
    }

    private static void dfsRec(int[][] adj, int v, boolean[] visited, ArrayList<Integer> order) {
        visited[v] = true;
        order.add(v);

        for (int i = 0; i < adj.length; i++)
            if (adj[v][i] != 0 && !visited[i])
                dfsRec(adj, i, visited, order);
    }
}
