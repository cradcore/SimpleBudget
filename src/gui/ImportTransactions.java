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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ImportTransactions {

    private JFrame mainWindow;
    private JFrame window;
    private Scanner inFile;
    private String line;
    private int totalLines = 0;
    private int csvSize = 0;
    private SQLConnector sql;

    public ImportTransactions(JFrame mainWindow) {
        this.mainWindow = mainWindow;
        sql = new SQLConnector();
        window = new JFrame();
        window.setBounds(500, 325, 800, 450);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                new AllAccounts(mainWindow, new SQLConnector());
            }
        });
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
        window.add(title, "dock north, hidemode 3");

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
        select.doClick();
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
            try {
                Scanner in = new Scanner(selectedFile);
                while(in.hasNextLine()) {
                    csvSize++;
                    in.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        JLabel l2 = new JLabel("<html><br>Name:</html>");
        JLabel l3 = new JLabel("<html><br>&nbsp &nbsp Category:</html>");
        JTextField jtf = new JTextField();
        JTextField jtf2 = new JTextField();
        JTextField jtf3 = new JTextField("Parent category");
        JTextField jtf4 = new JTextField("Child category");
        JButton jb = new JButton("Save");
        JComboBox<String> jcb = new JComboBox<>(getCategories());
        JProgressBar jpb = new JProgressBar(0, 100);

        l1.setName("JLabel - Instructions");
        l1.setFont(new Font("Lato", Font.PLAIN, 20));
        l1.setHorizontalAlignment(JLabel.CENTER);
        panel.add(l1, "wrap, span 2");

        jtf.setName("JTextField - Description");
        jtf.setPreferredSize(new Dimension((int) (window.getSize().width), 30));
        jtf.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf.setHorizontalAlignment(JTextField.CENTER);
        jtf.setEditable(false);
        jtf.setOpaque(true);
        jtf.setBackground(Color.decode("#ecf0f8"));
        jtf.setBorder(new LineBorder(Color.BLACK, 1));
        panel.add(jtf, "span 2, wrap");

        l2.setName("JLabel - Name");
        l2.setFont(new Font("Lato", Font.PLAIN, 18));
        panel.add(l2);

        l3.setName("JLabel - Category");
        l3.setFont(new Font("Lato", Font.PLAIN, 18));
        panel.add(l3, "wrap");

        jtf2.setName("JTextField - Name");
        jtf2.setPreferredSize(new Dimension((int) (window.getSize().width * .5), 30));
        jtf2.setFont(new Font("Lato", Font.PLAIN, 15));
        jtf2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!jtf2.getText().isEmpty())
                    jcb.setEnabled(true);
            }
        });
        panel.add(jtf2);

        jtf3.setName("JTextField - Parent category");
        jtf3.setForeground(Color.GRAY);
        jtf3.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf3.setPreferredSize(new Dimension((int) (window.getSize().width * .4), 30));
        jtf3.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jtf3.getText().equals("Parent category")) {
                    jtf3.setText("");
                    jtf3.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jtf3.getText().isEmpty()) {
                    jtf3.setText("Parent category");
                    jtf3.setForeground(Color.GRAY);
                }
            }
        });
        jtf3.setVisible(false);

        jtf4.setName("JTextField - Child category");
        jtf4.setForeground(Color.GRAY);
        jtf4.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf4.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jtf4.getText().equals("Child category")) {
                    jtf4.setText("");
                    jtf4.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jtf4.getText().isEmpty()) {
                    jtf4.setText("Child category");
                    jtf4.setForeground(Color.GRAY);
                }
            }
        });
        jtf4.setPreferredSize(jtf3.getPreferredSize());
        jtf4.setVisible(false);
        panel.add(jtf4, "al right, span 2, hidemode 3, wrap");

        panel.add(jtf3, "span 2, al right, wrap, hidemode 3");

        jb.setName("JButton");
        jb.setForeground(Color.BLACK);
        jb.setBackground(Color.decode("#b3c5e5"));
        jb.setVisible(false);
        jb.setPreferredSize(jtf3.getPreferredSize());
        jb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(jtf2.getText().isEmpty() || jtf3.getText().isEmpty() || jtf3.getText().equals("Parent category") || jtf4.getText().isEmpty() || jtf4.getText().equals("Child category")))
                    saveTransaction(jcb, jtf2, jtf3, jtf4, jb);
            }
        });
        panel.add(jb, "wrap, hidemode 3, al right, span 2");

        jcb.setName("JComboBox");
        jcb.setFont(new Font("Lato", Font.PLAIN, 18));
        jcb.setPreferredSize(new Dimension((int) (window.getSize().width * .4), 30));
        jcb.setBackground(Color.WHITE);
        jcb.setEnabled(false);
        jcb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jcb.getSelectedItem().toString().equals("<Add new category>")) {
                    jtf3.setVisible(true);
                    jtf4.setVisible(true);
                    jb.setVisible(true);
                    jcb.setVisible(false);
                    jtf2.requestFocus();
                } else if (jcb.getSelectedIndex() != 0) {
                    saveTransaction(jcb, jtf2, jtf3, jtf4, jb);
                }
            }
        });
        panel.add(jcb, "al right, wrap, hidemode 3");

        jpb.setName("JProgressBar");
        jpb.setValue(0);
        jpb.setStringPainted(true);
        jpb.setPreferredSize(new Dimension(panel.getMaximumSize().width, 30));
        panel.add(jpb, "span 2, gapy 50");

        panel.setVisible(false);
        window.add(panel, "hidemode 3");
    }

    private void saveTransaction(JComboBox<String> jcb, JTextField jtf2, JTextField jtf3, JTextField jtf4, JButton jb) {
        String des = line.split(",")[1],
                childCat = null,
                payee = jtf2.getText();
        if (jcb.getSelectedItem().toString().equals("<Add new category>"))
            childCat = jtf4.getText();
        else childCat = jcb.getSelectedItem().toString();
        sql.update("INSERT INTO `simpleBudget`.`CategoryParser` (`description`, `childCategory`, `payee`) " +
                "VALUES ('" + des + "', '" + childCat + "', '" + payee + "')");
        addTransaction(childCat, payee);
        processLine();
        jcb.setSelectedIndex(0);
        jcb.setEnabled(false);
        jcb.setVisible(true);
        jtf2.setText("");
        jtf2.requestFocus();
        jtf3.setText("Parent category");
        jtf3.setForeground(Color.GRAY);
        jtf3.setVisible(false);
        jtf4.setText("Child category");
        jtf4.setForeground(Color.GRAY);
        jtf4.setVisible(false);
        jb.setVisible(false);
    }

    private String[] getCategories() {
        ArrayList<String> cat = new ArrayList<>();
        cat.add("Select a category");
        try {
            ResultSet rs = sql.select("SELECT DISTINCT childName FROM MonthBudget ORDER BY childName");
            while (rs.next()) {
                cat.add(rs.getString("childName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cat.add("<Add new category>");
        return cat.toArray(new String[0]);
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
            totalLines++;
            line = inFile.nextLine();
            while(line.contains("\""))
                line = line.substring(0, line.indexOf("\"")) + line.substring(line.indexOf("\"") + 1, line.length());
            while(line.contains("'"))
                line = line.substring(0, line.indexOf("'")) + line.substring(line.indexOf("'") + 1, line.length());
            String[] catName = parseCategory(line.split(","));
            while (catName != null) {
                if (inFile.hasNextLine()) {
                    totalLines++;
                    addTransaction(catName[0], catName[1]);
                    line = inFile.nextLine();
                    while(line.contains("\""))
                        line = line.substring(0, line.indexOf("\"")) + line.substring(line.indexOf("\"") + 1, line.length());
                    while(line.contains("'"))
                        line = line.substring(0, line.indexOf("'")) + line.substring(line.indexOf("'") + 1, line.length());
                    catName = parseCategory(line.split(","));
                } else {
                    catName = null;
                    closeImportWindow();
                }
            }
        } else {
            closeImportWindow();
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
            ResultSet rs = sql.select("SELECT Entry.payee, B.childName FROM Entry LEFT JOIN MonthBudget B on Entry.catID = B.catID");
            while (rs.next()) {
                String payee = rs.getString("payee");
                if (des.toLowerCase().contains(payee.toLowerCase())) {
                    String[] s = {rs.getString("childName"), payee};
                    return s;
                }
            }
            rs = sql.select("SELECT * FROM CategoryParser WHERE description = '" + des + "'");
            if (rs.next()) {
                String[] s = {rs.getString("childCategory"), rs.getString("payee")};
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTextField(line[1]);
        return null;
    }

    private void addTransaction(String cat, String name) {
        getProgressBar().setValue((int) ((Double.parseDouble(totalLines + "") / Double.parseDouble(csvSize + "")) * 100));
        getProgressBar().setStringPainted(true);
        String[]lineArr = line.split(",");
        String id = AllAccounts.getEntryID();
        String acc = "Debit card";
        String[] date = lineArr[0].split("/");
        String catID = getCatID(cat, date[0], date[2]);
        String memo = lineArr[2];
        String inflow = null;
        String outflow = null;
        if (lineArr[4].charAt(0) == '(') {
            inflow = "0.00";
            outflow = lineArr[4].substring(2, lineArr[4].length() - 1);
        } else {
            inflow = lineArr[4].substring(2, lineArr[4].length() - 1);
            outflow = "0.00";
        }
        try {
            ResultSet rs = sql.select("SELECT * FROM Entry WHERE accountName = '" + acc + "' AND dateDay = " +
                    date[1] + " AND dateMonth = " + date[0] + " AND dateYear = " + date[2] + " AND payee = '" + name + "' AND catID = '" + catID +
                    "' AND memo = '" + memo + "' AND outflow = " + outflow + " AND inflow = " + inflow);
            if (rs.next()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String update = "INSERT INTO `simpleBudget`.`Entry` (`entryID`, `accountName`, `dateDay`, " +
                "`dateMonth`, `dateYear`, `payee`, `catID`, `memo`, `outflow`, `inflow`) VALUES ('" +
                id + "', '" + acc + "', " + date[1] + ", " + date[0] + ", " + date[2] + ", '" + name + "', '" + catID +
                "', '" + memo + "', " + outflow + ", " + inflow + ")";
        sql.update(update);
    }

    private String getCatID(String catName, String dateMonth, String dateYear) {
        String catID = null;
        ResultSet rs = sql.select("SELECT * FROM MonthBudget WHERE dateMonth = " + dateMonth + " AND dateYear = " + dateYear + " AND childName = '" + catName + "'");
        try {
            if (rs.next())
                catID = rs.getString("catID");
            else {
                JComboBox<String> jcb = null;
                JTextField jtf2 = null,
                        jtf3 = null,
                        jtf4 = null;
                for (Component c : window.getContentPane().getComponents()) {
                    if (c.getName().equals("JPanel - Import screen"))
                        for (Component cc : ((JPanel) c).getComponents()) {
                            if (cc.getName().equals("JTextField - Name"))
                                jtf2 = ((JTextField) cc);
                            else if (cc.getName().equals("JTextField - Child category"))
                                jtf3 = ((JTextField) cc);
                            else if (cc.getName().equals("JTextField - Parent category"))
                                jtf4 = ((JTextField) cc);
                            else if (cc.getName().equals("JComboBox"))
                                jcb = ((JComboBox<String>) cc);
                        }
                }
                String childCat = null,
                        parCat = null,
                        payee = null;
                payee = jtf2.getText();
                if (jcb.getSelectedItem().toString().equals("<Add new category>")) {
                    childCat = jtf3.getText();
                    parCat = jtf4.getText();
                } else {
                    childCat = jcb.getSelectedItem().toString();
                    try {
                        rs = sql.select("SELECT * FROM MonthBudget WHERE childName = '" + childCat + "'");
                        if (!rs.next())
                            throw new SQLException();
                        parCat = rs.getString("parentName");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sql.update("INSERT INTO MonthBudget(`catID`, `dateMonth`, `dateYear`, `childName`, `parentName`, `budgeted`) VALUES " +
                        "('" + Budget.getNewCatID() + "', " + dateMonth + ", " + dateYear + ", '" + childCat + "', '" + parCat + "', 0.00);");
                rs = sql.select("SELECT * FROM MonthBudget WHERE dateMonth = " + dateMonth + " AND dateYear = " + dateYear +
                " AND childName = '" + childCat + "' AND parentName = '" + parCat + "'");
                if(!rs.next())
                    throw new SQLException();
                catID = rs.getString("catID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return catID;
    }

    private void closeImportWindow() {
        JPanel panel = new JPanel(new MigLayout());
        JLabel l = new JLabel("<html><center>Successfully processed " + totalLines + " transactions!</center></html>");
        l.setForeground(Color.RED);
        l.setFont(new Font("Lato", Font.PLAIN, 25));
        l.setHorizontalAlignment(JLabel.CENTER);
        panel.add(l, "al center");
        for (Component c : window.getContentPane().getComponents())
            c.setVisible(false);
        window.add(panel, "dock north, al center, hidemode 3");
        try {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                }
            }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JProgressBar getProgressBar() {
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Import screen"))
                for (Component cc : ((JPanel) c).getComponents())
                    if (cc.getName().equals("JProgressBar"))
                        return ((JProgressBar) cc);
        return null;
    }
}
