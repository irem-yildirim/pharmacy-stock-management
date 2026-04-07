package com.pharmacy.views.pages;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Drug;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.dialogs.PurchaseDialog;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BrandsPage extends AbstractPage {

    private final JPanel grid;

    public BrandsPage(DashboardView parent, InventoryController invC, TransactionController transC, ReportController repC) {
        super("Brands", parent, invC, transC, repC);

        getContainer().setBackground(BG_LIGHT);
        getContainer().setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("🏷️ Managed Brands");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        getContainer().add(title, BorderLayout.NORTH);

        grid = new JPanel(new GridLayout(0, 3, 18, 18));
        grid.setBackground(BG_LIGHT);
        grid.setBorder(new EmptyBorder(20, 0, 0, 0));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_LIGHT);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        getContainer().add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void onPageEnter() {
        grid.removeAll();
        for (Brand b : inventoryController.getAllBrands()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(BG_WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(16, 16, 16, 16)));

            JLabel name = new JLabel(b.getBrandName());
            name.setFont(FONT_HEADER);
            String drugNames = inventoryController.getMedicinesByBrand(b.getBrandId()).stream()
                    .map(Drug::getName)
                    .reduce((med1, med2) -> med1 + "<br>" + med2)
                    .orElse("No drugs");

            JLabel drugsLabel = new JLabel(
                    "<html><i style='color:#a0a0a0; font-size:12px;'>" + drugNames + "</i></html>");

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.add(name);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(drugsLabel);

            card.add(infoPanel, BorderLayout.CENTER);

            JButton buyBtn = createPrimaryButton("Buy Stock");
            buyBtn.addActionListener(e -> new PurchaseDialog(parent, transactionController, inventoryController, b.getBrandId()).setVisible(true));
            card.add(buyBtn, BorderLayout.SOUTH);
            grid.add(card);
        }
        grid.revalidate();
        grid.repaint();
    }

    @Override
    public void onPageExit() {}

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isArmed() ? ACCENT_DARK : getModel().isRollover() ? ACCENT_HOVER : ACCENT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 18, 6, 18));
        return btn;
    }
}
