package FlappyBird;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import javax.swing.Timer;
import javax.swing.*;

import javax.swing.JPanel;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 370;
    int boardHeight = 640;

    // Images
    Image backImage;
    Image birdImage;
    Image topImage;
    Image botImage;

    // Bird 
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipe
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4;  // moves pipes to left speed (simulates bird moving right)
    int velocityY = 0; // moves bird up and down
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    // Game Timer
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLUE);
        setFocusable(true);
        addKeyListener(this);

        // load images
        backImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        botImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // bird
        bird = new Bird(birdImage);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        // game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipes() {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight

        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * pipeHeight / 2);
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(botImage);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backImage, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipeWidth, pipeHeight, null);
        }

        // score
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        if (gameOver) {
            g.drawString("Game Over : " + String.valueOf((int) score), 9, 35);
        } else {
            g.drawString(String.valueOf((int) score), 9, 35);
        }
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score = score + 0.5; 
                // 0.5 as there are 2 pipes, 0.5 for top one and 0.5 for bottom so user gets 1 score after passing 1 set
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;         
            }
        }
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height && 
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop(); // stops placing pipes
            gameLoop.stop(); // stops the game
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            if (gameOver) {
                // restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear(); // eliminates all elements from pipes list
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
