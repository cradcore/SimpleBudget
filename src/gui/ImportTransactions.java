package gui;

import com.sun.java.swing.plaf.motif.MotifInternalFrameTitlePane;
import net.miginfocom.swing.MigLayout;
import sqlConnector.SQLConnector;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class ImportTransactions {

    private JFrame mainWindow;
    private JFrame window;
    private Scanner inFile;
    private String line;

    public ImportTransactions(JFrame mainWindow) {
        this.mainWindow = mainWindow;
        window = new JFrame();
        window.setBounds(500, 325, 800, 450);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setContentPane(new JPanel(new MigLayout("gap rel 0", "grow")));
        window.getContentPane().setName("Popup window content pane");
        window.setVisible(true);
        window.setTitle("Import Transactions");
        window.getContentPane().setBackground(Color.decode("#ecf0f8"));


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
        panel.setOpaque(false);
        panel.setPreferredSize(window.getSize());

        JLabel l1 = new JLabel("<htmL><center>Simple Budget was unable to match all of the imported transactions to a category. " +
                "Please select a category for each of the entries listed below.<br><hr></center></html>");
        l1.setName("JLabel - Instructions");
        l1.setFont(new Font("Lato", Font.PLAIN, 20));
        l1.setHorizontalAlignment(JLabel.CENTER);
        panel.add(l1, "wrap, span 2");

        JTextField jtf = new JTextField();
        jtf.setName("JTextField - Description");
        jtf.setPreferredSize(new Dimension((int) (window.getSize().width), 30));
        jtf.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf.setHorizontalAlignment(JTextField.CENTER);
        jtf.setEditable(false);
        jtf.setOpaque(true);
        jtf.setBackground(Color.decode("#ecf0f8"));
        jtf.setBorder(new LineBorder(Color.BLACK, 1));
        panel.add(jtf, "span 2, wrap");

        JLabel l2 = new JLabel("<html><br>Name:</html>");
        l2.setName("JLabel - Name");
        l2.setFont(new Font("Lato", Font.PLAIN, 18));
        panel.add(l2);

        JLabel l3 = new JLabel("<html><br>Category:</html>");
        l3.setName("JLabel - Category");
        l3.setFont(new Font("Lato", Font.PLAIN, 18));
        panel.add(l3, "wrap");

        JTextField jtf2 = new JTextField();
        jtf2.setName("JTextField - Name");
        jtf2.setPreferredSize(new Dimension((int) (window.getSize().width * .6), 30));
        jtf2.setFont(new Font("Lato", Font.PLAIN, 15));
        panel.add(jtf2);

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
        jcb.setPreferredSize(new Dimension((int) (window.getSize().width * .4), 30));
        jcb.setBackground(Color.WHITE);
        jcb.setEnabled(false);
        jcb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction(jcb.getSelectedItem().toString(), jtf2.getText());
                processLine();
                jcb.setSelectedIndex(0);
                jcb.setEnabled(false);
                jtf2.setText("");
                jtf2.requestFocus();
            }
        });
        panel.add(jcb, "wrap");

        jtf2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!jtf2.getText().isEmpty())
                    jcb.setEnabled(true);
            }
        });

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
            if (inFile.hasNextLine())
                inFile.nextLine();
            processLine();

        } catch (Exception e) {
            for (Component c : window.getContentPane().getComponents())
                if (c.getName().equals("JPanel - File chooser"))
                    for (Component cc : ((JPanel) c).getComponents())
                        if (cc.getName().equals("JLabel - Error"))
                            cc.setVisible(true);
        }

    }


    private void processLine() {
        if (inFile.hasNextLine()) {
            line = inFile.nextLine();
            String[] catName = parseCategory(line.split(","));
            while (catName != null) {
                if(inFile.hasNextLine()) {
                    addTransaction(catName[0], catName[1]);
                    line = inFile.nextLine();
                    catName = parseCategory(line.split(","));
                }
                break;
            }
        } else {
            System.out.println("DONE BOIII");
        }
    }

    private void updateTextField(String text) {
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Import screen"))
                for (Component cc : ((JPanel) c).getComponents())
                    if (cc.getName().equals("JTextField - Description")) {
                        ((JTextField) cc).setText(text);
                        return;
                    }

    }

    private String[] parseCategory(String[] line) {
        String des = line[1];
        try {
            ResultSet rs = new SQLConnector().select("SELECT Entry.payee, B.childName FROM Entry LEFT JOIN MonthBudget B on Entry.catID = B.catID");
            while (rs.next()) {
                String payee = rs.getString("payee");
                if (des.toLowerCase().contains(payee.toLowerCase())) {
                    String[] s = {rs.getString("childName"), payee};
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTextField(line[1]);
        return null;
    }

    private void addTransaction(String cat, String name) {
        System.out.println("(" + cat + ", " + name + ")\t\t\t\t\t\t\t\t" + line);
        String id = AllAccounts.getEntryID();
        String acc = "Debit card";
        String[] date = line.split(",")[0].split("//");
        String catID = null;

    }
}
