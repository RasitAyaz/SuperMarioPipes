import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    private double oldSceneX, oldSceneY, oldTranslateX, oldTranslateY, tileSize, stageHeight;
    private int[][] grid;

    private ArrayList<Tile> tiles;

    private MediaPlayer shellPlayer;

    private ImageView ballView;
    private ImageView[][] tileViews;
    private StackPane centerPane;

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
        this.centerPane = centerPane;
        this.stageHeight = stageHeight;

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

        centerPane.getChildren().addAll(ballView);

        // Check tile movements
        relocateGrid(grid, tileViews, stageHeight);
    }

    private void relocateGrid(int[][] grid, ImageView[][] tileViews, double stageHeight) {
        this.stageHeight = stageHeight;

        tileSize = stageHeight / 7;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int type = grid[c][r];
                if (type != 0 && !stageControl.isLevelCompleted()) {
                    final ImageView tileView = tileViews[r][c];

                    boolean isLeftFree = false;
                    boolean isUpFree = false;
                    boolean isRightFree = false;
                    boolean isDownFree = false;

                    if ((grid[c][r] >= 12 && grid[c][r] <= 17) || grid[c][r] == 1) {
                        try {
                            isLeftFree = grid[c - 1][r] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            isLeftFree = false;
                        }

                        try {
                            isUpFree = grid[c][r - 1] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            isUpFree = false;
                        }

                        try {
                            isRightFree = grid[c + 1][r] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            isRightFree = false;
                        }

                        try {
                            isDownFree = grid[c][r + 1] == 0;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            isDownFree = false;
                        }
                    }

                    boolean canMoveLeft = isLeftFree;
                    boolean canMoveUp = isUpFree;
                    boolean canMoveRight = isRightFree;
                    boolean canMoveDown = isDownFree;

                    final int row = r;
                    final int col = c;

                    tileView.setOnMousePressed(event -> {
                        oldSceneX = event.getSceneX();
                        oldTranslateX = tileView.getTranslateX();
                        oldSceneY = event.getSceneY();
                        oldTranslateY = tileView.getTranslateY();
                    });

                    tileView.setOnMouseDragged(event -> dragTile(event, tileView, canMoveUp, canMoveRight, canMoveDown, canMoveLeft));

                    tileView.setOnMouseReleased(event -> dropTile(tileView, col, row));
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
        int tileIndex = 4 * row + col;
        int targetTileIndex = tileIndex + x + y * 4;
        Tile tileToMove = tiles.get(tileIndex);
        Tile targetTile = tiles.get(targetTileIndex);
        tileToMove.setX(tileToMove.getX() + x);
        tileToMove.setY(tileToMove.getY() + y);
        tiles.set(targetTileIndex, tileToMove);
        targetTile.setX(targetTile.getX() - x);
        targetTile.setY(targetTile.getY() - y);
        tiles.set(tileIndex, targetTile);
    }

    private void dragTile(MouseEvent event, ImageView tileView,
                          boolean canMoveUp, boolean canMoveRight, boolean canMoveDown, boolean canMoveLeft) {
        double sceneX = event.getSceneX();
        double sceneY = event.getSceneY();

        boolean horizontalMovement = Math.abs(sceneX - oldSceneX) > Math.abs(sceneY - oldSceneY);

        if (canMoveDown && canMoveRight && canMoveUp && canMoveLeft)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, -tileSize, tileSize);
            else
                dragOnY(tileView, sceneY, -tileSize, tileSize);
        else if (canMoveDown && canMoveRight && canMoveLeft)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, -tileSize, tileSize);
            else
                dragOnY(tileView, sceneY, 0, tileSize);
        else if (canMoveUp && canMoveRight && canMoveLeft)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, -tileSize, tileSize);
            else
                dragOnY(tileView, sceneY, -tileSize, 0);
        else if (canMoveDown && canMoveRight && canMoveUp)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, 0, tileSize);
            else
                dragOnY(tileView, sceneY, -tileSize, tileSize);
        else if (canMoveDown && canMoveUp && canMoveLeft)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, -tileSize, 0);
            else
                dragOnY(tileView, sceneY, -tileSize, tileSize);
        else if (canMoveRight && canMoveDown)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, 0, tileSize);
            else
                dragOnY(tileView, sceneY, 0, tileSize);
        else if (canMoveLeft && canMoveDown)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, -tileSize, 0);
            else
                dragOnY(tileView, sceneY, 0, tileSize);
        else if (canMoveLeft && canMoveUp)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, -tileSize, 0);
            else
                dragOnY(tileView, sceneY, -tileSize, 0);
        else if (canMoveRight && canMoveUp)
            if (horizontalMovement)
                dragOnX(tileView, sceneX, 0, tileSize);
            else
                dragOnY(tileView, sceneY, -tileSize, 0);
        else if (canMoveRight && canMoveLeft) dragOnX(tileView, sceneX, -tileSize, tileSize);
        else if (canMoveDown && canMoveUp) dragOnY(tileView, sceneY, -tileSize, tileSize);
        else if (canMoveDown) dragOnY(tileView, sceneY, 0, tileSize);
        else if (canMoveRight) dragOnX(tileView, sceneX, 0, tileSize);
        else if (canMoveUp) dragOnY(tileView, sceneY, -tileSize, 0);
        else if (canMoveLeft) dragOnX(tileView, sceneX, -tileSize, 0);
    }

    private void dragOnX(ImageView tileView, double sceneX, double leftBorder, double rightBorder) {
        tileView.setTranslateY(oldTranslateY);

        if (sceneX - oldSceneX < leftBorder)
            tileView.setTranslateX(oldTranslateX + leftBorder);
        else if (sceneX - oldSceneX > rightBorder)
            tileView.setTranslateX(oldTranslateX + rightBorder);
        else
            tileView.setTranslateX(oldTranslateX + sceneX - oldSceneX);
    }

    private void dragOnY(ImageView tileView, double sceneY, double upBorder, double downBorder) {
        tileView.setTranslateX(oldTranslateX);

        if (sceneY - oldSceneY < upBorder)
            tileView.setTranslateY(oldTranslateY + upBorder);
        else if (sceneY - oldSceneY > downBorder)
            tileView.setTranslateY(oldTranslateY + downBorder);
        else
            tileView.setTranslateY(oldTranslateY + sceneY - oldSceneY);
    }

    private void dropTile(ImageView tileView, int col, int row) {
        double translateY = tileView.getTranslateY();
        double translateX = tileView.getTranslateX();

        if (translateY == oldTranslateY) {
            if (translateX - oldTranslateX < tileSize / 2 && translateX - oldTranslateX > -tileSize / 2)
                tileView.setTranslateX(oldTranslateX);
            else {
                moveTileLR(tileView, col, row);
            }
        } else if (translateX == oldTranslateX) {
            if (translateY - oldTranslateY < tileSize / 2 && translateY - oldTranslateY > -tileSize / 2)
                tileView.setTranslateY(oldTranslateY);
            else {
                moveTileUD(tileView, col, row);
            }
        }

        if (main.isAudioOn()) {
            shellPlayer.stop();
            shellPlayer.play();
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
