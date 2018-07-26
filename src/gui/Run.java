package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Run {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Home(window);
                } catch (Exception e) {e.printStackTrace();}
            }
        });
    }

}
