import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

import java.util.Arrays;

public class WordNet {
  private final ST<Integer, String> idNounMap = new ST<>();
  private final ST<String, Bag<Integer>> nounIdMap = new ST<>();
  private final Digraph digraph;
  private final SAP sap;

  // constructor takes the name of the two input files
  public WordNet(String synsets, String hypernyms) {
    if (synsets == null || hypernyms == null)
      throw new IllegalArgumentException("argument is null");

    In in = new In(synsets);
    while (in.hasNextLine()) {
      String line = in.readLine();
      String[] lineSplitted = line.split(",");
      int id = Integer.parseInt(lineSplitted[0]);
      String[] words = lineSplitted[1].split(" ");
      Bag<String> itemsBag = new Bag<>();
      Arrays.stream(words).forEach(item -> {
        itemsBag.add(item);
        if (!nounIdMap.contains(item)) {
          nounIdMap.put(item, new Bag<>());
        }
        nounIdMap.get(item).add(id);
      });
      idNounMap.put(id, lineSplitted[1]);
    }
    digraph = new Digraph(idNounMap.size());
    In hypernymsIn = new In(hypernyms);
    while (hypernymsIn.hasNextLine()) {
      String line = hypernymsIn.readLine();
      String[] lineSplitted = line.split(",");
      int currentId = Integer.parseInt(lineSplitted[0]);
      Arrays.stream(lineSplitted).skip(1).map(Integer::parseInt).forEach(hypernym -> {
        digraph.addEdge(currentId, hypernym);
      });
    }
    DirectedCycle directedCycle = new DirectedCycle(this.digraph);
    if (directedCycle.hasCycle()) {
      throw new IllegalArgumentException("Graph has cycle");
    }
    RootedGraph rootedGraph = new RootedGraph(this.digraph);
    if (!rootedGraph.isSingleRooted()) {
      throw new IllegalArgumentException("Graph is not rooted");
    }
    this.sap = new SAP(this.digraph);
  }

  // returns all WordNet nouns
  public Iterable<String> nouns() {
    return this.nounIdMap.keys();
  }

  // is the word a WordNet noun?
  public boolean isNoun(String word) {
    return this.nounIdMap.contains(word);
  }

  // distance between nounA and nounB (defined below)
  public int distance(String nounA, String nounB) {
    if (!nounIdMap.contains(nounA) || !nounIdMap.contains(nounB)) {
      throw new IllegalArgumentException("either nounA or nounB is not present");
    }

    return sap.length(this.nounIdMap.get(nounA), this.nounIdMap.get(nounB));
  }

  // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
  // in a shortest ancestral path (defined below)
  public String sap(String nounA, String nounB) {
    if (!nounIdMap.contains(nounA) || !nounIdMap.contains(nounB)) {
      throw new IllegalArgumentException("either nounA or nounB is not present");
    }

    return this.idNounMap.get(
        sap.ancestor(this.nounIdMap.get(nounA), this.nounIdMap.get(nounB)));
  }

  // do unit testing of this class
  public static void main(String[] args) {
    // Just check the constructor for cycle/non-rooted graph
    Iterable<String> nouns = new WordNet(args[0], args[1]).nouns();
    nouns.forEach(System.out::println);
  }
}