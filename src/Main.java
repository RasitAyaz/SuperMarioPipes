import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

// Ahmet Emirhan Bakkal
// Muhammed Raşit Ayaz

@SuppressWarnings("WeakerAccess")
public class Main extends Application {
    private double stageWidth, stageHeight;

    private boolean musicOn, audioOn;
    private MediaPlayer bgmPlayer, sfxPlayer, ringPlayer;

    private Text txtStatus;
    private Button btNextLv;

    private ImageView audioOnView, audioOffView, musicOnView, musicOffView;

    private PathTransition pathTransition;

    private StageControl stageControl;
    private TileControl tileControl;

    public static void main(String[] args) {
        launch(args);
    }

    public Main() {
        stageControl = new StageControl();
        tileControl = new TileControl();

        stageControl.connect(this);
        tileControl.connect(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        musicOn = true;
        audioOn = true;
        stageWidth = 800;
        stageHeight = 600;

        // Initialization of the background music
        Media bgmClip = new Media(new File("audio/bgm.mp3").toURI().toString());
        bgmPlayer = new MediaPlayer(bgmClip);
        bgmPlayer.setVolume(0.5);
        bgmPlayer.setAutoPlay(true);
        bgmPlayer.play();

        // Launching main menu method with primary stage argument
        mainMenu(stage);

        // Setting up stage properties
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setTitle("Super Mario: Pipes");
        stage.show();
    }

    /* ---------------------------------------------- MAIN MENU ---------------------------------------------- */

    private void mainMenu(Stage stage) throws FileNotFoundException {
        // Loading background image and binding its properties to the stage
        Image imgBg = new Image(new FileInputStream("graphics/bg.gif"));
        ImageView bgView = new ImageView(imgBg);

        bgView.fitWidthProperty().bind(stage.widthProperty());
        bgView.fitHeightProperty().bind(stage.heightProperty());

        // This pane is used to show bg image and other objects in our scene
        StackPane bgPane = new StackPane();

        // Coin audio from super mario for button clicks
        Media coinAudio = new Media(new File("audio/coin.mp3").toURI().toString());
        sfxPlayer = new MediaPlayer(coinAudio);

        // Coin audio from super mario for level button clicks
        Media ringAudio = new Media(new File("audio/ring.mp3").toURI().toString());
        ringPlayer = new MediaPlayer(ringAudio);

        DropShadow ds = new DropShadow();
        ds.setColor(Color.color(0.2f, 0.2f, 0.2f));
        ds.setSpread(0.2);

        Text gameName = new Text("Super Mario: Pipes");
        gameName.setFill(Color.WHITE);
        gameName.setEffect(ds);
        gameName.setFont(Font.font("Calibri", FontWeight.BOLD, 60));
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(gameName);

        BorderPane.setAlignment(gameName, Pos.CENTER);
        BorderPane.setMargin(gameName, new Insets(40));

        VBox buttons = new VBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(40);

        // Music and audio pane for keeping both music and audio toggle buttons
        HBox musicAndAudio = new HBox();
        musicAndAudio.setAlignment(Pos.CENTER);
        musicAndAudio.setSpacing(40);

        audioOnView = createImage("audioOn");
        audioOffView = createImage("audioOff");
        musicOnView = createImage("musicOn");
        musicOffView = createImage("musicOff");

        audioOnView.setFitHeight(30);
        audioOnView.setFitWidth(30);
        audioOffView.setFitHeight(30);
        audioOffView.setFitWidth(30);
        musicOnView.setFitHeight(30);
        musicOnView.setFitWidth(30);
        musicOffView.setFitHeight(30);
        musicOffView.setFitWidth(30);

        Button btAudio = new Button();
        Button btMusic = new Button();

        initializeToggleButtons(btAudio, btMusic);

        musicAndAudio.getChildren().addAll(btMusic, btAudio);

        Button btStart = new Button("Start");
        btStart.setFont(Font.font("Calibri", 35));

        buttons.getChildren().addAll(btStart, musicAndAudio);

        Text developers = new Text("Developed by\nAhmet Emirhan Bakkal & Muhammed Raşit Ayaz");
        developers.setTextAlignment(TextAlignment.CENTER);
        developers.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
        developers.setEffect(ds);
        developers.setFill(Color.WHITE);
        developers.setStrokeType(StrokeType.OUTSIDE);
        BorderPane.setAlignment(developers, Pos.CENTER);
        BorderPane.setMargin(developers, new Insets(40));

        borderPane.setCenter(buttons);
        borderPane.setBottom(developers);

        bgPane.getChildren().addAll(bgView, borderPane);

        Scene sceneMain = new Scene(bgPane);
        btStart.setOnAction(event -> {
            if (audioOn) {
                sfxPlayer.stop();
                sfxPlayer.play();
            }
            try {
                levels(stage, sceneMain);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(true);

        stage.setScene(sceneMain);
    }

    private void initializeToggleButtons(Button btAudio, Button btMusic) {
        // Change audio button image according to audio's current state

        setButtonState(btAudio, audioOnView, audioOffView, audioOn);
        toggleAudio(btAudio, audioOffView, audioOnView);

        // Change music button image according to music's current state

        setButtonState(btMusic, musicOnView, musicOffView, musicOn);
        toggleMusic(btMusic, musicOffView, musicOnView);
    }

    private void setButtonState(Button button, ImageView stateOn, ImageView stateOff, boolean state) {
        if (state)
            button.setGraphic(stateOn);
        else
            button.setGraphic(stateOff);
    }

    private ImageView createImage(String name) throws FileNotFoundException {
        Image image = new Image(new FileInputStream("graphics/" + name + ".png"));
        return new ImageView(image);
    }

    /* ---------------------------------------------- LEVELS ---------------------------------------------- */

    private void levels(Stage stage, Scene sceneMain) throws Exception {
        // Loading background image and binding its properties to the stage
        Image imgBg = new Image(new FileInputStream("graphics/clouds.gif"));
        ImageView bgView = new ImageView(imgBg);

        bgView.fitWidthProperty().bind(stage.widthProperty().add(200));
        bgView.fitHeightProperty().bind(stage.heightProperty());

        StackPane bgPane = new StackPane();

        DropShadow ds = new DropShadow();
        ds.setColor(Color.color(0.2f, 0.2f, 0.2f));
        ds.setSpread(0.2);

        Text txtLevels = new Text("Levels");
        txtLevels.setFill(Color.WHITE);
        txtLevels.setEffect(ds);
        txtLevels.setFont(Font.font("Calibri", FontWeight.BOLD, 40));

        // Back to main menu button
        Button btBack = new Button("<- Back");
        btBack.setFont(Font.font("Calibri", 15));
        btBack.setOnAction(event -> {
            if (audioOn) {
                sfxPlayer.stop();
                sfxPlayer.play();
            }
            try {
                mainMenu(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        StackPane headerPane = new StackPane();
        headerPane.getChildren().addAll(btBack, txtLevels);

        BorderPane outerPane = new BorderPane();

        FlowPane levelsPane = new FlowPane();
        levelsPane.setAlignment(Pos.CENTER);

        ArrayList<Button> btLevelsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            btLevelsList.add(new Button("Level " + (i + 1)));
            btLevelsList.get(i).setFont(Font.font("Calibri", 30));
            levelsPane.getChildren().addAll(btLevelsList.get(i));
        }

        for (int i = 0; i < 5; i++) {
            final int lvl = i + 1;
            btLevelsList.get(i).setOnAction(event -> {
                if (audioOn) {
                    ringPlayer.stop();
                    ringPlayer.play();
                }
                try {
                    game(stage, sceneMain, lvl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        outerPane.setTop(headerPane);
        outerPane.setCenter(levelsPane);

        for (int i = 0; i < 5; i++) {
            FlowPane.setMargin(btLevelsList.get(i), new Insets(40));
        }

        StackPane.setAlignment(txtLevels, Pos.CENTER);
        StackPane.setAlignment(btBack, Pos.CENTER_LEFT);
        StackPane.setMargin(txtLevels, new Insets(40, 0, 0, 0));
        StackPane.setMargin(btBack, new Insets(40, 0, 0, 40));

        bgPane.getChildren().addAll(bgView, outerPane);

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(true);

        Scene sceneLevels = new Scene(bgPane);
        stage.setScene(sceneLevels);
    }

    /* ---------------------------------------------- GAME ---------------------------------------------- */

    private void game(Stage stage, Scene sceneMain, int level) throws Exception {
        DropShadow ds = new DropShadow();
        ds.setColor(Color.color(0.2f, 0.2f, 0.2f));
        ds.setSpread(0.2);

        Text txtGame = new Text("Level " + level);
        txtGame.setFont(Font.font("Calibri", FontWeight.BOLD, 40));
        txtGame.setFill(Color.WHITE);
        txtGame.setEffect(ds);

        // Exit to main menu button
        Button btExit = new Button("Exit");
        btExit.setFont(Font.font("Calibri", 15));

        Image audioOnImg = new Image(new FileInputStream("graphics/audioOn.png"));
        Image audioOffImg = new Image(new FileInputStream("graphics/audioOff.png"));
        Image musicOnImg = new Image(new FileInputStream("graphics/musicOn.png"));
        Image musicOffImg = new Image(new FileInputStream("graphics/musicOff.png"));

        ImageView audioOnView = new ImageView(audioOnImg);
        ImageView audioOffView = new ImageView(audioOffImg);
        ImageView musicOnView = new ImageView(musicOnImg);
        ImageView musicOffView = new ImageView(musicOffImg);

        audioOnView.setFitHeight(25);
        audioOnView.setFitWidth(25);
        audioOffView.setFitHeight(25);
        audioOffView.setFitWidth(25);
        musicOnView.setFitHeight(25);
        musicOnView.setFitWidth(25);
        musicOffView.setFitHeight(25);
        musicOffView.setFitWidth(25);

        Button btAudio = new Button();
        if (audioOn)
            btAudio.setGraphic(audioOnView);
        else
            btAudio.setGraphic(audioOffView);

        toggleAudio(btAudio, audioOffView, audioOnView);

        Button btMusic = new Button();
        if (musicOn)
            btMusic.setGraphic(musicOnView);
        else
            btMusic.setGraphic(musicOffView);

        toggleMusic(btMusic, musicOffView, musicOnView);

        StackPane headerPane = new StackPane();
        headerPane.getChildren().addAll(txtGame, btExit, btAudio, btMusic);

        StackPane.setAlignment(txtGame, Pos.CENTER);
        StackPane.setAlignment(btExit, Pos.CENTER_RIGHT);
        StackPane.setAlignment(btAudio, Pos.CENTER_LEFT);
        StackPane.setAlignment(btMusic, Pos.CENTER_LEFT);
        StackPane.setMargin(txtGame, new Insets(40, 0, 0, 0));
        StackPane.setMargin(btExit, new Insets(40, 40, 0, 0));
        StackPane.setMargin(btAudio, new Insets(40, 0, 0, 100));
        StackPane.setMargin(btMusic, new Insets(40, 0, 0, 40));

        txtStatus = new Text("");
        txtStatus.setFont(Font.font("Calibri", 20));
        txtStatus.setFill(Color.WHITE);
        txtStatus.setEffect(ds);

        // Next level button
        btNextLv = new Button("Next Level ->");
        btNextLv.setFont(Font.font("Calibri", 15));

        // Make invisible next level button for the last level
        if (level == 5)
            btNextLv.setVisible(false);
        else
            btNextLv.setVisible(true);

        btNextLv.setDisable(true);

        // Back to levels button
        Button btLevels = new Button("<- Levels");
        btLevels.setFont(Font.font("Calibri", 15));

        StackPane bottomPane = new StackPane();
        bottomPane.getChildren().addAll(txtStatus, btLevels, btNextLv);

        StackPane.setAlignment(txtStatus, Pos.CENTER);
        StackPane.setAlignment(btNextLv, Pos.CENTER_RIGHT);
        StackPane.setAlignment(btLevels, Pos.CENTER_LEFT);
        StackPane.setMargin(txtStatus, new Insets(0, 0, 40, 0));
        StackPane.setMargin(btNextLv, new Insets(0, 40, 40, 0));
        StackPane.setMargin(btLevels, new Insets(0, 0, 40, 40));

        btExit.setOnAction(event -> {
            if (audioOn) {
                sfxPlayer.stop();
                sfxPlayer.play();
            }
            try {
                mainMenu(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btLevels.setOnAction(event -> {
            if (audioOn) {
                sfxPlayer.stop();
                sfxPlayer.play();
            }
            try {
                levels(stage, sceneMain);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btNextLv.setOnAction(event -> {
            if (audioOn) {
                sfxPlayer.stop();
                sfxPlayer.play();
            }
            try {
                game(stage, sceneMain, level + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stageWidth = stage.getWidth();
        stageHeight = stage.getHeight();

        // Read level input file for initializing stage
        stageControl.readStage(level);

        // Get game map from read file
        int[][] grid = stageControl.getGrid();

        // Pane which holds tiles
        StackPane centerPane = new StackPane();

        BorderPane outerPane = new BorderPane();
        outerPane.setTop(headerPane);
        outerPane.setCenter(centerPane);
        outerPane.setBottom(bottomPane);
        outerPane.setStyle("-fx-background-color: #98e0e0");

        tileControl.readGrid(grid, centerPane, stageHeight);

        // Find starter tile of the level
        Tile starterTile = stageControl.getStarterTile();

        // Check whether the level has completed
        stageControl.checkIsReached(starterTile, stageControl.getExitPoint());

        pathTransition = new PathTransition();

        stage.setResizable(false);

        Scene sceneGame = new Scene(outerPane);
        stage.setScene(sceneGame);
    }

    public void playTransition() {
        pathTransition.setNode(tileControl.getBallView());
        pathTransition.setPath(stageControl.getPath());
        pathTransition.setDuration(Duration.millis(2000));
        pathTransition.play();
    }

    private void toggleMusic(Button btMusic, ImageView musicOffView, ImageView musicOnView) {
        btMusic.setOnAction(event -> {
            if (audioOn) {
                sfxPlayer.stop();
                sfxPlayer.play();
            }
            if (musicOn) {
                btMusic.setGraphic(musicOffView);
                bgmPlayer.stop();
                stageControl.getDungeonWinPlayer().stop();
                musicOn = false;
            } else {
                btMusic.setGraphic(musicOnView);
                bgmPlayer.play();
                musicOn = true;
            }
        });
    }

    private void toggleAudio(Button btAudio, ImageView audioOffView, ImageView audioOnView) {
        btAudio.setOnAction(event -> {
            if (audioOn) {
                btAudio.setGraphic(audioOffView);
                sfxPlayer.stop();
                sfxPlayer.play();
                audioOn = false;
            } else {
                btAudio.setGraphic(audioOnView);
                audioOn = true;
            }
        });
    }

    public Text getTxtStatus() {
        return txtStatus;
    }

    public Button getBtNextLv() {
        return btNextLv;
    }

    public MediaPlayer getBgmPlayer() {
        return bgmPlayer;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public boolean isAudioOn() {
        return audioOn;
    }

    public StageControl getStageControl() {
        return stageControl;
    }

    public TileControl getTileControl() {
        return tileControl;
    }
}