package com.pharmacy.views.pages;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.PresType;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.components.MedicineCard;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

// İlaç envanterinin kart görünümünde listelendiği ana sayfa
public class InventoryPage extends AbstractPage {

    // Kartların yerleştirildiği panel — 3 sütunlu grid layout kullanıyoruz
    private final JPanel cardsPanel;
    // Arama kutusu — isim, barkod, marka veya kategoriye göre filtrele
    private final JTextField searchField;

    public InventoryPage(DashboardView parent, InventoryController invC, TransactionController transC, ReportController repC) {
        super("Inventory", parent, invC, transC, repC);

        getContainer().setBackground(BG_LIGHT);

        // Her satırda 3 kart gösteriyoruz, aralarında 18px boşluk bırakıyoruz
        cardsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        cardsPanel.setBackground(BG_LIGHT);
        cardsPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Kartların kaydırılabilir olması için ScrollPane içine sarıyoruz
        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setBackground(BG_LIGHT);
        cardsWrapper.add(cardsPanel, BorderLayout.NORTH);

        JScrollPane medScroll = new JScrollPane(cardsWrapper);
        medScroll.setBorder(null);
        medScroll.getViewport().setBackground(BG_LIGHT);
        medScroll.getVerticalScrollBar().setUnitIncrement(16); // Kaydırma hızı

        // Üst kısma arama çubuğu ekliyoruz
        JPanel topSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topSearchPanel.setOpaque(false);
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        // Butona da, Enter'a da basınca arama çalışsın
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchMedicines());
        searchField.addActionListener(e -> searchMedicines());
        topSearchPanel.add(searchField);
        topSearchPanel.add(searchBtn);

        getContainer().add(topSearchPanel, BorderLayout.NORTH);
        getContainer().add(medScroll, BorderLayout.CENTER);
    }

    // Arama kutusundaki metne göre ilaçları filtreler ve kartları yeniler
    private void searchMedicines() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            // Arama alanı boşsa tüm ilaçları göster
            loadTableData();
            return;
        }
        // Controller üzerinden filtreleme yapılır, sonuçlar kartlara dönüştürülür
        updateCardsPanel(inventoryController.searchMedicines(kw));
    }

    // Sol menüden bir kategori seçildiğinde o kategoriye ait ilaçları gösteriyor
    public void filterByCategory(long catId) {
        try {
            List<Drug> meds = inventoryController.getMedicinesByCategory(catId);
            updateCardsPanel(meds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sayfaya her girildiğinde tüm ilaçlar taze yükleniyor
    @Override
    public void onPageEnter() {
        loadTableData();
    }

    @Override
    public void onPageExit() {}

    // Tüm ilaçları veritabanından çekip kartlara dönüştürüyor
    public void loadTableData() {
        try {
            List<Drug> meds = inventoryController.getAllMedicines();
            updateCardsPanel(meds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kartlar panelini tamamen temizleyip verilen ilaç listesiyle yeniden dolduruyor
    private void updateCardsPanel(List<Drug> meds) {
        cardsPanel.removeAll(); // Önce eski kartları temizle
        if (meds != null) {
            for (Drug m : meds) {
                Brand b = m.getBrand();
                PresType p = m.getPresType();
                // Her ilaç için bir MedicineCard oluşturuyoruz; tıklanınca düzenleme formu açılıyor
                cardsPanel.add(new MedicineCard(m, b != null ? b.getBrandName() : "Unknown",
                        p != null ? p.getPrescription() : "Unknown", (drug) -> parent.openMedicineForm(drug)));
            }
            // Grid 3 sütunlu olduğu için son satırdaki boşlukları saydam panellerle dolduruyoruz
            int rem = meds.size() % 3;
            if (!meds.isEmpty() && rem != 0) {
                for (int i = 0; i < 3 - rem; i++) {
                    JPanel g = new JPanel();
                    g.setOpaque(false);
                    g.setPreferredSize(new Dimension(240, 160));
                    cardsPanel.add(g);
                }
            }
        }
        // Değişiklikler ekrana yansısın diye yeniliyoruz
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}
