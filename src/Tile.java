// Ahmet Emirhan Bakkal
// Muhammed Raşit Ayaz

public class Tile {
    private int x;
    private int y;
    private int type;
    private int entranceOne;
    private int entranceTwo;

    public Tile(int x, int y, int type, int entranceOne, int entranceTwo) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.entranceOne = entranceOne;
        this.entranceTwo = entranceTwo;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEntranceOne() {
        return entranceOne;
    }

    public void setEntranceOne(int entranceOne) {
        this.entranceOne = entranceOne;
    }

    public int getEntranceTwo() {
        return entranceTwo;
    }

    public void setEntranceTwo(int entranceTwo) {
        this.entranceTwo = entranceTwo;
    }
}