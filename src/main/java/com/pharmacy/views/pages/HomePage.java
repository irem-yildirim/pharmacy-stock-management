package com.pharmacy.views.pages;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.PresType;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.components.MedicineCard;
import com.pharmacy.views.components.MedicineCard.ExpiryMode;
import com.pharmacy.views.components.StatCard;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Uygulamanın ana sayfası — istatistikler, kritik stok ve son kullanma tarihi uyarıları burada
public class HomePage extends AbstractPage {

    // Tüm widget'ları tutan ana panel — GridBagLayout ile esnek yerleşim yapıyoruz
    private final JPanel homeWidgetsRow;

    public HomePage(DashboardView parent, InventoryController invC, TransactionController transC, ReportController repC) {
        super("Home", parent, invC, transC, repC);

        getContainer().setBackground(BG_LIGHT);
        getContainer().setBorder(new EmptyBorder(20, 15, 20, 15));

        homeWidgetsRow = new JPanel(new GridBagLayout());
        homeWidgetsRow.setBackground(BG_LIGHT);
        getContainer().add(homeWidgetsRow, BorderLayout.CENTER);
    }

    // Sayfaya her gelindiğinde verileri tazele — eski bilgilerin üstüne yaz
    @Override
    public void onPageEnter() {
        try {
            List<Drug> all = inventoryController.getAllMedicines();
            ReportController.FinancialSummary stats = reportController.getFinancialSummary();
            // İlaçları ve istatistikleri alıp dashboard'a çiziyoruz
            updateBentoDashboard(all, stats);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageExit() {}

    // Tüm widget'ları sıfırlayıp yeni verilere göre yeniden inşa ediyor
    private void updateBentoDashboard(List<Drug> all, ReportController.FinancialSummary stats) {
        homeWidgetsRow.removeAll(); // Eski içeriği temizle
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // Üst sıra: 4 küçük istatistik kartı (Inventory, Revenue, Low Stock, Expiry)
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);
        statsRow.add(new StatCard("Inventory", String.valueOf(stats.totalInventory), "📦", SIDEBAR_BG));
        statsRow.add(new StatCard("Revenue", String.format("%.2f TL", stats.todayRevenue), "💰", SUCCESS));
        statsRow.add(new StatCard("Low Stock", String.valueOf(stats.lowStockCount), "⚠️", DANGER));
        statsRow.add(new StatCard("Expiry", String.valueOf(stats.expiryCount), "📅", new Color(240, 140, 50)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        statsRow.setPreferredSize(new Dimension(0, 95));
        homeWidgetsRow.add(statsRow, gbc);

        // Sol alt widget: Son kullanma tarihi yaklaşan ilaçlar
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 0.5;
        homeWidgetsRow.add(buildExpiryWidget(all), gbc);

        // Sağ alt widget: Stoğu düşük olan ilaçlar
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        homeWidgetsRow.add(buildLowStockWidget(all), gbc);

        // En alt tam genişlik: Genel finans özeti (Ciro / Gider / Net Kar)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        JPanel financeW = buildFullWidthFinancePanel(stats);
        financeW.setPreferredSize(new Dimension(0, 85));
        homeWidgetsRow.add(financeW, gbc);

        homeWidgetsRow.revalidate();
        homeWidgetsRow.repaint();
    }

    // 30 günden az kalan ilaçları filtreler, kalan gün sayısına göre sıralar
    private JPanel buildExpiryWidget(List<Drug> all) {
        LocalDate now = LocalDate.now();
        List<Drug> expirations = all.stream()
                .filter(m -> m.getExpiry() != null && m.getExpiry().getExpirationDate() != null)
                // Son kullanma tarihine 30'dan az gün kalan ilaçları alıyoruz
                .filter(m -> java.time.temporal.ChronoUnit.DAYS.between(now, m.getExpiry().getExpirationDate()) <= 30)
                .sorted(Comparator.comparing(m -> m.getExpiry().getExpirationDate()))
                .collect(Collectors.toList());

        return buildSectionWidget("📅  Critical Expirations (<30 Days)", new Color(240, 140, 50), expirations, ExpiryMode.EXPIRING_SOON);
    }

    // Stoğu 10'un altına düşmüş ilaçları filtreler; kritik yoksa en düşük stoklular gösterilir
    private JPanel buildLowStockWidget(List<Drug> all) {
        List<Drug> lowStock = all.stream().filter(m -> m.getStockQuantity() < 10)
                .sorted(Comparator.comparingInt(Drug::getStockQuantity))
                .collect(Collectors.toList());

        boolean hasUrgent = !lowStock.isEmpty();
        // Kritik ilaç varsa onları göster, yoksa en az stoğu olanları listele
        List<Drug> toShow = hasUrgent ? lowStock : all.stream().sorted(Comparator.comparingInt(Drug::getStockQuantity)).limit(6).collect(Collectors.toList());
        ExpiryMode cardMode = hasUrgent ? ExpiryMode.EXPIRING_SOON : ExpiryMode.EXPIRY_SAFE;

        return buildSectionWidget(hasUrgent ? "⚠️  Critical Stock" : "📦  Stock Monitor", hasUrgent ? DANGER : SUCCESS, toShow, cardMode);
    }

    // İki widget (expiry + low stock) için aynı panel yapısını oluşturan yardımcı metot
    private JPanel buildSectionWidget(String headerText, Color headerColor, List<Drug> toShow, ExpiryMode cardMode) {
        JPanel widget = createBentoPanel();
        widget.setLayout(new BorderLayout());

        // Widget başlığı — renkli ve büyük
        JLabel headerLbl = new JLabel(headerText);
        headerLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerLbl.setForeground(headerColor);
        widget.add(headerLbl, BorderLayout.NORTH);

        // İlaç kartları tek sütunda alt alta sıralanıyor
        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 12));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(15, 0, 0, 0));

        for (Drug m : toShow) {
            Brand b = m.getBrand();
            PresType p = m.getPresType();
            // Her ilaç için kart oluşturuyoruz; karta tıklanınca düzenleme formu açılıyor
            grid.add(new MedicineCard(m, b != null ? b.getBrandName() : "Unknown", p != null ? p.getPrescription() : "Unknown", cardMode, (drug) -> parent.openMedicineForm(drug)));
        }

        // Çok ilaç varsa scroll ile kaydırılabilir yapıyoruz
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Scrollbar görünmesini gizliyoruz
        scroll.getVerticalScrollBar().setUnitIncrement(30); // Kaydırma hızını artır
        widget.add(scroll, BorderLayout.CENTER);
        return widget;
    }

