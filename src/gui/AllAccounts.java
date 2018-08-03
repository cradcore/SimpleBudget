package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.Grid;
import net.miginfocom.swing.*;
import sqlConnector.SQLConnector;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;

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
        window.setContentPane(new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow")));
        window.getContentPane().setBackground(Color.decode("#b3c5e5"));
        window.setVisible(true);

        addSideMenu();
        addTitle();
        addTopMenu();
        addNewTransaction();
        addTable();
    }

    private void addTitle() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setName("JPanel - Title");
        panel.setBackground(new Color(0,0,0,0));

        JLabel title = new JLabel("All Accounts:");
        title.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(title, "align center");

        window.add(panel, "dock north");
    }

    private void addSideMenu() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setName("JPanel - Side Menu");
        panel.setBackground(Color.decode("#8faadc"));

        sideMenuAddButton(panel, "Home", 1, 330, 130);
        sideMenuAddButton(panel, "All Accounts", 2, 330, 100);
        sideMenuAddButton(panel, "Budget", 3, 330, 100);
        sideMenuAddButton(panel, "Reports", 4, 330, 800);

        window.add(panel, "dock west");
    }

    private void sideMenuAddButton(JPanel panel, String name, int image, int width, int height) {
        Insets margins = new Insets(0, 0, 0, 0);
        JButton button = new JButton();
        button.setName(name);
        button.setMargin(margins);
        button.setIcon(new ImageIcon(new ImageIcon("resources/all_accounts-side_menu_" + image + ".png").getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT)));
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Lato", Font.BOLD, 60));
        panel.add(button, "wrap");

        sideMenuAddButtonListener(button);
    }

    private void sideMenuAddButtonListener(JButton button) {
        String name = button.getName();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println(name + " clicked");
                switch(name) {
                    case "Home":
                        new Home(window);
                        break;
                    case "All Accounts":
                        new AllAccounts(window);
                        break;
                    case "Budget":
                        break;
                    case "Reports":
                        break;
                }
            }
        });
    }

    private void addTopMenu() {
        Insets margins = new Insets(0, 0, 0, 0);
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0, hidemode 3", "grow"));
        panel.setName("JPanel - Top Menu");
        panel.setBackground(Color.decode("#547cc4"));
        CC constraints = new CC();
        constraints.alignX("center").spanX();
        constraints.wrap();

        JButton viewAccountButton =  new JButton();

        ResultSet rs = SQLConnector.select("SELECT DISTINCT accountName FROM Entry");
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("All Accounts");
        try {
            while (rs.next())
                categories.add(rs.getString("accountName"));
        } catch (Exception e) { e.printStackTrace(); }

        JComboBox<String> jcb = new JComboBox<String>(categories.toArray(new String[0]));
        jcb.setName("Top Menu - JComboBox");
        jcb.setFont(new Font ("Lato", Font.PLAIN, 22));
        jcb.setForeground(Color.WHITE);
        jcb.setBackground(Color.decode("#547cc4"));
        jcb.setBorder(new EmptyBorder(0, 30, 0, 0));
        jcb.setVisible(false);
        jcb.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if(jcb.getSelectedItem().toString().equals("All Accounts")) {
                    jcb.setVisible(false);
                    viewAccountButton.setVisible(true);
                }
                viewOneAccount(jcb.getSelectedItem().toString());
            }
        });
        panel.add(jcb);

        viewAccountButton.setVisible(true);
        viewAccountButton.setIcon(new ImageIcon(new ImageIcon("resources/all_accounts-top_menu_1.png").getImage()));
        viewAccountButton.setBorderPainted(false);
        viewAccountButton.setBorder(null);
        viewAccountButton.setContentAreaFilled(false);
        panel.add(viewAccountButton);
        viewAccountButton.setMargin(margins);
        viewAccountButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                jcb.setVisible(true);
                viewAccountButton.setVisible(false);
                jcb.showPopup();
                jcb.setPopupVisible(true);
            }
        });

        JButton addTransactionButton =  new JButton();
        addTransactionButton.setIcon(new ImageIcon(new ImageIcon("resources/all_accounts-top_menu_2.png").getImage()));
        addTransactionButton.setBorderPainted(false);
        addTransactionButton.setBorder(null);
        addTransactionButton.setContentAreaFilled(false);
        panel.add(addTransactionButton, "wrap");
        addTransactionButton.setMargin(margins);
        addTransactionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                toggleTransactionButtons();
            }
        });

        window.add(panel, "dock north");
    }

    private void toggleTransactionButtons() {
        for(Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - New Transaction"))
                c.setVisible(!c.isVisible());
            if (c.getName().equals("JPanel - New Transaction Buttons"))
                c.setVisible(!c.isVisible());
        }
        resetTextFields();
    }

    private void resetTextFields() {
        Component[] comp = null;
        for(Component c : window.getContentPane().getComponents())
            if(c.getName().equals("JPanel - New Transaction"))
                comp = ((JPanel) c).getComponents();
        ((JTextField) comp[0]).setText("Account");
        ((JTextField) comp[1]).setText("Date");
        ((JTextField) comp[2]).setText("Payee");
        ((JTextField) comp[3]).setText("Category");
        ((JTextField) comp[4]).setText("Memo");
        ((JTextField) comp[5]).setText("Outflow");
        ((JTextField) comp[6]).setText("Inflow");
    }

    private void addTable() {
        JPanel panel = new JPanel(new BorderLayout());


        panel.setBackground(new Color(0,0,0,0));
        panel.setName("JPanel - Table");

        String[] headings = {"Account", "Date", "Payee", "Category", "Memo", "Outflow", "Inflow"};
        String[][] data = getTableData(SQLConnector.select("SELECT * FROM Entry"));

        JTable jt = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                comp.setBackground(row % 2 == 0 ? Color.decode("#c6d3eb") : Color.decode("#ecf0f8"));
                return comp;
            }
        };

        jt.setModel(new DefaultTableModel());
        ((DefaultTableModel) jt.getModel()).setDataVector(data, headings);

        jt.setName("JTable");
        jt.getTableHeader().setFont(new Font("Lato", Font.BOLD, 17));
        jt.setFont(new Font("Lato", Font.PLAIN, 17));
        jt.setFillsViewportHeight(true);
        jt.setRowHeight(25);
        jt.getColumnModel().getColumn(0).setPreferredWidth(150);
        jt.getColumnModel().getColumn(1).setPreferredWidth(100);
        jt.getColumnModel().getColumn(2).setPreferredWidth(200);
        jt.getColumnModel().getColumn(3).setPreferredWidth(150);
        jt.getColumnModel().getColumn(4).setPreferredWidth(450);
        jt.getColumnModel().getColumn(5).setPreferredWidth(75);
        jt.getColumnModel().getColumn(6).setPreferredWidth(75);
        jt.setAutoCreateRowSorter(true);
        jt.getRowSorter().toggleSortOrder(1);

        JScrollPane jsp = new JScrollPane((jt));
        jsp.setName("Scroll Pane");

        panel.add(jsp, BorderLayout.CENTER);

        window.add(panel, "dock north, w 100%, h 100%, span, wrap");
    }

    private void addNewTransaction() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setName("JPanel - New Transaction");
        panel.setBackground(Color.decode("#547cc4"));
        panel.setPreferredSize(new Dimension(panel.getWidth(), 45));

        setTextFieldNewTransaction(panel, "Account", 150);
        setTextFieldNewTransaction(panel, "Date", 100);
        setTextFieldNewTransaction(panel, "Payee", 200);
        setTextFieldNewTransaction(panel, "Category", 150);
        setTextFieldNewTransaction(panel, "Memo", 450);
        setTextFieldNewTransaction(panel, "Outflow", 75);
        setTextFieldNewTransaction(panel, "Inflow", 75);

        panel.setVisible(false);
        window.add(panel, "dock north, hidemode 2");

        setButtonsNewTransaction();
    }

    private void setTextFieldNewTransaction(JPanel panel, String text, int width) {
        JTextField jtf = new JTextField(text);
        jtf.setFont(new Font("Lato", Font.PLAIN, 17));
        jtf.setPreferredSize(new Dimension(width, 30));
        jtf.setHorizontalAlignment(JTextField.CENTER);
        jtf.setBorder(null);
        if(text.equals("Inflow"))
            panel.add(jtf, "push, align center, wrap");
        else panel.add(jtf, "push, align center");
        jtf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(jtf.getText().equals(text))
                    jtf.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(jtf.getText().equals(""))
                    jtf.setText(text);
            }
        });
    }

    private void setButtonsNewTransaction() {
        JPanel panel = new JPanel(new MigLayout("", "[][][]push[]", ""));
        panel.setBackground(Color.decode("#547cc4"));
        panel.setName("JPanel - New Transaction Buttons");

        JButton buffer = new JButton();
        buffer.setBackground(new Color(0, 0, 0, 0));
        buffer.setPreferredSize(new Dimension(1200, 50));
        buffer.setBorderPainted(false);
        buffer.setBorder(null);
        buffer.setContentAreaFilled(false);
        panel.add(buffer, "align right");

        JLabel label = new JLabel("You must fill in all the above boxes");
        label.setName("Transaction error");
        label.setForeground(Color.RED);
        label.setFont(new Font("Lato", Font.BOLD | Font.ITALIC, 22));
        label.setVisible(false);
        panel.add(label, "hidemode 3");

        JButton cancel = new JButton();
        cancel.setIcon(new ImageIcon(new ImageIcon("resources/all_accounts-new_transaction-cancel.png").getImage().getScaledInstance(100, 59, Image.SCALE_DEFAULT)));
        cancel.setBorderPainted(false);
        cancel.setBorder(null);
        cancel.setContentAreaFilled(false);
        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label.setVisible(false);
                toggleTransactionButtons();
            }
        });
        panel.add(cancel, "align right");

        JButton save = new JButton();
        save.setIcon(new ImageIcon(new ImageIcon("resources/all_accounts-new_transaction-save.png").getImage().getScaledInstance(100, 59, Image.SCALE_DEFAULT)));
        save.setBorderPainted(false);
        save.setBorder(null);
        save.setContentAreaFilled(false);
        save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(saveTransaction())
                    toggleTransactionButtons();
            }
        });
        panel.add(save, "align right");

        panel.setVisible(false);
        window.add(panel, "dock north, hidemode 2");
    }

    private boolean saveTransaction() {
        JPanel jt = null;
        for(Component c : window.getContentPane().getComponents())
            if(c.getName().equals("JPanel - New Transaction"))
                jt = (JPanel) c;

        String entryID = getEntryID();
        String[] data = {((JTextField) jt.getComponent(0)).getText(), ((JTextField) jt.getComponent(1)).getText(),
                ((JTextField) jt.getComponent(2)).getText(), ((JTextField) jt.getComponent(3)).getText(),
                ((JTextField) jt.getComponent(4)).getText(), ((JTextField) jt.getComponent(5)).getText(),
                ((JTextField) jt.getComponent(6)).getText()};

        if(data[4].equals("Memo"))
            data[4] = "";

        if(!checkTransactionValidity(jt, data)) {
            for (Component c : window.getContentPane().getComponents())
                if (c.getName().equals("JPanel - New Transaction Buttons")) {
                    ((JLabel) ((JPanel) c).getComponent(1)).setVisible(true);
                    return false;
                }
        }
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - New Transaction Buttons"))
                ((JLabel) ((JPanel) c).getComponent(1)).setVisible(false);

        String[] date = data[1].split("/");
        int month = Integer.parseInt(date[0]);
        int day = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);

        SQLConnector.update("INSERT INTO `simpleBudget`.`Entry` (`entryID`, `accountName`, `dateDay`, " +
                "`dateMonth`, `dateYear`, `payee`, `childCategory`, `memo`, `outflow`, `inflow`) VALUES ('" +
                getEntryID() + "', '" + data[0] + "', " + day + ", " + month + ", " + year + ", '" + data[2] + "', '" +
                data[3] + "', '" + data[4] + "', " + data[5] + ", " + data[6] + ")");

        String[][] tableData = getTableData(SQLConnector.select("SELECT * FROM Entry"));
        String[] headings = {"Account", "Date", "Payee", "Category", "Memo", "Outflow", "Inflow"};

        for(Component c : window.getContentPane().getComponents())
            if(c.getName().equals("JPanel - Table"))
                ((DefaultTableModel) ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView())).getModel()).setDataVector(tableData, headings);
        return true;
    }

    private boolean checkTransactionValidity(JPanel panel, String[] data) {
        if(data[0].equals("Account") || data[1].equals("Date") || data[2].equals("Payee") || data[3].equals("Category")
                || data[5].equals("Outflow") || data[6].equals("Inflow"))
            return false;

        return true;
    }

    private String getEntryID() {
        String ret = "";

        for(int i = 0; i < 6; i++)
            ret += (int) (Math.random() * 10) + "";

        try {
            ResultSet rs = SQLConnector.select("SELECT * FROM Entry");
            while(rs.next()) {
                String id = rs.getString("entryID");
                if(id.equals(ret)) {
                    return getEntryID();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        return ret;
    }

    private String[][] getTableData(ResultSet rs) {
        ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
        try {
            while (rs.next()) {
                ArrayList<String> entry = new ArrayList<String>();
                entry.add(rs.getString("accountName"));
                entry.add(rs.getString("dateMonth") + "/" + rs.getString("dateDay")
                        + "/" + rs.getString("dateYear"));
                entry.add(rs.getString("payee"));
                entry.add(rs.getString("childCategory"));
                entry.add(rs.getString("memo"));
                entry.add(rs.getString("outflow"));
                entry.add(rs.getString("inflow"));
                entries.add(entry);
            }
        } catch(Exception e) { e.printStackTrace(); }

        String[][] data = new String[entries.size()][entries.get(0).size() + 1];
        for(int i = 0; i < entries.size(); i++)
            for(int j = 0; j < entries.get(i).size(); j++)
                data[i][j] = "0";
        for(int i = 0; i < entries.size(); i++)
            for(int j = 0; j < entries.get(i).size(); j++)
                if(j == 5 || j == 6) {
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    if(entries.get(i).get(j) == null)
                        data[i][j] = nf.format(0);
                    else
                        data[i][j] = nf.format(Double.parseDouble(entries.get(i).get(j)));
                }
                else data[i][j] = entries.get(i).get(j);

        return data;
    }

    private void viewOneAccount(String accountName) {
        String[][] tableData;
        if(accountName.equals("All Accounts"))
            tableData = getTableData(SQLConnector.select("SELECT * FROM Entry"));
        else tableData = getTableData(SQLConnector.select("SELECT * FROM Entry WHERE accountName = '" + accountName + "'"));
        String[] headings = {"Account", "Date", "Payee", "Category", "Memo", "Outflow", "Inflow"};

        for(Component c : window.getContentPane().getComponents())
            if(c.getName().equals("JPanel - Table"))
                ((DefaultTableModel) ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView())).getModel()).setDataVector(tableData, headings);;
    }
}
