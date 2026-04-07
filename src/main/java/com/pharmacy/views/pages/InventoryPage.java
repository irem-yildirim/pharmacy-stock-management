package com.pharmacy.views.pages;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.PresType;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.components.MedicineCard;
import com.pharmacy.views.dialogs.MedicineFormView;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class InventoryPage extends AbstractPage {

    private final JPanel cardsPanel;
    private final JTextField searchField;

    public InventoryPage(DashboardView parent, InventoryController invC, TransactionController transC, ReportController repC) {
        super("Inventory", parent, invC, transC, repC);

        getContainer().setBackground(BG_LIGHT);

        cardsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        cardsPanel.setBackground(BG_LIGHT);
        cardsPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setBackground(BG_LIGHT);
        cardsWrapper.add(cardsPanel, BorderLayout.NORTH);

        JScrollPane medScroll = new JScrollPane(cardsWrapper);
        medScroll.setBorder(null);
        medScroll.getViewport().setBackground(BG_LIGHT);
        medScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Search bar on top
        JPanel topSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topSearchPanel.setOpaque(false);
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchMedicines());
        searchField.addActionListener(e -> searchMedicines());
        topSearchPanel.add(searchField);
        topSearchPanel.add(searchBtn);

        getContainer().add(topSearchPanel, BorderLayout.NORTH);
        getContainer().add(medScroll, BorderLayout.CENTER);
    }

    private void searchMedicines() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            loadTableData();
            return;
        }
        updateCardsPanel(inventoryController.searchMedicines(kw));
    }

    public void filterByCategory(long catId) {
        new SwingWorker<List<Drug>, Void>() {
            @Override
            protected List<Drug> doInBackground() {
                return inventoryController.getMedicinesByCategory(catId);
            }
            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public void onPageEnter() {
        loadTableData();
    }

    @Override
    public void onPageExit() {}

    private void loadTableData() {
        new SwingWorker<List<Drug>, Void>() {
            @Override
            protected List<Drug> doInBackground() {
                return inventoryController.getAllMedicines();
            }
            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateCardsPanel(List<Drug> meds) {
        cardsPanel.removeAll();
        if (meds != null) {
            for (Drug m : meds) {
                Brand b = m.getBrand();
                PresType p = m.getPresType();
                cardsPanel.add(new MedicineCard(m, b != null ? b.getBrandName() : "Unknown",
                        p != null ? p.getPrescription() : "Unknown", (drug) -> parent.openMedicineForm(drug)));
            }
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
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}
