package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

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
        title.setName("JLabel - Title");
        title.setFont(new Font("Lato", Font.BOLD, 50));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBackground(Color.decode("#b3c5e5"));
        title.setOpaque(true);
        window.add(title, "dock north");

        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));
        panel.setName("JPanel - Import transactions");

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
        open.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(Component c : window.getContentPane().getComponents()) {
                    if (c.getName().equals("JPanel - Import transactions"))
                        c.setVisible(false);
                    else if (c.getName().equals("JPanel - File explorer"))
                        c.setVisible(true);
                }
            }
        });
        panel.add(open,  "align 50% 50%");

        addFileExplorer();
        addImportScreen();

        window.add(panel, "align 50% 50%, hidemode 3");
    }

    private void addFileExplorer() {
        JPanel panel2 = new JPanel();
        panel2.setName("JPanel - File explorer");

        JFileChooser jfc = new JFileChooser(System.getProperty("user.home") + "/Downloads");
        jfc.setDialogTitle("Select a .csv file");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        jfc.addChoosableFileFilter(filter);
        jfc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(javax.swing.JFileChooser.APPROVE_SELECTION)) {
                    beginImport(jfc.getSelectedFile());
                } else if (e.getActionCommand().equals(javax.swing.JFileChooser.CANCEL_SELECTION)) {
                    for(Component c :window.getContentPane().getComponents()) {
                        if(c.getName().equals("JPanel - Import transactions"))
                            c.setVisible(true);
                        else if (c.getName().equals("JPanel - File explorer"))
                            c.setVisible(false);
                    }
                }
            }
        });

        panel2.add(jfc);
        panel2.setVisible(false);
        window.add(panel2, "align 50% 50%, hidemode 3");
    }

    private void addImportScreen() {
        JPanel panel = new JPanel();
        panel.setName("JPanel - Import");




        window.add(panel, "hidemode 3");
    }

    private void beginImport(File file) {

    }
}
