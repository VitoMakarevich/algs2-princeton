import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
  private final WordNet wordNet;

  public Outcast(WordNet wordnet) {
    this.wordNet = wordnet;
  }

  public String outcast(String[] nouns) {
    int sum = Integer.MIN_VALUE;
    String termToExclude = null;
    for (String noun : nouns) {
      int curSum = 0;
      for (String nounInner : nouns) {
        curSum += wordNet.distance(noun, nounInner);
      }
      if (curSum > sum) {
        sum = curSum;
        termToExclude = noun;
      }
    }

    return termToExclude;
  }

  public static void main(String[] args) {
    WordNet wordnet = new WordNet(args[0], args[1]);
    Outcast outcast = new Outcast(wordnet);
    for (int t = 2; t < args.length; t++) {
      In in = new In(args[t]);
      String[] nouns = in.readAllStrings();
      StdOut.println(args[t] + ": " + outcast.outcast(nouns));
    }
  }
}