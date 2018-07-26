package gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.*;

public class AllAccounts {

    private JFrame window;

    // Create the application.
    public AllAccounts(JFrame window) {
        this.window = window;
        initialize();
        window.setVisible(true);
    }

    // Initialize the contents of the frame.
    private void initialize() {
        window.setTitle("Simple Budget");
        window.setBounds(100, 100, 1300, 690);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]");
        JPanel panel = new JPanel(layout);
        window.setContentPane(panel);

        addTitle(panel);
        addMenu(panel);
    }

    private void addTitle(JPanel panel) {
        CC constraints = new CC();
        constraints.alignX("center").spanX();
        constraints.wrap();

        JLabel title = new JLabel("All Accounts:");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(title, constraints);
    }

    private void addMenu(JPanel panel) {
        Insets margins = new Insets(0, 0, 0, 0);
        MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]");
        JPanel parentPanel = new JPanel(layout);

        JButton b1 = new JButton();
        b1.setMargin(margins);
        b1.setIcon((new ImageIcon("resources/all_accounts-side_menu_1.png")));
        b1.setBorderPainted(false);
        b1.setBorder(null);
        b1.setContentAreaFilled(false);
        b1.setFont(new Font("Lato", Font.BOLD, 60));
        parentPanel.add(b1, "wrap");
        b1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {

            }
        });

        JButton b2 = new JButton();
        b2.setMargin(margins);
        b2.setIcon(new ImageIcon("resources/all_accounts-side_menu_2.png"));
        b2.setBorderPainted(false);
        b2.setBorder(null);
        b2.setContentAreaFilled(false);
        b2.setFont(new Font("Lato", Font.BOLD, 60));
        parentPanel.add(b2, "wrap");
        b2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {

            }
        });


        window.add(parentPanel, "dock west");
    }
}
