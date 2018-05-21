package airplanes_01;

/**
 * Coordinates used in the grid, as a pair of row, col
 * 
 * @author Ade
 * 
 */
public class Coord {
  public enum Corner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
  };

  private int row;
  private int col;

  /**
   * Constructor with field params
   * 
   * @param row
   * @param col
   */
  public Coord(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public Coord(Coord c) {
    this.row = c.row;
    this.col = c.col;
  }

  /**
   * only used internally, use <b>getRow</b> when given user input
   * 
   * @return
   */
  public int getTrueRow() {
    return row;
  }

  public int getRow() {
    return row - 'A';
  }

  public void setRow(int row) {
    this.row = row;
  }

  /**
   * only used internally, use <b>getCol</b> when given user input
   * 
   * @return
   */
  public int getTrueCol() {
    return col;
  }

  public int getCol() {
    return col - 1;
  }

  public void setCol(int col) {
    this.col = col;
  }

  /**
   * @return true if it's within 0-9
   */
  public boolean isInRange() {
    int size = Grid.size;
    int row = this.getRow();
    int col = this.getCol();
    if (0 <= row && row <= size - 1 && 0 <= col && col <= size - 1) {
      return true;
    }
    return false;
  }

  public boolean isHeadInCorner() {
    if (this.getCorner() != null) {
      System.out.println("Head cannot be in a corner");
      return true;
    }
    return false;
  }

  /**
   * @return one of the 4 corners of the grid for current coord; <b>null</b> if
   *         not in corner
   */
  public Corner getCorner() {
    int size = Grid.size;
    int row = this.getRow();
    int col = this.getCol();
    Corner corner = null;
    if (row == 0 && col == 0) {
      corner = Corner.TOP_LEFT;
    }
    if (row == 0 && col == size - 1) {
      corner = Corner.TOP_RIGHT;
    }
    if (row == size - 1 && col == 0) {
      corner = Corner.BOTTOM_LEFT;
    }
    if (row == size - 1 && col == size - 1) {
      corner = Corner.BOTTOM_RIGHT;
    }
    return corner;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + col;
    result = prime * result + row;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Coord other = (Coord) obj;
    if (col != other.col)
      return false;
    if (row != other.row)
      return false;
    return true;
  }
}
