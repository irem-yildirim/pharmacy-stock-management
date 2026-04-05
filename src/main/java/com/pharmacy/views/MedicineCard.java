package com.pharmacy.views;

import com.pharmacy.models.Medicine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static com.pharmacy.views.ThemeConstants.*;

/**
 * A styled card component that displays a single Medicine's summary info.
 */
public class MedicineCard extends JPanel {

    public enum ExpiryMode {
        EXPIRING_SOON, EXPIRY_SAFE
    }

    private final Medicine medicine;
    private final Consumer<Medicine> onUpdate;

    // Constructor used by updateCardsPanel (normal listing)
    public MedicineCard(Medicine medicine, String brandName, String presType, Consumer<Medicine> onUpdate) {
        this(medicine, brandName, presType, ExpiryMode.EXPIRY_SAFE, onUpdate);
    }

    // Constructor used by widgets (expiry/low-stock)
    public MedicineCard(Medicine medicine, String brandName, String presType, ExpiryMode mode,
            Consumer<Medicine> onUpdate) {
        this.medicine = medicine;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(10, 8));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 16, 14, 16));
        setPreferredSize(new Dimension(240, 160));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Top: Name + Dose
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(medicine.getMedName());
        nameLabel.setFont(FONT_HEADER);
        nameLabel.setForeground(TEXT_PRIMARY);

        JLabel doseLabel = new JLabel(medicine.getDose() != null ? medicine.getDose() : "");
        doseLabel.setFont(FONT_SMALL);
        doseLabel.setForeground(TEXT_SECONDARY);

        top.add(nameLabel, BorderLayout.NORTH);
        top.add(doseLabel, BorderLayout.SOUTH);

        // Center: Brand + PresType
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(createInfoLabel("Brand: " + brandName));
        center.add(createInfoLabel("Type: " + presType));

        // Expiration date if available
        if (medicine.getExpirationDate() != null) {
            String dateStr = medicine.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            JLabel expLabel = createInfoLabel("Exp: " + dateStr);
            if (mode == ExpiryMode.EXPIRING_SOON) {
                expLabel.setForeground(new Color(195, 50, 50));
                expLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            }
            center.add(expLabel);
        }

        // Bottom: Price + Stock
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel priceLabel = new JLabel(String.format("%.2f TL", medicine.getPrice()));
        priceLabel.setFont(FONT_LABEL);
        priceLabel.setForeground(ACCENT_DARK);

        Color stockColor = medicine.getQuantity() < 10 ? new Color(195, 50, 50) : new Color(40, 140, 80);
        JLabel stockLabel = new JLabel("Stock: " + medicine.getQuantity());
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

        // Card background
        g2.setColor(BG_CARD);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

        // Subtle border
        g2.setColor(new Color(220, 220, 220));
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);

        // Hover effect
        if (getMousePosition() != null) {
            g2.setColor(new Color(0, 0, 0, 8));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
