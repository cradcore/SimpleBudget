package gui;

import net.miginfocom.swing.MigLayout;
import sqlConnector.SQLConnector;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import javax.swing.*;

public class Run {

    public static void main(String[] args) {

        SQLConnector sql = new SQLConnector();
        JFrame window = new JFrame();
        window.setBounds(100, 100, 1600, 900);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new JPanel(new MigLayout("gap rel 0", "grow")));
        window.getContentPane().setName("Home content pane");
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                removeUnusedBudgetMonths(sql);
            }
        });

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Budget(window, sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void removeUnusedBudgetMonths(SQLConnector sql) {
        ResultSet rs = sql.select("SELECT * FROM MonthBudget m WHERE NOT EXISTS(SELECT * FROM Entry e WHERE m.dateYear = e.dateYear AND m.dateMonth = e.dateMonth)");
        try {
            while (rs.next()) {
                String catID = rs.getString("catID");
                new SQLConnector().update("DELETE FROM `simpleBudget`.`MonthBudget` WHERE `catID` LIKE '" + catID + "' ESCAPE '#'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
