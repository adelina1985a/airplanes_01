package airplanes_01;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;

public class Game {
  public int difficulty = 0;
  public Scanner reader;
  public static int size = Config.gridSize;
  
  public Game(int difficulty, Scanner reader) {
    super();
    this.difficulty = difficulty;
    this.reader = reader;
  }
  
  public void start() {
    String s = "";
    Grid playerGrid = new Grid();
    int noOfPlanes = playerGrid.maxNoOfPlanes;
    System.out.println("Let's build our " + noOfPlanes + " planes");
    if (Config.testMode && Config.playerPlanes != null) {
      // hardcoded player planes when testing
      for (String coordStr : Config.playerPlanes) {
        Airplane airplane = Utils.parseCoords(coordStr);
        if (airplane == null) {
          System.out.println("invalid inputs");
          System.exit(1);
        } else {
          if (!playerGrid.buildPlane(airplane, false)) {
            System.exit(1);
          }
        }
      }
      playerGrid.drawGrid(true, true);
    } else {
      while (noOfPlanes != 0) {
        boolean valid = false;
        Coord coord = null;
        Airplane.Directions dir = null;
        while (!valid) {
          System.out.println("Enter coordinates:");
          s = reader.next();
          coord = Utils.readCoords(s);
          if (coord != null && !coord.isHeadInCorner()) {
            valid = true;
          }
        }
        valid = false;
        while (!valid) {
          System.out.println("Enter direction (U,D,L,R): ");
          s = reader.next();
          dir = Utils.readDir(s);
          if (dir != null) {
            valid = true;
          }
        }
        if (playerGrid.buildPlane(coord, dir, false)) {
          playerGrid.drawGrid(true, true);
          noOfPlanes--;
        }
      }
    }
    // build AI grid and planes
    Grid aiGrid = new Grid();
    // from cfg, rarely used
    if (Config.testMode && Config.aiPlanes != null) {
      for (String coordStr : Config.aiPlanes) {
        Airplane airplane = Utils.parseCoords(coordStr);
        if (airplane == null) {
          System.out.println("invalid inputs");
          System.exit(1);
        } else {
          if (!aiGrid.buildPlane(airplane, false)) {
            System.exit(1);
          }
        }
      }
    } else {
      // or build AI grid with random planes
      noOfPlanes = playerGrid.maxNoOfPlanes;
      int noOfDirs = Airplane.Directions.values().length;// 4...
      while (noOfPlanes != 0) {
        boolean planeOK = false;
        while (!planeOK) {
          Random rand = new Random();
          int row = rand.nextInt(size) + 'A';
          int col = rand.nextInt(size) + 1;
          Airplane.Directions dir = Airplane.Directions.values()[rand.nextInt(noOfDirs)];
          planeOK = aiGrid.buildPlane(new Coord(row, col), dir, true);
        }
        noOfPlanes--;
      }
    }// end of AI planes
    
    System.out.println("Let's attack!");// aaattaaaaaaaaaaaaaaaaaaaaaaaaack!!!!!!!!!!!!!!!!!!!!!
    aiGrid.drawGrid(false, false);
    boolean lastAiAttackWasAHit = false;
    /** used only on difficulty 3, to form hit clusters */
    LinkedHashSet<Coord> aiHits = new LinkedHashSet<Coord>();
    /** coords used by AI, ironically defaulted to A1 */
    // init this so it's never null
    Coord attack = new Coord('A', 1);
    // LinkedHashSet is ord and unique
    /** used for main udlr cluster, tmp used for adjacent hits */
    LinkedHashSet<Coord> clusterAttacks = new LinkedHashSet<Coord>();
    LinkedHashSet<Coord> tmpClusterAttacks = new LinkedHashSet<Coord>();
    // til 1 grid has no planes left
    while (playerGrid.isOneAlive() && aiGrid.isOneAlive()) {
      // player turn
      if (Config.testMode && Config.skipPlayerInput) {
        // do nothing or prompt/pause for next move
      } else {
        boolean valid = false;
        boolean youAlreadyHit = true;
        Coord coord = null;
        LinkedHashSet<Coord> aiGridTries = new LinkedHashSet<>(aiGrid.tries);
        while (!valid || youAlreadyHit) {
          System.out.println("Enter coordinates to attack: ");
          s = reader.next();
          coord = Utils.readCoords(s);
          if (coord != null) {
            valid = true;
            youAlreadyHit = aiGridTries.contains(coord);
            if (youAlreadyHit) {
              System.out.println("You already hit there");
            }
          }
        }
        aiGrid.attacked(coord);
        aiGrid.drawGrid(false, false);
      }
      // end game condition
      if (!aiGrid.isOneAlive()) {
        int noOfMoves = aiGrid.tries.size();
        String cheater = "";
        if (aiGrid.getNoOfPlanes() == noOfMoves) {
          cheater = " Cheater!";
        }
        System.out.println("You won in " + noOfMoves + " moves." + cheater);
        break;
      }
      // ai's turn
      LinkedHashSet<Coord> playerGridTries = new LinkedHashSet<>(playerGrid.tries);
      switch (difficulty) {
      case 1:// 1 = easiest, all AI hits are random
        attack = genRandCoords(playerGridTries);
        break;
      case 2:// 2 = medium, some thinking involved
        if (lastAiAttackWasAHit) {// this won't run at first attack
          // based on last coords, we try to attack U, D, L, R
          attackUDLR(attack, clusterAttacks, playerGridTries);
        }
        // if first attack, or we're out of prospects
        // aka potential coords to attack
        if (clusterAttacks.size() == 0) {
          // random, or close to a corner?
          attack = genRandCoords(playerGridTries);
        } else {// if we have prospects, pick first and attack it
          attack = getNextCoords(clusterAttacks);
        }
        break;
      case 3: // 3 = hard, patterns? TODO WIP
        if (lastAiAttackWasAHit) {// this won't run at first attack
          aiHits.add(attack);
          if (clusterAttacks.size() == 0) {
            // based on last coords, we try to attack U, D, L, R
            attackUDLR(attack, clusterAttacks, playerGridTries);
          } else {// if main cluster non empty, add prospects to tmp
            attackUDLR(attack, tmpClusterAttacks, playerGridTries);
          }
        } // if first attack, or we're out of prospects
        if (clusterAttacks.size() == 0 && tmpClusterAttacks.size() == 0) {
          attack = genRandCoords(playerGridTries);
        } else {// if we have prospects
          // if 1 out of 4 directions left
          if (clusterAttacks.size() == 1) {
            /**
             * Redundancy case #1: when trying cluster udlr, if 3 miss, 4th is
             * redundant so we assume 2 more hits in the 4th direction
             **/
            Coord currentAttack = clusterAttacks.iterator().next();
            int clusterOriginRow = currentAttack.getTrueRow();
            int clusterOriginCol = currentAttack.getTrueCol() - 1;
            Coord clusterOrigin = new Coord(clusterOriginRow, clusterOriginCol);
            boolean clusterMissed3 = false;
            if (clusterOrigin.isInRange() && aiHits.contains(clusterOrigin)) {
              // check if U, D, L missed
              Coord tmp = new Coord(clusterOriginRow - 1, clusterOriginCol);
              LinkedHashSet<Coord> tries = new LinkedHashSet<>(playerGridTries);
              // up
              if (tmp.isInRange() && tries.contains(tmp) && !aiHits.contains(tmp)) {
                // down
                tmp = new Coord(clusterOriginRow + 1, clusterOriginCol);
                if (tmp.isInRange() && tries.contains(tmp) && !aiHits.contains(tmp)) {
                  // left
                  tmp = new Coord(clusterOriginRow, clusterOriginCol - 1);
                  if (tmp.isInRange() && tries.contains(tmp)
                      && !aiHits.contains(tmp)) {
                    clusterMissed3 = true;
                  }
                }
              }
            }
            if (clusterMissed3) {// if we missed in 3 directions
              clusterAttacks.remove(currentAttack);
              int currAttCol = currentAttack.getTrueCol();
              // that makes R, the 4th, redundant and
              // marks 2 hits in that direction
              playerGridTries.add(currentAttack);// mark 1st hit
              // follow up with potential U, D
              attackUDLR(currentAttack, tmpClusterAttacks, playerGridTries);
              currentAttack.setCol(currAttCol + 1);
              if (currentAttack.isInRange()) {
                // mark 2nd hit
                playerGridTries.add(currentAttack);
                // follow up with potential U, D, R
                attackUDLR(currentAttack, tmpClusterAttacks, playerGridTries);
              }
              // we still need to attack something this turn
              if (clusterAttacks.size() == 0) {
                attack = genRandCoords(playerGridTries);
              } else {
                attack = getNextCoords(clusterAttacks);
              }
            } else {// proceed as planned
              clusterAttacks.remove(currentAttack);
              attack = new Coord(currentAttack.getTrueRow(), currentAttack.getTrueCol());
            }
          } // end of case #1  
          else {// cluster or tmpCluster non empty ctd.
            if (clusterAttacks.size() != 0) {
              // pick first and attack it
              attack = getNextCoords(clusterAttacks);
            } else {// we finished a main cluster
              // check if all hit and pick next accordingly
              boolean clusterHit4 = false;//TODO after R#2
              //...
              //attack = getNextCoords(tmpClusterAttacks);
              /**
               * Redundancy case #2: prospect in a corner surrounded by 3 hits;
               * also means one of the hits on the edge is a Head so we attack
               * tails in 2 directions opposite of the edge
               */
              Coord currentAttack = getNextCoords(tmpClusterAttacks);
              Coord.Corner corner = currentAttack.getCorner();
              if (corner != null) {//depending on which corner, check surroundings
                int currentRow = currentAttack.getTrueRow();
                int currentCol = currentAttack.getTrueCol();
                switch (corner) {
                case TOP_LEFT:
                  //if(aiHits.contains(new Coord())
                  break;
                case TOP_RIGHT:
                  if (aiHits.contains(new Coord(currentRow, currentCol - 1)) &&
                      aiHits.contains(new Coord(currentRow + 1, currentCol - 1)) &&
                      aiHits.contains(new Coord(currentRow + 1, currentCol))) {
                    // empty tmpCluster; TODO wise?
                    tmpClusterAttacks.clear();
                    // attack tail in 2 possible directions
                    Coord toAttack = new Coord(currentRow, currentCol - 3);
                    addCoord(toAttack, tmpClusterAttacks, playerGridTries);
                    toAttack = new Coord(currentRow + 1, currentCol - 3);
                    addCoord(toAttack, tmpClusterAttacks, playerGridTries);
                    toAttack = new Coord(currentRow + 2, currentCol - 3);
                    addCoord(toAttack, tmpClusterAttacks, playerGridTries);       
                    
                    toAttack = new Coord(currentRow - 3, currentCol);
                    addCoord(toAttack, tmpClusterAttacks, playerGridTries);
                    toAttack = new Coord(currentRow - 3, currentCol - 1);
                    addCoord(toAttack, tmpClusterAttacks, playerGridTries);
                    toAttack = new Coord(currentRow - 3, currentCol - 2);
                    addCoord(toAttack, tmpClusterAttacks, playerGridTries);
                  }
                  break;
                case BOTTOM_LEFT:
                  break;
                case BOTTOM_RIGHT:
                  break;
                default:
                  break;
                }// end of corner switch
              } else {// proceed as planned
                attack = new Coord(currentAttack);
              }
            }
          }
        }
        break;
      default:
        break;
      }// end of difficulty switch
      System.out.println("You were attacked at " + (char) attack.getTrueRow()
          + attack.getTrueCol());
      lastAiAttackWasAHit = playerGrid.attacked(attack);
      playerGrid.drawGrid(true, true);
      // end game condition
      if (!playerGrid.isOneAlive()) {
        System.out.println("You lost in " + playerGridTries.size() + " moves");
        aiGrid.drawGrid(false, true);
      }
    }
  }
  
