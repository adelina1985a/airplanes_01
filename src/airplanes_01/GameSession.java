package airplanes_01;

import java.util.Scanner;

public class GameSession {
  private static GameSession gameSesh = null;

  private GameSession() {
    System.out.println("---Console airplanes---");
    Grid exampleGrid = new Grid();
    int row = 'D';
    int col = 5;
    exampleGrid.buildPlane(new Coord(row, col), Airplane.Directions.LEFT, false);
    System.out.println("An example of a plane at " + (char) row + col + ", head facing "
        + Airplane.Directions.LEFT);
    exampleGrid.drawGrid(true, true);
    // Reading from System.in
    Scanner reader = new Scanner(System.in);
    String s = "";
    boolean keepPlaying = true;
    while (keepPlaying) {
      int difficulty = 0;
      // read and set difficulty
      while (difficulty == 0) {
        System.out.println("Enter difficulty: (1 - easy, 2 - medium, 3 - hard)");
        s = reader.next();
        if (!Utils.isNumeric(s)) {
          System.out.println("Not a number");
        } else {
          int diff = Integer.valueOf(s);
          if (diff < 1 || diff > 3) {
            System.out.println("Difficulty not in range");
          } else {
            difficulty = diff;
          }
        }
      }
      // let the game begin
      Game g = new Game(difficulty, reader);
      g.start();
      System.out.println("Play again? (y/n)");
      s = reader.next();
      if(Character.toUpperCase(s.charAt(0)) != 'Y') {
        keepPlaying = false;
        System.out.println("Been fun!");
      }
    }
    // once finished
    reader.close();
  }

  public static GameSession getInstance() {
    if (gameSesh == null)
      gameSesh = new GameSession();
    return gameSesh;
  }
}
