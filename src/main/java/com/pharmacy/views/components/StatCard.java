package com.pharmacy.views.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static com.pharmacy.views.components.ThemeConstants.*;

// Ana sayfadaki istatistik kutucukları — Envanter, Ciro, Düşük Stok, SKT gibi sayıları gösteriyor
public class StatCard extends JPanel {

    // title: başlık metni, value: gösterilecek sayı/metin, icon: emoji, accent: vurgu rengi
    public StatCard(String title, String value, String icon, Color accent) {
        setOpaque(false);
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setLayout(new BorderLayout(15, 0));
        setPreferredSize(new Dimension(200, 95));

        // Sol taraf: büyük emoji ikonu
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        add(iconLbl, BorderLayout.WEST);

        // Sağ taraf: başlık (küçük gri) + değer (büyük renkli)
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(FONT_SMALL);
        t.setForeground(TEXT_SECONDARY);
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 18));
        v.setForeground(accent); // Renk dışarıdan geliyor — Yeşil, Kırmızı vs.
        info.add(t);
        info.add(v);
        add(info, BorderLayout.CENTER);
    }

    // Kartın yuvarlak köşeli beyaz arka planını çiziyoruz
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
        g2.setColor(new Color(230, 230, 230));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}
