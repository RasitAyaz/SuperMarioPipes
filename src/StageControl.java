import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

// Ahmet Emirhan Bakkal
// Muhammed Raşit Ayaz

@SuppressWarnings("WeakerAccess")
public class StageControl {
    private int[][] grid;
    private ArrayList<Tile> tiles;
    private Tile nextTile;
    private int nextEntrance;
    private boolean isReached, levelCompleted;
    private Path path;

    private PathElement moveTo, lineTo, moveTo2, lineTo2;

    private MediaPlayer dungeonWinPlayer;

    Main main;
    TileControl tileControl;

    public StageControl() {
        grid = new int[4][4];
        tiles = new ArrayList<>();
        path = new javafx.scene.shape.Path();
        Media dungeonWinAudio = new Media(new File("audio/dungeon-win.mp3").toURI().toString());
        dungeonWinPlayer = new MediaPlayer(dungeonWinAudio);

        moveTo = new MoveTo(0, 0);
        lineTo = new LineTo(0, 0);
        moveTo2 = new MoveTo(0, 0);
        lineTo2 = new LineTo(0, 0);
    }

    public void connect(Main main) {
        this.main = main;
        tileControl = main.getTileControl();
    }

    public void readStage(int level) {
        try {
            Scanner br = new Scanner(new FileReader("stages/level" + level + ".txt"));
            String line;

            // Clear the game map to use it on different level
            tiles.clear();

            // Read input file
            while (br.hasNext()) {
                line = br.nextLine();
                if (line.equals("")) continue;

                String[] splitLine = line.split(",");
                int id = Integer.parseInt(splitLine[0]);
                int x = (id - 1) % 4;
                int y = (id - 1) / 4;
                String type = splitLine[1];
                String rotation = splitLine[2];
                switch (type) {
                    case "Starter":
                        if (rotation.equals("Vertical")) {
                            grid[x][y] = 2;
                            tiles.add(new Tile(x, y, 2, 0, 3));
                        } else if (rotation.equals("Horizontal")) {
                            grid[x][y] = 3;
                            tiles.add(new Tile(x, y, 3, 0, 4));
                        }
                        break;
                    case "Empty":
                        if (rotation.equals("none")) {
                            grid[x][y] = 1;
                            tiles.add(new Tile(x, y, 1, 0, 0));
                        } else if (rotation.equals("Free")) {
                            grid[x][y] = 0;
                            tiles.add(new Tile(x, y, 0, 0, 0));
                        }
                        break;
                    case "Pipe":
                        switch (rotation) {
                            case "Vertical":
                                grid[x][y] = 12;
                                tiles.add(new Tile(x, y, 12, 1, 3));
                                break;
                            case "Horizontal":
                                grid[x][y] = 13;
                                tiles.add(new Tile(x, y, 13, 2, 4));
                                break;
                            case "00":
                                grid[x][y] = 14;
                                tiles.add(new Tile(x, y, 14, 1, 4));
                                break;
                            case "01":
                                grid[x][y] = 15;
                                tiles.add(new Tile(x, y, 15, 1, 2));
                                break;
                            case "10":
                                grid[x][y] = 16;
                                tiles.add(new Tile(x, y, 16, 3, 4));
                                break;
                            case "11":
                                grid[x][y] = 17;
                                tiles.add(new Tile(x, y, 17, 2, 3));
                                break;
                        }
                        break;
                    case "PipeStatic":
                        switch (rotation) {
                            case "Vertical":
                                grid[x][y] = 6;
                                tiles.add(new Tile(x, y, 6, 1, 3));
                                break;
                            case "Horizontal":
                                grid[x][y] = 7;
                                tiles.add(new Tile(x, y, 7, 2, 4));
                                break;
                            case "00":
                                grid[x][y] = 8;
                                tiles.add(new Tile(x, y, 8, 1, 4));
                                break;
                            case "01":
                                grid[x][y] = 9;
                                tiles.add(new Tile(x, y, 9, 1, 2));
                                break;
                            case "10":
                                grid[x][y] = 10;
                                tiles.add(new Tile(x, y, 10, 3, 4));
                                break;
                            case "11":
                                grid[x][y] = 11;
                                tiles.add(new Tile(x, y, 11, 2, 3));
                                break;
                        }
                        break;
                    case "End":
                        if (rotation.equals("Vertical")) {
                            grid[x][y] = 4;
                            tiles.add(new Tile(x, y, 4, 3, 0));
                        } else if (rotation.equals("Horizontal")) {
                            grid[x][y] = 5;
                            tiles.add(new Tile(x, y, 5, 4, 0));
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean canMoveNext(Tile currentTile, int exitPoint) {
        int firstExit = currentTile.getEntranceOne();
        int secondExit = currentTile.getEntranceTwo();

        int xCurrent = currentTile.getX();
        int yCurrent = currentTile.getY();

        int xNext, yNext;

        int type = currentTile.getType();

        // If the current tile has a pipe on it create a path element for ball animation
        if (type != 0 && type != 1)
            createPath(currentTile, nextEntrance);

        // Starter and end tiles conditions
        if (currentTile.getType() == 2) {
            xNext = xCurrent;
            yNext = yCurrent + 1;
            nextEntrance = 1;
            nextTile = getTileById(yNext * 4 + xNext);
            return true;
        } else if (currentTile.getType() == 3) {
            xNext = xCurrent - 1;
            yNext = yCurrent;
            nextEntrance = 2;
            nextTile = getTileById(yNext * 4 + xNext);
            return true;
        } else if (currentTile.getType() == 4 || currentTile.getType() == 5) {
            isReached = true;
        }

        // If tile has a rounded pipe on it, add two path elements for animation
        if ((type <= 11 && type >= 8) || (type <= 17 && type >= 14)) {
            path.getElements().addAll(moveTo, lineTo);
            path.getElements().addAll(moveTo2, lineTo2);
        } else {
            path.getElements().addAll(moveTo, lineTo);
        }

        // These parts are used to connect pipes to each other
        int nextExit = 0;
        if (exitPoint == firstExit)
            nextExit = secondExit;
        else if (exitPoint == secondExit)
            nextExit = firstExit;

        switch (nextExit) {
            case 1:
                yNext = yCurrent - 1;
                xNext = xCurrent;
                if ((xNext >= 0 && xNext <= 3) && (yNext >= 0 && yNext <= 3)) {
                    nextTile = getTileById(yNext * 4 + xNext);
                    if (nextTile.getEntranceOne() == 3 || nextTile.getEntranceTwo() == 3) {
                        if (nextTile.getType() == 4 || nextTile.getType() == 5) isReached = true;
                        nextEntrance = 3;
                        return true;
                    } else return false;
                } else return false;

            case 2:
                yNext = yCurrent;
                xNext = xCurrent + 1;
                if ((xNext >= 0 && xNext <= 3) && (yNext >= 0 && yNext <= 3)) {
                    nextTile = getTileById(yNext * 4 + xNext);
                    if (nextTile.getEntranceOne() == 4 || nextTile.getEntranceTwo() == 4) {
                        if (nextTile.getType() == 4 || nextTile.getType() == 5) isReached = true;
                        nextEntrance = 4;
                        return true;
                    } else return false;
                } else return false;

            case 3:
                yNext = yCurrent + 1;
                xNext = xCurrent;
                if ((xNext >= 0 && xNext <= 3) && (yNext >= 0 && yNext <= 3)) {
                    nextTile = getTileById(yNext * 4 + xNext);
                    if (nextTile.getEntranceOne() == 1 || nextTile.getEntranceTwo() == 1) {
                        if (nextTile.getType() == 4 || nextTile.getType() == 5) isReached = true;
                        nextEntrance = 1;
                        return true;
                    } else return false;
                } else return false;

            case 4:
                yNext = yCurrent;
                xNext = xCurrent - 1;
                if ((xNext >= 0 && xNext <= 3) && (yNext >= 0 && yNext <= 3)) {
                    nextTile = getTileById(yNext * 4 + xNext);
                    if (nextTile.getEntranceOne() == 2 || nextTile.getEntranceTwo() == 2) {
                        if (nextTile.getType() == 4 || nextTile.getType() == 5) isReached = true;
                        nextEntrance = 2;
                        return true;
                    } else return false;
                } else return false;
        }
        return false;
    }

    public void checkIsReached(Tile currentTile, int exitPoint) {
        // Check whether the connection of the pipes is completed and reached exit point
        if (canMoveNext(currentTile, exitPoint)) {
            if (isReached) {
                createPath(nextTile, 0);
                path.getElements().addAll(moveTo, lineTo);
                main.playTransition();
                main.getTxtStatus().setText("Congratulations! You've completed this level.");
                main.getBtNextLv().setDisable(false);
                levelCompleted = true;

                if (main.isMusicOn()) {
                    dungeonWinPlayer.play();
                    main.getBgmPlayer().pause();
                    dungeonWinPlayer.setOnEndOfMedia(() -> {
                        dungeonWinPlayer.stop();
                        if (main.isMusicOn())
                            main.getBgmPlayer().play();
                    });
                }
                isReached = false;
                return;
            }
            checkIsReached(nextTile, nextEntrance);
        } else {
            levelCompleted = false;
            path.getElements().clear();
            createPath(getStarterTile(), 0);
            path.getElements().addAll(moveTo, lineTo);
        }
    }

    private Tile getTileById(int id) {
        return tiles.get(id);
    }

    public Tile getStarterTile() {
        for (Tile tile : tiles) {
            if (tile.getType() == 2 || tile.getType() == 3) {
                return tile;
            }
        }
        return null;
    }

    private void createPath(Tile tile, int entrance) {
        double tileSize = tileControl.getTileSize();
        int type = tile.getType();

        double x = tileControl.getTileViews()[tile.getY()][tile.getX()].getTranslateX() + tileSize / 8;
        double y = tileControl.getTileViews()[tile.getY()][tile.getX()].getTranslateY() + tileSize / 8;

        // Create path elements according to current tile type
        if (type == 2) {
            moveTo = new MoveTo(x, y - tileSize / 4);
            lineTo = new LineTo(x, y + tileSize / 2);
        } else if (type == 3) {
            moveTo = new MoveTo(x + tileSize / 4, y);
            lineTo = new LineTo(x - tileSize / 2, y);
        } else if (type == 4) {
            moveTo = new MoveTo(x, y + tileSize / 2);
            lineTo = new LineTo(x, y - tileSize / 4);
        } else if (type == 5) {
            moveTo = new MoveTo(x - tileSize / 2, y);
            lineTo = new LineTo(x + tileSize / 4, y);
        } else if (type == 6 || type == 12) {
            if (entrance == 1) {
                moveTo = new MoveTo(x, y - tileSize / 2);
                lineTo = new LineTo(x, y + tileSize / 2);
            } else if (entrance == 3) {
                moveTo = new MoveTo(x, y + tileSize / 2);
                lineTo = new LineTo(x, y - tileSize / 2);
            }
        } else if (type == 7 || type == 13) {
            if (entrance == 4) {
                moveTo = new MoveTo(x - tileSize / 2, y);
                lineTo = new LineTo(x + tileSize / 2, y);
            } else if (entrance == 2) {
                moveTo = new MoveTo(x + tileSize / 2, y);
                lineTo = new LineTo(x - tileSize / 2, y);
            }
        } else if (type == 8 || type == 14) {
            if (entrance == 1) {
                moveTo = new MoveTo(x, y - tileSize / 2);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x - tileSize / 2, y);
            } else if (entrance == 4) {
                moveTo = new MoveTo(x - tileSize / 2, y);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x, y - tileSize / 2);
            }
        } else if (type == 9 || type == 15) {
            if (entrance == 1) {
                moveTo = new MoveTo(x, y - tileSize / 2);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x + tileSize / 2, y);
            } else if (entrance == 2) {
                moveTo = new MoveTo(x + tileSize / 2, y);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x, y - tileSize / 2);
            }
        } else if (type == 10 || type == 16) {
            if (entrance == 3) {
                moveTo = new MoveTo(x, y + tileSize / 2);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x - tileSize / 2, y);
            } else if (entrance == 4) {
                moveTo = new MoveTo(x - tileSize / 2, y);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x, y + tileSize / 2);
            }
        } else if (type == 11 || type == 17) {
            if (entrance == 2) {
                moveTo = new MoveTo(x + tileSize / 2, y);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x, y + tileSize / 2);
            } else if (entrance == 3) {
                moveTo = new MoveTo(x, y + tileSize / 2);
                lineTo = new LineTo(x, y);
                moveTo2 = new MoveTo(x, y);
                lineTo2 = new LineTo(x + tileSize / 2, y);
            }
        }
    }

    public int getExitPoint() {
        return getStarterTile().getEntranceTwo();
    }

    public int[][] getGrid() {
        return grid;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public Path getPath() {
        return path;
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public MediaPlayer getDungeonWinPlayer() {
        return dungeonWinPlayer;
    }
}
