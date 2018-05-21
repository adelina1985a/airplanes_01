package airplanes_01;

public class Airplane {
	public enum Directions {
		UP, DOWN, LEFT, RIGHT
	};

	private Coord position;
	private Directions dir;
	private boolean headHit;

	/** empty constructor */
	public Airplane() {
		super();
	}

	/**
	 * @param position row, col for head
	 * @param dir U, D, L, R
	 */
	public Airplane(Coord position, Directions dir) {
		super();
		this.position = position;
		this.dir = dir;
		this.headHit = false;
	}

	/** a row, col for the head of the plane */
	public Coord getPosition() {
		return position;
	}

	/** U, D, L, R */
	public Directions getDir() {
		return dir;
	}

	/** U, D, L, R */
	public void setDir(Directions dir) {
		this.dir = dir;
	}

	/*
	 * public void move(Point position, directions dir) { setPosition(position);
	 * setDir(dir); }
	 */

	public boolean isHeadHit() {
		return headHit;
	}

	public void setHeadHit(boolean headHit) {
		this.headHit = headHit;
	}

}