  public static void attackUDLR(Coord toAttack, LinkedHashSet<Coord> clusterAttacks,
      LinkedHashSet<Coord> tries) {
    int rowToAttack = toAttack.getTrueRow();
    int colToAttack = toAttack.getTrueCol();
    Coord tmp = new Coord(rowToAttack - 1, colToAttack);
    addCoord(tmp, clusterAttacks, tries);
    tmp = new Coord(rowToAttack + 1, colToAttack);
    addCoord(tmp, clusterAttacks, tries);
    tmp = new Coord(rowToAttack, colToAttack - 1);
    addCoord(tmp, clusterAttacks, tries);
    tmp = new Coord(rowToAttack, colToAttack + 1);
    addCoord(tmp, clusterAttacks, tries);
  }

  public static void attackTail(Coord head, LinkedHashSet<Coord> clusterAttacks,
      LinkedHashSet<Coord> tries) {

  }

  public static void addCoord(Coord toAttack, LinkedHashSet<Coord> clusterAttacks,
      LinkedHashSet<Coord> tries) {
    boolean alreadyHit = tries.contains(toAttack);
    if (toAttack.isInRange() && !alreadyHit) {
      clusterAttacks.add(toAttack);
    }
  }

  public static Coord genRandCoords(LinkedHashSet<Coord> tries) {
    int rowToAttack = 'A';
    int colToAttack = 1;
    Coord attack = null;
    boolean alreadyHit = true;
    while (alreadyHit) {
      Random rand = new Random();
      rowToAttack = rand.nextInt(size) + 'A';
      colToAttack = rand.nextInt(size) + 1;
      attack = new Coord(rowToAttack, colToAttack);
      alreadyHit = tries.contains(attack);
    }
    return attack;
  }

  public static Coord getNextCoords(LinkedHashSet<Coord> coords) {
    Iterator<Coord> it = coords.iterator();
    Coord tmp = it.next();
    coords.remove(tmp);
    return new Coord(tmp.getTrueRow(), tmp.getTrueCol());
  }
}
