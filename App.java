package FlappyBird;

import javax.swing.*;

public class App {
    public static void main(String[] args){
        int boardWidth=370;
        int boardheight=640;


        JFrame frame=new JFrame("Flappy Bird");

        frame.setSize(boardWidth,boardheight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
    
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
    
}

