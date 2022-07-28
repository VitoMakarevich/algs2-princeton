/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;

class RootedGraph {
  private final Digraph digraph;

  public RootedGraph(Digraph digraph) {
    this.digraph = digraph;
  }

  public boolean isSingleRooted() {
    int countRoots = 0;
    for (int vert = 0; vert < this.digraph.V(); vert++) {
      int indegree = this.digraph.indegree(vert);
      int outdegree = this.digraph.outdegree(vert);
      // root is a vertex with no edges coming from it(outdegree == 0)
      // and > 0 edges coming to it(indegree > 0)
      if (outdegree == 0 && indegree > 0) {
        countRoots++;
      }
    }

    return countRoots == 1;
  }
}
