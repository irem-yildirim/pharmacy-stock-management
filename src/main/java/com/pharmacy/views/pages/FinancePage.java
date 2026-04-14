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

// Alış ve satış işlemlerini kronolojik olarak listeleyen, CSV'ye aktarabilen finans sayfası
public class FinancePage extends AbstractPage {

    // Finans tablomuz — her satır bir işlem kaydı (satış veya alım)
    private final JTable table;
    private final DefaultTableModel model;

    public FinancePage(DashboardView parent, InventoryController invC, TransactionController transC, ReportController repC) {
        super("Finance", parent, invC, transC, repC);

        getContainer().setBorder(new EmptyBorder(24, 24, 24, 24));
        getContainer().setBackground(BG_LIGHT);

        // Tablonun sütun başlıkları
        String[] cols = { "Date", "Type", "Reference", "Amount (TL)" };
        // isCellEditable false döndürüyor — kullanıcı hücreyi düzenlemesin!
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

        // Alt panel — CSV butonu sağa yaslanmış
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BG_LIGHT);
        JButton btnExport = new JButton("Export to CSV");
        btnExport.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnExport.setBackground(new Color(40, 140, 80));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        // Butona basılınca tablodaki verileri masaüstüne .csv olarak kaydediyoruz
        btnExport.addActionListener(e -> exportToCSV(parent));
        bottomPanel.add(btnExport);

        getContainer().add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Ekranda görünen JTable içerisindeki finans kayıtlarını alıp masaüstüne bir .csv metin dosyası olarak yazar.
     * Bu işlem tamamen Java kütüphaneleriyle harici bağımlılık olmadan yapılır.
     */
    private void exportToCSV(DashboardView parentView) {
        // Masaüstü dizinini bulup dosyanın tam yolunu ayarlıyoruz
        String desktopPath = System.getProperty("user.home") + "/Desktop/finance_export.csv";

        // Try-with-resources: Dosya yazma işlemi bittikten sonra yazıcıyı otomatik kapatır
        try (java.io.FileWriter writer = new java.io.FileWriter(desktopPath)) {
            // Adım 1: Sütun başlıklarını virgülle ayırarak yaz
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.append(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.append(",");
            }
            writer.append("\n");

            // Adım 2: Tablo satırlarını tek tek gezerek verileri yaz
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    // Sayılardaki virgülleri sil ki CSV formatı bozulmasın
                    writer.append(val != null ? val.toString().replace(",", "") : "");
                    if (j < model.getColumnCount() - 1) writer.append(",");
                }
                writer.append("\n");
            }
            com.pharmacy.views.components.ThemedDialog.showMessage(parentView, "Export successfully saved to Desktop!", com.pharmacy.views.components.ThemedDialog.Kind.SUCCESS);
        } catch (java.io.IOException ex) {
            // Yetki veya disk sorunu olursa kullanıcıya hata göster — program çökmesin
            com.pharmacy.views.components.ThemedDialog.showMessage(parentView, "Error exporting CSV: " + ex.getMessage(), com.pharmacy.views.components.ThemedDialog.Kind.ERROR);
        }
    }

    // Sayfaya her girildiğinde tabloyu sıfırlayıp veritabanından taze işlemleri yükle
    @Override
    public void onPageEnter() {
        model.setRowCount(0); // Eski satırları temizle
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
