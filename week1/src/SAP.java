import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SAP {
  private final Digraph digraph;

  // constructor takes a digraph (not necessarily a DAG)
  public SAP(Digraph G) {
    this.digraph = new Digraph(G);
  }

  // length of shortest ancestral path between v and w; -1 if no such path
  public int length(int v, int w) {
    ArrayList<Integer> vIter = new ArrayList<>();
    vIter.add(v);
    ArrayList<Integer> wIter = new ArrayList<>();
    wIter.add(w);
    return this.getSap(vIter, new ArrayList<>(wIter))[1];
  }

  private int[] getSap(Iterable<Integer> v, Iterable<Integer> w) {
    int minDistance = Integer.MAX_VALUE;
    int ancestor = 0;
    BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(this.digraph, v);
    BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(this.digraph, w);
    for (int i = 0; i < this.digraph.V(); i++) {
      if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
        int distance = bfs1.distTo(i) + bfs2.distTo(i);
        if (distance < minDistance) {
          minDistance = distance;
          ancestor = i;
        }
      }
    }
    if (minDistance == Integer.MAX_VALUE) {
      return new int[] { -1, -1 };
    }
    return new int[] { ancestor, minDistance };
  }

  // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
  public int ancestor(int v, int w) {
    ArrayList<Integer> vIter = new ArrayList<>();
    vIter.add(v);
    ArrayList<Integer> wIter = new ArrayList<>();
    wIter.add(w);
    return this.getSap(vIter, new ArrayList<>(wIter))[0];
  }

  // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
  public int length(Iterable<Integer> v, Iterable<Integer> w) {
    this.check(v, w);
    if (!v.iterator().hasNext() || !w.iterator().hasNext()) {
      return -1;
    }
    return this.getSap(v, w)[1];
  }

  private void check(Iterable<Integer> v, Iterable<Integer> w) {
    if (v == null || w == null) {
      throw new IllegalArgumentException("arguments must not be null");
    }
    Consumer<Integer> isNotNull = (Integer item) -> {
      if (item == null) {
        throw new IllegalArgumentException("Iterable item must not be null");
      }
      if (item < 0 || item >= this.digraph.V()) {
        throw new IllegalArgumentException(
            "Iterable item must be in range 0 and " + this.digraph.V());
      }
    };
    v.forEach(isNotNull);
    w.forEach(isNotNull);
  }

  // a common ancestor that participates in shortest ancestral path; -1 if no such path
  public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
    this.check(v, w);
    if (!v.iterator().hasNext() || !w.iterator().hasNext()) {
      return -1;
    }
    return this.getSap(v, w)[0];
  }

  // do unit testing of this class
  public static void main(String[] args) {
    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    SAP sap = new SAP(G);
    while (!StdIn.isEmpty()) {
      int v = StdIn.readInt();
      int w = StdIn.readInt();
      int length = sap.length(v, w);
      int ancestor = sap.ancestor(v, w);
      StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
  }
}
