package gui;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.*;
import javax.swing.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Home {

    private JFrame window;

    // Create the application.
    public Home(JFrame window) {
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
        addButtons(panel);

    }

    private void addTitle(JPanel panel) {
        CC constraints = new CC();
        constraints.alignX("center").spanX();
        constraints.wrap();

        JLabel title = new JLabel("Welcome to Simple Budget!");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(title, constraints);
    }

    private void addButtons(JPanel panel) {
        CC constraints = new CC();

        JButton b1 = new JButton();
        b1.setIcon(new ImageIcon(new ImageIcon("resources/home-all_accounts.png").getImage().getScaledInstance(398, 532, Image.SCALE_DEFAULT)));
        b1.setBorderPainted(false);
        b1.setBorder(null);
        b1.setContentAreaFilled(false);
        b1.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(b1, constraints);
        b1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                new AllAccounts(window);
            }
        });

        JButton b2 = new JButton();
        b2.setIcon(new ImageIcon(new ImageIcon("resources/home-budget.png").getImage().getScaledInstance(398, 532, Image.SCALE_DEFAULT)));
        b2.setBorderPainted(false);
        b2.setBorder(null);
        b2.setContentAreaFilled(false);
        b2.setFont(new Font("Lato", Font.BOLD, 60));
        b2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            }
        });
        panel.add(b2, constraints);

        JButton b3 = new JButton();
        b3.setIcon(new ImageIcon(new ImageIcon("resources/home-reports.png").getImage().getScaledInstance(398, 532, Image.SCALE_DEFAULT)));
        b3.setBorderPainted(false);
        b3.setBorder(null);
        b3.setContentAreaFilled(false);
        b3.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(b3, constraints);
        b3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            }
        });
    }

}
