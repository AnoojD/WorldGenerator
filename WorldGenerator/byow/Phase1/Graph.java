package byow.Phase1;


import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    /**
     * @source GeekForGeeks
     * used code to find strongly connected components
     * modified to fit project
     * url: https://www.geeksforgeeks.org/strongly-connected-components/
     */
    private final int v;
    private final LinkedList[] adj;
    private List<List<Integer>> scc;


    public Graph(int ver) {
        scc = new ArrayList<>();
        this.v = ver;
        adj = new LinkedList[ver];
        for (int i = 0; i < ver; i++) {
            adj[i] = new LinkedList();
        }
    }


    public void addEdge(int ver, int w) {
        adj[ver].add(w);
    }


    public void dFSUtil(int ver, boolean[] visited, List<Integer> lst) {
        lst.add(ver);
        visited[ver] = true;
        int n;
        Iterator<Integer> i = adj[ver].iterator();
        while (i.hasNext()) {
            n = i.next();
            if (!visited[n]) {
                dFSUtil(n, visited, lst);
            }
        }
        scc.add(lst);
    }

    public Graph getTranspose() {
        Graph g = new Graph(this.v);
        for (int i = 0; i < this.v; i++) {

            for (Integer integer : (Iterable<Integer>) adj[i]) {
                g.adj[integer].add(i);
            }
        }
        return g;
    }

    public void fillOrder(int ver, boolean[] visited, Stack<Integer> stack) {
        visited[ver] = true;

        Iterator<Integer> i = adj[ver].iterator();
        while (i.hasNext()) {
            int n = i.next();
            if (!visited[n]) {
                fillOrder(n, visited, stack);
            }
        }


        stack.push(ver);
    }

    public void printSCCs() {
        Stack<Integer> stack = new Stack<Integer>();


        boolean[] visited = new boolean[this.v];
        for (int i = 0; i < this.v; i++) {
            visited[i] = false;
        }


        for (int i = 0; i < this.v; i++) {
            if (!visited[i]) {
                fillOrder(i, visited, stack);
            }
        }


        Graph gr = getTranspose();

        for (int i = 0; i < this.v; i++) {
            visited[i] = false;
        }

        while (!stack.isEmpty()) {
            List<Integer> lst = new ArrayList<>();
            int ver = stack.pop();
            if (!visited[ver]) {
                gr.dFSUtil(ver, visited, lst);
            }
        }
        List<List<Integer>> lst =  getRidOfDups(gr.getScc());
        this.setScc(lst);
    }

    public List<List<Integer>> getScc() {
        return this.scc;
    }

    public void setScc(List<List<Integer>> sos) {
        this.scc = sos;
    }

    public List<List<Integer>> getRidOfDups(List<List<Integer>> lst) {
        ArrayList<List<Integer>> noDup = new ArrayList<>();
        for (List<Integer> element : lst) {
            if (!noDup.contains(element)) {
                noDup.add(element);
            }
        }
        return noDup;
    }
}
