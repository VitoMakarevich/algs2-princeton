/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Stack;

public class TopologicalDirectedPath {
  private double[] distTo;
  private Integer[] edgeTo;

  public TopologicalDirectedPath(EdgeWeightedDigraph edgeWeightedDigraph) {
    this.distTo = new double[edgeWeightedDigraph.V()];
    this.edgeTo = new Integer[edgeWeightedDigraph.V()];
    this.distTo[0] = 0;
    for (int i = 0; i < edgeWeightedDigraph.V(); i++) {
      if (i != 0)
        this.distTo[i] = Integer.MAX_VALUE;
    }
    this.edgeTo[0] = null;
    for (int v = 0; v < edgeWeightedDigraph.V(); v++) {
      for (DirectedEdge e : edgeWeightedDigraph.adj(v)) {
        if (this.distTo[e.to()] > this.distTo[e.from()] + e.weight()) {
          this.distTo[e.to()] = this.distTo[e.from()] + e.weight();
          this.edgeTo[e.to()] = e.from();
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
