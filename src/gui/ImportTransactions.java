package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ImportTransactions {

    private JFrame mainWindow;
    private JFrame window;

    public ImportTransactions(JFrame mainWindow) {
        this.mainWindow = mainWindow;
        window = new JFrame();
        window.setBounds(500, 325, 800, 450);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setContentPane(new JPanel(new MigLayout("gap rel 0", "grow")));
        window.getContentPane().setName("Popup window content pane");
        window.setVisible(true);
        window.setTitle("Import Transactions");

        initialize();
    }

    private void initialize() {
        JLabel title = new JLabel("Import Transactions:");
        title.setFont(new Font("Lato", Font.BOLD, 50));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBackground(Color.decode("#b3c5e5"));
        title.setOpaque(true);
        window.add(title, "dock north");

        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));

        JLabel instructions = new JLabel("<html>Select the file you would like to import: &nbsp &nbsp &nbsp </html>");
        instructions.setFont(new Font("Lato", Font.PLAIN, 20));
        instructions.setForeground(Color.BLACK);
        instructions.setVerticalAlignment(JLabel.CENTER);
        panel.add(instructions,  "align 50% 50%");

        JTextField fileName = new JTextField();
        fileName.setFont(new Font("Lato", Font.PLAIN, 20));
        fileName.setForeground(Color.BLACK);
        fileName.setPreferredSize(new Dimension(250, 30));
        fileName.setMaximumSize(new Dimension(250, 30));
        fileName.setHorizontalAlignment(JTextField.CENTER);
        panel.add(fileName, "align 50% 50%");

        JButton open = new JButton("Select");
        open.setFont(new Font("Lato", Font.PLAIN, 15));
        open.setForeground(Color.BLACK);
        open.setPreferredSize(new Dimension(80, 30));
        open.setMaximumSize(new Dimension(80, 30));
        open.setVerticalAlignment(JButton.CENTER);
        panel.add(open,  "align 50% 50%");




        window.add(panel, "align 50% 50%");
    }


}
