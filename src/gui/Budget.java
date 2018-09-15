package gui;


import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class Budget {

    // Class variables
    private JFrame window;

    // Constructor
    public Budget(JFrame window) {
        this.window = window;
        initialize();
    }

    private void initialize() {
        window.setTitle("Simple Budget - All Accounts");
        window.getContentPane().removeAll();
        JPanel jp = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        jp.setName("JPanel - Content Pane");
        window.add(jp);
        window.getContentPane().setBackground(Color.decode("#b3c5e5"));
        window.getContentPane().setName("Budget content pane");
        window.getContentPane().setVisible(true);
        window.getContentPane().revalidate();
        window.getContentPane().repaint();
        window.setVisible(true);

        Home.addSideMenu(window, "Budget");
        Home.addTitle(window, "Budget:");
        addTopMenu();

        for(Component c : window.getContentPane().getComponents())
            System.out.println(c.getName());
    }

    private void addTopMenu() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setBackground(new Color(0, 0, 0));
        panel.setName("JPanel - Top Menu");
        panel.setVisible(true);





        window.add(panel, "dock north");
    }



}
