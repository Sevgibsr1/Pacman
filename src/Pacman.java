import java.awt.*;

public class Pacman extends Sprite {
    private Image left;
    private Image right;
    private Image up;
    private Image down;
    private int fspeed;

    public void drawLeft(Graphics2D g2d) {
        g2d.drawImage(left, this.getX() + 1, this.getY() + 1, null);
    }

    public void drawRight(Graphics2D g2d) {
        g2d.drawImage(right, this.getX() + 1, this.getY() + 1, null);
    }

    public void drawUp(Graphics2D g2d) {
        g2d.drawImage(up, this.getX() + 1, this.getY() + 1, null);
    }

    public void drawDown(Graphics2D g2d) {
        g2d.drawImage(down, this.getX() + 1, this.getY() + 1, null);
    }

    public int getFirstSpeed() {
        return fspeed;
    }

    public void setFirstSpeed(int fspeed) {
        this.fspeed = fspeed;
    }

    public void setLeft(Image left) {
        this.left = left;
    }

    public void setRight(Image right) {
        this.right = right;
    }

    public void setUp(Image up) {
        this.up = up;
    }

    public void setDown(Image down) {
        this.down = down;
    }

}
