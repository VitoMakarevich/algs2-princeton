Result: 88/100.

Not passing:

- Memory tests. It's probably because we create `EdgeWeightedDigraph` and `DirectedEdge`.
  In practice, it's not necessary, because for our case topological sort is
  just iteration, but I don't want to spend time on that.
  Result per report: `Estimated student memory (bytes) = 48.00 n^2 - 127.00 n + 182.42`.
  Allowed: `12 n^2 bytes`.
- Timing tests - 2 are failing, I assume it's because of previous point,
  the diff to make it passing is very small.
