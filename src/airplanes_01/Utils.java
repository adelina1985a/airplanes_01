package airplanes_01;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Utils {

	/**
	 * @param obj
	 * @return serialized obj
	 */
	public static String objectToString(Object obj) {
		String serializedObject = "";
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(obj);
			so.flush();
			serializedObject = bo.toString();
		} catch (Exception e) {
			System.out.println(e);
		}
		return serializedObject;
	}

	/**
	 * @param serializedObject
	 * @return de-serialized obj
	 */
	public static Object stringToObject(String serializedObject) {
		Object obj = null;
		try {
			byte b[] = serializedObject.getBytes();
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			obj = si.readObject();
		} catch (Exception e) {
			System.out.println(e);
		}
		return obj;
	}

	/**
	 * @param str
	 * @return true if <b>str</b> is numeric
	 */
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * turns given string <b>s</b> into plane coords, row and col
	 * 
	 * @param s
	 * @return <b>null</b> if invalid coords,</br>Point(row, col) otherwise
	 */
	public static Coord readCoords(String s) {
		s = s.trim();
		char firstChar = s.charAt(0);
		if (!Character.isLetter(firstChar)) {
			System.out.println("First coord not a letter");
			return null;
		} else {
			firstChar = Character.toUpperCase(firstChar);
			if ('A' > firstChar || firstChar > 'A' + 9) {
				System.out.println("First coord not within range");
				return null;
			} else {
				String strCol = s.substring(1);
				if (!isNumeric(strCol)) {
					System.out.println("Second coord not a number");
					return null;
				} else {
					int col = Integer.valueOf(strCol);
					if (1 > col || col > 10) {
						System.out.println("Second coord not within range");
						return null;
					} else {
						return new Coord(firstChar, col);
					}
				}
			}
		}
	}
	
	public static Airplane.Directions readDir(String s){
		Airplane.Directions dir = null;
		if (s.length() > 1) {
			System.out.println("Invalid direction");
		} else {
			switch (s.toUpperCase()) {
			case "U":
				dir = Airplane.Directions.UP;
				break;
			case "D":
				dir = Airplane.Directions.DOWN;
				break;
			case "L":
				dir = Airplane.Directions.LEFT;
				break;
			case "R":
				dir = Airplane.Directions.RIGHT;
				break;
			}
			if (dir == null) {
				System.out.println("Invalid direction");
			}
		}
		return dir;
	}
	
	public static Airplane parseCoords(String s){
		Coord coords = readCoords(s.substring(0, 2));
		Airplane.Directions dir = readDir(s.substring(2));
		return new Airplane(coords, dir);
	}
}
