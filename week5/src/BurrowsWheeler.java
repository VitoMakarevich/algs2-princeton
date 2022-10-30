import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
  private static final int R = 256;

  // apply Burrows-Wheeler transform,
  // reading from standard input and writing to standard output
  public static void transform() {
    StringBuilder sourceStringBuilder = new StringBuilder();
    while (!BinaryStdIn.isEmpty()) {
      sourceStringBuilder.append(BinaryStdIn.readChar(8));
    }
    CircularSuffixArray circularSuffixArray = new CircularSuffixArray(
        sourceStringBuilder.toString());
    StringBuilder sortedSuffixes = new StringBuilder();
    int sourceStringPos = 0;
    for (int i = 0; i < sourceStringBuilder.length(); i++) {
      int sourceIndex = circularSuffixArray.index(i);
      if (sourceIndex == 0) {
        sourceStringPos = i;
        sortedSuffixes.append(sourceStringBuilder.charAt(sourceStringBuilder.length() - 1));
      }
      else {
        sortedSuffixes.append(sourceStringBuilder.charAt(sourceIndex - 1));
      }
    }

    BinaryStdOut.write(sourceStringPos);
    BinaryStdOut.write(sortedSuffixes.toString());
    BinaryStdOut.flush();
  }

  // apply Burrows-Wheeler inverse transform,
  // reading from standard input and writing to standard output
  public static void inverseTransform() {
    int first = BinaryStdIn.readInt();
    String input = BinaryStdIn.readString();
    internalDecode(input, first);
  }

  private static void internalDecode(String input, int first) {
    int[] count = new int[R + 1];
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      count[c + 1]++;
    }
    for (int i = 0; i < R; i++) {
      count[i + 1] += count[i];
    }

    char[] aux = new char[input.length()];
    int[] next = new int[input.length()];
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      aux[count[c]] = c;
      next[count[c]] = i;
      count[c]++;
    }

    int ptr = first;
    for (int i = 0; i < input.length(); i++) {
      BinaryStdOut.write(aux[ptr]);
      ptr = next[ptr];
    }
    BinaryStdOut.close();
  }

  // if args[0] is "-", apply Burrows-Wheeler transform
  // if args[0] is "+", apply Burrows-Wheeler inverse transform
  public static void main(String[] args) {
    if (args[0].equals("-")) {
      transform();
    }
    else if (args[0].equals("+")) {
      inverseTransform();
    }
  }
}
