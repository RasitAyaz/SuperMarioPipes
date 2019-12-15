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
import java.util.ArrayList;

// Ahmet Emirhan Bakkal
// Muhammed Raşit Ayaz

public class Main extends Application {
    private static double stageWidth = 800;
    private static double stageHeight = 600;

    private static boolean musicOn = true;
    private static boolean audioOn = true;
    private static MediaPlayer bgmPlayer;
    private static MediaPlayer sfxPlayer;
    private static MediaPlayer ringPlayer;

    private static Text txtStatus;
    private static Button btNextLv;

    private static PathTransition pathTransition;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
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

    public void mainMenu(Stage stage) throws Exception {
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

        borderPane.setAlignment(gameName, Pos.CENTER);
        borderPane.setMargin(gameName, new Insets(40));

        VBox buttons = new VBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(40);

        // Music and audio pane for keeping both music and audio toggle buttons
        HBox musicAndAudio = new HBox();
        musicAndAudio.setAlignment(Pos.CENTER);
        musicAndAudio.setSpacing(40);

        Image audioOnImg = new Image(new FileInputStream("graphics/audioOn.png"));
        Image audioOffImg = new Image(new FileInputStream("graphics/audioOff.png"));
        Image musicOnImg = new Image(new FileInputStream("graphics/musicOn.png"));
        Image musicOffImg = new Image(new FileInputStream("graphics/musicOff.png"));

        ImageView audioOnView = new ImageView(audioOnImg);
        ImageView audioOffView = new ImageView(audioOffImg);
        ImageView musicOnView = new ImageView(musicOnImg);
        ImageView musicOffView = new ImageView(musicOffImg);

        audioOnView.setFitHeight(30);
        audioOnView.setFitWidth(30);
        audioOffView.setFitHeight(30);
        audioOffView.setFitWidth(30);
        musicOnView.setFitHeight(30);
        musicOnView.setFitWidth(30);
        musicOffView.setFitHeight(30);
        musicOffView.setFitWidth(30);

        // Change audio button image according to audio's current state
        Button btAudio = new Button();
        if (audioOn)
            btAudio.setGraphic(audioOnView);
        else
            btAudio.setGraphic(audioOffView);

        toggleAudio(btAudio, audioOffView, audioOnView);

        // Change music button image according to music's current state
        Button btMusic = new Button();
        if (musicOn)
            btMusic.setGraphic(musicOnView);
        else
            btMusic.setGraphic(musicOffView);

        toggleMusic(btMusic, musicOffView, musicOnView);

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
        borderPane.setAlignment(developers, Pos.CENTER);
        borderPane.setMargin(developers, new Insets(40));

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

    /* ---------------------------------------------- LEVELS ---------------------------------------------- */

    public void levels(Stage stage, Scene sceneMain) throws Exception {
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
            levelsPane.setMargin(btLevelsList.get(i), new Insets(40));
        }

        headerPane.setAlignment(txtLevels, Pos.CENTER);
        headerPane.setAlignment(btBack, Pos.CENTER_LEFT);
        headerPane.setMargin(txtLevels, new Insets(40, 0, 0, 0));
        headerPane.setMargin(btBack, new Insets(40, 0, 0, 40));

        bgPane.getChildren().addAll(bgView, outerPane);

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(true);

        Scene sceneLevels = new Scene(bgPane);
        stage.setScene(sceneLevels);
    }

    /* ---------------------------------------------- GAME ---------------------------------------------- */

    public void game(Stage stage, Scene sceneMain, int level) throws Exception {
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

        headerPane.setAlignment(txtGame, Pos.CENTER);
        headerPane.setAlignment(btExit, Pos.CENTER_RIGHT);
        headerPane.setAlignment(btAudio, Pos.CENTER_LEFT);
        headerPane.setAlignment(btMusic, Pos.CENTER_LEFT);
        headerPane.setMargin(txtGame, new Insets(40, 0, 0, 0));
        headerPane.setMargin(btExit, new Insets(40, 40, 0, 0));
        headerPane.setMargin(btAudio, new Insets(40, 0, 0, 100));
        headerPane.setMargin(btMusic, new Insets(40, 0, 0, 40));

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

        bottomPane.setAlignment(txtStatus, Pos.CENTER);
        bottomPane.setAlignment(btNextLv, Pos.CENTER_RIGHT);
        bottomPane.setAlignment(btLevels, Pos.CENTER_LEFT);
        bottomPane.setMargin(txtStatus, new Insets(0, 0, 40, 0));
        bottomPane.setMargin(btNextLv, new Insets(0, 40, 40, 0));
        bottomPane.setMargin(btLevels, new Insets(0, 0, 40, 40));

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
        StageControl.readStage(level);

        // Get game map from read file
        int[][] grid = StageControl.getGrid();

        // Pane which holds tiles
        StackPane centerPane = new StackPane();

        BorderPane outerPane = new BorderPane();
        outerPane.setTop(headerPane);
        outerPane.setCenter(centerPane);
        outerPane.setBottom(bottomPane);
        outerPane.setStyle("-fx-background-color: #98e0e0");

        TileControl.readGrid(grid, centerPane, stageHeight);

        // Find starter tile of the level
        Tile starterTile = StageControl.getStarterTile();

        // Check whether the level has completed
        StageControl.checkIsReached(starterTile, StageControl.getExitPoint());

        pathTransition = new PathTransition();

        stage.setResizable(false);

        Scene sceneGame = new Scene(outerPane);
        stage.setScene(sceneGame);
    }

    public static void playTransition() {
        pathTransition.setNode(TileControl.getBallView());
        pathTransition.setPath(StageControl.getPath());
        pathTransition.setDuration(Duration.millis(2000));
        pathTransition.play();
    }

    private static void toggleMusic(Button btMusic, ImageView musicOffView, ImageView musicOnView) {
        btMusic.setOnAction(event -> {
            if (audioOn) {
                sfxPlayer.stop();
                sfxPlayer.play();
            }
            if (musicOn) {
                btMusic.setGraphic(musicOffView);
                bgmPlayer.stop();
                StageControl.getDungeonWinPlayer().stop();
                musicOn = false;
            } else {
                btMusic.setGraphic(musicOnView);
                bgmPlayer.play();
                musicOn = true;
            }
        });
    }

    private static void toggleAudio(Button btAudio, ImageView audioOffView, ImageView audioOnView) {
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

    public static Text getTxtStatus() {
        return txtStatus;
    }

    public static Button getBtNextLv() {
        return btNextLv;
    }

    public static MediaPlayer getBgmPlayer() {
        return bgmPlayer;
    }

    public static boolean isMusicOn() {
        return musicOn;
    }

    public static boolean isAudioOn() {
        return audioOn;
    }
}