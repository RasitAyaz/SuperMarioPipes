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

@SuppressWarnings("WeakerAccess")
public class TileControl {
    private final int COL = 4;
    private final int ROW = 4;

    private double oldSceneX, oldSceneY, oldTranslateX, oldTranslateY, tileSize;
    private int[][] grid;

    private ArrayList<Tile> tiles;

    private MediaPlayer shellPlayer;

    private ImageView ballView;
    private ImageView[][] tileViews;

    private Main main;
    private StageControl stageControl;

    public TileControl() {
        Media shellAudio = new Media(new File("audio/shell-hit.mp3").toURI().toString());
        shellPlayer = new MediaPlayer(shellAudio);
    }

    public void connect(Main main) {
        this.main = main;
        stageControl = main.getStageControl();
        tiles = stageControl.getTiles();
    }

    public void readGrid(int[][] inputGrid, StackPane centerPane, double stageHeight) throws Exception {
        grid = inputGrid;
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

        Tile starterTile = stageControl.getStarterTile();

        stageControl.checkIsReached(starterTile, stageControl.getExitPoint());

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

    private void relocateGrid(int[][] grid, ImageView[][] tileViews, StackPane centerPane, double stageHeight) {
        tileSize = stageHeight / 7;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int type = grid[c][r];
                if (type != 0 && !stageControl.isLevelCompleted()) {
                    final ImageView tileView = tileViews[r][c];

                    boolean canMoveLeft = false;
                    boolean canMoveUp = false;
                    boolean canMoveRight = false;
                    boolean canMoveDown = false;

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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTileLR(tileView, col, row);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTileUD(tileView, col, row);
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTileLR(tileView, col, row);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTile(0, 1, col, row, tileView);
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
                            if (Math.abs(event.getSceneX() - oldSceneX) > -(event.getSceneY() - oldSceneY)) {
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTileLR(tileView, col, row);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTile(0, -1, col, row, tileView);
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTile(1, 0, col, row, tileView);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTileUD(tileView, col, row);
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
                            if (-(event.getSceneX() - oldSceneX) > Math.abs(event.getSceneY() - oldSceneY)) {
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTile(-1, 0, col, row, tileView);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTileUD(tileView, col, row);
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTile(1, 0, col, row, tileView);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTile(0, 1, col, row, tileView);
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
                            if (-(event.getSceneX() - oldSceneX) > event.getSceneY() - oldSceneY) {
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTile(-1, 0, col, row, tileView);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTile(0, 1, col, row, tileView);
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
                            if (-(event.getSceneX() - oldSceneX) > -(event.getSceneY() - oldSceneY)) {
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTile(-1, 0, col, row, tileView);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTile(0, -1, col, row, tileView);
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
                            if (event.getSceneX() - oldSceneX > -(event.getSceneY() - oldSceneY)) {
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() == oldTranslateY) {
                                if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                    tileView.setTranslateX(oldTranslateX);
                                else {
                                    moveTile(1, 0, col, row, tileView);
                                }
                            } else if (tileView.getTranslateX() == oldTranslateX) {
                                if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                    tileView.setTranslateY(oldTranslateY);
                                else {
                                    moveTile(0, -1, col, row, tileView);
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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateX() - oldTranslateX < tileSize / 2 && tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                tileView.setTranslateX(oldTranslateX);
                            else {
                                moveTileLR(tileView, col, row);

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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() - oldTranslateY < tileSize / 2 && tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                tileView.setTranslateY(oldTranslateY);
                            else {
                                moveTileUD(tileView, col, row);

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
                    } else if (canMoveDown) {
                        tileView.setOnMouseDragged(event -> {
                            if (event.getSceneY() - oldSceneY < 0)
                                tileView.setTranslateY(oldTranslateY);
                            else if (event.getSceneY() - oldSceneY > tileSize)
                                tileView.setTranslateY(oldTranslateY + tileSize);
                            else
                                tileView.setTranslateY(oldTranslateY + event.getSceneY() - oldSceneY);
                        });

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() - oldTranslateY < tileSize / 2)
                                tileView.setTranslateY(oldTranslateY);
                            else {
                                moveTile(0, 1, col, row, tileView);

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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateX() - oldTranslateX < tileSize / 2)
                                tileView.setTranslateX(oldTranslateX);
                            else {
                                moveTile(1, 0, col, row, tileView);

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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateY() - oldTranslateY > -tileSize / 2)
                                tileView.setTranslateY(oldTranslateY);
                            else {
                                moveTile(0, -1, col, row, tileView);

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

                        tileView.setOnMouseReleased(event -> {
                            if (main.isAudioOn()) {
                                shellPlayer.stop();
                                shellPlayer.play();
                            }
                            if (tileView.getTranslateX() - oldTranslateX > -tileSize / 2)
                                tileView.setTranslateX(oldTranslateX);
                            else {
                                moveTile(-1, 0, col, row, tileView);

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

    private void moveTileLR(ImageView tileView, int col, int row) {
        if (tileView.getTranslateX() - oldTranslateX < -tileSize / 2) {
            moveTile(-1, 0, col, row, tileView);
        } else {
            moveTile(1, 0, col, row, tileView);
        }
    }

    private void moveTileUD(ImageView tileView, int col, int row) {
        if (tileView.getTranslateY() - oldTranslateY < -tileSize / 2) {
            moveTile(0, -1, col, row, tileView);
        } else {
            moveTile(0, 1, col, row, tileView);
        }
    }

    public void moveTile(int x, int y, int col, int row, ImageView tileView) {
        tileView.setTranslateX(oldTranslateX + x * tileSize);
        tileView.setTranslateY(oldTranslateY + y * tileSize);
        grid[col + x][row + y] = grid[col][row];
        grid[col][row] = 0;
        int tileIndex = COL * row + col;
        int targetTileIndex = tileIndex + x + y * COL;
        Tile tileToMove = tiles.get(tileIndex);
        Tile targetTile = tiles.get(targetTileIndex);
        tileToMove.setX(tileToMove.getX() + x);
        tileToMove.setY(tileToMove.getY() + y);
        tiles.set(targetTileIndex, tileToMove);
        targetTile.setX(targetTile.getX() - x);
        targetTile.setY(targetTile.getY() - y);
        tiles.set(tileIndex, targetTile);
    }

    public ImageView getBallView() {
        return ballView;
    }

    public double getTileSize() {
        return tileSize;
    }

    public ImageView[][] getTileViews() {
        return tileViews;
    }
}
