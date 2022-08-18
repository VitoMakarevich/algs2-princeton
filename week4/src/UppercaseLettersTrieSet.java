/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

/*
 * R-way trie which has only upper case English alphabet letters(R-way trie of 26).
 * */
public class UppercaseLettersTrieSet {

  private static final int NUMBER_OF_CHARS = 26;

  private final Node root;

  private static class Node {
    private boolean isString;
    private Node[] next;

    private Node() {
      this.isString = false;
      this.next = new Node[NUMBER_OF_CHARS];
    }

    private void addNext(char c, Node node) {
      int id = c - 'A';
      this.next[id] = node;
    }

    public Node getNode(char c) {
      return this.next[c - 'A'];
    }

    private void setString() {
      this.isString = true;
    }
  }

  public UppercaseLettersTrieSet() {
    this.root = new Node();
  }

  public void add(String string) {
    addInternal(string, 0, root);
  }

  private Node addInternal(String string, int pos, Node curRoot) {
    Node cur = curRoot.getNode(string.charAt(pos));
    if (cur == null) {
      cur = new Node();
      curRoot.addNext(string.charAt(pos), cur);
    }
    if (string.length() - 1 == pos) {
      if (!cur.isString) {
        cur.setString();
      }
      return cur;
    }
    return addInternal(string, pos + 1, cur);
  }

  public boolean hasKeysWithPrefix(String prefix) {
    Node prefixNode = get(prefix, root, 0, prefix.length());
    return prefixNode != null;
  }

  public boolean contains(String word) {
    Node res = get(word, root, 0, word.length());
    if (res == null) return false;
    return res.isString;
  }

  private Node get(String q, Node cur, int pos, int length) {
    if (cur == null) return null;
    if (pos == length) {
      return cur;
    }
    return get(q, cur.getNode(q.charAt(pos)), pos + 1, length);
  }
}
