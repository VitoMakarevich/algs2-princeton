/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
 * Ties Boggle board with underlying graph.
 * */
public class BoggleGraph {
  private final BoggleBoard board;
  private final Map<Integer, String> charIdToString = new HashMap<>();
  private final Map<String, Boolean> prefixCache = new TreeMap<>();
  private final Map<String, Boolean> dictCache = new TreeMap<>();
  private final Map<Integer, Iterable<Integer>> adjCache = new TreeMap<>();

  private final UppercaseLettersTrieSet dictionary;
  private final Set<String> foundWords = new HashSet<>();
  private final Digraph digraph;
  private boolean computed;

  public BoggleGraph(BoggleBoard board, UppercaseLettersTrieSet dictionary) {
    this.board = board;
    this.dictionary = dictionary;
    this.digraph = new Digraph(board.cols() * board.rows());

    for (int i = 0; i < board.cols(); i++) {
      for (int j = 0; j < board.rows(); j++) {
        for (Integer[] coords : adj(i, j)) {
          digraph.addEdge(twoDimToOneDim(i, j, board.cols()),
                          twoDimToOneDim(coords[0], coords[1], board.cols()));
        }
      }
    }
    for (int i = 'A'; i <= 'Z'; i++) {
      this.charIdToString.put(i, i == 'Q' ? "QU" : String.valueOf((char) i));
    }
  }

  /**
   * Return string representation of letter
   */
  public String getLetter(int pos) {
    int[] coord = this.oneDimToTwoDim(pos);
    return this.charIdToString.get((int) this.board.getLetter(coord[1], coord[0]));
  }

  private void internalDFS(int pos, boolean[] marked, StringBuilder prev) {
    marked[pos] = true;
    StringBuilder newWord = new StringBuilder(prev).append(
        this.getLetter(pos));
    String candidate = newWord.toString();
    if (newWord.length() >= 3 && this.dictionary.contains(candidate)) {
      foundWords.add(newWord.toString());
    }

    if (this.dictionary.hasKeysWithPrefix(candidate)) {
      for (int v : this.digraph.adj(pos)) {
        if (!marked[v]) {
          internalDFS(v, marked, newWord);
        }
      }
    }
    marked[pos] = false;
  }

  private List<Integer[]> adj(int i, int j) {
    int xSize = this.board.cols();
    int ySize = this.board.rows();
    List<Integer[]> results = new ArrayList<>();
    if (i - 1 >= 0) {
      results.add(new Integer[] { i - 1, j });
      if (j - 1 >= 0) {
        results.add(new Integer[] { i - 1, j - 1 });
      }
      if (j + 1 < ySize) {
        results.add(new Integer[] { i - 1, j + 1 });
      }
    }
    if (i + 1 < xSize) {
      results.add(new Integer[] { i + 1, j });
      if (j - 1 >= 0) {
        results.add(new Integer[] { i + 1, j - 1 });
      }
      if (j + 1 < ySize) {
        results.add(new Integer[] { i + 1, j + 1 });
      }
    }
    if (j - 1 >= 0) {
      results.add(new Integer[] { i, j - 1 });
    }
    if (j + 1 < ySize) {
      results.add(new Integer[] { i, j + 1 });
    }

    return results;
  }

  private int twoDimToOneDim(int x, int y, int xSize) {
    return y * (xSize) + x;
  }

  private int[] oneDimToTwoDim(int pos) {
    return new int[] {
        Math.floorMod(pos, this.board.cols()), Math.floorDiv(pos, this.board.cols())
    };
  }

  private void compute() {
    boolean[] marked = new boolean[board.cols() * board.rows()];
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < board.cols() * board.rows(); i++) {
      this.internalDFS(i, marked, sb);
    }
    this.computed = true;
  }

  public Set<String> getFoundWords() {
    if (!computed) this.compute();
    return new HashSet<>(foundWords);
  }

  public Digraph getDigraph() {
    return this.digraph;
  }
}
