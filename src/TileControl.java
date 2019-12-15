import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

// Ahmet Emirhan Bakkal
// Muhammed Raşit Ayaz

public class TileControl {
    private static double oldSceneX, oldSceneY;
    private static double oldTranslateX, oldTranslateY;
    private static double tileSize;

    private static ArrayList<Tile> tiles = StageControl.getTiles();

    private static Media shellAudio = new Media(new File("audio/shell-hit.mp3").toURI().toString());
    private static MediaPlayer shellPlayer = new MediaPlayer(shellAudio);

    private static ImageView ballView;
    private static ImageView[][] tileViews;

    private static boolean canMoveLeft = false, canMoveUp = false, canMoveRight = false, canMoveDown = false;

    public static void readGrid(int[][] grid, StackPane centerPane, double stageHeight) throws Exception {
        tileViews = new ImageView[4][4];
        tileSize = stageHeight / 7;

        // Clear the image view objects each time a tile moves
        centerPane.getChildren().clear();

        // Load bg image of the game map which is 4x4 empty tiles
        Image imgBg = new Image(new FileInputStream("graphics/bg.png"));
        ImageView bgView = new ImageView(imgBg);
        bgView.setFitWidth(tileSize * 4);
        bgView.setFitHeight(tileSize * 4);
        centerPane.getChildren().addAll(bgView);

        // Load tile images
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int type = grid[c][r];
                if (type != 0) {
                    Image imgTile = new Image(new FileInputStream("graphics/" + type + ".png"));
                    tileViews[r][c] = new ImageView(imgTile);
                    tileViews[r][c].setFitHeight(tileSize + 0.5);
                    tileViews[r][c].setFitWidth(tileSize + 0.5);
                    centerPane.getChildren().addAll(tileViews[r][c]);

                    tileViews[r][c].setTranslateX(-tileSize * 3 / 2 + c * tileSize);
                    tileViews[r][c].setTranslateY(-tileSize * 3 / 2 + r * tileSize);
                }
            }
        }

        // Load ball image
        Image ball = new Image(new FileInputStream("graphics/ball.png"));
        ballView = new ImageView(ball);
        ballView.setFitHeight(tileSize / 4);
        ballView.setFitWidth(tileSize / 4);

        // Relocate ball image
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double m = 0, n = 0;
                if (grid[j][i] == 2) {
                    m = tileSize / 4;
                    n = 0;
                } else if (grid[j][i] == 3) {
                    m = 0;
                    n = tileSize / 4;
                }

                if (grid[j][i] == 2 || grid[j][i] == 3) {
                    ballView.setTranslateX(-tileSize * 3 / 2 + j * tileSize + n);
                    ballView.setTranslateY(-tileSize * 3 / 2 + i * tileSize - m);
                }
            }
        }

        Tile starterTile = StageControl.getStarterTile();

        StageControl.checkIsReached(starterTile, StageControl.getExitPoint());

        /* DRAG BALL
        ballView.setOnMousePressed(event -> {
            oldSceneX = event.getSceneX();
            oldSceneY = event.getSceneY();
            oldTranslateX = ballView.getTranslateX();
            oldTranslateY = ballView.getTranslateY();
        });
        ballView.setOnMouseDragged(event -> {
            ballView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
            ballView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
        }); */

        centerPane.getChildren().addAll(ballView);

        // Check tile movements
        relocateGrid(grid, tileViews, centerPane, stageHeight);
    }

    private static void relocateGrid(int[][] grid, ImageView[][] tileViews, StackPane centerPane, double stageHeight) {
        double tileSize = stageHeight / 7;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int type = grid[c][r];
                if (type != 0 && !StageControl.isLevelCompleted()) {
                    final ImageView tileView = tileViews[r][c];

                    canMoveLeft = false;
                    canMoveUp = false;
                    canMoveRight = false;
                    canMoveDown = false;

                    if ((grid[c][r] >= 12 && grid[c][r] <= 17) || grid[c][r] == 1) {
                        try {
                            canMoveLeft = grid[c - 1][r] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            canMoveLeft = false;
                        }

                        try {
                            canMoveUp = grid[c][r - 1] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            canMoveUp = false;
                        }

                        try {
                            canMoveRight = grid[c + 1][r] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            canMoveRight = false;
                        }

                        try {
                            canMoveDown = grid[c][r + 1] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            canMoveDown = false;
                        }
                    }

                    final int row = r;
                    final int col = c;

                    tileView.setOnMousePressed(event -> {
                        oldSceneX = event.getSceneX();
                        oldTranslateX = tileView.getTranslateX();
                        oldSceneY = event.getSceneY();
                        oldTranslateY = tileView.getTranslateY();
                    });

                    if (canMoveDown && canMoveRight && canMoveUp && canMoveLeft) { /* ---- ALL WAYS ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (Math.abs(event.getSceneX() - oldSceneX) > Math.abs(event.getSceneY() - oldSceneY)) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX < -tileSize)
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                else if (event.getSceneX() - oldSceneX > tileSize)
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY < -tileSize)
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                else if (event.getSceneY() - oldSceneY > tileSize)
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    if (tileView.getTranslateX() - oldTranslateX < -tileSize / 2) {
                                        tileView.setTranslateX(oldTranslateX - tileSize);
                                        grid[col - 1][row] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col - 1);
                                        tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);
                                    } else {
                                        tileView.setTranslateX(oldTranslateX + tileSize);
                                        grid[col + 1][row] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col + 1);
                                        tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);
                                    }
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    if (tileView.getTranslateY() - oldTranslateY < -tileSize / 2) {
                                        tileView.setTranslateY(oldTranslateY - tileSize);
                                        grid[col][row - 1] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col - 4);
                                        tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);
                                    } else {
                                        tileView.setTranslateY(oldTranslateY + tileSize);
                                        grid[col][row + 1] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col + 4);
                                        tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);
                                    }
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveDown && canMoveRight && canMoveLeft) { /* ---- DOWN, RIGHT, LEFT ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (Math.abs(event.getSceneX() - oldSceneX) > event.getSceneY() - oldSceneY) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX < -tileSize)
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                else if (event.getSceneX() - oldSceneX > tileSize)
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY < 0)
                                    tileView.setTranslateY(oldTranslateY);
                                else if (event.getSceneY() - oldSceneY > tileSize)
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    if (tileView.getTranslateX() - oldTranslateX < -tileSize / 2) {
                                        tileView.setTranslateX(oldTranslateX - tileSize);
                                        grid[col - 1][row] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col - 1);
                                        tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);
                                    } else {
                                        tileView.setTranslateX(oldTranslateX + tileSize);
                                        grid[col + 1][row] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col + 1);
                                        tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);
                                    }
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                    grid[col][row + 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 4);
                                    tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveUp && canMoveRight && canMoveLeft) { /* ---- UP, RIGHT, LEFT ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (Math.abs(event.getSceneX() - oldSceneX) > - (event.getSceneY() - oldSceneY)) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX < -tileSize)
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                else if (event.getSceneX() - oldSceneX > tileSize)
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY > 0)
                                    tileView.setTranslateY(oldTranslateY);
                                else if (event.getSceneY() - oldSceneY < -tileSize)
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    if (tileView.getTranslateX() - oldTranslateX < -tileSize / 2) {
                                        tileView.setTranslateX(oldTranslateX - tileSize);
                                        grid[col - 1][row] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col - 1);
                                        tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);
                                    } else {
                                        tileView.setTranslateX(oldTranslateX + tileSize);
                                        grid[col + 1][row] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col + 1);
                                        tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);
                                    }
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                    grid[col][row - 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 4);
                                    tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveDown && canMoveRight && canMoveUp) { /* ---- DOWN, RIGHT, UP ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneX() - oldSceneX > Math.abs(event.getSceneY() - oldSceneY)) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX < 0)
                                    tileView.setTranslateX(oldTranslateX);
                                else if (event.getSceneX() - oldSceneX > tileSize)
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY < -tileSize)
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                else if (event.getSceneY() - oldSceneY > tileSize)
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                    grid[col + 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 1);
                                    tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    if (tileView.getTranslateY() - oldTranslateY < -tileSize / 2) {
                                        tileView.setTranslateY(oldTranslateY - tileSize);
                                        grid[col][row - 1] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col - 4);
                                        tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);
                                    } else {
                                        tileView.setTranslateY(oldTranslateY + tileSize);
                                        grid[col][row + 1] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col + 4);
                                        tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);
                                    }
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveDown && canMoveUp && canMoveLeft) { /* ---- DOWN, LEFT, UP ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (- (event.getSceneX() - oldSceneX) > Math.abs(event.getSceneY() - oldSceneY)) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX > 0)
                                    tileView.setTranslateX(oldTranslateX);
                                else if (event.getSceneX() - oldSceneX < -tileSize)
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY < -tileSize)
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                else if (event.getSceneY() - oldSceneY > tileSize)
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                    grid[col - 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 1);
                                    tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    if (tileView.getTranslateY() - oldTranslateY < -tileSize / 2) {
                                        tileView.setTranslateY(oldTranslateY - tileSize);
                                        grid[col][row - 1] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col - 4);
                                        tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);
                                    } else {
                                        tileView.setTranslateY(oldTranslateY + tileSize);
                                        grid[col][row + 1] = grid[col][row];
                                        grid[col][row] = 0;
                                        Tile tempTile = tiles.get(4 * row + col + 4);
                                        tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                        tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                        tiles.set(4 * row + col, tempTile);
                                        tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);
                                    }
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveRight && canMoveDown) { /* ---- DOWN, RIGHT ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneX() - oldSceneX > event.getSceneY() - oldSceneY) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX < 0)
                                    tileView.setTranslateX(oldTranslateX);
                                else if (event.getSceneX() - oldSceneX > tileSize)
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY < 0)
                                    tileView.setTranslateY(oldTranslateY);
                                else if (event.getSceneY() - oldSceneY > tileSize)
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                    grid[col + 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 1);
                                    tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                    grid[col][row + 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 4);
                                    tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveLeft && canMoveDown) { /* ---- DOWN, LEFT ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (- (event.getSceneX() - oldSceneX) > event.getSceneY() - oldSceneY) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX > 0)
                                    tileView.setTranslateX(oldTranslateX);
                                else if (event.getSceneX() - oldSceneX < -tileSize)
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY < 0)
                                    tileView.setTranslateY(oldTranslateY);
                                else if (event.getSceneY() - oldSceneY > tileSize)
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                    grid[col - 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 1);
                                    tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                    grid[col][row + 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 4);
                                    tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveLeft && canMoveUp) { /* ---- UP, LEFT ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (- (event.getSceneX() - oldSceneX) > - (event.getSceneY() - oldSceneY)) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX > 0)
                                    tileView.setTranslateX(oldTranslateX);
                                else if (event.getSceneX() - oldSceneX < -tileSize)
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY > 0)
                                    tileView.setTranslateY(oldTranslateY);
                                else if (event.getSceneY() - oldSceneY < -tileSize)
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                    grid[col - 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 1);
                                    tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                    grid[col][row - 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 4);
                                    tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveRight && canMoveUp) { /* ---- UP, RIGHT ---- */
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneX() - oldSceneX > - (event.getSceneY() - oldSceneY)) {
                                tileView.setTranslateY(oldTranslateY);

                                if (event.getSceneX() - oldSceneX < 0)
                                    tileView.setTranslateX(oldTranslateX);
                                else if (event.getSceneX() - oldSceneX > tileSize)
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                else
                                    tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                            } else {
                                tileView.setTranslateX(oldTranslateX);

                                if (event.getSceneY() - oldSceneY > 0)
                                    tileView.setTranslateY(oldTranslateY);
                                else if (event.getSceneY() - oldSceneY < -tileSize)
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                else
                                    tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                            }
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                    grid[col + 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 1);
                                    tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                    grid[col][row - 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 4);
                                    tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);
                                }
                            }

                            tileView.setOnMousePressed(null);
                            tileView.setOnMouseDragged(null);
                            tileView.setOnMouseReleased(null);

                            try {
                                readGrid(grid, centerPane, stageHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (canMoveRight && canMoveLeft) {
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneX() - oldSceneX < -tileSize)
                                tileView.setTranslateX(oldTranslateX - tileSize);
                            else if (event.getSceneX() - oldSceneX > tileSize)
                                tileView.setTranslateX(oldTranslateX + tileSize);
                            else
                                tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                tileView.setTranslateX(oldTranslateX);
                            else {
                                if (tileView.getTranslateX() - oldTranslateX < -tileSize / 2) {
                                    tileView.setTranslateX(oldTranslateX - tileSize);
                                    grid[col - 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 1);
                                    tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);
                                } else {
                                    tileView.setTranslateX(oldTranslateX + tileSize);
                                    grid[col + 1][row] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 1);
                                    tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);
                                }

                                tileView.setOnMousePressed(null);
                                tileView.setOnMouseDragged(null);
                                tileView.setOnMouseReleased(null);

                                try {
                                    readGrid(grid, centerPane, stageHeight);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (canMoveDown && canMoveUp) {
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneY() - oldSceneY < -tileSize)
                                tileView.setTranslateY(oldTranslateY - tileSize);
                            else if (event.getSceneY() - oldSceneY > tileSize)
                                tileView.setTranslateY(oldTranslateY + tileSize);
                            else
                                tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                tileView.setTranslateY(oldTranslateY);
                            else {
                                if (tileView.getTranslateY() - oldTranslateY < -tileSize / 2) {
                                    tileView.setTranslateY(oldTranslateY - tileSize);
                                    grid[col][row - 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col - 4);
                                    tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);
                                } else {
                                    tileView.setTranslateY(oldTranslateY + tileSize);
                                    grid[col][row + 1] = grid[col][row];
                                    grid[col][row] = 0;
                                    Tile tempTile = tiles.get(4 * row + col + 4);
                                    tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                    tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                    tiles.set(4 * row + col, tempTile);
                                    tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);
                                }

                                tileView.setOnMousePressed(null);
                                tileView.setOnMouseDragged(null);
                                tileView.setOnMouseReleased(null);

                                try {
                                    readGrid(grid, centerPane, stageHeight);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    else if (canMoveDown) {
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneY() - oldSceneY < 0)
                                tileView.setTranslateY(oldTranslateY);
                            else if (event.getSceneY() - oldSceneY > tileSize)
                                tileView.setTranslateY(oldTranslateY + tileSize);
                            else
                                tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                tileView.setTranslateY(oldTranslateY);
                            else {
                                tileView.setTranslateY(oldTranslateY + tileSize);
                                grid[col][row + 1] = grid[col][row];
                                grid[col][row] = 0;
                                Tile tempTile = tiles.get(4 * row + col + 4);
                                tiles.set(4 * row + col + 4, tiles.get(4 * row + col));
                                tiles.get(4 * row + col + 4).setY(tiles.get(4 * row + col + 4).getY() + 1);
                                tiles.set(4 * row + col, tempTile);
                                tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() - 1);

                                tileView.setOnMousePressed(null);
                                tileView.setOnMouseDragged(null);
                                tileView.setOnMouseReleased(null);

                                try {
                                    readGrid(grid, centerPane, stageHeight);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (canMoveRight) {
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneX() - oldSceneX < 0)
                                tileView.setTranslateX(oldTranslateX);
                            else if (event.getSceneX() - oldSceneX > tileSize)
                                tileView.setTranslateX(oldTranslateX + tileSize);
                            else
                                tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                tileView.setTranslateX(oldTranslateX);
                            else {
                                tileView.setTranslateX(oldTranslateX + tileSize);
                                grid[col + 1][row] = grid[col][row];
                                grid[col][row] = 0;
                                Tile tempTile = tiles.get(4 * row + col + 1);
                                tiles.set(4 * row + col + 1, tiles.get(4 * row + col));
                                tiles.get(4 * row + col + 1).setX(tiles.get(4 * row + col + 1).getX() + 1);
                                tiles.set(4 * row + col, tempTile);
                                tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() - 1);

                                tileView.setOnMousePressed(null);
                                tileView.setOnMouseDragged(null);
                                tileView.setOnMouseReleased(null);

                                try {
                                    readGrid(grid, centerPane, stageHeight);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (canMoveUp) {
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneY() - oldSceneY > 0)
                                tileView.setTranslateY(oldTranslateY);
                            else if (event.getSceneY() - oldSceneY < -tileSize)
                                tileView.setTranslateY(oldTranslateY - tileSize);
                            else
                                tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                tileView.setTranslateY(oldTranslateY);
                            else {
                                tileView.setTranslateY(oldTranslateY - tileSize);
                                grid[col][row - 1] = grid[col][row];
                                grid[col][row] = 0;
                                Tile tempTile = tiles.get(4 * row + col - 4);
                                tiles.set(4 * row + col - 4, tiles.get(4 * row + col));
                                tiles.get(4 * row + col - 4).setY(tiles.get(4 * row + col - 4).getY() - 1);
                                tiles.set(4 * row + col, tempTile);
                                tiles.get(4 * row + col).setY(tiles.get(4 * row + col).getY() + 1);

                                tileView.setOnMousePressed(null);
                                tileView.setOnMouseDragged(null);
                                tileView.setOnMouseReleased(null);

                                try {
                                    readGrid(grid, centerPane, stageHeight);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (canMoveLeft) {
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneX() - oldSceneX > 0)
                                tileView.setTranslateX(oldTranslateX);
                            else if (event.getSceneX() - oldSceneX < -tileSize)
                                tileView.setTranslateX(oldTranslateX - tileSize);
                            else
                                tileView.setTranslateX(oldTranslateX + event.getSceneX() - oldSceneX);
                        });

                        tileView.setOnMouseReleased(event  -> {
                            if (Main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                tileView.setTranslateX(oldTranslateX);
                            else {
                                tileView.setTranslateX(oldTranslateX - tileSize);
                                grid[col - 1][row] = grid[col][row];
                                grid[col][row] = 0;
                                Tile tempTile = tiles.get(4 * row + col - 1);
                                tiles.set(4 * row + col - 1, tiles.get(4 * row + col));
                                tiles.get(4 * row + col - 1).setX(tiles.get(4 * row + col - 1).getX() - 1);
                                tiles.set(4 * row + col, tempTile);
                                tiles.get(4 * row + col).setX(tiles.get(4 * row + col).getX() + 1);

                                tileView.setOnMousePressed(null);
                                tileView.setOnMouseDragged(null);
                                tileView.setOnMouseReleased(null);

                                try {
                                    readGrid(grid, centerPane, stageHeight);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public static ImageView getBallView() {
        return ballView;
    }

    public static double getTileSize() {
        return tileSize;
    }

    public static ImageView[][] getTileViews() {
        return tileViews;
    }
}
