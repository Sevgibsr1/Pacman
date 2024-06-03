public class Ghost extends Sprite {
    private int xs;
    private int ys;
    private int tspeed;

    public void setXs(int xs) {
        this.xs = xs;
    }

    public void setYs(int ys) {
        this.ys = ys;
    }

    public void setSpeedtemp(int tspeed) {
        this.tspeed = tspeed;
    }

    public int getXs() {
        return xs;
    }

    public int getYs() {
        return ys;
    }

    public int getSpeedtemp(short i) {
        return tspeed;
    }
}
