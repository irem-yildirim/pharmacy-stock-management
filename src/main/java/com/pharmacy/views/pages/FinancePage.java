package com.pharmacy.views.pages;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.views.DashboardView;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FinancePage extends AbstractPage {

    private final JTable table;
    private final DefaultTableModel model;

    public FinancePage(DashboardView parent, InventoryController invC, TransactionController transC, ReportController repC) {
        super("Finance", parent, invC, transC, repC);

        getContainer().setBorder(new EmptyBorder(24, 24, 24, 24));
        getContainer().setBackground(BG_LIGHT);

        String[] cols = { "Date", "Type", "Reference", "Amount (TL)" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setFillsViewportHeight(true);

        getContainer().add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void onPageEnter() {
        model.setRowCount(0);
        try {
            List<ReportController.FinancialTransaction> trans = reportController.getFinancialTransactions();
            for (ReportController.FinancialTransaction tx : trans) {
                model.addRow(new Object[] { tx.date, tx.type, tx.reference, String.format("%.2f", tx.amount) });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageExit() {}
}
