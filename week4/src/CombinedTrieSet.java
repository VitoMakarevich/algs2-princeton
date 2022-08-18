/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.TST;

/*
 * Uses 26 R-way trie for keys with keys below size threshold and ternary search trie for keys
 * longer or equal to the threshold.
 * */
public class CombinedTrieSet {
  private final int lengthThreshold;
  private final UppercaseLettersTrieSet smallKeys;
  // As it's only has Map interface, we put single Boolean.TRUE always just for contract
  private final TST<Boolean> longKeys;

  public CombinedTrieSet(int lengthThreshold) {
    this.lengthThreshold = lengthThreshold;
    this.smallKeys = new UppercaseLettersTrieSet();
    this.longKeys = new TST<>();
  }

  public boolean contains(String word) {
    if (isSmall(word)) {
      return this.smallKeys.contains(word);
    }
    return this.longKeys.contains(word);
  }

  public boolean hasPrefix(String prefix) {
    return this.smallKeys.hasKeysWithPrefix(prefix) || this.longKeys.keysWithPrefix(prefix)
                                                                    .iterator().hasNext();
  }

  public void add(String word) {
    if (isSmall(word)) {
      this.smallKeys.add(word);
    }
    else {
      this.longKeys.put(word, Boolean.TRUE);
    }
  }

  private boolean isSmall(String word) {
    return word.length() < this.lengthThreshold;
  }
}
