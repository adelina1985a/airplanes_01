package airplanes_01;

import java.io.InvalidObjectException;
import java.util.LinkedHashSet;

public class Grid {
	public enum State {
		empty, miss, plane, planeHit
	};

	private Object[][] grid;
	public static int size = 10;
	public final int maxNoOfPlanes = 3;
	public final int planeSize = 8;// 1 for head, 3 for body, 4 for wings
	private int noOfPlanes;
	public Airplane[] planes;
	/**
	 * list of previous attacks on this grid
	 */
	public LinkedHashSet<Coord> tries;

	public Grid() {
		grid = new Object[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = State.empty;
			}
		}
		noOfPlanes = 0;
		tries = new LinkedHashSet<Coord>();
		planes = new Airplane[maxNoOfPlanes];
	}

	/*
	 * public Object[][] getGrid() { return grid; }
	 * 
	 * public void setGrid(Object[][] grid) { this.grid = grid; }
	 */

	public int getNoOfPlanes() {
		return noOfPlanes;
	}

	public void setNoOfPlanes(int noOfPlanes) {
		this.noOfPlanes = noOfPlanes;
	}

	private void setPoint(int row, int col) throws InvalidObjectException {
		if (grid[row][col] == State.plane) {
			throw new InvalidObjectException(null);
		} else {
			grid[row][col] = State.plane;
		}
	}

	/**
	 * builds a plane on the grid
	 * @param position row, col for head
	 * @param dir U, D, L, R
	 * @param isAI we print msgs to player only
	 * @return true if all OK
	 */
	public boolean buildPlane(Coord position, Airplane.Directions dir, boolean isAI) {
		if (noOfPlanes < maxNoOfPlanes) {
			// serialize the grid so we can revert in case of invalid coords
			String tmpGrid = Utils.objectToString(grid);
			planes[noOfPlanes] = new Airplane(position, dir);
			int row = position.getRow();
			int col = position.getCol();
			// if within limits
			if (0 <= row && row < size && 0 <= col && col < size) {
				if (new Coord(row, col).isHeadInCorner()) {
					return false;
				} else {// all ok
					try {
						// set head
						setPoint(row, col);

					} catch (InvalidObjectException ioe) {
						if (!isAI) {
							System.out.println("overlapping planes");
						}
						return false;
					}
					boolean valid = true;
					boolean overlap = false;
					switch (dir) {// depending on direction
					case UP:
						if (row + 3 < size && // if body fits
								col - 1 >= 0 && col + 1 < size) {// if wings fit
							try {
								// set body
								setPoint(row + 1, col);
								setPoint(row + 2, col);
								setPoint(row + 3, col);
								// set wings
								setPoint(row + 1, col - 1);
								setPoint(row + 1, col + 1);
								setPoint(row + 3, col - 1);
								setPoint(row + 3, col + 1);
							} catch (InvalidObjectException ioe) {
								overlap = true;
							}
						} else {
							valid = false;
						}
						break;
					case DOWN:
						if (row - 3 >= 0 && // if body fits
								col - 1 >= 0 && col + 1 < size) {// if wings fit
							try {
								// set body
								setPoint(row - 1, col);
								setPoint(row - 2, col);
								setPoint(row - 3, col);
								// set wings
								setPoint(row - 1, col - 1);
								setPoint(row - 1, col + 1);
								setPoint(row - 3, col - 1);
								setPoint(row - 3, col + 1);
							} catch (InvalidObjectException ioe) {
								overlap = true;
							}
						} else {
							valid = false;
						}
						break;
					case LEFT:
						if (col + 3 < size && // if body fits
								row - 1 >= 0 && row + 1 < size) {// if wings fit
							try {
								// set body
								setPoint(row, col + 1);
								setPoint(row, col + 2);
								setPoint(row, col + 3);
								// set wings
								setPoint(row - 1, col + 1);
								setPoint(row + 1, col + 1);
								setPoint(row - 1, col + 3);
								setPoint(row + 1, col + 3);
							} catch (InvalidObjectException ioe) {
								overlap = true;
							}
						} else {
							valid = false;
						}
						break;
					case RIGHT:
						if (col - 3 >= 0 && // if body fits
								row - 1 >= 0 && row + 1 < size) {// if wings fit
							try {
								// set body
								setPoint(row, col - 1);
								setPoint(row, col - 2);
								setPoint(row, col - 3);
								// set wings
								setPoint(row - 1, col - 1);
								setPoint(row + 1, col - 1);
								setPoint(row - 1, col - 3);
								setPoint(row + 1, col - 3);
							} catch (InvalidObjectException ioe) {
								overlap = true;
							}
						} else {
							valid = false;
						}
						break;
					}
					if (!valid) {
						grid = (Object[][]) Utils.stringToObject(tmpGrid);
						if (!isAI) {
							System.out.println("invalid inputs");
						}
						return false;
					}
					if (overlap) {
						grid = (Object[][]) Utils.stringToObject(tmpGrid);
						if (!isAI) {
							System.out.println("overlapping planes");
						}
						return false;
					}
					noOfPlanes++;
					return true;
				}
			} else {
				System.out.println("inputs not within limits");
				return false;
			}
		} else {
			System.out.println("too many planes");
			return false;
		}
	}
	
	/**
   * builds a plane on the grid
	 * @param airplane has head of plane and direction
	 * @param isAI we print msgs to player only
   * @return true if all OK
	 */
	public boolean buildPlane(Airplane airplane, boolean isAI){
		return buildPlane(airplane.getPosition(), airplane.getDir(), isAI);
	}

	/**
	 * marks a coordinate with hit or miss
	 * 
	 * @param p
	 * @return <b>true</b> if hit
	 */
	public boolean attacked(Coord p) {
		int row = p.getRow();
		int col = p.getCol();
		tries.add(p);
		for (int i = 0; i < maxNoOfPlanes; i++) {// for each plane
			if (planes[i].getPosition().equals(p)) {// check if head
				planes[i].setHeadHit(true);
			}
		}
		if ((State) grid[row][col] == State.plane) {
			grid[row][col] = State.planeHit;
			return true;
		} else {
			grid[row][col] = State.miss;
			return false;
		}
	}

	public boolean isOneAlive() {
		for (int i = 0; i < noOfPlanes; i++) {
			if (!planes[i].isHeadHit()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param isPlayer will player planes be shown
	 */
	public void drawGrid(boolean isPlayer, boolean showPlanes) {
		if (isPlayer) {
			System.out.println("Y o u r   g r i d");
		} else {
			System.out.println("A I   g r i d");
		}
		char row = 'A';
		System.out.print("  ");// corner between letters and numbers
		// first line - numbers
		for (int i = 0; i < size; i++) {
			System.out.print((i + 1) + " ");
		}
		System.out.println();
		for (int i = 0; i < size; i++) {
			// first char in the row will be the row letter
			System.out.print(row + " ");
			for (int j = 0; j < size; j++) {
				switch ((State) grid[i][j]) {
				case plane:
					if (showPlanes) {
						Airplane.Directions dir = null;
						for (int index = 0; index < noOfPlanes; index++) {
							Coord head = planes[index].getPosition();
							if (i == head.getRow() && j == head.getCol()) {
								// if we found a head, we get Dir to draw it
								dir = planes[index].getDir();
								break;
							}
						}
						if (dir != null) {
							switch (dir) {
							case UP:
								System.out.print("A");
								break;
							case DOWN:
								System.out.print("V");
								break;
							case LEFT:
								System.out.print("<");
								break;
							case RIGHT:
								System.out.print(">");
								break;
							}
						} else
							System.out.print("@");
					} else
						System.out.print("•");
					break;
				case empty:
					System.out.print("•");
					break;
				case planeHit:
					System.out.print("X");
					break;
				case miss:
					System.out.print("o");
					break;
				}
				System.out.print(" ");
			}
			row++;
			System.out.println();
		}
		System.out.println();
	}
}
