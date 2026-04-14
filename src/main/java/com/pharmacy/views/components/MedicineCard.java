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
 * Her bir ilacı temsil eden kart bileşeni.
 * Son kullanma tarihi < 30 gün ise kırmızı, stok < 10 ise turuncu kenarlık çizer.
 * Karta tıklayınca ilaç düzenleme formu açılıyor.
 */
public class MedicineCard extends JPanel {

    // Kartın durumunu belirleyen enum — kenarlık rengine karar vermek için kullanılıyor
    public enum ExpiryMode {
        EXPIRY_URGENT, EXPIRING_SOON, EXPIRY_SAFE, LOW_STOCK
    }

    private final ExpiryMode mode;
    private boolean isHovered = false; // Fare üstündeyken gölge efekti için
    private final Color badgeColor; // Reçete tipi renkli badge nokta rengi

    // Varsayılan mod: EXPIRY_SAFE — hiçbir uyarı yok
    public MedicineCard(Drug drug, String brandName, String presType, Consumer<Drug> onUpdate) {
        this(drug, brandName, presType, ExpiryMode.EXPIRY_SAFE, onUpdate);
    }

    public MedicineCard(Drug drug, String brandName, String presType, ExpiryMode requestedMode,
            Consumer<Drug> onUpdate) {

        // 1. Kartın uyarı modunu belirliyoruz — son kullanma tarihi öncelikli!
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
            this.mode = requestedMode; // Gelen modu doğrudan kullan
        }

        // 2. Reçete tipine göre sağ üst köşedeki renkli badge noktasını ayarla
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
            this.badgeColor = new Color(189, 189, 189); // Beyaz reçete — gri
        }

        setLayout(new BorderLayout(10, 8));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 16, 14, 16));
        setPreferredSize(new Dimension(240, 170));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tıklama → onUpdate çağrılıyor (bu da openMedicineForm'u tetikliyor)
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
                repaint(); // Hover efekti için yenile
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });

        // Üst kısım: İlaç adı
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(drug.getName());
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nameLabel.setForeground(TEXT_PRIMARY);
        top.add(nameLabel, BorderLayout.NORTH);

        // Orta kısım: Marka, Reçete Tipi ve Son Kullanma Tarihi
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(createInfoLabel("Brand: " + brandName));
        center.add(createInfoLabel("Type: " + presType));

        Expiry exp = drug.getExpiry();
        if (exp != null && exp.getExpirationDate() != null) {
            String dateStr = exp.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            JLabel expLabel = createInfoLabel("Exp: " + dateStr);
            // Tarihe yakınsa tarih yazısını kırmızı ve kalın yap
            if (mode == ExpiryMode.EXPIRING_SOON || mode == ExpiryMode.EXPIRY_URGENT) {
                expLabel.setForeground(new Color(195, 50, 50));
                expLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            }
            center.add(expLabel);
        }

        // Alt kısım: Satış fiyatı (solda) ve stok durumu (sağda)
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel priceLabel = new JLabel(String.format("%.2f TL", drug.getSellingPrice().doubleValue()));
        priceLabel.setFont(FONT_LABEL);
        priceLabel.setForeground(ACCENT_DARK);

        // 10'un altındaki stok kırmızı, normal stok yeşil renkte gösterilir
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

    // Gri, küçük bilgi etiketi oluşturmak için yardımcı metot
    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    // Kartın kenarlığını ve arka planını çizen metot — uyarı modu burada rengi belirliyor
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isHovered) {
            // Fare üstteyse gölge efekti ekle
            g2.setColor(SHADOW_COLOR);
            g2.fillRoundRect(3, 3, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
            g2.setColor(BG_CARD_HOVER);
        } else {
            g2.setColor(BG_CARD);
        }

        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, CARD_RADIUS, CARD_RADIUS);

        // Kenarlık rengini ve kalınlığını uyarı moduna göre ayarla
        if (mode == ExpiryMode.EXPIRY_URGENT || mode == ExpiryMode.EXPIRING_SOON) {
            g2.setColor(new Color(220, 60, 60)); // Kırmızı — SKT kritik
            g2.setStroke(new BasicStroke(3f));
        } else if (mode == ExpiryMode.LOW_STOCK) {
            g2.setColor(new Color(255, 152, 0)); // Turuncu — düşük stok
            g2.setStroke(new BasicStroke(3f));
        } else {
            g2.setColor(new Color(220, 220, 220)); // Normal gri kenarlık
        }
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, CARD_RADIUS, CARD_RADIUS);

        // Sağ üst köşeye reçete tipi renkli badge noktası çiz
        if (badgeColor != null) {
            g2.setColor(badgeColor);
            g2.fillOval(getWidth() - 18, 12, 10, 10);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
