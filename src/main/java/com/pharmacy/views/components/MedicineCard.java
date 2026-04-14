package com.pharmacy.views.components;

import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Expiry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static com.pharmacy.views.components.ThemeConstants.*;

/**
 * A card that displays a single Drug summary info.
 */
public class MedicineCard extends JPanel {

    public enum ExpiryMode {
        EXPIRY_URGENT, EXPIRING_SOON, EXPIRY_SAFE, LOW_STOCK
    }

    private final ExpiryMode mode;
    private boolean isHovered = false;
    private final Color badgeColor;

    public MedicineCard(Drug drug, String brandName, String presType, Consumer<Drug> onUpdate) {
        this(drug, brandName, presType, ExpiryMode.EXPIRY_SAFE, onUpdate);
    }

    public MedicineCard(Drug drug, String brandName, String presType, ExpiryMode requestedMode,
            Consumer<Drug> onUpdate) {
        
        // Öncelikli olarak Son Kullanma Tarihi ve Stok Kontrolü yapılır
        boolean isExpiring = false;
        if (drug.getExpiry() != null && drug.getExpiry().getExpirationDate() != null) {
            java.time.LocalDate expDate = drug.getExpiry().getExpirationDate();
            if (expDate.isBefore(java.time.LocalDate.now().plusDays(30))) {
                isExpiring = true;
            }
        }

        if (isExpiring) {
            this.mode = ExpiryMode.EXPIRY_URGENT; // Kırmızı Kenarlık
        } else if (drug.getStockQuantity() < 10) {
            this.mode = ExpiryMode.LOW_STOCK; // Turuncu Kenarlık
        } else {
            this.mode = requestedMode;
        }

        if (presType == null) presType = "";
        if (presType.contains("Green")) {
            this.badgeColor = new Color(76, 175, 80);
        } else if (presType.contains("Red")) {
            this.badgeColor = new Color(244, 67, 54);
        } else if (presType.contains("Orange")) {
            this.badgeColor = new Color(255, 152, 0);
        } else if (presType.contains("Purple")) {
            this.badgeColor = new Color(156, 39, 176);
        } else {
            this.badgeColor = new Color(189, 189, 189); // Default Light Gray
        }

        setLayout(new BorderLayout(10, 8));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 16, 14, 16));
        setPreferredSize(new Dimension(240, 170));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onUpdate != null) {
                    onUpdate.accept(drug);
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });

        // Top: Name
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(drug.getName());
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nameLabel.setForeground(TEXT_PRIMARY);

        top.add(nameLabel, BorderLayout.NORTH);

        // Center: Brand + PresType
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(createInfoLabel("Brand: " + brandName));
        center.add(createInfoLabel("Type: " + presType));

        // Expiration date
        Expiry exp = drug.getExpiry();
        if (exp != null && exp.getExpirationDate() != null) {
            String dateStr = exp.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            JLabel expLabel = createInfoLabel("Exp: " + dateStr);
            if (mode == ExpiryMode.EXPIRING_SOON || mode == ExpiryMode.EXPIRY_URGENT) {
                expLabel.setForeground(new Color(195, 50, 50));
                expLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            }
            center.add(expLabel);
        }

        // Bottom: Price + Stock
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel priceLabel = new JLabel(String.format("%.2f TL", drug.getSellingPrice().doubleValue()));
        priceLabel.setFont(FONT_LABEL);
        priceLabel.setForeground(ACCENT_DARK);

        Color stockColor = drug.getStockQuantity() < 10 ? new Color(195, 50, 50) : new Color(40, 140, 80);
        JLabel stockLabel = new JLabel("Stock: " + drug.getStockQuantity());
        stockLabel.setFont(FONT_SMALL);
        stockLabel.setForeground(stockColor);

        bottom.add(priceLabel, BorderLayout.WEST);
        bottom.add(stockLabel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isHovered) {
            g2.setColor(SHADOW_COLOR);
            g2.fillRoundRect(3, 3, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
            g2.setColor(BG_CARD_HOVER);
        } else {
            g2.setColor(BG_CARD);
        }

        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, CARD_RADIUS, CARD_RADIUS);

        if (mode == ExpiryMode.EXPIRY_URGENT || mode == ExpiryMode.EXPIRING_SOON) {
            g2.setColor(new Color(220, 60, 60)); // Kırmızı (Red)
            g2.setStroke(new BasicStroke(3f)); // 3 piksel kalınlık
        } else if (mode == ExpiryMode.LOW_STOCK) {
            g2.setColor(new Color(255, 152, 0)); // Turuncu (Orange)
            g2.setStroke(new BasicStroke(3f)); // 3 piksel kalınlık
        } else {
            g2.setColor(new Color(220, 220, 220));
        }
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, CARD_RADIUS, CARD_RADIUS);

        // Prescription Type Badge
        if (badgeColor != null) {
            g2.setColor(badgeColor);
            g2.fillOval(getWidth() - 18, 12, 10, 10);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
