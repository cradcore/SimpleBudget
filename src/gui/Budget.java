package gui;


import net.miginfocom.swing.MigLayout;
import sqlConnector.SQLConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Array;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;

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
                ld = LocalDate.parse(LocalDate.now().getYear() + "-0" + (i + 1) + "-" + LocalDate.now().getDayOfMonth());
            else
                ld = LocalDate.parse(LocalDate.now().getYear() + "-" + (i + 1) + "-" + LocalDate.now().getDayOfMonth());
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
//                    System.out.println(getDate()[0] + "-" + getDate()[1]);
                    addTable();
                }
            });
            panel.add(month, "al center center, height 50");
        }
    }

    private void listComponents() {
        for (Component c : window.getContentPane().getComponents()) {
            System.out.println(c.getName());
            for (Component cc : ((JPanel) c).getComponents())
                System.out.println("\t" + cc.getName());
        }
    }

    private void addTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setName("JPanel - Table");

        String[] headings = {"Category", "Budgeted", "Activity", "Available"};
        String[][] data = getTableData();

        JTable jt = new JTable(data, headings);
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

        JScrollPane jsp = new JScrollPane(jt);
        jsp.setName("Scroll Pane");

        panel.add(jsp, BorderLayout.CENTER);

        window.add(panel, "dock north, w 100%, h 100%, span, wrap");


//        System.out.println("\n\n" + getDate()[0] + "/" + getDate()[1]);
//        for (int i = 0; i < data.length; i++) {
//            for (int j = 0; j < data[i].length; j++)
//                System.out.print(data[i][j] + "\t");
//            System.out.println();
//        }
    }

    private String[][] getTableData() {
        int[] d = getDate();
        setMonthBudget(d[0], d[1]);

        ArrayList<ArrayList<String>> data = getCategoryAndBudget(d[0], d[1]);
        data = getActivityAndAvailable(d[0], d[1], data);

        String[][] ret = new String[data.size()][data.get(0).size()];
        for (int i = 0; i < data.size(); i++)
            for (int j = 0; j < data.get(i).size(); j++) {
                if (j == 0)
                    ret[i][j] = data.get(i).get(j);
                else {
                    if(data.get(i).get(j).charAt(0) == '-')
                        ret[i][j] = data.get(i).get(j).charAt(0) + "$" + data.get(i).get(j).substring(1);
                    else ret[i][j] = "$" + data.get(i).get(j);
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
                        if (((JLabel) cc).getBackground().equals(Color.decode("#345998")))
                            date[0] = DateTimeFormatter.ofPattern("MMM").parse(((JLabel) cc).getText().charAt(2) +
                                    ((JLabel) cc).getText().substring(3, 5).toLowerCase()).get(ChronoField.MONTH_OF_YEAR);
                }
        return date;
    }

    private ArrayList<ArrayList<String>> getCategoryAndBudget(int month, int year) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        ResultSet rs = sql.select("SELECT * FROM MonthBudget WHERE dateMonth = " + month +
                " AND dateYear = " + year + " ORDER BY parentName");
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
        for (int i = 0; i < data.size(); i++)
            data.get(i).add("0");

        try {
            ResultSet rs = sql.select("SELECT * FROM Entry LEFT JOIN MonthBudget ON Entry.catID = " +
                    "MonthBudget.catID WHERE Entry.dateMonth = " + month + " AND Entry.dateYear = " + year +
                    " ORDER BY childName");
            while (rs.next()) {
                String childCat = rs.getString("childName");
                double activity = Double.parseDouble(rs.getString("outflow")) + Double.parseDouble(rs.getString("inflow"));
                for (int i = 0; i < data.size(); i++)
                    if (data.get(i).get(0).equals("\t" + childCat)) {
                        double total = Double.parseDouble(data.get(i).get(2)) + activity;
                        data.get(i).set(2, total + "");
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
                data.get(i).set(2, total + "");
            }
        }

        for (int i = 0; i < data.size(); i++)
            data.get(i).add((Double.parseDouble(data.get(i).get(1)) - Double.parseDouble(data.get(i).get(2))) + "");

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
                rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getNewCatID() {
        String cID = "";
        for (int i = 0; i < 6; i++)
            cID += (int) (Math.random() * 10) + "";
        try {
            ResultSet rs = sql.select("SELECT * FROM MonthBudget");
            while (rs.next()) {
                String id = rs.getString("catID");
                if (id.equals(cID))
                    return getNewCatID();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cID;
    }
}
