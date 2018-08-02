package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Run {

    public static void main(String[] args) {

        JFrame window = new JFrame();
        window.setBounds(100, 100, 1600, 900);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new AllAccounts(window);
                } catch (Exception e) {e.printStackTrace();}
            }
        });
    }

}
