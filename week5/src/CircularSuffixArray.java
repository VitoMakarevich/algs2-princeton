/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.Arrays;

public class CircularSuffixArray {
  private final int length;
  private final Integer[] index;

  // circular suffix array of s
  public CircularSuffixArray(String s) {
    if (s == null) throw new IllegalArgumentException("Argument must be not null");
    length = s.length();
    index = new Integer[length];

    for (int i = 0; i < length; i++) {
      index[i] = i;
    }

    Arrays.sort(index, (Integer t, Integer t1) -> {
      for (int i = 0; i < length; i++) {
        char c = s.charAt((t + i) % length);
        char c1 = s.charAt((t1 + i) % length);

        if (c < c1) return -1;
        if (c > c1) return 1;
      }
      return t.compareTo(t1);
    });
  }

  public int length() {
    return length;
  }

  // returns index of ith sorted suffix
  public int index(int i) {
    if (i < 0 || i >= length) {
      throw new java.lang.IllegalArgumentException(
          "I must be in range");
    }
    return index[i];
  }

  // unit testing (required)
  public static void main(String[] args) {
  }

}
