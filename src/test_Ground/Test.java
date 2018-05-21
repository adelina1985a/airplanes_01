package test_Ground;

import java.util.Iterator;
import java.util.LinkedHashSet;

import airplanes_01.Coord;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("test package running");
		LinkedHashSet<Coord> someList = new LinkedHashSet<Coord>();
		someList.add(new Coord(1, 2));
		someList.add(new Coord(2, 3));
		someList.add(new Coord(1, 2));
		someList.add(new Coord(1, 2));
		someList.add(new Coord(2, 3));
		someList.add(new Coord(1, 2));
		Iterator<Coord> i = someList.iterator();
		while(i.hasNext()){
			Coord tmp = i.next();
			System.out.println(tmp.getRow() + " " + tmp.getCol());
		}
	}

}
