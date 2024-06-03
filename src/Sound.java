import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

public class Sound {
    private Clip yellowBall;
    private Clip fruit;
    private Clip highSScore;
    private Clip darkness;
    private Clip deathPacman;
    private Clip pacmanEatingGhost;
    private Clip startUp;
    private Clip speedup;
    private Clip ghostSirenSound;
    private Clip pacmanChasingGhosts;

    public Sound() {
        initSound();
    }

    private void initSound() {
        try {
            startUp = loadClip("./pacman/start.wav");
            yellowBall = loadClip("./pacman/eating-point.wav");
            fruit = loadClip("./pacman/eating-fruit.wav");
            highSScore = loadClip("./pacman/high-score.wav");
            deathPacman = loadClip("./pacman/death-pacman.wav");
            pacmanEatingGhost = loadClip("./pacman/pacman-eating-ghost.wav");
            ghostSirenSound = loadClip("./pacman/ghost-siren-sound-1.wav");
            pacmanChasingGhosts = loadClip("./pacman/pacman-chasing-ghosts.wav");
            darkness = loadClip("./pacman/darkness.wav");
            speedup = loadClip("./pacman/speedup.wav");
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    private Clip loadClip(String filePath) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File audioFile = new File(filePath);
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(audioFile));
        return clip;
    }

    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }

    public void playYellowBallSound() {
        playSound(yellowBall, 0.7f);
    }
    public void playDarknessSound(boolean play){
        playLoopedSound(darkness, play,0.7f);
    }

    public void playSpeedUpSound(boolean play){
        playLoopedSound(speedup, play,0.7f);
    }

    public void playHighScoreSound() {
        playSound(highSScore, 0.7f);
    }

    public void playDeathPacmanSound() {
        playSound(deathPacman, 0.7f);
    }

    public void playFruitSound() {
        playSound(fruit, 0.7f);
    }

    public void playPacmanEatingGhostSound() {
        playSound(pacmanEatingGhost, 0.7f);
    }

    public void playStartUpSound(boolean play) {
        playLoopedSound(startUp, play, 0.7f);
    }

    public void playGhostSirenSound(boolean play) {
        playLoopedSound(ghostSirenSound, play, 0.3f);
    }

    public void playPacmanChasingGhostsSound(boolean play) {
        playLoopedSound(pacmanChasingGhosts, play, 0.7f);
    }

    private void playSound(Clip clip, float volume) {
        if (clip != null) {
            clip.setFramePosition(0);
            setVolume(clip, volume);
            clip.start();
        }
    }

    private void playLoopedSound(Clip clip, boolean play, float volume) {
        if (clip != null) {
            if (play) {
                clip.setFramePosition(0);
                setVolume(clip, volume);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.stop();
                clip.flush();
            }
        }
    }
}
