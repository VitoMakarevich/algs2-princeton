/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Stack;

public class DijkstraShortestPath {
  private final double[] distTo;
  private final Integer[] edgeTo;

  public DijkstraShortestPath(EdgeWeightedDigraph edgeWeightedDigraph, int source) {
    this.distTo = new double[edgeWeightedDigraph.V()];
    this.edgeTo = new Integer[edgeWeightedDigraph.V()];
    IndexMinPQ<Double> minPQ = new IndexMinPQ<>(edgeWeightedDigraph.V());
    this.distTo[source] = 0;
    for (int i = 0; i < edgeWeightedDigraph.V(); i++) {
      if (i != source)
        this.distTo[i] = Double.POSITIVE_INFINITY;
    }
    this.edgeTo[0] = null;
    minPQ.insert(0, 0.0d);

    while (!minPQ.isEmpty()) {
      int vertex = minPQ.delMin();
      for (DirectedEdge e : edgeWeightedDigraph.adj(vertex)) {
        if (this.distTo[e.to()] > this.distTo[vertex] + e.weight()) {
          this.distTo[e.to()] = this.distTo[vertex] + e.weight();
          this.edgeTo[e.to()] = vertex;
          if (minPQ.contains(e.to())) {
            minPQ.decreaseKey(e.to(), this.distTo[vertex] + e.weight());
          }
          else {
            minPQ.insert(e.to(), this.distTo[vertex] + e.weight());
          }
        }
      }
    }
  }

  public boolean hasPathTo(int v) {
    return this.distTo[v] != Integer.MAX_VALUE;
  }

  public double distTo(int v) {
    if (!hasPathTo(v)) {
      throw new IllegalArgumentException("Vertex isn't connected to source");
    }
    return this.distTo[v];
  }

  public Iterable<Integer> pathTo(int v) {
    Stack<Integer> stack = new Stack<>();
    for (Integer i = edgeTo[v]; i != null; i = edgeTo[i]) {
      stack.push(i);
    }

    return stack;
  }
}
