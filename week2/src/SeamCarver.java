/* *****************************************************************************
 *  Name: Vitali Makarevich
 *  Date: 01-08-2022
 **************************************************************************** */

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public class SeamCarver {
  private Color[][] pictureMatrix;
  private int height;
  private int width;

  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    checkNull(picture);
    this.pictureMatrix = new Color[picture.width()][picture.height()];
    for (int i = 0; i < picture.width(); i++) {
      for (int j = 0; j < picture.height(); j++) {
        this.pictureMatrix[i][j] = picture.get(i, j);
      }
    }
    this.width = picture.width();
    this.height = picture.height();
  }

  // current picture
  public Picture picture() {
    Picture picture = new Picture(width, height);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        picture.set(i, j, this.pictureMatrix[i][j]);
      }
    }

    return picture;
  }

  // width of current picture
  public int width() {
    return this.width;
  }

  // height of current picture
  public int height() {
    return this.height;
  }

  // energy of pixel at column x and row y
  public double energy(int x, int y) {
    if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
      throw new IllegalArgumentException("Argument out of bounds");
    }
    if (x == 0 || x == this.width() - 1 || y == 0 || y == this.height() - 1) {
      return 1000;
    }
    double xEnergyDiffPow2 = Math.pow(
        this.pictureMatrix[x - 1][y].getRed() - this.pictureMatrix[x + 1][y].getRed(), 2
    ) +
        Math.pow(
            this.pictureMatrix[x - 1][y].getGreen() - this.pictureMatrix[x + 1][y].getGreen(), 2) +
        Math.pow(
            this.pictureMatrix[x - 1][y].getBlue() - this.pictureMatrix[x + 1][y].getBlue(), 2);
    double yEnergyDiffPow2 =
        Math.pow(this.pictureMatrix[x][y + 1].getRed() - this.pictureMatrix[x][y - 1].getRed(), 2) +
            Math.pow(
                this.pictureMatrix[x][y + 1].getGreen() - this.pictureMatrix[x][y - 1].getGreen(),
                2) +
            Math.pow(
                this.pictureMatrix[x][y + 1].getBlue() - this.pictureMatrix[x][y - 1].getBlue(), 2);

    return Math.sqrt(xEnergyDiffPow2 + yEnergyDiffPow2);
  }

  // sequence of indices for horizontal seam
  public int[] findHorizontalSeam() {
    EdgeWeightedDigraph edgeWeightedDigraph = this.constructHorizontalDigraph();
    // feel free to use either, Topological is just faster since in our case its DAG.
    // Topological: O(E + V);
    // Dijkstra: O(VlogV + E);
    // DijkstraShortestPath shortestPath = new DijkstraShortestPath(edgeWeightedDigraph, 0);
    TopologicalDirectedPath shortestPath = new TopologicalDirectedPath(edgeWeightedDigraph);
    Iterable<Integer> path = shortestPath.pathTo(this.width * this.height + 1);
    int[] oneDimPath = new int[this.width];
    int j = 0;

    for (int pathItem : path) {
      // As our algo is modified with synthetic vertices at the left and right, we need to skip
      // first and last items(they are synthetic elements)
      if (j == 0 || j == this.width + 2) {
        j++;
        continue;
      }
      // push only y part(index 1)
      oneDimPath[j - 1] = oneDimensionalToTwoDimensional(pathItem)[1];
      j++;
    }

    return oneDimPath;
  }

  // sequence of indices for vertical seam
  public int[] findVerticalSeam() {
    EdgeWeightedDigraph edgeWeightedDigraph = this.constructVerticalDigraph();
    DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(edgeWeightedDigraph, 0);
    Iterable<Integer> path = dijkstraShortestPath.pathTo(this.width * this.height + 1);
    int[] oneDimPath = new int[this.height];
    int j = 0;
    for (int pathItem : path) {
      // As our algo is modified with synthetic vertices at the top and bottom, we need to skip
      // first and last items(they are synthetic elements)
      if (j == 0 || j == this.height + 2) {
        j++;
        continue;
      }
      int[] coord = oneDimensionalToTwoDimensional(pathItem);

      // push only x part(index 0)
      oneDimPath[j - 1] = coord[0];
      j++;
    }

    return oneDimPath;
  }

  // Constructs special digraph with 2 special vertices that serve as start & end, so
  // start connects to every first row pixel and last row each pixel connects to the end
  private EdgeWeightedDigraph constructVerticalDigraph() {
    // +2 because of 2 special vertices
    EdgeWeightedDigraph edgeWeightedDigraph = new EdgeWeightedDigraph(
        this.width * this.height + 2);
    for (int i = 0; i < this.width; i++) {
      // one time connect start item to each 1st row pixel
      edgeWeightedDigraph.addEdge(
          new DirectedEdge(0, twoDimensionalCoordToOneDimensional(i, 0), this.energy(i, 0)));
      // one time connect each end row to the end(weight is 0)
      // as numeration starts from 0
      int endItem = width * height + 1;
      int source = twoDimensionalCoordToOneDimensional(i, this.height - 1);
      edgeWeightedDigraph.addEdge(
          new DirectedEdge(source, endItem,
                           0));
    }
    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height - 1; j++) {
        List<int[]> neighbours = bottomNeighboursForCoordinates(i, j);
        for (int[] neighbour : neighbours) {
          double weight = this.energy(neighbour[0], neighbour[1]);
          edgeWeightedDigraph.addEdge(
              new DirectedEdge(
                  twoDimensionalCoordToOneDimensional(i, j),
                  twoDimensionalCoordToOneDimensional(neighbour[0], neighbour[1]),
                  weight
              )
          );
        }
      }
    }

    return edgeWeightedDigraph;
  }

  // Constructs special digraph with 2 special vertices that serve as start & end, so
  // start connects to every first row pixel and last row each pixel connects to the end
  private EdgeWeightedDigraph constructHorizontalDigraph() {
    // +2 because of 2 special vertices
    EdgeWeightedDigraph edgeWeightedDigraph = new EdgeWeightedDigraph(
        this.width * this.height + 2);
    for (int i = 0; i < this.height; i++) {
      // one time connect start item to each 1st column pixel
      edgeWeightedDigraph.addEdge(
          new DirectedEdge(0, twoDimensionalCoordToOneDimensional(0, i), this.energy(0, i)));
      // one time connect each end item to the end(weight is 0)
      // as numeration starts from 0
      int endItem = width * height + 1;
      edgeWeightedDigraph.addEdge(
          new DirectedEdge(twoDimensionalCoordToOneDimensional(this.width - 1, i), endItem,
                           0));
    }
    for (int i = 0; i < this.width - 1; i++) {
      for (int j = 0; j < this.height; j++) {
        List<int[]> neighbours = rightNeighboursForCoordinates(i, j);
        for (int[] neighbour : neighbours) {
          double weight = this.energy(neighbour[0], neighbour[1]);
          edgeWeightedDigraph.addEdge(
              new DirectedEdge(
                  twoDimensionalCoordToOneDimensional(i, j),
                  twoDimensionalCoordToOneDimensional(neighbour[0], neighbour[1]),
                  weight
              )
          );
        }
      }
    }

    return edgeWeightedDigraph;
  }

  // Returns existing neighbours from 3 to the bottom of the pixel, knowing the size of x-y.
  private List<int[]> bottomNeighboursForCoordinates(int x, int y) {
    List<int[]> neighbours = new LinkedList<>();
    if (y + 1 == this.height) return neighbours;
    if (x - 1 >= 0) {
      neighbours.add(new int[] { x - 1, y + 1 });
    }
    neighbours.add(new int[] { x, y + 1 });
    if (x + 1 < this.width) {
      neighbours.add(new int[] { x + 1, y + 1 });
    }


    return neighbours;
  }

  // Returns existing neighbours from 3 to the right of the pixel, knowing the size of x-y.
  private List<int[]> rightNeighboursForCoordinates(int x, int y) {
    List<int[]> neighbours = new LinkedList<>();
    if (x + 1 == this.width) return neighbours;
    neighbours.add(new int[] { x + 1, y });
    if (y - 1 >= 0) {
      neighbours.add(new int[] { x + 1, y - 1 });
    }
    if (y + 1 < this.height) {
      neighbours.add(new int[] { x + 1, y + 1 });
    }

    return neighbours;
  }

  private void checkNull(Object a) {
    if (a == null) throw new IllegalArgumentException("Argument must not be null");
  }

  private void checkSeamArray(int[] array, int expSize, int maxCoordinate) {
    if (array.length != expSize) {
      throw new IllegalArgumentException("Array is of unexpected size");
    }
    int prevItem = Integer.MIN_VALUE;
    for (int item : array) {
      if (item < 0 || item >= maxCoordinate) {
        throw new IllegalArgumentException("Coordinate is out of range");
      }
      if (prevItem != Integer.MIN_VALUE && Math.abs(item - prevItem) > 1) {
        throw new IllegalArgumentException("Coordinate is not adjacent");
      }
      prevItem = item;
    }
  }

  // Used to work in one dimensional space like graph V identifiers
  private int twoDimensionalCoordToOneDimensional(int x, int y) {
    int result = x * this.height + y + 1;
    return result;
  }

  private int[] oneDimensionalToTwoDimensional(int oneDimCoord) {
    int x = Math.floorDiv(oneDimCoord - 1, this.height);
    int y = Math.floorMod(oneDimCoord - 1, this.height);
    return new int[] { x, y };
  }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    checkNull(seam);
    if (this.height == 1)
      throw new IllegalArgumentException("Cannot remove last line from picture");
    checkSeamArray(seam, this.width, this.height);
    int x = 0;
    for (int yCoord : seam) {
      for (int coordAfterY = yCoord; coordAfterY < height - 1; coordAfterY++) {
        this.pictureMatrix[x][coordAfterY] = this.pictureMatrix[x][coordAfterY + 1];
      }
      x++;
    }
    for (int i = 0; i < this.width; i++) {
      // Release Color objects for the last line since it's now last -1
      this.pictureMatrix[i][this.height - 1] = null;
    }
    this.height--;
  }

  // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    checkNull(seam);
    if (this.width == 1) throw new IllegalArgumentException("Cannot remove last line from picture");
    checkSeamArray(seam, this.height, this.width);
    int y = 0;
    for (int xCoord : seam) {
      for (int coordAfterX = xCoord; coordAfterX < width - 1; coordAfterX++) {
        this.pictureMatrix[coordAfterX][y] = this.pictureMatrix[coordAfterX + 1][y];
      }
      y++;
    }
    for (int i = 0; i < this.height; i++) {
      // Release Color objects for the last line since it's now last -1
      this.pictureMatrix[this.width - 1][i] = null;
    }
    this.width--;
  }
}
