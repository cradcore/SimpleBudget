package gui;

import net.miginfocom.swing.MigLayout;
import sqlConnector.SQLConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unchecked")
class Budget {

    // Class variables
    private JFrame window;
    private SQLConnector sql;

    // Constructor
    Budget(JFrame window, SQLConnector sql) {
        this.window = window;
        this.sql = sql;
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

        Home.addSideMenu(window, "Budget", sql);
        Home.addTitle(window, "Budget:");
        addTopMenu();
        addTable();
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
                year.setText("  " + (Integer.parseInt(year.getText().substring(2, 6)) - 1) + "  ");
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
                year.setText("  " + (Integer.parseInt(year.getText().substring(2, 6)) + 1) + "  ");
            }
        });
        panel.add(rightArrow);

        addTopMenuMonths(panel);

        window.add(panel, "dock north");
    }

    private void resetMonthColors(JPanel panel) {
        for (Component c : panel.getComponents())
            if (c.getName().length() >= 5 && c.getName().substring(0, 5).equals("Month"))
                c.setBackground(Color.decode("#547dc4"));
    }

    private void addTopMenuMonths(JPanel panel) {
        for (int i = 0; i < 12; i++) {
            LocalDate ld;
            if (i < 9)
                ld = LocalDate.parse(LocalDate.now().getYear() + "-0" + (i + 1) + "-01");
            else
                ld = LocalDate.parse(LocalDate.now().getYear() + "-" + (i + 1) + "-01");
            JLabel month = new JLabel("  " + ld.getMonth().toString().substring(0, 3) + "  ");
            month.setFont(new Font("Lato", Font.BOLD, 25));
            month.setForeground(Color.WHITE);
            month.setOpaque(true);
            if (ld.getMonth().equals(LocalDate.now().getMonth()))
                month.setBackground(Color.decode("#345998"));
            else month.setBackground(Color.decode("#547dc4"));
            month.setName("Month - " + ld.getMonth().toString().substring(0, 3));
            month.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    resetMonthColors(panel);
                    month.setBackground(Color.decode("#345998"));
                    for (Component c : window.getContentPane().getComponents())
                        if (c.getName().equals("JPanel - Table")) {
                            JTable jt = ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView()));
                            DefaultTableModel dtm = ((DefaultTableModel) jt.getModel());
                            String[][] data = getTableData();
                            String[] headings = {"Category", "Budgeted", "Activity", "Available"};
                            dtm.setDataVector(data, headings);
                            jt.getColumnModel().getColumn(0).setPreferredWidth(jt.getColumnModel().getTotalColumnWidth() * 2);

                        }
                }
            });
            panel.add(month, "al center center, height 50");
        }
    }

    private void addTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setName("JPanel - Table");

        String[] headings = {"Category", "Budgeted", "Activity", "Available"};
        String[][] data = getTableData();

        JTable jt = formatAndFillTable(data, headings);
        JScrollPane jsp = new JScrollPane(jt);
        jsp.setName("Scroll Pane");

        panel.add(jsp, BorderLayout.CENTER);
        window.add(panel, "dock west, w 100%, h 100%, span, wrap");

        addTableOptions();
    }

    private JTable formatAndFillTable(String[][] data, String[] headings) {
        JTable jt = new JTable(data, headings) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                comp.setBackground(isParentRow(data, row) ? Color.decode("#c6d3eb") : Color.decode("#ecf0f8"));
                comp.setFont(column > 0 ? new Font("Lato", Font.PLAIN, 18) : isParentRow(data, row) ? new Font("Lato", Font.BOLD, 22) : new Font("Lato", Font.PLAIN, 21));
                this.setRowHeight(row, isParentRow(data, row) ? 40 : 30);

                if (isRowSelected(row))
                    comp.setBackground(Color.decode("#7b99d1"));

                return comp;
            }


        };
        jt.setModel(new DefaultTableModel(data, headings) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        jt.setName("JTable");
        jt.getTableHeader().setFont(new Font("Lato", Font.BOLD, 23));
        jt.setFillsViewportHeight(true);
        jt.setRowHeight(40);
        jt.setGridColor(Color.decode("#c6d3eb"));
        jt.setIntercellSpacing(new Dimension(0, 1));
        jt.getColumnModel().getColumn(0).setPreferredWidth(jt.getColumnModel().getTotalColumnWidth() * 2);
        jt.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                JPanel tableOptions = null;
                for (Component c : window.getContentPane().getComponents())
                    if (c.getName().equals("JPanel - Table options"))
                        tableOptions = ((JPanel) c);
                for (Component c : window.getContentPane().getComponents())
                    if (c.getName().equals("JPanel - Table")) {
                        int row = ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView())).getSelectedRow();
                        if (row == -1)
                            return;
                        if (isParentRow(data, row)) {
                            if (tableOptions != null) {
                                tableOptions.getComponent(1).setVisible(false);
                                tableOptions.getComponent(2).setVisible(false);
                            }
                        } else {
                            if (tableOptions != null) {
                                tableOptions.getComponent(1).setVisible(true);
                                tableOptions.getComponent(2).setVisible(true);
                            }
                        }
                    }
            }
        });

        return jt;
    }

    private void addTableOptions() {
        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));
        panel.setName("JPanel - Table options");
        panel.setBackground(Color.decode("#8faadc"));
        for (int i = 1; i < 4; i++) {
            JLabel l = new JLabel();
            if (i != 1)
                l.setVisible(false);
            l.setIcon(new ImageIcon(new ImageIcon("resources/budget-side_menu_" + i + ".png").getImage().getScaledInstance(250, 50, Image.SCALE_DEFAULT)));
            l.setName("Option " + i);
            l.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (l.getName().charAt(7)) {
                        case '1':
                            addCategory();
                            break;
                        case '2':
                            removeCategory();
                            break;
                        case '3':
                            editCategory();
                            break;
                    }
                }
            });
            panel.add(l, "dock north");
        }
        window.add(panel, "dock west, hidemode 3");
        addCategoryOptions();
    }

    private void addCategoryOptions() {
        addCategoryOption1();
        addCategoryOption2();
        addCategoryOption3();
    }

    private void addCategoryOption1() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0, gap rel 0", "grow"));
        panel.setName("JPanel - Add category");
        panel.setVisible(false);
        panel.setBackground(Color.decode("#8faadc"));

        addCategoryOption1Labels(panel, "Add icon", "resources/budget-side_menu_1.png", true, 250, 50, 0, "dock north");
        addCategoryOption1Labels(panel, "Instructions", "<html><center><br><hr>Please fill in the following info to add a new category<hr><br><br></center></html>",
                false, 0, 0, 20, "dock north");
        addCategoryOption1Labels(panel, "Parent category", "Parent category:", false, 0, 0, 18, "dock north");

        ArrayList<String> parentCats = new ArrayList<>();
        int[] d = getDate();
        ResultSet rs = sql.select("SELECT DISTINCT parentName FROM MonthBudget WHERE dateMonth = " + d[0] + " AND dateYear = " + d[1] + ";");
        try {
            while (rs.next())
                parentCats.add(rs.getString("parentName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        parentCats.add("<Add new parent category>");
        JComboBox<String> jc = new JComboBox<>(parentCats.toArray(new String[0]));
        jc.setFont(new Font("Lato", Font.PLAIN, 18));
        jc.setName("JComboBox - Parent categories");
        jc.addActionListener(e -> {
            if (Objects.requireNonNull(jc.getSelectedItem()).toString().equals("<Add new parent category>"))
                for (Component c : panel.getComponents()) {
                    if (c.getName().equals("JComboBox - Parent categories"))
                        c.setVisible(false);
                    if (c.getName().equals("JTextField - Parent category")) {
                        c.setVisible(true);
                        ((JTextField) c).setText("");
                    }
                }
        });
        panel.add(jc, "dock north, hidemode 3");

        JTextField jtf3 = new JTextField();
        jtf3.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf3.setForeground(Color.BLACK);
        jtf3.setName("JTextField - Parent category");
        jtf3.setVisible(false);
        panel.add(jtf3, "dock north, hidemode 3");

        addCategoryOption1Labels(panel, "Child categories", "<html><br>Child category:</html>", false, 0, 0, 18, "dock north");

        JTextField jtf1 = new JTextField();
        jtf1.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf1.setForeground(Color.BLACK);
        jtf1.setName("JTextField - Child categories");
        panel.add(jtf1, "dock north");

        addCategoryOption1Labels(panel, "Budgeted", "<html><br>Amount to be budgeted:</html>", false, 0, 0, 18, "dock north");

        JTextField jtf2 = new JTextField();
        jtf2.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf2.setForeground(Color.BLACK);
        jtf2.setName("JTextField - Budgeted");
        panel.add(jtf2, "dock north");

        addCategoryOption1Labels(panel, "Error", "<html><center>All options must be filled in, and the budget must be a valid number<br><br></center></html>",
                false, 0, 0, 23, "dock north");
        JPanel panel2 = new JPanel(new MigLayout("fill"));
        panel2.setName("Save or cancel icons");
        addCategoryOption1Labels(panel2, "Save icon", "resources/budget-side_menu-add_1.png", true, 125, 50, 0, "dock west");
        addCategoryOption1Labels(panel2, "Cancel icon", "resources/budget-side_menu-add_2.png", true, 125, 50, 0, "dock west");
        panel.add(panel2, "dock north");

        window.add(panel, "dock west, hidemode 3");
    }

    private void addCategoryOption1Labels(JPanel panel, String name, String text, boolean icon, int iconWidth, int iconHeight, int fontSize, String constraints) {
        JLabel l = new JLabel();
        if (icon)
            l.setIcon(new ImageIcon(new ImageIcon(text).getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_DEFAULT)));
        else l.setText(text);
        l.setFont(new Font("Lato", Font.PLAIN, fontSize));
        l.setForeground(Color.BLACK);
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setName(name);
        panel.add(l, constraints);

        switch (name) {
            case "Save icon":
                l.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        saveCategory();
                    }
                });
                break;
            case "Cancel icon":
                l.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        for (Component c : window.getContentPane().getComponents()) {
                            if (c.getName().equals("JPanel - Table options"))
                                c.setVisible(true);
                            if (c.getName().equals("JPanel - Add category")) {
                                c.setVisible(false);
                                for (Component cc : ((JPanel) c).getComponents()) {
                                    if (cc.getName().equals("JComboBox - Parent categories")) {
                                        cc.setVisible(true);
                                        ((JComboBox<String>) cc).setSelectedIndex(0);
                                    }
                                    if (cc.getName().equals("JTextField - Parent category"))
                                        cc.setVisible(false);
                                    if (cc.getName().contains("JTextField"))
                                        ((JTextField) cc).setText("");
                                }
                            }
                        }
                    }
                });
                break;
            case "Error":
                l.setForeground(Color.RED);
                l.setVisible(false);
        }
    }

    private void saveCategory() {
        String parCat = "";
        String childCat = "";
        String budgeted = "";
        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - Add category")) {
                for (Component cc : ((JPanel) c).getComponents()) {
                    switch (cc.getName()) {
                        case "JComboBox - Parent categories":
                            parCat = Objects.requireNonNull(((JComboBox<String>) cc).getSelectedItem()).toString();
                            break;
                        case "JTextField - Parent category":
                            if (!((JTextField) cc).getText().isEmpty())
                                parCat = ((JTextField) cc).getText();
                            break;
                        case "JTextField - Child categories":
                            childCat = ((JTextField) cc).getText();
                            break;
                        case "JTextField - Budgeted":
                            budgeted = ((JTextField) cc).getText();
                            break;
                    }
                }
                break;
            }
        }
        boolean isDouble = true;
        for (char c : budgeted.toCharArray())
            if (!Character.isDigit(c) && c != '.')
                isDouble = false;
        JLabel error = null;
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Add category"))
                for (Component cc : ((JPanel) c).getComponents()) {
                    if (cc.getName().equals("Error"))
                        error = ((JLabel) cc);
                }

        if (parCat.isEmpty() || childCat.isEmpty() || budgeted.isEmpty() || !isDouble) {
            if (error != null) {
                error.setVisible(true);
            }
        } else {
            if (error != null) {
                error.setVisible(false);
            }
            int[] d = getDate();
            sql.update("INSERT INTO MonthBudget(`catID`, `dateMonth`, `dateYear`, `childName`, `parentName`, `budgeted`) VALUES " +
                    "('" + getNewCatID() + "', " + d[0] + ", " + d[1] + ", '" + childCat + "', '" + parCat + "', " + budgeted + ");");
            String[] headings = {"Category", "Budgeted", "Activity", "Available"};
            for (Component c : window.getContentPane().getComponents()) {
                if (c.getName().equals("JPanel - Table options"))
                    c.setVisible(true);
                if (c.getName().equals("JPanel - Add category")) {
                    c.setVisible(false);
                    for (Component cc : ((JPanel) c).getComponents()) {
                        if (cc.getName().equals("JTextField - Parent category")) {
                            cc.setVisible(false);
                            ((JTextField) cc).setText("");
                        }
                        if (cc.getName().equals("JComboBox - Parent categories")) {
                            cc.setVisible(true);
                            ((JComboBox<String>) cc).setSelectedIndex(0);
                        }
                    }
                }
                if (c.getName().equals("JPanel - Table"))
                    new Budget(window, sql);
            }

        }
    }

    private void addCategoryOption2() {
        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));
        panel.setName("JPanel - Remove category");
        panel.setBackground(Color.decode("#8faadc"));
        panel.setVisible(false);

        JLabel l1 = new JLabel();
        l1.setIcon(new ImageIcon(new ImageIcon("resources/budget-side_menu_2.png").getImage().getScaledInstance(250, 50, Image.SCALE_DEFAULT)));
        l1.setName("Remove icon");
        panel.add(l1, "dock north");

        JLabel l2 = new JLabel("<html><center><br><hr>Are you sure you want to remove this category?<br><hr><br><br></center></html>");
        l2.setForeground(Color.BLACK);
        l2.setFont(new Font("Lato", Font.PLAIN, 20));
        l2.setName("Confirmation message");
        panel.add(l2, "dock north");

        JPanel panel2 = new JPanel(new MigLayout("fill, insets 0, gap rel 0, hidemode 3", "grow"));
        panel2.setName("JPanel - Icons");
        JLabel l3 = new JLabel();
        l3.setIcon(new ImageIcon(new ImageIcon("resources/budget-side_menu-remove.png").getImage().getScaledInstance(125, 50, Image.SCALE_DEFAULT)));
        l3.setName("Save icon");
        panel2.add(l3, "dock west");

        JLabel l4 = new JLabel();
        l4.setIcon(new ImageIcon(new ImageIcon("resources/budget-side_menu-add_2.png").getImage().getScaledInstance(125, 50, Image.SCALE_DEFAULT)));
        l4.setName("Cancel icon");
        l4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Component c : window.getContentPane().getComponents()) {
                    if (c.getName().equals("JPanel - Table options"))
                        c.setVisible(true);
                    if (c.getName().equals("JPanel - Remove category")) {
                        c.setVisible(false);
                        for (Component cc : ((JPanel) c).getComponents())
                            if (cc.getName().equals("Error"))
                                cc.setVisible(false);

                    }
                }

            }
        });
        panel2.add(l4, "dock west");

        JLabel l5 = new JLabel();
        l5.setForeground(Color.RED);
        l5.setHorizontalAlignment(JLabel.CENTER);
        l5.setVisible(false);
        l5.setFont(new Font("Lato", Font.PLAIN, 20));
        l5.setName("Error");
        panel.add(l5, "dock north, hidemode 3");

        panel.add(panel2, "dock north, wmin 0");

        l3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String catSelected = categoryOption2GetSelected();
                int numEntries = categoryOption2GetNumEntries(catSelected);
                if (numEntries != 0) {
                    l5.setText("<html><center>You must change the<br>category or delete all<br>the entries with " + catSelected +
                            " as a category. There are " + numEntries + " entries to be edited/deleted.<br><br></center></html>");
                    l5.setVisible(true);
                } else {
                    try {
                        int[] d = getDate();
                        ResultSet rs = sql.select("SELECT * FROM MonthBudget WHERE childName = '" + catSelected +
                                "' AND dateMonth = " + d[0] + " AND dateYear = " + d[1] + ";");
                        String catID = null;
                        if (rs.next())
                            catID = rs.getString("catID");
                        sql.update("DELETE FROM `simpleBudget`.`MonthBudget` WHERE `catID` LIKE '" + catID + "' ESCAPE '#'");

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    String[] headings = {"Category", "Budgeted", "Activity", "Available"};
                    for (Component c : window.getContentPane().getComponents()) {
                        if (c.getName().equals("JPanel - Table options"))
                            c.setVisible(true);
                        if (c.getName().equals("JPanel - Remove category"))
                            c.setVisible(false);
                        if (c.getName().equals("JPanel - Table")) {
                            new Budget(window, sql);
                        }

                    }
                }
            }
        });

        window.add(panel, "dock west, hidemode 3");
    }

    private String categoryOption2GetSelected() {
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Table")) {
                JTable t = ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView()));
                String cat = t.getModel().getValueAt(t.getSelectedRow(), 0).toString();
                while (cat.charAt(0) == ' ')
                    cat = cat.substring(1);
                return cat;
            }
        return null;
    }

    private int categoryOption2GetNumEntries(String catName) {
        int[] d = getDate();
        int numEntries = 0;
        ResultSet rs = sql.select("SELECT * FROM Entry e LEFT JOIN MonthBudget b ON e.catID = b.catID WHERE childName = '" +
                catName + "' AND e.dateMonth = " + d[0] + " AND e.dateYear = " + d[1] + ";");
        try {
            while (rs.next())
                numEntries++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numEntries;
    }

    private void addCategoryOption3() {
        JPanel panel = new JPanel(new MigLayout("fill", "grow", ""));
        panel.setName("JPanel - Edit category");
        panel.setBackground(Color.decode("#8faadc"));
        panel.setVisible(false);

        JLabel l1 = new JLabel();
        l1.setName("JLabel - Edit icon");
        l1.setIcon(new ImageIcon(new ImageIcon("resources/budget-side_menu_3.png").getImage().getScaledInstance(250, 50, Image.SCALE_DEFAULT)));
        panel.add(l1, "dock north");

        JLabel l2 = new JLabel("<html><br><hr><center>Please edit the following info and click save to edit this category</center><hr><br><br></html>");
        l2.setName("JLabel - Edit instructions");
        l2.setFont(new Font("Lato", Font.PLAIN, 20));
        l2.setForeground(Color.BLACK);
        panel.add(l2, "dock north");

        JLabel l3 = new JLabel("Parent Category:");
        l3.setName("JLabel - Parent category");
        l3.setFont(new Font("Lato", Font.PLAIN, 18));
        l3.setForeground(Color.BLACK);
        l3.setHorizontalAlignment(JLabel.CENTER);
        panel.add(l3, "dock north");

        ArrayList<String> parCat = new ArrayList<>();
        try {
            ResultSet rs = sql.select("SELECT DISTINCT parentName FROM MonthBudget");
            while (rs.next())
                parCat.add(rs.getString("parentName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        parCat.add("<Add new parent category>");
        JComboBox<String> jcb = new JComboBox<>(parCat.toArray(new String[0]));
        jcb.setFont(new Font("Lato", Font.PLAIN, 18));
        jcb.setForeground(Color.BLACK);
        jcb.setName("JComboBox - Parent category");
        jcb.addActionListener(e -> {
            if (Objects.requireNonNull(jcb.getSelectedItem()).toString().equals("<Add new parent category>")) {
                for (Component c : panel.getComponents()) {
                    if (c.getName().equals("JComboBox - Parent category"))
                        c.setVisible(false);
                    if (c.getName().equals("JTextField - Parent category"))
                        c.setVisible(true);
                }
            }
        });
        panel.add(jcb, "dock north, hidemode 3");

        JTextField jtf3 = new JTextField();
        jtf3.setVisible(false);
        jtf3.setForeground(Color.BLACK);
        jtf3.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf3.setName("JTextField - Parent category");
        panel.add(jtf3, "dock north, hidemode 3");

        JLabel l4 = new JLabel("<html><br><center>Child category:</center></html>");
        l4.setFont(new Font("Lato", Font.PLAIN, 18));
        l4.setForeground(Color.BLACK);
        l4.setHorizontalAlignment(JLabel.CENTER);
        l4.setName("JLabel - Child category");
        panel.add(l4, "dock north");

        JTextField jtf1 = new JTextField();
        jtf1.setForeground(Color.BLACK);
        jtf1.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf1.setName("JTextField - Child category");
        panel.add(jtf1, "dock north");

        JLabel l5 = new JLabel("<html><br><center>Amount to be budgeted:</center></html>");
        l5.setFont(new Font("Lato", Font.PLAIN, 18));
        l5.setForeground(Color.BLACK);
        l5.setHorizontalAlignment(JLabel.CENTER);
        l5.setName("JLabel - Budgeted");
        panel.add(l5, "dock north");

        JTextField jtf2 = new JTextField();
        jtf2.setForeground(Color.BLACK);
        jtf2.setFont(new Font("Lato", Font.PLAIN, 18));
        jtf2.setName("JTextField - Budgeted");
        panel.add(jtf2, "dock north");

        JLabel error = new JLabel("<html><center>All options must be filled in, and the budget must be a valid number<br><br></center></html>");
        error.setFont(new Font("Lato", Font.PLAIN, 20));
        error.setForeground(Color.RED);
        error.setHorizontalAlignment(JLabel.CENTER);
        error.setVisible(false);
        error.setName("JLabel - Error");
        panel.add(error, "dock north");

        JPanel panel2 = new JPanel(new MigLayout("fill", "grow", ""));
        panel2.setName("JPanel - Save/cancel");

        JLabel l6 = new JLabel();
        l6.setIcon(new ImageIcon(new ImageIcon("resources/budget-side_menu-add_1.png").getImage().getScaledInstance(125, 50, Image.SCALE_DEFAULT)));
        l6.setName("JLabel - Save");
        l6.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Component c : window.getContentPane().getComponents()) {
                    if (c.getName().equals("JPanel - Edit category")) {
                        c.setVisible(false);
                        saveEditedCategory();
                    }
                    if (c.getName().equals("JPanel - Table options"))
                        c.setVisible(true);

                }
            }
        });
        panel2.add(l6, "dock west");

        JLabel l7 = new JLabel();
        l7.setIcon(new ImageIcon(new ImageIcon("resources/budget-side_menu-add_2.png").getImage().getScaledInstance(125, 50, Image.SCALE_DEFAULT)));
        l7.setName("JLabel - Cancel");
        l7.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Component c : window.getContentPane().getComponents()) {
                    if (c.getName().equals("JPanel - Table options"))
                        c.setVisible(true);
                    if (c.getName().equals("JPanel - Edit category")) {
                        c.setVisible(false);
                        for (Component cc : ((JPanel) c).getComponents()) {
                            if (cc.getName().equals("JComboBox - Parent category"))
                                cc.setVisible(true);
                            if (cc.getName().equals("JTextField - Parent category")) {
                                cc.setVisible(false);
                                ((JTextField) cc).setText("");
                            }
                        }
                    }
                }
            }
        });
        panel2.add(l7, "dock west");

        panel.add(panel2, "dock north");

        window.add(panel, "dock west, hidemode 3");
    }

    private void saveEditedCategory() {
        String oldChildCat = "",
                childCat = "",
                oldParCat = "",
                parCat = "",
                oldBudgeted = "",
                budgeted = "";
        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - Table")) {
                JTable jt = ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView()));

                int row = jt.getSelectedRow();
                oldChildCat = jt.getValueAt(row, 0).toString();
                while (oldChildCat.charAt(0) == ' ')
                    oldChildCat = oldChildCat.substring(1);
                oldBudgeted = jt.getValueAt(row, 1).toString().substring(1);
                while (jt.getValueAt(row, 0).toString().charAt(0) == ' ')
                    row--;
                oldParCat = jt.getValueAt(row, 0).toString();
            } else if (c.getName().equals("JPanel - Edit category")) {
                for (Component cc : ((JPanel) c).getComponents()) {
                    if (cc.getName().equals("JComboBox - Parent category"))
                        parCat = Objects.requireNonNull(((JComboBox<String>) cc).getSelectedItem()).toString();
                    else if (cc.getName().equals("JTextField - Parent category") && !((JTextField) cc).getText().isEmpty())
                        parCat = ((JTextField) cc).getText();
                    else if (cc.getName().equals("JTextField - Child category"))
                        childCat = ((JTextField) cc).getText();
                    else if (cc.getName().equals("JTextField - Budgeted"))
                        budgeted = ((JTextField) cc).getText();
                }
            }
        }
        try {
            int[] d = getDate();
            ResultSet rs = sql.select("SELECT catID FROM MonthBudget WHERE dateMonth = " + d[0] + " AND dateYear = " +
                    d[1] + " AND childName = '" + oldChildCat + "' AND parentName = '" + oldParCat + "' AND budgeted = " + oldBudgeted);
            rs.next();
            String catID = rs.getString("catID");
            sql.update("UPDATE `simpleBudget`.`MonthBudget` t SET t.`childName` = '" + childCat + "', t.`parentName` = '" + parCat +
                    "', t.`budgeted` = " + budgeted + " WHERE t.`catID` LIKE '" + catID + "' ESCAPE '#'");
            new Budget(window, sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void addCategoryOption3FillInInfo(JPanel panel) {
        String parCat = "";
        String childCat = "";
        String budgeted = "";

        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Table")) {
                JTable jt = ((JTable) ((((JScrollPane) ((JPanel) c).getComponent(0)).getViewport()).getView()));
                int row = jt.getSelectedRow();
                while (jt.getValueAt(row, 0).toString().charAt(0) == ' ')
                    row--;
                parCat = jt.getValueAt(row, 0).toString();
                row = jt.getSelectedRow();
                childCat = jt.getValueAt(row, 0).toString();
                while (childCat.charAt(0) == ' ')
                    childCat = childCat.substring(1);
                budgeted = jt.getValueAt(row, 1).toString().substring(1);
                break;
            }
        for (Component c : panel.getComponents()) {
            if (c.getName().equals("JComboBox - Parent category")) {
                JComboBox<String> jcb = (JComboBox<String>) c;
                for (int i = 0; i < jcb.getSize().height; i++)
                    if (jcb.getItemAt(i).equals(parCat)) {
                        jcb.setSelectedIndex(i);
                        break;
                    }
            }
            if (c.getName().equals("JTextField - Child category")) {
                ((JTextField) c).setText(childCat);
            }
            if (c.getName().equals("JTextField - Budgeted"))
                ((JTextField) c).setText(budgeted);
        }
    }

    private boolean isParentRow(String[][] data, int row) {
        if (data.length <= row)
            return false;
        if (data.length == 0)
            return false;
        if (data[0].length == 0)
            return false;
        return data[row][0].charAt(0) != ' ';
    }

    private String[][] getTableData() {
        int[] d = getDate();
        setMonthBudget(d[0], d[1]);

        ArrayList<ArrayList<String>> data = getCategoryAndBudget(d[0], d[1]);
        data = getActivityAndAvailable(d[0], d[1], data);

        String[][] ret = new String[data.size()][data.get(0).size()];
        for (int i = 0; i < data.size(); i++)
            for (int j = 0; j < data.get(i).size(); j++) {
                if (j == 0) {
                    if (data.get(i).get(j).charAt(0) == '\t')
                        ret[i][j] = "        " + data.get(i).get(j).substring(1);
                    else ret[i][j] = data.get(i).get(j);
                } else {
                    if (data.get(i).get(j).charAt(0) == '-')
                        ret[i][j] = data.get(i).get(j).charAt(0) + "$" + data.get(i).get(j).substring(1);
                    else ret[i][j] = "$" + data.get(i).get(j);
                    int pI = ret[i][j].indexOf('.');
                    if (pI == -1)
                        ret[i][j] += ".00";
                    else if (ret[i][j].length() - 1 - pI < 2)
                        ret[i][j] += "0";
                }
            }
        return ret;
    }

    // Returns int[] containing {month, year} selected in GUI
    private int[] getDate() {
        int[] date = {-1, -1};
        for (Component c : window.getContentPane().getComponents())
            if (c.getName().equals("JPanel - Top Menu"))
                for (Component cc : ((JPanel) c).getComponents()) {
                    if (cc.getName().equals("Year"))
                        date[1] = Integer.parseInt(((JLabel) cc).getText().substring(2, 6));
                    if (cc.getName().contains("Month"))
                        if (cc.getBackground().equals(Color.decode("#345998")))
                            date[0] = DateTimeFormatter.ofPattern("MMM").parse(((JLabel) cc).getText().charAt(2) +
                                    ((JLabel) cc).getText().substring(3, 5).toLowerCase()).get(ChronoField.MONTH_OF_YEAR);
                }
        return date;
    }

    private ArrayList<ArrayList<String>> getCategoryAndBudget(int month, int year) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        ResultSet rs = sql.select("SELECT * FROM MonthBudget WHERE dateMonth = " + month +
                " AND dateYear = " + year + " ORDER BY parentName, childName");
        try {
            String oldParCat = "";
            while (rs.next()) {
                String parCat = rs.getString("parentName");
                ArrayList<String> row = new ArrayList<>();
                if (!parCat.equals(oldParCat)) {
                    ArrayList<String> parentRow = new ArrayList<>();
                    parentRow.add(parCat);
                    data.add(parentRow);
                    oldParCat = parCat;
                }
                row.add('\t' + rs.getString("childName"));
                row.add(rs.getString("budgeted"));
                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).size() == 1) {
                double total = 0;
                for (int j = i + 1; j < data.size() && data.get(j).size() != 1; j++)
                    total += Double.parseDouble(data.get(j).get(1));
                data.get(i).add(total + "");
            }
        }
        return data;
    }

    private ArrayList<ArrayList<String>> getActivityAndAvailable(int month, int year, ArrayList<ArrayList<String>> data) {
        for (ArrayList<String> aData : data)
            aData.add("0");

        try {
            ResultSet rs = sql.select("SELECT * FROM Entry LEFT JOIN MonthBudget ON Entry.catID = " +
                    "MonthBudget.catID WHERE Entry.dateMonth = " + month + " AND Entry.dateYear = " + year +
                    " ORDER BY childName");
            while (rs.next()) {
                String childCat = rs.getString("childName");
                double activity = Double.parseDouble(rs.getString("outflow")) + Double.parseDouble(rs.getString("inflow"));
                for (ArrayList<String> aData : data)
                    if (aData.get(0).equals("\t" + childCat)) {
                        double total = Double.parseDouble(aData.get(2)) + activity;
                        aData.set(2, String.format("%.2f", total));
                        break;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Update parent Category totals
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get(0).charAt(0) != '\t') {
                double total = 0;
                for (int j = i + 1; j < data.size() && data.get(j).get(0).charAt(0) == '\t'; j++)
                    total += Double.parseDouble(data.get(j).get(2));
                data.get(i).set(2, String.format("%.2f", total));
            }
        }

        for (ArrayList<String> aData : data)
            aData.add(String.format("%.2f", Double.parseDouble(aData.get(1)) - Double.parseDouble(aData.get(2))));

        return data;
    }

    private void setMonthBudget(int month, int year) {
        try {
            ResultSet rs = new SQLConnector().select("SELECT * FROM MonthBudget WHERE dateMonth = " + month +
                    " AND dateYear = " + year);
            if (rs.next())
                return;
            boolean past = false;
            if (year < LocalDate.now().getYear() || (year == LocalDate.now().getYear() && month < LocalDate.now().getMonthValue()))
                past = true;
            if (past)
                rs = new SQLConnector().select("SELECT * FROM MonthBudget ORDER BY dateMonth ASC, dateYear ASC");
            else rs = new SQLConnector().select("SELECT * FROM MonthBudget ORDER BY dateMonth DESC, dateYear DESC");
            rs.next();
            int mirrorMonth = Integer.parseInt(rs.getString("dateMonth"));
            int mirrorYear = Integer.parseInt(rs.getString("dateYear"));
            while (Integer.parseInt(rs.getString("dateMonth")) == mirrorMonth && Integer.parseInt(rs.getString("dateYear")) == mirrorYear) {
                sql.update("INSERT INTO `simpleBudget`.`MonthBudget` (`catID`, `dateMonth`, `dateYear`, `childName`, `parentName`, `budgeted`)\n" +
                        "VALUES ('" + getNewCatID() + "', " + month + ", " + year + ", '" + rs.getString("childName") + "', '" +
                        rs.getString("parentName") + "', " + Integer.parseInt(rs.getString("budgeted")) + ");");
                if (!rs.next())
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String getNewCatID() {
        StringBuilder cID = new StringBuilder();
        for (int i = 0; i < 6; i++)
            cID.append((int) (Math.random() * 10));
        try {
            ResultSet rs = new SQLConnector().select("SELECT * FROM MonthBudget");
            while (rs.next()) {
                String id = rs.getString("catID");
                if (id.equals(cID.toString()))
                    return getNewCatID();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cID.toString();
    }

    private void addCategory() {
        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - Add category"))
                c.setVisible(true);
            else if (c.getName().equals("JPanel - Table options"))
                c.setVisible(false);
        }
    }

    private void removeCategory() {
        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - Remove category"))
                c.setVisible(true);
            else if (c.getName().equals("JPanel - Table options"))
                c.setVisible(false);
        }
    }

    private void editCategory() {
        for (Component c : window.getContentPane().getComponents()) {
            if (c.getName().equals("JPanel - Edit category")) {
                c.setVisible(true);
                addCategoryOption3FillInInfo(((JPanel) c));
            }
            else if(c.getName().equals("JPanel - Table options"))
                c.setVisible(false);
        }
    }
}
