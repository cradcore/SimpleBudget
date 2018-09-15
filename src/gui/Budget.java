package gui;


import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

class Budget {

    // Class variables
    private JFrame window;

    // Constructor
    Budget(JFrame window) {
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

        for (Component c : window.getContentPane().getComponents())
            System.out.println(c.getName());
    }

    private void addTopMenu() {
        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));
        panel.setBackground(Color.decode("#547dc4"));
        panel.setName("JPanel - Top Menu");
        panel.setVisible(true);

        JLabel year = new JLabel("  " + LocalDate.now().getYear() + "  ");
        year.setFont(new Font("Lato", Font.BOLD, 40));
        year.setForeground(Color.WHITE);
        year.setName("Year");

        JLabel leftArrow = new JLabel("  ");
        leftArrow.setIcon(new ImageIcon(new ImageIcon("resources/budget-top_menu_1.png").getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
        leftArrow.setName("Left arrow");
        leftArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                year.setText("  " + (Integer.parseInt(year.getText().substring(2,6)) - 1) + "  ");
            }
        });
        panel.add(leftArrow, "dock west");

        panel.add(year, "dock west");

        JLabel rightArrow = new JLabel("  ");
        rightArrow.setIcon(new ImageIcon(new ImageIcon("resources/budget-top_menu_2.png").getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
        rightArrow.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        rightArrow.setName("Right arrow");
        rightArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                year.setText("  " + (Integer.parseInt(year.getText().substring(2,6)) + 1) + "  ");
            }
        });
        panel.add(rightArrow);

        for(int i = 0; i < 12; i++) {
            LocalDate ld;
            if(i < 9)
                ld = LocalDate.parse(LocalDate.now().getYear() + "-0" + (i + 1) + "-" + LocalDate.now().getDayOfMonth());
            else ld = LocalDate.parse(LocalDate.now().getYear() + "-" + (i + 1) + "-" + LocalDate.now().getDayOfMonth());
            JLabel month = new JLabel("  " + ld.getMonth().toString().substring(0, 3) + "  ");
            month.setFont(new Font("Lato", Font.BOLD, 25));
            month.setForeground(Color.WHITE);
            month.setOpaque(true);
            if(ld.getMonth().equals(LocalDate.now().getMonth()))
                month.setBackground(Color.decode("#345998"));
            else month.setBackground(Color.decode("#547dc4"));
            month.setName("Month - " + ld.getMonth().toString().substring(0, 3));
            month.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    resetMonthColors(panel);
                    month.setBackground(Color.decode("#345998"));
                }
            });
            panel.add(month, "al center center, height 50");
        }

        window.add(panel, "dock north");
    }

    private void resetMonthColors(JPanel panel) {
        for(Component c : panel.getComponents())
            if(c.getName().length() >= 5 && c.getName().substring(0, 5).equals("Month"))
                c.setBackground(Color.decode("#547dc4"));
    }
}