    // En alttaki geniş finans panelini oluşturuyor — Ciro, Gider, Net Kar yan yana
    private JPanel buildFullWidthFinancePanel(ReportController.FinancialSummary fin) {
        JPanel panel = createBentoPanel();
        panel.setLayout(new GridLayout(1, 3, 40, 0));

        panel.add(createFinanceItem("Total Revenue", String.format("%.2f TL", fin.totalSales), SUCCESS));
        panel.add(createFinanceItem("Total Expenditure", String.format("%.2f TL", fin.totalPurchases), DANGER));
        // Net performans: kârdaysa yeşil, zarardaysa kırmızı göster
        panel.add(createFinanceItem("Net Performance", String.format("%.2f TL", fin.netProfit), fin.netProfit.compareTo(java.math.BigDecimal.ZERO) >= 0 ? SUCCESS : DANGER));

        return panel;
    }

    // Her bir finans bilgisini (üst label, alt büyük rakam) gösteren küçük panel
    private JPanel createFinanceItem(String lbl, String val, Color col) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel l = new JLabel(lbl, SwingConstants.CENTER);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_SECONDARY);
        JLabel v = new JLabel(val, SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 20));
        v.setForeground(col);
        p.add(l);
        p.add(v);
        return p;
    }

    // Bento tasarım tarzında (Notion tarzı) yuvarlak köşeli beyaz kart paneli oluşturuyor
    private JPanel createBentoPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Kart arka planı beyaz, kenarlık açık gri
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 20, 15, 20));
        return p;
    }
}
