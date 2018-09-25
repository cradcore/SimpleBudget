package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.*;
import sqlConnector.SQLConnector;

import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

class AllAccounts {

    private JFrame window;
    private SQLConnector sql;

    // Constructor
    AllAccounts(JFrame window, SQLConnector sql) {
        this.window = window;
        this.sql = sql;
        initialize();
        window.setVisible(true);
    }

    // Initialize the contents of the frame.
    private void initialize() {
        window.setTitle("Simple Budget - All Accounts");
        window.getContentPane().removeAll();
        window.setContentPane(new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow")));
        window.getContentPane().setBackground(Color.decode("#b3c5e5"));
        window.setVisible(true);
        window.getContentPane().revalidate();
        window.getContentPane().repaint();

        Home.addSideMenu(window, "All Accounts", sql);
        Home.addTitle(window, "All Accounts:");
        addTopMenu();
        addNewTransaction();
        addTable();
    }

    private void addTopMenu() {
        Insets margins = new Insets(0, 0, 0, 0);
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0, hidemode 3", "grow"));
        panel.setName("JPanel - Top Menu");
        panel.setBackground(Color.decode("#547cc4"));
        CC constraints = new CC();
        constraints.alignX("center").spanX();
        constraints.wrap();

        JButton viewAccountButton = new JButton();

        ResultSet rs = sql.select("SELECT DISTINCT accountName FROM Entry");
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All Accounts");
        try {
            while (rs != null && rs.next())
                categories.add(rs.getString("accountName"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JComboBox<String> jcb = new JComboBox<>(categories.toArray(new String[0]));
        jcb.setName("Top Menu - JComboBox");
        jcb.setFont(new Font("Lato", Font.PLAIN, 22));
        jcb.setForeground(Color.WHITE);
        jcb.setBackground(Color.decode("#547cc4"));
        jcb.setBorder(new EmptyBorder(0, 30, 0, 0));
        jcb.setVisible(false);
        jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Objects.requireNonNull(jcb.getSelectedItem()).toString().equals("All Accounts")) {
                    jcb.setVisible(false);
                    viewAccountButton.setVisible(true);
                }
                viewOneAccount(jcb.getSelectedItem().toString());
            }
        });
        panel.add(jcb);

        viewAccountButton.setVisible(true);
        viewAccountButton.setIcon(new ImageIcon("resources/all_accounts-top_menu_1.png"));
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

        JButton addTransactionButton = new JButton();
        addTransactionButton.setIcon(new ImageIcon("resources/all_accounts-top_menu_2.png"));
        addTransactionButton.setBorderPainted(false);
        addTransactionButton.setBorder(null);
        addTransactionButton.setContentAreaFilled(false);
        panel.add(addTransactionButton);
        addTransactionButton.setMargin(margins);
        addTransactionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                toggleTransactionButtons(true);
                for(Component c : window.getContentPane().getComponents())
                    if(c.getName().equals("JPanel - New Transaction"))
                        for(Component cc : ((JPanel) c).getComponents())
                            if(cc.getName().equals("JComboBox - Categories"))
                                ((JComboBox<String>) cc).setSelectedIndex(0);
            }
        });

        JButton editTransactionButton = new JButton();
        editTransactionButton.setIcon(new ImageIcon("resources/all_accounts-top_menu_3.png"));
        editTransactionButton.setBorderPainted(false);
        editTransactionButton.setBorder(null);
        editTransactionButton.setContentAreaFilled(false);
        panel.add(editTransactionButton, "wrap");
        editTransactionButton.setMargin(margins);
        editTransactionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                boolean transactionSelected = false;
                for (Component c : window.getContentPane().getComponents())
                    if (c.getName().equals("JPanel - Table"))
                        if (((JTable) ((JScrollPane) ((JPanel) c).getComponent(0)).getViewport().getView()).getSelectedRow() != -1)
                            transactionSelected = true;

                for (Component c : window.getContentPane().getComponents())
                    if (c.getName().equals("JPanel - Top Menu"))
                        for (Component cc : ((JPanel) c).getComponents())
                            if (cc.getName() != null && cc.getName().equals("Edit transaction error")) {
                                if (!transactionSelected)
                                    cc.setVisible(true);
                                else cc.setVisible(false);
                            }

                if (transactionSelected)
                    toggleTransactionButtons(false);
            }
        });

        JLabel transLabel = new JLabel("    You must select a transaction to edit it");
        transLabel.setName("Edit transaction error");
        transLabel.setVisible(false);
        transLabel.setForeground(Color.RED);
        transLabel.setFont(new Font("Lato", Font.BOLD | Font.ITALIC, 22));
        panel.add(transLabel, "hidemode 3, spanx 3");

        window.add(panel, "dock north");
    }

    private void toggleTransactionButtons(boolean newTransaction) {
        JScrollPane jsp = null;

        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - New Transaction"))
                c.setVisible(!c.isVisible());
            if (c.getName().equals("JPanel - New Transaction Buttons"))
                c.setVisible(!c.isVisible());
            if (c.getName().equals("JPanel - Table"))
                jsp = ((JScrollPane) ((JPanel) c).getComponent(0));
        }
        if (newTransaction)
            resetTextFields();
        else
            fillTextFieldsWithRow((JTable) Objects.requireNonNull(jsp).getViewport().getView());
    }

    private void fillTextFieldsWithRow(JTable jt) {
        if (jt.getSelectedRow() == -1) {
            System.out.println("MUST HAVE ROW SELECTED");
            return;
        }
        ArrayList<String> row = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            row.add(((String) jt.getValueAt(jt.getSelectedRow(), i)));

        Component[] comp = null;
        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - New Transaction"))
                comp = ((JPanel) c).getComponents();
        }

        ((JTextField) Objects.requireNonNull(comp)[1]).setText(row.get(0));
        ((DatePicker) comp[2]).setDate(LocalDate.parse(row.get(1), DateTimeFormatter.ofPattern("M/d/yyyy")));
        ((JTextField) comp[3]).setText(row.get(2));
        String cat = row.get(3);
        ArrayList<String> catArr = new ArrayList<>();
        for (int i = 0; i < ((JComboBox<String>) comp[4]).getItemCount(); i++)
            catArr.add(((JComboBox<String>) comp[4]).getItemAt(i).split(" \\(")[0]);
        ((JComboBox<String>) comp[4]).setSelectedIndex(catArr.indexOf(cat));
        ((JTextField) comp[5]).setText(row.get(4));
        ((JTextField) comp[6]).setText(row.get(5));
        ((JTextField) comp[7]).setText(row.get(6));
        String[] date = comp[2].toString().split("-");
        String out = ((JTextField) comp[6]).getText();
        String in = ((JTextField) comp[7]).getText();
        for (int i = 0; i < out.length(); i++)
            if (out.charAt(i) == ',')
                out = out.substring(0, i) + out.substring(i + 1, out.length() - 1);
        for (int i = 0; i < in.length(); i++)
            if (in.charAt(i) == ',')
                in = in.substring(0, i) + in.substring(i + 1, in.length() - 1);

        String select = "SELECT * FROM Entry WHERE accountName = '" + ((JTextField) comp[1]).getText() +
                "' AND dateDay = " + date[2] + " AND dateMonth = " + date[1] + " AND dateYear = " + date[0] +
                " AND outflow = " + out.substring(1) + " AND inflow = " + in.substring(1);

        ResultSet rs = sql.select(select);
        String entryID = null;
        try {
            Objects.requireNonNull(rs).next();
            entryID = rs.getString("entryID");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - New Transaction"))
                for (Component cc : ((JPanel) c).getComponents())
                    if (cc.getName().equals("New Transaction - Entry ID"))
                        ((JLabel) cc).setText(entryID);
    }

    private void resetTextFields() {
        Component[] comp = null;
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - New Transaction"))
                comp = ((JPanel) c).getComponents();
        ((JTextField) Objects.requireNonNull(comp)[1]).setText("Account");
        ((DatePicker) comp[2]).setDate(LocalDate.now());
        ((JTextField) comp[3]).setText("Payee");
        ((JTextField) comp[5]).setText("Memo");
        ((JTextField) comp[6]).setText("Outflow");
        ((JTextField) comp[7]).setText("Inflow");
    }

    private void addTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setName("JPanel - Table");

        String[] headings = {"Account", "Date", "Payee", "Category", "Memo", "Outflow", "Inflow"};
        String[][] data = getTableData(sql.select("SELECT * FROM Entry"));

        JTable jt = new JTable(data, headings) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                if (isRowSelected(row))
                    comp.setBackground(Color.decode("#7b99d1"));
                else
                    comp.setBackground(row % 2 == 0 ? Color.decode("#c6d3eb") : Color.decode("#ecf0f8"));

                return comp;
            }
        };
        jt.setName("JTable");
        jt.setModel(new DefaultTableModel(data, headings) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

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

        JScrollPane jsp = new JScrollPane(jt);
        jsp.setName("Scroll Pane");

        panel.add(jsp, BorderLayout.CENTER);

        window.add(panel, "dock north, w 100%, h 100%, span, wrap");
    }

    private void addNewTransaction() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setName("JPanel - New Transaction");
        panel.setBackground(Color.decode("#547cc4"));
        panel.setPreferredSize(new Dimension(panel.getWidth(), 45));

        addAccountDropdown(panel);                                              // Account dropdown
        setTextFieldNewTransaction(panel, "Account", 150);            // Account text field
        addDateDropdown(panel);                                                 // Date dropdown
        setTextFieldNewTransaction(panel, "Payee", 180);              // Payee text field
        addCategoryDropDown(panel);                                             // Category dropdown
        setTextFieldNewTransaction(panel, "Memo", 450);               // Memo text field
        setTextFieldNewTransaction(panel, "Outflow", 75);             // Outflow text field
        setTextFieldNewTransaction(panel, "Inflow", 75);              // Inflow text field

        JLabel label = new JLabel();                                            // Entry ID
        label.setName("New Transaction - Entry ID");
        label.setVisible(false);
        panel.add(label);

        panel.setVisible(false);
        window.add(panel, "dock north, hidemode 2");

        setButtonsNewTransaction();
    }

    private void addCategoryDropDown(JPanel panel) {
        ResultSet rs = sql.select("SELECT * FROM MonthBudget GROUP BY childName ORDER BY parentName, childName");
        ArrayList<String> cats = new ArrayList<>();
        try {
            while (rs.next())
                cats.add(rs.getString("childName") + " (" + rs.getString("parentName") + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JComboBox<String> jcb = new JComboBox<>(cats.toArray(new String[0]));
        jcb.setName("JComboBox - Categories");
        jcb.setFont(new Font("Lato", Font.PLAIN, 15));
        jcb.setPreferredSize(new Dimension(50, 33));
        jcb.setForeground(Color.BLACK);
        panel.add(jcb, "push, align center, hidemode 3");
    }

    private void addAccountDropdown(JPanel panel) {
        ResultSet rs = sql.select("SELECT DISTINCT accountName FROM Entry");
        ArrayList<String> categories = new ArrayList<>();
        try {
            while (Objects.requireNonNull(rs).next())
                categories.add(rs.getString("accountName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        categories.add("<Add New Account>");

        JComboBox<String> jcb = new JComboBox<>(categories.toArray(new String[0]));
        jcb.setName("Account Dropdown");
        jcb.setFont(new Font("Lato", Font.PLAIN, 14));
        jcb.setPreferredSize(new Dimension(100, 33));
        jcb.setVisible(true);
        jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (Objects.requireNonNull(jcb.getSelectedItem()).equals("<Add New Account>"))
                    addNewAccount(panel);
            }
        });
        panel.add(jcb, "hidemode 3");

    }

    private void addNewAccount(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c.getName() != null && c.getName().equals("Account Dropdown"))
                c.setVisible(false);
            if (c.getName() != null && c.getName().equals("New Transaction - Account"))
                c.setVisible(true);
        }
    }

    private void addDateDropdown(JPanel panel) {
        DatePickerSettings ds;
        DatePicker date;
        ds = new DatePickerSettings();
        ds.setFormatForDatesCommonEra("MM/dd/yyy");
        date = new DatePicker(ds);
        ds.setFontCalendarDateLabels(new Font("Lato", Font.PLAIN, 22));
        ds.setFontCalendarWeekdayLabels(new Font("Lato", Font.PLAIN, 22));
        ds.setFontClearLabel(new Font("Lato", Font.PLAIN, 22));
        ds.setFontMonthAndYearMenuLabels(new Font("Lato", Font.PLAIN, 22));
        ds.setFontMonthAndYearNavigationButtons(new Font("Lato", Font.PLAIN, 22));
        ds.setFontTodayLabel(new Font("Lato", Font.PLAIN, 22));
        ds.setFontValidDate(new Font("Lato", Font.PLAIN, 13));
        date.setForeground(Color.GRAY);
        date.setPreferredSize(new Dimension(80, 33));
        date.setDateToToday();
        date.setName("New Transaction - Date");
        panel.add(date);

    }

    private void setTextFieldNewTransaction(JPanel panel, String text, int width) {
        JTextField jtf = new JTextField(text);
        jtf.setFont(new Font("Lato", Font.PLAIN, 17));
        jtf.setPreferredSize(new Dimension(width, 30));
        jtf.setHorizontalAlignment(JTextField.CENTER);
        jtf.setBorder(null);
        jtf.setName("New Transaction - " + text);
        if (text.equals("Inflow"))
            panel.add(jtf, "push, align center, hidemode 3, wrap");
        else panel.add(jtf, "push, align center, hidemode 3");
        jtf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jtf.getText().equals(text))
                    jtf.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jtf.getText().equals(""))
                    jtf.setText(text);
            }
        });
        if (jtf.getName().equals("New Transaction - Account"))
            jtf.setVisible(false);
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
                toggleTransactionButtons(true);
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
                if (saveTransaction())
                    toggleTransactionButtons(true);
            }
        });
        panel.add(save, "align right");

        panel.setVisible(false);
        window.add(panel, "dock north, hidemode 2");
    }

    private boolean saveTransaction() {
        JPanel jt = null;
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - New Transaction"))
                jt = (JPanel) c;
        String date = null;
        for (Component c : Objects.requireNonNull(jt).getComponents())
            if (c.getName().equals("New Transaction - Date"))
                date = ((DatePicker) c).getDateStringOrEmptyString();

        String account;
        if (((JTextField) jt.getComponent(1)).getText().equals("Account"))
            account = ((JComboBox<String>) jt.getComponent(0)).getSelectedItem().toString();
        else account = ((JTextField) jt.getComponent(1)).getText();
        String cat = ((JComboBox<String>) jt.getComponent(4)).getSelectedItem().toString().split(" \\(")[0];
        String[] data = {account, date, ((JTextField) jt.getComponent(3)).getText(),
                cat, ((JTextField) jt.getComponent(5)).getText(),
                ((JTextField) jt.getComponent(6)).getText(), ((JTextField) jt.getComponent(7)).getText()};
        if (data[5].charAt(0) == '$')
            data[5] = data[5].substring(1);
        if (data[6].charAt(0) == '$')
            data[6] = data[6].substring(1);
        if (data[4].equals("Memo"))
            data[4] = "";

        if (!checkTransactionValidity(jt, data))
            for (Component c : window.getContentPane().getComponents())
                if (c.getName().equals("JPanel - New Transaction Buttons")) {
                    ((JLabel) ((JPanel) c).getComponent(1)).setVisible(true);
                    return false;
                }

        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - New Transaction Buttons"))
                ((JLabel) ((JPanel) c).getComponent(1)).setVisible(false);

        String[] d = date.split("-");
        String entryID = null;
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - New Transaction"))
                for (Component cc : ((JPanel) c).getComponents())
                    if (cc.getName().equals("New Transaction - Entry ID"))
                        entryID = ((JLabel) cc).getText();

        String catID = "ERROR";
        try {
            String command = "SELECT * FROM MonthBudget WHERE childName = '" + data[3] + "' AND " +
                    "dateYear = " + d[0] + " AND dateMonth = " + d[1];
            ResultSet rs = sql.select(command);
            if (rs.next())
                catID = rs.getString("catID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkIfEdit(entryID)) {
            String command = "UPDATE Entry SET accountName = '" + data[0] + "',  dateDay = " + d[2] +
                    ", dateMonth = " + d[1] + ", dateYear = " + d[0] + ", payee = '" + data[2] + "', catID = '" +
                    catID + "', memo = '" + data[4] + "', outflow = " + data[5] + ", inflow = " + data[6] + " WHERE entryID = " +
                    entryID;
            sql.update(command);
            System.out.println(command);
        } else sql.update("INSERT INTO `simpleBudget`.`Entry` (`entryID`, `accountName`, `dateDay`, " +
                "`dateMonth`, `dateYear`, `payee`, `catID`, `memo`, `outflow`, `inflow`) VALUES ('" +
                getEntryID() + "', '" + data[0] + "', " + Integer.parseInt(d[2]) + ", " + Integer.parseInt(d[1]) +
                ", " + Integer.parseInt(d[0]) + ", '" + data[2] + "', '" + catID + "', '" + data[4] + "', " +
                data[5] + ", " + data[6] + ")");

        String[][] tableData = getTableData(sql.select("SELECT * FROM Entry"));
        String[] headings = {"Account", "Date", "Payee", "Category", "Memo", "Outflow", "Inflow"};

        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - Table"))
                ((DefaultTableModel) ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView())).getModel()).setDataVector(tableData, headings);
            if (c.getName().equals("JPanel - New Transaction"))
                for (Component cc : ((JPanel) c).getComponents()) {
                    String ccName = cc.getName();
                    if (ccName.equals("Account Dropdown")) {
                        ((JComboBox<String>) cc).addItem(data[0]);
                        cc.setVisible(true);
                        cc.repaint();
                        cc.validate();
                    }
                    if (ccName.equals("New Transaction - Account"))
                        cc.setVisible(false);
                }
            c.repaint();
            c.validate();
        }
        return true;
    }

    private boolean checkIfEdit(String entryID) {
        ResultSet rs = sql.select("SELECT * FROM Entry WHERE entryID = '" + entryID + "'");
        try {
            if (rs.next())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkTransactionValidity(JPanel panel, String[] data) {
        if (data[0].equals("Account") || data[1].equals("Date") || data[2].equals("Payee") || data[3].equals("Category")
                || data[5].equals("Outflow") || data[6].equals("Inflow"))
            return false;
        return true;
    }

    private String getEntryID() {
        String ret = "";
        for (int i = 0; i < 6; i++)
            ret += (int) (Math.random() * 10) + "";
        try {
            ResultSet rs = sql.select("SELECT * FROM Entry");
            while (rs.next()) {
                String id = rs.getString("entryID");
                if (id.equals(ret)) {
                    return getEntryID();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String[][] getTableData(ResultSet rs) {
        ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
        try {
            while (rs.next()) {
                ArrayList<String> entry = new ArrayList<>();
                entry.add(rs.getString("accountName"));
                entry.add(rs.getString("dateMonth") + "/" + rs.getString("dateDay")
                        + "/" + rs.getString("dateYear"));
                entry.add(rs.getString("payee"));
                String catID = rs.getString("catID");
                ResultSet rs2 = new SQLConnector().select("SELECT * FROM MonthBudget WHERE catID = '" + catID + "'");
                if (rs2.next())
                    entry.add(rs2.getString("childName"));
                entry.add(rs.getString("memo"));
                entry.add(rs.getString("outflow"));
                entry.add(rs.getString("inflow"));
                entries.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[][] data = new String[entries.size()][entries.get(0).size() + 1];
        for (int i = 0; i < entries.size(); i++)
            for (int j = 0; j < entries.get(i).size(); j++)
                data[i][j] = "0";
        for (int i = 0; i < entries.size(); i++)
            for (int j = 0; j < entries.get(i).size(); j++)
                if (j == 5 || j == 6) {
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    if (entries.get(i).get(j) == null)
                        data[i][j] = nf.format(0);
                    else
                        data[i][j] = nf.format(Double.parseDouble(entries.get(i).get(j)));
                } else data[i][j] = entries.get(i).get(j);
        return data;
    }

    private void viewOneAccount(String accountName) {
        String[][] tableData;
        if (accountName.equals("All Accounts"))
            tableData = getTableData(sql.select("SELECT * FROM Entry"));
        else
            tableData = getTableData(sql.select("SELECT * FROM Entry WHERE accountName = '" + accountName + "'"));
        String[] headings = {"Account", "Date", "Payee", "Category", "Memo", "Outflow", "Inflow"};
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Table"))
                ((DefaultTableModel) ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView())).getModel()).setDataVector(tableData, headings);
    }
}
