import java.awt.*;

public class Sprite {
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int speed;
    private Image image;

    public void draw(Graphics2D g2d) {
        this.x = this.x + (this.dx * this.speed);
        this.y = this.y + (this.dy * this.speed);
        g2d.drawImage(image, x, y, null);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getSpeed() {
        return speed;
    }
}
