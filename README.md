# Super Mario: Pipes
A simple JavaFX game which uses mouse gestures as input and Super Mario game contents as graphics and sounds.

Developed by
Ahmet Emirhan Bakkal & Muhammed Raşit Ayaz

# Purpose
There are 16 tiles in this game’s map. Some tiles are dynamic tiles, these tiles are moveable, the other tiles are static and not moveable. In these tiles, there are various pipes. Our purpose is to complete the path which the ball moves into. Firstly connect the pipes each other by moving tiles one by one, then complete the path. When the path is complete, the ball has to understand that it can move through the path until it reaches the end of the path. Reading level maps is provided from external files.

# Implementation Details
|**Main**|
|---|
|- stageWidth: double<br>- stageHeight: double<br>- musicOn: boolean<br>- audioOn: boolean<br>- bgmPlayer: MediaPlayer<br>- sfxPlayer: MediaPlayer<br>- ringPlayer: MediaPlayer<br>- txtStatus: Text<br>- btNextLevel: Button<br>- ballTransition: PathTransition|
|+ main(args: String[]): void<br>+ start(stage: Stage): void<br>- viewMainMenu(stage: Stage): void<br>- viewLevelsScene(stage: Stage, sceneMain: Scene): void<br>- viewGameScene(stage: Stage, sceneMain: Scene, level: int): void<br>+ playPathTransition(): void<br>- toggleMusic(btMusic: Button, musicOffView: ImageView, musicOnView: ImageView): void<br>- toggleAudio(btAudio: Button, audioOffView: ImageView, audioOnView: ImageView): void<br>+ getters|

musicOn and audioOn variables are to keep settings of them.

MediaPlayer variables are to control audio files.

txtStatus is used to show level completion status.

btNextLevel button is disable until level is complete.

ballTransition is the transition of the ball in the level map’s path.

start method creates the stage which includes all scenes and initialize width and height of the stage by 800x600 pixels.

viewMainMenu, viewLevelsScene and viewGameScene methods are used to change the scene and locate panes. game method takes level variable to understand which chapter to read.

playPathTransition method is to play the move transition of the ball when the path is complete.

toggleMusic and toggleAudio methods are used to control music and audio buttons.

|**Tile**|
|---|
|- x: int<br>- y: int<br>- type: int<br>- entranceOne: int<br>- entranceTwo: int|
|+ getters / setters|

Tile class keeps properties of various tiles.

|**TileControl**|
|---|
|- oldSceneX: double<br>- oldSceneY: double<br>- oldTranslateX: double<br>- oldTranslateY: double<br>- tileSize: double<br>- tiles: ArrayList<Tile><br>- shellAudio: Media<br>- shellPlayer: MediaPlayer<br>- ballView: ImageView<br>- tileViews: ImageView[][]<br>- canMoveLeft: boolean<br>- canMoveTop: boolean<br>- canMoveRight: boolean<br>- canMoveBottom: boolean|
|+ readGrid(grid: int[][], centerPane: StackPane, stageHeight: double): void<br>+ relocateGrid(grid: int[][], tileViews: ImageView[][], centerPane: StackPane, stageHeight: double): void<br>- moveTile(x: int, y: int, col: int, row: int,tileView: ImageView): void<br>- dragTile(event: MouseEvent,tileView: ImageView, canMoveUp, canMoveRight, canMoveDown, canMoveLeft: boolean): void<br>- dropTile(tileView: ImageView, col, row: int): void<br>+ getters|

oldSceneX, oldSceneY, oldTranslateX and oldTranslateY are to keep old coordinates of the tile and the cursor when moving tiles.

tiles keeps the game map for every level.

shellAudio and shellPlayer are to control tile movement sound.

ballView is to show ball image in scene.

tileViews is to show tile images in scene.

canMoveLeft, canMoveTop, canMoveRight, canMoveBottom check which way tile can move.

readGrid method reads the tile array and locate tile and ball images.

relocateGrid moves tiles, after that it recalls readGrid and updates the list.

|**StageControl**|
|---|
|- grid: int[][]<br>- tiles: ArrayList<Tile><br>- nextTile: Tile<br>- nextEntrance: int<br>- isReached: boolean<br>- levelCompleted: boolean<br>- level: int<br>- path: Path<br>- moveTo: MoveTo<br>- lineTo: LineTo<br>- moveTo2: MoveTo<br>- lineTo2: LineTo<br>- dungeonWinPlayer: MediaPlayer|
|+ readStage(level: int): void<br>- canMoveNext(currentTile: Tile, exitPoint: int): boolean<br>+ checkIsReached(currentTile: Tile, exitPoint: int): void<br>- getTileById(id: int): Tile<br>+ getStarterTile(): Tile<br>- createPath(tile: Tile, entrance: int): void<br>+ getExitPoint(): int<br>+ getters|

grid and tiles variables keep game map for each level.

nextTile and nextEntrance keep the connection of pipes.

isReached keeps the completion of the path.

moveTo and lineTo keep starting coordinates of path elements.

moveTo2 and lineTo2 keep ending coordinates of path elements.

dungeonWinPlayer is to control level completion music.

readStage method reads the stage from external file.

canMoveNext checks the connection of pipes.

checkIsReached checks the completion of the path.

createPath connects path elements and create the path which ball move.

getExitPoint finds the exit point of the pipe in the tile.

# Screenshots
<p align="center">
  <img src="/screenshots/ss1.png"><br>
  <img src="/screenshots/ss2.png"><br>
  <img src="/screenshots/ss3.png"><br>
  <img src="/screenshots/ss4.png"><br>
</p>
