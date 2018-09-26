package gui;

import com.sun.java.swing.plaf.motif.MotifInternalFrameTitlePane;
import net.miginfocom.swing.MigLayout;
import sqlConnector.SQLConnector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

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
        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));
        panel.setName("JPanel - Select file");

        JLabel title = new JLabel("Import Transactions:");
        title.setName("JLabel - Title");
        title.setFont(new Font("Lato", Font.BOLD, 50));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBackground(Color.decode("#b3c5e5"));
        title.setOpaque(true);
        window.add(title, "dock north");

        JLabel instructions = new JLabel("<html>Select the file you would like to import: &nbsp &nbsp &nbsp </html>");
        instructions.setName("JLabel - Instructions");
        instructions.setFont(new Font("Lato", Font.PLAIN, 20));
        instructions.setForeground(Color.BLACK);
        instructions.setVerticalAlignment(JLabel.CENTER);
        panel.add(instructions, "align 50% 50%");

        JTextField fileName = new JTextField();
        fileName.setName("JTextField - File name");
        fileName.setFont(new Font("Lato", Font.PLAIN, 15));
        fileName.setForeground(Color.BLACK);
        fileName.setPreferredSize(new Dimension(250, 30));
        fileName.setMaximumSize(new Dimension(250, 30));
        fileName.setHorizontalAlignment(JTextField.CENTER);
        panel.add(fileName, "align 50% 50%");

        JButton select = new JButton("Select");
        select.setName("JButton - Select");
        select.setFont(new Font("Lato", Font.PLAIN, 15));
        select.setForeground(Color.BLACK);
        select.setPreferredSize(new Dimension(80, 30));
        select.setMaximumSize(new Dimension(80, 30));
        select.setVerticalAlignment(JButton.CENTER);
        panel.add(select, "align 50% 50%");
        select.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Component c : window.getContentPane().getComponents())
                    if (c.getName().equals("JPanel - File chooser"))
                        for (Component cc : ((JPanel) c).getComponents())
                            if (cc.getName().equals("JLabel - Error"))
                                cc.setVisible(false);
                String name = openFileExplorer();
                if (!name.isEmpty()) {
                    fileName.setText(name);
                    for (Component c : window.getContentPane().getComponents())
                        if (c.getName().equals("JPanel - File chooser"))
                            for (Component cc : ((JPanel) c).getComponents())
                                if (cc.getName().equals("JLabel - Import"))
                                    cc.setVisible(true);
                }

            }
        });

        window.add(panel, "align 50% 50%, hidemode 3, dock north");

        addFileChooser();
        addImportScreen();
        beginImport(new File("C:\\Users\\Corey\\Downloads\\S10 - Free Checking.csv"));
    }

    private String openFileExplorer() {

        JFileChooser jfc = new JFileChooser(System.getProperty("user.home") + "/Downloads");
        jfc.setDialogTitle("Select a .csv file");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else return "";

    }

    private void addFileChooser() {
        JPanel panel = new JPanel(new MigLayout());
        panel.setName("JPanel - File chooser");

        JLabel l = new JLabel("<html><center><br><br><br><br><br></center></html>");
        l.setVerticalTextPosition(JLabel.BOTTOM);
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setVisible(false);
        l.setName("JLabel - Import");
        l.setIcon(new ImageIcon(new ImageIcon("resources/import_1.png").getImage().getScaledInstance(250, 50, Image.SCALE_DEFAULT)));
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Component c : window.getContentPane().getComponents())
                    if (c.getName().equals("JPanel - Select file"))
                        for (Component cc : ((JPanel) c).getComponents())
                            if (cc.getName().equals("JTextField - File name"))
                                beginImport(new File(((JTextField) cc).getText()));
            }
        });
        panel.add(l, "hidemode 3, dock north");

        JLabel l2 = new JLabel("<html><br>You must select a valid file</html>");
        l2.setName("JLabel - Error");
        l2.setFont(new Font("Lato", Font.ITALIC, 20));
        l2.setForeground(Color.RED);
        l2.setHorizontalAlignment(JLabel.CENTER);
        l2.setVisible(false);
        panel.add(l2, "hidemode 3, dock north");

        window.add(panel, "hidemode 3, align 50% 50%");
    }

    private void addImportScreen() {
        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));
        panel.setName("JPanel - Import screen");

        JTextField jtf = new JTextField();
        jtf.setName("JTextField");
        jtf.setPreferredSize(new Dimension((int) (window.getSize().width * .7), 30));
        jtf.setFont(new Font("Lato", Font.PLAIN, 15));
        jtf.setEditable(false);
        panel.add(jtf);

        ArrayList<String> cat = new ArrayList<>();
        cat.add("Select a category");
        try {
            ResultSet rs = new SQLConnector().select("SELECT DISTINCT childName FROM MonthBudget ORDER BY childName");
            while (rs.next()) {
                cat.add(rs.getString("childName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cat.add("<Add new category>");
        JComboBox<String> jcb = new JComboBox<>(cat.toArray(new String[0]));
        jcb.setName("JComboBox");
        jcb.setFont(new Font("Lato", Font.PLAIN, 18));
        jcb.setPreferredSize(new Dimension((int) (window.getSize().width * .3), 30));
        jcb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TRIGGERED BOIII");
            }
        });
        panel.add(jcb, "wrap");

        JProgressBar jpb = new JProgressBar(0, 100);
        jpb.setName("JProgressBar");
        jpb.setValue(0);
        jpb.setStringPainted(true);
        jpb.setPreferredSize(new Dimension(panel.getMaximumSize().width, 30));
        panel.add(jpb, "span 2, gapy 50");

        panel.setVisible(false);
        window.add(panel, "hidemode 3");
    }

    private void beginImport(File file) {
        Scanner inFile;
        try {
            inFile = new Scanner(file);

            for (Component c : window.getContentPane().getComponents()) {
                if (c.getName().equals("JPanel - File chooser"))
                    c.setVisible(false);
                if (c.getName().equals("JPanel - Select file"))
                    c.setVisible(false);
                if (c.getName().equals("JPanel - Import screen"))
                    c.setVisible(true);
            }
            inFile.nextLine();
            while (inFile.hasNextLine()) {
                String[] line = inFile.nextLine().split(",");
                parseCategory(line);
                addTransaction(line);
            }

        } catch (Exception e) {
            for (Component c : window.getContentPane().getComponents())
                if (c.getName().equals("JPanel - File chooser"))
                    for (Component cc : ((JPanel) c).getComponents())
                        if (cc.getName().equals("JLabel - Error"))
                            cc.setVisible(true);
        }

    }

    private void updateTextField(String text) {
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Import screen"))
                for (Component cc : ((JPanel) c).getComponents())
                    if(cc.getName().equals("JTextField"))
                        ((JTextField) cc).setText(text);
    }

    private String parseCategory(String[] line) {
        String[] des = line[1].split(" ");
        try {
            ResultSet rs = new SQLConnector().select("SELECT * FROM CategoryParser");
            while (rs.next()) {
                String knownDes = rs.getString("description");
                for(int i = 0; i < des.length; i++)
                    if(knownDes.contains(des[i])) {
                        return rs.getString("category");
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTextField(line[1]);
        return null;
    }

    private void addTransaction(String[] line) {

    }
}
