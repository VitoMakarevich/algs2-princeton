public class BoggleSolver {
  private final UppercaseLettersTrieSet dictionary;

  // Initializes the data structure using the given array of strings as the dictionary.
  // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
  public BoggleSolver(String[] dictionary) {
    this.dictionary = new UppercaseLettersTrieSet();
    for (String word : dictionary) {
      this.dictionary.add(word);
    }
  }

  // Returns the set of all valid words in the given Boggle board, as an Iterable.
  public Iterable<String> getAllValidWords(BoggleBoard board) {
    BoggleGraph boggleGraph = new BoggleGraph(board, dictionary);
    return boggleGraph.getFoundWords();
  }

  // Returns the score of the given word if it is in the dictionary, zero otherwise.
  // (You can assume the word contains only the uppercase letters A through Z.)
  public int scoreOf(String word) {
    if (this.dictionary.contains(word)) {
      if (word.length() == 3 || word.length() == 4) return 1;
      if (word.length() == 5) return 2;
      if (word.length() == 6) return 3;
      if (word.length() == 7) return 5;
      if (word.length() >= 8) return 11;
    }

    return 0;
  }
}
