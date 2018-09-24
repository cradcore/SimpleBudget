package gui;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.*;
import sqlConnector.SQLConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Home {

    private JFrame window;
    private SQLConnector sql;
    // Create the application.
    public Home(JFrame window, SQLConnector sql) {
        this.window = window;
        this.sql = sql;
        initialize();
        window.setVisible(true);
    }

    // Initialize the contents of the frame.
    private void initialize() {
        window.setTitle("Simple Budget");

        JPanel panel = new JPanel(new MigLayout("gap rel 0", "grow"));
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
        JButton b1 = new JButton();
        b1.setIcon(new ImageIcon(new ImageIcon("resources/home-all_accounts.png").getImage().getScaledInstance(500, 665, Image.SCALE_DEFAULT)));
        b1.setBorderPainted(false);
        b1.setBorder(null);
        b1.setContentAreaFilled(false);
        b1.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(b1, "alignx center");
        b1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("All Accounts clicked");
                new AllAccounts(window, sql);
            }
        });

        JButton b2 = new JButton();
        b2.setIcon(new ImageIcon(new ImageIcon("resources/home-budget.png").getImage().getScaledInstance(500, 665, Image.SCALE_DEFAULT)));
        b2.setBorderPainted(false);
        b2.setBorder(null);
        b2.setContentAreaFilled(false);
        b2.setFont(new Font("Lato", Font.BOLD, 60));
        b2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("Budget clicked");
                new Budget(window, sql);
            }
        });
        panel.add(b2, "id b1, alignx center");

        JButton b3 = new JButton();
        b3.setIcon(new ImageIcon(new ImageIcon("resources/home-reports.png").getImage().getScaledInstance(500, 665, Image.SCALE_DEFAULT)));
        b3.setBorderPainted(false);
        b3.setBorder(null);
        b3.setContentAreaFilled(false);
        b3.setFont(new Font("Lato", Font.BOLD, 60));
//        panel.add(b3, "alignx center");
        b3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("Reports clicked");
            }
        });
    }

    protected static void addTitle(JFrame window, String title) {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setName("JPanel - Title");
        panel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setName("Title");
        titleLabel.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(titleLabel, "align center");
        panel.setVisible(true);
        window.add(panel, "dock north");
    }

    protected static void addSideMenu(JFrame window, String page, SQLConnector sql) {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setName("JPanel - Side Menu");
        panel.setBackground(Color.decode("#8faadc"));
        panel.setVisible(true);

        sideMenuAddButton(window, panel, "Home", 1, 130, sql);
        if (page.equals("All Accounts"))
            sideMenuAddButton(window, panel, "All Accounts", 2, 100, sql);
        else sideMenuAddButton(window, panel, "All Accounts", 5, 100, sql);
        if (page.equals("Budget"))
            sideMenuAddButton(window, panel, "Budget", 6, 100, sql);
        else sideMenuAddButton(window, panel, "Budget", 3, 100, sql);
//        sideMenuAddButton(window, panel, "Reports", 4, 800, sql);

        window.add(panel, "dock west");
    }

    protected static void sideMenuAddButton(JFrame window, JPanel panel, String name, int image, int height, SQLConnector sql) {
        Insets margins = new Insets(0, 0, 0, 0);
        JButton button = new JButton();
        button.setName(name);
        button.setMargin(margins);
        button.setIcon(new ImageIcon(new ImageIcon("resources/side_menu_" + image + ".png").getImage().getScaledInstance(330, height, Image.SCALE_DEFAULT)));
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(button, "dock north");

        sideMenuAddButtonListener(window, button, sql);
    }

    protected static void sideMenuAddButtonListener(JFrame window, JButton button, SQLConnector sql) {
        String name = button.getName();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                switch (name) {
                    case "Home":
                        new Home(window, sql);
                        break;
                    case "All Accounts":
                        new AllAccounts(window, sql);
                        break;
                    case "Budget":
                        new Budget(window, sql);
                        break;
                    case "Reports":
                        break;
                }
            }
        });
    }
}
