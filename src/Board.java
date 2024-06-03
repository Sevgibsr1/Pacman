import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import static java.lang.Math.random;

public class Board extends JPanel implements ActionListener {

    private Dimension d;               // Pencerenin boyutunu temsil eden bir Dimension nesnesi.
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);  // Küçük yazı fontu.

    // Çift taramalı çizim için kullanılan bir görüntü.
    private final Color dotColor = new Color(192, 192, 0);  // Yem renkleri için kullanılan renk.
    private Color mazeColor;             // Labirentin arka plan rengi.

    private boolean inGame = false;     // Oyunun oynanıp oynanmadığını belirten bayrak.
    boolean dark = false;
    private boolean dying = false;      // Pacman'in ölüp ölmediğini belirten bayrak.

    private final int BLOCK_SIZE = 24;   // Labirent bloklarının boyutu.
    private final int N_BLOCKS = 29;     // Labirentteki blok sayısı.
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;  // Ekranın genişliği ve yüksekliği
    private int N_GHOSTS = 6;            // Başlangıçta oyundaki hayalet sayısı.
    private int pacsLeft, score;         // Oyundaki can sayısı ve skor.
    private int[] dx, dy;                // ghostun'in hareket yönlerini tutan diziler.

    private int level = 1;
    private boolean noLives = false;
    private final int DELAY_BEFORE_NEW_FRUIT = 10000;
    private boolean beastmode = false;

    private Ghost[] ghost;
    private Timer giftBoxTimer;

    private Image ghostAll, fruitAll, ghost1, ghost2, giftBox, escape, pacman1, pacman3left;
    private Image left, right, down, up;
    private int req_dx, req_dy;  // Kullanıcının girişi ve görüntü yönlendirmesi için değişkenler.
    private int fruit_x, fruit_y, giftBox_x, giftBox_y;

    private Pacman pacman;
    private Sound sound;
    private final short[] levelData1 = {19, 26, 26, 26, 26, 18, 26, 26, 26, 26, 26, 22, 0, 0, 0, 0, 0, 19, 26, 26, 26,
            26, 26, 18, 26, 26, 26, 26, 22, 21, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21,
            0, 0, 0, 0, 21, 21, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 21,
            21, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 21, 17, 26, 26, 26,
            26, 16, 26, 26, 18, 26, 26, 24, 26, 26, 26, 26, 26, 24, 26, 26, 18, 26, 26, 16, 26, 26, 26, 26, 20, 21, 0,
            0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 21, 21, 0, 0, 0, 0, 21, 0,
            0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 21, 25, 26, 26, 26, 26, 20, 0, 0, 25, 26,
            26, 22, 0, 0, 0, 0, 0, 19, 26, 26, 28, 0, 0, 17, 26, 26, 26, 26, 28, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 5, 0,
            0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 5, 0,
            0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 19, 26, 26, 24, 26, 26, 18, 26, 26, 24, 26, 26, 22,
            0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 5, 0, 3, 2, 2, 2, 0, 2, 2, 2, 6, 0, 5, 0, 0, 21, 0, 0, 0, 0, 0, 27, 26,
            26, 26, 26, 16, 26, 26, 16, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 16, 26, 26, 16, 26, 26, 26, 26, 30, 0, 0, 0,
            0, 0, 21, 0, 0, 5, 0, 9, 8, 8, 8, 0, 8, 8, 8, 12, 0, 5, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0,
            21, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 17, 26, 26, 26,
            26, 26, 24, 26, 26, 26, 26, 26, 20, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            21, 0, 0, 21, 0, 0, 0, 0, 0, 19, 26, 26, 26, 26, 16, 26, 26, 24, 26, 26, 22, 0, 0, 0, 0, 0, 19, 26, 26, 24,
            26, 26, 16, 26, 26, 26, 26, 22, 21, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21,
            0, 0, 0, 0, 21, 21, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 21,
            25, 26, 22, 0, 0, 17, 26, 26, 18, 26, 26, 24, 10, 10, 10, 10, 10, 24, 26, 26, 18, 26, 26, 20, 0, 0, 19, 26,
            28, 0, 0, 21, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 21,
            0, 0, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 21, 0, 0, 21, 0, 0, 19, 26, 24, 26, 26, 28,
            0, 0, 25, 26, 26, 22, 0, 0, 0, 0, 0, 19, 26, 26, 28, 0, 0, 25, 26, 26, 24, 26, 22, 21, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0,
            0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 24, 26, 26,
            26, 26, 26, 24, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 28};

    // Level 2
    private final short[] levelData2 = {0, 0, 0, 0, 0, 0, 0, 19, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
            22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 19, 26, 28, 0, 19, 26, 26, 26, 26, 26, 26, 26, 26, 26, 22, 0, 25, 26, 22, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 19, 28, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 25, 22, 0, 0, 0, 0, 0, 0, 0, 19,
            28, 0, 0, 19, 18, 24, 26, 26, 18, 18, 18, 18, 18, 26, 26, 24, 18, 22, 0, 0, 25, 22, 0, 0, 0, 0, 0, 0, 21, 0,
            0, 19, 16, 28, 0, 0, 0, 9, 0, 0, 0, 12, 0, 0, 0, 25, 16, 22, 0, 0, 21, 0, 0, 0, 0, 19, 26, 28, 0, 19, 24,
            20, 0, 0, 23, 0, 0, 1, 0, 4, 0, 0, 23, 0, 0, 17, 24, 22, 0, 25, 26, 22, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 19,
            16, 22, 0, 1, 0, 4, 0, 19, 16, 22, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21, 0, 19, 26, 24, 26, 20, 0, 17, 16,
            20, 0, 1, 0, 4, 0, 17, 16, 20, 0, 17, 26, 24, 26, 22, 0, 21, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 25, 16, 28, 0,
            1, 0, 4, 0, 25, 16, 28, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 21, 0, 17, 26, 26, 26, 20, 0, 0, 21, 0, 0, 1, 0, 4,
            0, 0, 21, 0, 0, 17, 26, 26, 26, 20, 0, 21, 0, 0, 21, 0, 21, 0, 0, 0, 17, 22, 0, 21, 0, 19, 0, 0, 0, 22, 0,
            21, 0, 19, 20, 0, 0, 0, 21, 0, 21, 0, 19, 28, 0, 17, 26, 26, 26, 24, 24, 26, 24, 26, 24, 24, 24, 24, 24, 26,
            24, 26, 24, 24, 26, 26, 26, 20, 0, 25, 22, 21, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 21, 0, 0, 21, 21, 0, 19, 24, 26, 26, 26, 26, 26, 26, 26, 26, 26, 18, 26, 18, 26, 26, 26, 26, 26,
            26, 26, 26, 26, 24, 22, 0, 21, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 21, 0, 21, 21, 0, 21, 0, 19, 26, 26, 26, 26, 26, 26, 26, 26, 28, 0, 25, 26, 26, 26, 26, 26, 26, 26,
            26, 22, 0, 21, 0, 21, 17, 10, 20, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 17,
            10, 20, 21, 0, 21, 0, 25, 26, 26, 26, 26, 26, 26, 26, 26, 22, 0, 19, 26, 26, 26, 26, 26, 26, 26, 26, 28, 0,
            21, 0, 21, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 21,
            0, 21, 0, 19, 26, 26, 26, 26, 26, 26, 30, 0, 21, 0, 21, 0, 27, 26, 26, 26, 26, 26, 26, 22, 0, 21, 0, 21, 21,
            0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 21, 21, 0, 17, 18,
            24, 18, 22, 0, 19, 18, 26, 22, 0, 21, 0, 21, 0, 19, 26, 18, 22, 0, 19, 18, 24, 18, 20, 0, 21, 21, 0, 17, 28,
            0, 25, 20, 0, 17, 28, 0, 25, 18, 20, 0, 17, 18, 28, 0, 25, 20, 0, 17, 28, 0, 25, 20, 0, 21, 21, 0, 29, 0, 0,
            0, 25, 18, 28, 0, 0, 0, 25, 16, 2, 16, 28, 0, 0, 0, 25, 18, 28, 0, 0, 0, 29, 0, 21, 21, 0, 0, 0, 23, 0, 0,
            29, 0, 0, 23, 0, 0, 25, 0, 28, 0, 0, 23, 0, 0, 29, 0, 0, 23, 0, 0, 0, 21, 21, 0, 0, 19, 24, 22, 0, 0, 0, 19,
            24, 22, 0, 0, 21, 0, 0, 19, 24, 22, 0, 0, 0, 19, 24, 22, 0, 0, 21, 21, 0, 19, 28, 0, 25, 22, 0, 19, 28, 0,
            25, 22, 0, 21, 0, 19, 28, 0, 25, 22, 0, 19, 28, 0, 25, 22, 0, 21, 25, 26, 28, 0, 0, 0, 25, 26, 28, 0, 0, 0,
            25, 26, 24, 26, 28, 0, 0, 0, 25, 26, 28, 0, 0, 0, 25, 26, 28,};

    // Level 3
    private final short[] levelData3 = {0, 0, 0, 19, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19,
            22, 0, 0, 0, 0, 0, 19, 24, 24, 22, 0, 0, 0, 0, 0, 0, 19, 18, 24, 18, 22, 0, 0, 0, 0, 0, 0, 19, 24, 24, 22,
            0, 0, 0, 0, 21, 0, 0, 17, 26, 30, 0, 0, 27, 26, 24, 28, 0, 25, 24, 26, 22, 0, 0, 0, 27, 20, 0, 0, 17, 26,
            22, 0, 0, 25, 22, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 21, 0, 19, 28, 0, 21, 0, 0, 0,
            21, 0, 21, 0, 0, 0, 19, 26, 18, 26, 26, 26, 26, 26, 18, 24, 22, 0, 0, 0, 21, 0, 21, 0, 0, 21, 19, 26, 26,
            20, 0, 25, 18, 26, 26, 20, 0, 21, 0, 0, 0, 0, 0, 21, 0, 17, 26, 26, 18, 28, 0, 21, 0, 0, 21, 29, 0, 0, 21,
            0, 0, 29, 0, 0, 25, 18, 24, 26, 26, 18, 30, 0, 25, 18, 28, 0, 0, 21, 0, 0, 21, 0, 27, 20, 0, 0, 0, 25, 22,
            0, 0, 0, 0, 0, 29, 0, 0, 0, 21, 0, 0, 0, 29, 0, 0, 0, 21, 0, 19, 28, 0, 0, 21, 23, 0, 0, 0, 21, 0, 23, 0, 0,
            0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 29, 21, 0, 0, 0, 21, 0, 25, 26, 26, 26, 26, 26,
            18, 26, 24, 26, 18, 26, 26, 26, 22, 0, 21, 0, 21, 0, 0, 0, 0, 21, 0, 0, 19, 28, 0, 0, 0, 0, 0, 0, 0, 21, 0,
            0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 25, 22, 0, 0, 23, 21, 0, 19, 28, 0, 0, 19, 26, 26, 26, 26, 18, 16, 26, 26,
            26, 16, 18, 26, 26, 24, 26, 20, 0, 0, 25, 22, 0, 21, 21, 0, 21, 0, 0, 19, 28, 0, 0, 0, 0, 17, 20, 0, 0, 0,
            17, 20, 0, 0, 0, 0, 25, 22, 0, 0, 21, 0, 21, 21, 0, 21, 0, 19, 28, 0, 0, 23, 0, 0, 25, 28, 0, 0, 0, 25, 28,
            0, 0, 23, 0, 0, 25, 22, 0, 21, 0, 21, 21, 0, 21, 0, 21, 0, 0, 19, 24, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 24,
            22, 0, 0, 21, 0, 21, 0, 21, 21, 0, 21, 0, 17, 26, 26, 28, 0, 17, 26, 22, 0, 23, 0, 23, 0, 19, 26, 20, 0, 25,
            26, 26, 20, 0, 21, 0, 21, 25, 26, 16, 26, 20, 0, 0, 0, 0, 21, 0, 17, 26, 16, 18, 16, 26, 20, 0, 21, 0, 0, 0,
            0, 17, 26, 16, 26, 28, 0, 0, 21, 0, 25, 26, 26, 22, 0, 21, 0, 21, 0, 25, 16, 28, 0, 21, 0, 21, 0, 19, 26,
            26, 28, 0, 21, 0, 0, 0, 0, 21, 0, 0, 0, 0, 25, 18, 28, 0, 21, 0, 0, 29, 0, 0, 21, 0, 25, 18, 28, 0, 0, 0, 0,
            17, 26, 22, 0, 0, 21, 0, 27, 22, 0, 0, 29, 0, 0, 17, 26, 30, 0, 27, 26, 20, 0, 0, 29, 0, 0, 19, 30, 0, 21,
            0, 21, 19, 26, 20, 0, 0, 25, 22, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 19, 28, 0, 0, 21, 0, 21, 21,
            0, 25, 22, 0, 0, 25, 26, 26, 26, 26, 24, 26, 26, 26, 26, 26, 24, 26, 26, 26, 26, 28, 0, 0, 19, 28, 0, 21,
            21, 0, 0, 25, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 28, 0, 0, 21, 21, 0, 0, 0,
            25, 26, 26, 18, 18, 26, 18, 18, 18, 30, 0, 27, 18, 18, 18, 26, 26, 18, 26, 26, 28, 0, 0, 0, 21, 25, 22, 0,
            0, 0, 0, 0, 17, 28, 0, 25, 24, 28, 0, 0, 0, 25, 24, 20, 0, 0, 21, 0, 0, 0, 0, 0, 0, 21, 0, 25, 26, 22, 0, 0,
            19, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 27, 16, 22, 0, 0, 0, 0, 19, 28, 0, 0, 0, 29, 0, 19, 28, 0, 0,
            0, 27, 18, 26, 26, 26, 26, 26, 18, 28, 0, 0, 25, 16, 26, 22, 0, 19, 28, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0,
            21, 0, 0, 0, 0, 0, 29, 0, 0, 0, 0, 21, 0, 29, 0, 29, 0, 0, 0, 0, 0, 0, 0, 29, 0, 0, 0, 0, 0, 25, 26, 26, 26,
            30, 0, 0, 0, 0, 0, 0, 29, 0, 0, 0, 0, 0, 0,};

    // Level 4 - Bonus
    private final short[] levelData4 = {19, 26, 26, 26, 26, 26, 26, 26, 18, 26, 18, 26, 26, 26, 26, 26, 26, 26, 18, 26, 18, 26, 26, 26, 26, 26, 26, 26, 22,
            21, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 17,
            21, 0, 19, 26, 18, 26, 18, 26, 16, 26, 20, 0, 19, 26, 18, 26, 18, 26, 16, 26, 16, 26, 18, 26, 18, 26, 18, 26, 20,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            21, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            21, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            21, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            21, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            21, 0, 17, 18, 16, 18, 16, 18, 16, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 8, 10, 10, 10, 10, 10, 2, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            17, 16, 16, 16, 16, 16, 20, 0, 17, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 2, 21, 2, 21, 2, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            17, 26, 16, 26, 16, 26, 20, 0, 17, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            17, 26, 16, 26, 16, 26, 20, 0, 17, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            17, 26, 16, 26, 16, 26, 20, 0, 17, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            17, 26, 16, 26, 16, 26, 20, 0, 17, 26, 20, 0, 17, 26, 16, 26, 16, 26, 16, 26, 20, 0, 17, 26, 16, 26, 20, 0, 21,
            21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            17, 26, 24, 26, 24, 26, 28, 0, 17, 26, 20, 0, 25, 26, 24, 26, 24, 26, 16, 26, 20, 0, 25, 26, 24, 26, 28, 0, 21,
            21, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 21,
            17, 26, 18, 26, 18, 26, 18, 26, 16, 26, 16, 26, 18, 26, 18, 26, 18, 26, 16, 26, 16, 26, 18, 26, 18, 26, 18, 26, 20,
            17, 13, 16, 13, 16, 13, 16, 1, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21, 0, 21,
            25, 24, 24, 24, 24, 24, 24, 10, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 28, };

    private final short x1[] = {8, 20, 8, 20, 14, 14};
    private final short y1[] = {12, 12, 14, 14, 11, 15};
    private final short x2[] = {13, 15, 13, 15, 13, 15, 1, 27};
    private final short y2[] = {6, 6, 8, 8, 10, 10, 17, 17};
    private final short x3[] = {6, 23, 2, 26, 7, 21, 18, 14};
    private final short y3[] = {5, 1, 16, 16, 23, 23, 6, 9};
    private final short x4[] = {10, 8, 20, 28, 6, 28, 1, 28, 1, 8, 20, 28};
    private final short y4[] = {1, 1, 1, 1, 6, 6, 22, 22, 28, 28, 28, 28};
    private final int validSpeeds[] = {1, 2, 3, 4, 4, 4};

    private int currentSpeed = validSpeeds[3];
    private short[] screenData;
    private Timer timer;

    public Board() {
//
        initSound();

        initPacman();
//
        initGhost();
        loadImages();
        initVariables();
        initBoard();
        Thread thread = new Thread();
        try {
            thread.sleep(500);
            sound.playStartUpSound(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void initPacman() {
        pacman = new Pacman();
        pacman.setSpeed(currentSpeed);
        pacman.setFirstSpeed(currentSpeed);
    }

    public void initSound(){
        sound = new Sound();
    }

    //en yüksek skor derecesi için ayarlandı
    private void setScoreFile(String sc) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./pacman/score.txt"))) {
            writer.write(sc);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getScoreFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("./pacman/score.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }
    //en yüksek skor derecesi için ayarlandı

    public void initGhost() {
        ghost = new Ghost[N_GHOSTS];
        for (int i = 0; i < ghost.length; i++) {
            ghost[i] = new Ghost();
            ghost[i].setImage(ghostAll);
        }
    }

    private void initBoard() {

        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);
    }

    private void showGameOverScreen(Graphics2D g2d) {
        // Border
        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 4 - 10, SCREEN_SIZE - 100, 380);
        g2d.setColor(dotColor);
        g2d.drawRect(50, SCREEN_SIZE / 4 - 10, SCREEN_SIZE - 100, 380);
        g2d.drawLine(50, 510, 645, 510);

        Font font1 = new Font("Helvetica", Font.BOLD, 14);
        Font font2 = new Font("Helvetica", Font.BOLD, 30);
        Font font3 = new Font("Helvetica", Font.BOLD, 18);

        String over = "GAME OVER";
        FontMetrics metr1 = this.getFontMetrics(font2);

        g2d.setFont(font2);
        g2d.drawString(over, (SCREEN_SIZE - metr1.stringWidth(over)) / 2, 1 * SCREEN_SIZE / 4 + 100);
        if (score > Integer.parseInt(getScoreFile())){
            setScoreFile(Integer.toString(score));
            sound.playHighScoreSound();
        }

        String f_score = "Your score: " + score;
        FontMetrics metr2 = this.getFontMetrics(font3);

        g2d.setFont(font3);
        g2d.drawString(f_score, (SCREEN_SIZE - metr2.stringWidth(f_score)) / 2, SCREEN_SIZE / 2 - 20);

        String h_score = "High Score: " + getScoreFile();
        g2d.drawString(h_score,(SCREEN_SIZE - metr2.stringWidth(h_score)) / 2, (SCREEN_SIZE / 2 - 20)+25);



        String link = "Press any key to restart";
        FontMetrics metr4 = this.getFontMetrics(font1);

        g2d.setFont(font1);
        g2d.drawString(link, (SCREEN_SIZE - metr4.stringWidth(link)) / 2, 3 * SCREEN_SIZE / 4 + 10);
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(709, 756);
        dx = new int[4];
        dy = new int[4];

        fruit_x = -1;
        fruit_y = -1;

        timer = new Timer(16, this);
        timer.start();

        giftBoxTimer = new Timer(7000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                beastmode = false; // Yenilme süresi dolduğunda yenilme durumunu sıfırla
                dark = false;
                pacman.setSpeed(pacman.getFirstSpeed());
                sound.playGhostSirenSound(false);
                sound.playDarknessSound(false);
                sound.playSpeedUpSound(false);
                sound.playPacmanChasingGhostsSound(true);
                giftBoxTimer.stop(); // Zamanlayıcıyı durdur
                spawnGiftBox();
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }

    private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }

    }

    private void showIntroScreen(Graphics2D g2d) {

        // Border
        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 4 - 50, SCREEN_SIZE - 100, 450);
        g2d.setColor(dotColor);
        g2d.drawRect(50, SCREEN_SIZE / 4 - 50, SCREEN_SIZE - 100, 450);
        g2d.drawLine(50, 510, 645, 510);

        String pac = "P A C M A N";
        Font pacFont = new Font("Helvetica", Font.BOLD, 40);
        g2d.setFont(pacFont);
        FontMetrics pmetr = this.getFontMetrics(pacFont);
        g2d.drawString(pac,(SCREEN_SIZE - pmetr.stringWidth(pac)) / 2, SCREEN_SIZE / 4 );

        Font font1 = new Font("Helvetica", Font.BOLD, 14);
        g2d.setFont(font1);

        // Instruction menu
        String m = "Instruction:";
        String m1 = "Up";
        String m2 = "Down";
        String m3 = "Left";
        String m4 = "Right";
        String m5 = "Restart";
        String m5_1 = "Esc";
        String m6 = "Pause/Resume";
        String m6_1 = "Spacebar";

        g2d.drawString(m, 60, 185);


        // Up key
        g2d.drawRect(200, 250, 35, 35);
        g2d.drawLine(218, 260, 218, 275);
        g2d.drawLine(218, 260, 222, 265);
        g2d.drawLine(218, 260, 214, 265);
        g2d.drawLine(230, 255, 248, 225);
        g2d.drawLine(248, 225, 285, 225);
        g2d.drawString(m1, 290, 230);

        // Down key
        g2d.drawRect(200, 295, 35, 35);
        g2d.drawLine(218, 305, 218, 320);
        g2d.drawLine(218, 320, 222, 315);
        g2d.drawLine(218, 320, 214, 315);
        g2d.drawLine(230, 325, 240, 355);
        g2d.drawLine(240, 355, 285, 355);
        g2d.drawString(m2, 290, 360);

        // Left key
        g2d.drawRect(155, 295, 35, 35);
        g2d.drawLine(165, 313, 180, 313);
        g2d.drawLine(165, 313, 170, 309);
        g2d.drawLine(165, 313, 170, 317);
        g2d.drawLine(160, 300, 140, 265);
        g2d.drawLine(140, 265, 110, 265);
        g2d.drawString(m3, 80, 270);

        // Right key
        g2d.drawRect(245, 295, 35, 35);
        g2d.drawLine(255, 313, 270, 313);
        g2d.drawLine(270, 313, 265, 309);
        g2d.drawLine(270, 313, 265, 317);
        g2d.drawLine(275, 300, 295, 270);
        g2d.drawLine(295, 270, 325, 270);
        g2d.drawString(m4, 330, 275);

        // Escape key
        g2d.drawRect(440, 275, 35, 35);
        g2d.drawString(m5_1, 445, 296);
        g2d.drawLine(470, 280, 490, 250);
        g2d.drawLine(490, 250, 525, 250);
        g2d.drawString(m5, 530, 255);

        // Spacebar key
        g2d.drawRect(110, 400, 331, 35);
        g2d.drawString(m6_1, 243, 422);
        g2d.drawLine(276, 430, 300, 460);
        g2d.drawLine(300, 460, 485, 460);
        g2d.drawString(m6, 490, 465);

        String link = "Press any key to start";
        FontMetrics metr8 = this.getFontMetrics(font1);


        g2d.drawString(link, (SCREEN_SIZE - metr8.stringWidth(link)) / 2, 3 * SCREEN_SIZE / 4 + 20);
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;
        String lv;

        g.setFont(smallFont);
        g.setColor(mazeColor);
        s = "Score: " + score;
        lv = "Level: " + level;
        // Current level
        g.drawString(lv, SCREEN_SIZE / 2 - 28, SCREEN_SIZE + 16);

        // Scoreboard
        g.drawString(s, 5 * SCREEN_SIZE / 6, SCREEN_SIZE + 16);

        // For drawing lives left
        for (i = 0; i < pacsLeft; i++) {
            g.drawImage(pacman3left, i * 28 + 5, SCREEN_SIZE + 1, this);
        }
    }

    private void checkMaze() {
        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i] & 48) != 0) {
                finished = false;
            }
            i++;
        }

        switch (level) {
            case 1:
                if (finished) {
                    level = 2;
                    pacsLeft = 3;
                    score += 50;
                    initLevel();
                }
                break;
            case 2:
                if (finished) {
                    pacsLeft = 3;
                    level = 3;
                    score += 130;
                    initLevel();
                }
                break;
            case 3:
                if (finished) {
                    level = 4;
                    for (int j = 1; j <= pacsLeft; j++) {
                        score += 150;
                    }
                    pacsLeft = 1;
                    initLevel();
                }
                break;
            case 4:
                if (finished) {
                    score += 1000;
                    inGame = false;
                }
                break;

        }
    }

    private void death() {

        pacsLeft--;
        beastmode=false;
        dark=false;
        pacman.setSpeed(pacman.getFirstSpeed());

        if (pacsLeft == 0) {
            inGame = false;
            noLives = true;
            N_GHOSTS = 6;
            level = 1;
            sound.playPacmanChasingGhostsSound(false);
        } else {
            continueLevel();
        }
        sound.playSpeedUpSound(false);
        sound.playDarknessSound(false);
        sound.playDeathPacmanSound();
    }

    private void moveGhosts(Graphics2D g2d) {
        short i;
        int pos;
        int count;
        int targetX, targetY;


        for (i = 0; i < N_GHOSTS; i++) {
            if (ghost[i].getX() % BLOCK_SIZE == 0 && ghost[i].getY() % BLOCK_SIZE == 0) {
                pos = ghost[i].getX() / BLOCK_SIZE + N_BLOCKS * (int) (ghost[i].getY() / BLOCK_SIZE);

                count = 0;
                if (beastmode) {
                    // Hayalet rastgele bir hücre seçsin
                    int randomX = (int) (random() * N_BLOCKS);
                    int randomY = (int) (random() * N_BLOCKS);

                    targetX = randomX;
                    targetY = randomY;
                } else {
                    // Beastmode aktif değilse, hedef hücreyi belirle
                    targetX = pacman.getX() / BLOCK_SIZE;
                    targetY = pacman.getY() / BLOCK_SIZE;
                }

                int ghostCellX = ghost[i].getX() / BLOCK_SIZE;
                int ghostCellY = ghost[i].getY() / BLOCK_SIZE;

                // Hareket yönlerini belirle
                if (targetX < ghostCellX && (screenData[pos] & 1) == 0 && ghost[i].getDx() != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    ghostAll = ghost1;
                    count++;
                }

                if (targetX > ghostCellX && (screenData[pos] & 4) == 0 && ghost[i].getDx() != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    ghostAll = ghost2;
                    count++;
                }

                if (targetY < ghostCellY && (screenData[pos] & 2) == 0 && ghost[i].getDx() != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if (targetY > ghostCellY && (screenData[pos] & 8) == 0 && ghost[i].getDy() != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {
                    // Hedef hücreye doğru hareket mümkün değilse, rastgele bir yöne hareket et
                    // Bu kısmı değiştir, önceki kod hareket etmeyi durduruyordu
                    int randomDirection = (int) (random() * count);
                    dx[count] = dx[randomDirection];
                    dy[count] = dy[randomDirection];


                } else {
                    // Hareket yönlerinden birini seç
                    count = (int) (random() * count);

                    ghost[i].setDx(dx[count]);
                    ghost[i].setDy(dy[count]);
                }
            }


            // Duvarları kontrol et ve gerekirse düzelt
            checkWallCollision(i);

            if (pacman.getX() > (ghost[i].getX() - 12) && pacman.getX() < (ghost[i].getX() + 12)
                    && pacman.getY() > (ghost[i].getY() - 12) && pacman.getY() < (ghost[i].getY() + 12)
                    && inGame) {
                if (beastmode) {
                    sound.playPacmanEatingGhostSound();
                    score += 50;
                    ghost[i].setX(ghost[i].getXs());
                    ghost[i].setY(ghost[i].getYs());
                    ghost[i].setSpeed(0);
                } else {
                    dying = true;
                }
            }
            if (!beastmode) {
                ghost[i].setSpeed(ghost[i].getSpeedtemp(i));
            }
            drawGhost(g2d,i);
        }
    }

    private void drawGhost(Graphics2D g2d, int x) {
        if (beastmode) {
            // Eğer pacman hayaleti yiyebilir durumdaysa, farklı bir görüntü kullan
            ghost[x].setImage(escape);
        } else {
            // Diğer durumlarda normal hayalet görüntüsünü kullan
            ghost[x].setImage(ghostAll);
        }
        ghost[x].draw(g2d);
    }

    private void checkWallCollision(int i) {
        // Hayaletin mevcut hücrede olup olmadığını kontrol et
        if (ghost[i].getX() % BLOCK_SIZE == 0 && ghost[i].getY() % BLOCK_SIZE == 0) {
            int ghostCellX = ghost[i].getX() / BLOCK_SIZE;
            int ghostCellY = ghost[i].getY() / BLOCK_SIZE;

            if ((ghost[i].getDx() == -1 && (screenData[ghostCellX + N_BLOCKS * ghostCellY] & 1) != 0) ||
                    (ghost[i].getDx() == 1 && (screenData[ghostCellX + N_BLOCKS * ghostCellY] & 4) != 0) ||
                    (ghost[i].getDy() == -1 && (screenData[ghostCellX + N_BLOCKS * ghostCellY] & 2) != 0) ||
                    (ghost[i].getDy() == 1 && (screenData[ghostCellX + N_BLOCKS * ghostCellY] & 8) != 0)) {

                // Hareket yönüne bağlı olarak düzeltme yap

                ghost[i].setDx(-ghost[i].getDx());
                ghost[i].setDy(-ghost[i].getDy());
            }
        }
    }

    private void movePacman() {

        int pos;
        short ch;

        if (req_dx == -pacman.getDx() && req_dy == -pacman.getDy()) {
            pacman.setDx(req_dx);
            pacman.setDy(req_dy);
        }

        if (pacman.getX() % BLOCK_SIZE == 0 && pacman.getY() % BLOCK_SIZE == 0) {
            pos = pacman.getX() / BLOCK_SIZE + N_BLOCKS * (int) (pacman.getY() / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                sound.playYellowBallSound();
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacman.setDx(req_dx);
                    pacman.setDy(req_dy);
                }
            }

            // Check for standstill
            if ((pacman.getDx() == -1 && pacman.getDy() == 0 && (ch & 1) != 0)
                    || (pacman.getDx() == 1 && pacman.getDy() == 0 && (ch & 4) != 0)
                    || (pacman.getDx() == 0 && pacman.getDy() == -1 && (ch & 2) != 0)
                    || (pacman.getDx() == 0 && pacman.getDy() == 1 && (ch & 8) != 0)) {
                pacman.setDx(0);
                pacman.setDy(0);
            }
        }
        checkFruitCollision();
        pacman.setX(pacman.getX() + pacman.getSpeed() * pacman.getDx());
        pacman.setY(pacman.getY() + pacman.getSpeed() * pacman.getDy());

    }

    private void checkFruitCollision() {
        if (pacman.getX() == giftBox_x && pacman.getY() == giftBox_y) {
            giftBox_x = -1; // Meyveyi gizle
            giftBox_y = -1;
            Random random = new Random();
            int random1=random.nextInt(3);
            switch (random1) {
                case 0:
                    beastmode = true;
                    sound.playGhostSirenSound(true);
                    sound.playPacmanChasingGhostsSound(false);
                    giftBoxTimer.start();
                    break;
                case 1:
                    dark = true;
                    sound.playDarknessSound(true);
                    sound.playPacmanChasingGhostsSound(false);
                    giftBoxTimer.start();
                    break;
                case 2:
                    pacman.setSpeed(currentSpeed*2);
                    sound.playSpeedUpSound(true);
                    giftBoxTimer.start();
                    break;
                default:
                    break;
            }
        }

        // Eğer pacman meyveye çarparsa
        if (pacman.getX() == fruit_x && pacman.getY() == fruit_y) {
            sound.playFruitSound();
            score += 10; // meyve yemek 10 puan kazandırsın
            fruit_x = -1; // Meyveyi gizle
            fruit_y = -1;


            Timer delayTimer = new Timer(DELAY_BEFORE_NEW_FRUIT, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    spawnFruit();
                }

            });
            delayTimer.stop();
            delayTimer.setRepeats(false); // Yalnızca bir kez çalışacak şekilde ayarla
            delayTimer.start();
        }
    }

    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
            pacman.drawLeft(g2d);
        } else if (req_dx == 1) {
            pacman.drawRight(g2d);
        } else if (req_dy == -1) {
            pacman.drawUp(g2d);
        } else {
            pacman.drawDown(g2d);
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;
        switch (level) {
            case 1:
                // Level 1: 57, 255, 20
                mazeColor = new Color(57, 255, 20);
                break;
            case 2:
                // Level 2: 255, 66, 0
                mazeColor = new Color(255, 66, 0);
                break;
            case 3:
                // Level 3: 251, 72, 196
                mazeColor = new Color(251, 72, 196);
                break;
            case 4:
                // Bonus: 255, 131, 0
                mazeColor = new Color(255, 131, 0);
                break;
        }

        for (y = 1; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 10, y + 10, 3, 3);
                }

                i++;
            }
        }
        drawFruit(g2d);
        drawGiftBox(g2d);
    }

    private void initGame() {
        switch (level) {
            case 1:
                pacsLeft = 3;
                break;
            case 2:
                pacsLeft = 3;
                break;
            case 3:
                pacsLeft = 3;
                break;
            case 4:
                pacsLeft = 1;
                break;
        }
        score = 0;
        initLevel();

    }
    private void initLevel() {
        switch (level) {
            case 1:
                for (int i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
                    screenData[i] = levelData1[i];
                }
                break;
            case 2:
                for (int i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
                    screenData[i] = levelData2[i];
                }
                break;
            case 3:
                for (int i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
                    screenData[i] = levelData3[i];
                }
                break;
            case 4:
                for (int i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
                    screenData[i] = levelData4[i];
                }
                break;
        }
        spawnFruit();
        spawnGiftBox();
        continueLevel();

    }

    private void spawnFruit() {
        int newFruitX, newFruitY;

        do {
            // Rastgele bir konumda meyve oluştur
            newFruitX = (int) (random() * N_BLOCKS) * BLOCK_SIZE;
            newFruitY = (int) (random() * N_BLOCKS) * BLOCK_SIZE;
        } while (isWallCollision(newFruitX, newFruitY));

        fruit_x = newFruitX;
        fruit_y = newFruitY;
    }

    private boolean isWallCollision(int x, int y) {
        // Verilen x ve y koordinatlarında duvar var mı kontrol et
        int blockIndex = x / BLOCK_SIZE + N_BLOCKS * (y / BLOCK_SIZE);
        return (screenData[blockIndex] & 48) == 0;
    }

    private void drawFruit(Graphics2D g2d) {
        // Eğer meyve konumu geçerli ise meyveyi çiz
        if (fruit_x != -1 && fruit_y != -1) {
            g2d.drawImage(fruitAll, fruit_x + 11 , fruit_y + 11 , this);
        }
    }

    private void spawnGiftBox() {
        int newGiftBoxX, newGiftBoxY;

        do {
            // Rastgele bir konumda hediye kutusu oluştur
            newGiftBoxX = (int) (random() * N_BLOCKS) * BLOCK_SIZE;
            newGiftBoxY = (int) (random() * N_BLOCKS) * BLOCK_SIZE;
        } while (isWallCollision(newGiftBoxX, newGiftBoxY));

        giftBox_x = newGiftBoxX;
        giftBox_y = newGiftBoxY;
    }

    private void drawGiftBox(Graphics2D g2d) {
        // Eğer hediye kutusu konumu geçerli ise hediye kutusunu çiz
        if (giftBox_x != -1 && giftBox_y != -1) {
            g2d.drawImage(giftBox, giftBox_x , giftBox_y , this);
        }
    }

    private void continueLevel() {
        int dx = 1;
        switch (level) {
            case 1:
                for (short i = 0; i < N_GHOSTS; i++) {
                    // Starting place of the ghosts
                    ghost[i].setX(x1[i] * BLOCK_SIZE);
                    ghost[i].setXs(x1[i] * BLOCK_SIZE);
                    ghost[i].setY(y1[i] * BLOCK_SIZE);
                    ghost[i].setYs(y1[i] * BLOCK_SIZE);
                    ghost[i].setDy(0);
                    ghost[i].setDx(dx);
                    dx = -dx;

                    // Ghost speed set to 3
                    ghost[i].setSpeed(validSpeeds[2]);
                    ghost[i].setSpeedtemp(validSpeeds[2]);
                }

                // Starting place of pacman
                pacman.setX(14 * BLOCK_SIZE);
                pacman.setY(22 * BLOCK_SIZE);

                pacman.setDx(0);
                pacman.setDy(0);
                req_dx = 0;
                req_dy = 0;
                dying = false;
                break;
            case 2:
                for (short i = 0; i < N_GHOSTS; i++) {
                    // Starting place of the ghosts
                    ghost[i].setX(x2[i] * BLOCK_SIZE);
                    ghost[i].setXs(x2[i] * BLOCK_SIZE);
                    ghost[i].setY(y2[i] * BLOCK_SIZE);
                    ghost[i].setYs(y2[i] * BLOCK_SIZE);
                    ghost[i].setDy(0);
                    ghost[i].setDx(dx);
                    dx = -dx;

                    if (currentSpeed <= 3) {
                        ghost[i].setSpeed(validSpeeds[3]);
                        ghost[i].setSpeedtemp(validSpeeds[3]);
                    } else {
                        ghost[i].setSpeed(currentSpeed);
                        ghost[i].setSpeedtemp(currentSpeed);
                    }
                }

                // Starting place of pacman
                pacman.setX(14 * BLOCK_SIZE);
                pacman.setY(25 * BLOCK_SIZE);

                pacman.setDx(0);
                pacman.setDy(0);
                req_dx = 0;
                req_dy = 0;
                dying = false;
                break;
            case 3:
                for (short i = 0; i < N_GHOSTS; i++) {
                    // Starting place of the ghosts
                    ghost[i].setX(x3[i] * BLOCK_SIZE);
                    ghost[i].setXs(x3[i] * BLOCK_SIZE);
                    ghost[i].setY(y3[i] * BLOCK_SIZE);
                    ghost[i].setYs(y3[i] * BLOCK_SIZE);
                    ghost[i].setDy(0);
                    ghost[i].setDx(dx);
                    dx = -dx;
                    if (currentSpeed <= 4) {
                        ghost[i].setSpeed(validSpeeds[4]);
                        ghost[i].setSpeedtemp(validSpeeds[4]);
                    } else {
                        ghost[i].setSpeed(currentSpeed);
                        ghost[i].setSpeedtemp(currentSpeed);
                    }
                }
                // Starting place of pacman
                pacman.setX(14 * BLOCK_SIZE);
                pacman.setY(18 * BLOCK_SIZE);

                pacman.setDx(0);
                pacman.setDy(0);
                req_dx = 0;
                req_dy = 0;
                dying = false;
                break;
            case 4:
                // Ghost num set to max: 12

                N_GHOSTS = 6;
                for (short i = 0; i < N_GHOSTS; i++) {
                    // Starting place of the ghosts

                    ghost[i].setX(x4[i] * BLOCK_SIZE);
                    ghost[i].setXs(x4[i] * BLOCK_SIZE);
                    ghost[i].setY(y4[i] * BLOCK_SIZE);
                    ghost[i].setYs(y4[i] * BLOCK_SIZE);
                    ghost[i].setDy(0);
                    ghost[i].setDx(dx);
                    dx = -dx;
                    // Ghost speed set to 8
                    ghost[i].setSpeed(3);
                    ghost[i].setSpeedtemp(3);
                }
                // Starting place of pacman
                pacman.setX(14 * BLOCK_SIZE);
                pacman.setY(14 * BLOCK_SIZE);

                pacman.setDx(0);
                pacman.setDy(0);
                req_dx = 0;
                req_dy = 0;
                dying = false;
                break;
        }

    }

    private void loadImages() {

        ghost1 = new ImageIcon("./pacman/ghost1.gif").getImage();
        ghost2 = new ImageIcon("./pacman/ghost2.gif").getImage();
        pacman1 = new ImageIcon("./pacman/pacman.png").getImage();
        pacman3left = new ImageIcon("./pacman/left2.png").getImage();
        fruitAll = new ImageIcon("./pacman/redfruit2.png").getImage();

        giftBox = new ImageIcon("./pacman/giftbox.png").getImage();
        escape = new ImageIcon("./pacman/escape.gif").getImage();

        left = new ImageIcon("./pacman/left.gif").getImage();
        right = new ImageIcon("./pacman/right.gif").getImage();
        down = new ImageIcon("./pacman/down.gif").getImage();
        up = new ImageIcon("./pacman/up.gif").getImage();

        pacman.setLeft(left);
        pacman.setRight(right);
        pacman.setUp(up);
        pacman.setDown(down);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);
        drawMaze(g2d);
        drawScore(g2d);
        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);

        }
        if (noLives) {
            timer.stop();
            showGameOverScreen(g2d);
        }
        if (dark) {
            drawDarkness(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    private void drawDarkness(Graphics2D g2d) {
        int ovalPacmanWidth = 100;
        int ovalPacmanHeight = 100;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, pacman.getX() - (ovalPacmanWidth / 2) + (pacman1.getWidth(null) / 2), d.height - 40); // sol duvar
        g2d.fillRect(pacman.getX() - (ovalPacmanWidth / 2) + (pacman1.getWidth(null) / 2) + ovalPacmanWidth, 0, d.width, d.height - 40); // sağ duvar
        g2d.fillRect(0, 0, d.width, pacman.getY() - (ovalPacmanHeight / 2) + (pacman1.getHeight(null) / 2));
        g2d.fillRect(0, pacman.getY() - (ovalPacmanHeight / 2) + (pacman1.getHeight(null) / 2) + ovalPacmanHeight, d.width, d.height - 40);
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                    System.exit(1);
                } else if (key == KeyEvent.VK_SPACE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            }

            if (!inGame && key != KeyEvent.VK_ESCAPE && key != KeyEvent.VK_SPACE) { // press any to start için
                noLives=false;
                inGame = true;
                timer.start();
                sound.playStartUpSound(false);
                sound.playPacmanChasingGhostsSound(true);
                initGame();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
}



