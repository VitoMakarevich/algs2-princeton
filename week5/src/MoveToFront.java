/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;
import java.util.List;

public class MoveToFront {
  private static final short R = 256;
  private static List<Character> initialAlphabet = new LinkedList<Character>() {{
    for (int i = 0; i < R; i++) {
      add((char) i);
    }
  }};

  // apply move-to-front encoding, reading from standard input and writing to standard output
  public static void encode() {
    List<Character> characters = new LinkedList<>(initialAlphabet);

    while (!BinaryStdIn.isEmpty()) {
      char inputCharacter = BinaryStdIn.readChar();
      BinaryStdOut.write(insertCharAndPutToTheFront(characters, inputCharacter), 8);
    }
    BinaryStdOut.flush();
  }

  private static int insertCharAndPutToTheFront(List<Character> alphabet, char inputCharacter) {
    for (int pos = 0; pos < R; pos++) {
      char curChar = alphabet.get(pos);
      if (inputCharacter == curChar) {
        swapToStart(pos, alphabet);
        return pos;
      }
    }

    throw new IllegalArgumentException("Character not found");
  }

  private static char insertCharAndPutToTheFront(List<Character> alphabet, int pos) {
    char value = alphabet.get(pos);
    swapToStart(pos, alphabet);

    return value;
  }

  private static void swapToStart(int i, List<Character> state) {
    char temp = state.get(i);
    state.set(i, state.get(0));
    state.set(0, temp);
  }

  // apply move-to-front decoding, reading from standard input and writing to standard output
  public static void decode() {
    List<Character> characters = new LinkedList<>(initialAlphabet);

    while (!BinaryStdIn.isEmpty()) {
      int inputCharacterNumber = BinaryStdIn.readInt(8);
      BinaryStdOut.write(insertCharAndPutToTheFront(characters, inputCharacterNumber), 8);
    }
    BinaryStdOut.flush();
  }

  // if args[0] is "-", apply move-to-front encoding
  // if args[0] is "+", apply move-to-front decoding
  public static void main(String[] args) {
    if (args[0].equals("-")) {
      MoveToFront.encode();
    }
    else if (args[0].equals("+")) {
      MoveToFront.decode();
    }
    else {
      throw new IllegalArgumentException("Unknown argument " + args[0]);
    }
  }

}