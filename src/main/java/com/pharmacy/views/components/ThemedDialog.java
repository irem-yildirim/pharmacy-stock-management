package com.pharmacy.views.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.pharmacy.views.components.ThemeConstants.*;

/**
 * Utility class providing themed modal dialogs that match the app design
 * system.
 */
public class ThemedDialog {

    public enum Kind {
        INFO, SUCCESS, ERROR
    }

    public static void showMessage(Component parent, String message) {
        showMessage(parent, message, Kind.INFO);
    }

    public static void showMessage(Component parent, String message, Kind kind) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog d;
        if (owner instanceof Frame) {
            d = new JDialog((Frame) owner, "", true);
        } else if (owner instanceof Dialog) {
            d = new JDialog((Dialog) owner, "", true);
        } else {
            d = new JDialog((Frame) null, "", true);
        }
        d.setResizable(false);
        d.setSize(360, 190);
        d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(BG_WHITE);

        final Color iconBg = kind == Kind.ERROR ? new Color(240, 80, 80)
                : kind == Kind.SUCCESS ? new Color(50, 160, 90)
                        : ACCENT;

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2, r = 18;
                g2.setColor(iconBg);
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(Color.WHITE);
                String sym = kind == Kind.ERROR ? "!" : kind == Kind.SUCCESS ? "\u2713" : "i";
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(sym, cx - fm.stringWidth(sym) / 2, cy + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(52, 0));

        JLabel msgLbl = new JLabel("<html><body style='width:220px'>" + message + "</body></html>");
        msgLbl.setFont(FONT_BODY);
        msgLbl.setForeground(TEXT_PRIMARY);
        msgLbl.setBorder(new EmptyBorder(0, 12, 0, 20));

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_WHITE);
        body.setBorder(new EmptyBorder(28, 20, 10, 0));
        body.add(iconPanel, BorderLayout.WEST);
        body.add(msgLbl, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        footer.setBackground(new Color(248, 248, 248));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)));

        JButton okBtn = createAccentButton("OK", iconBg);
        okBtn.addActionListener(e -> d.dispose());
        d.getRootPane().setDefaultButton(okBtn);
        footer.add(okBtn);

        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        d.setVisible(true);
    }

    public static String showInput(Component parent, String prompt) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog d;
        if (owner instanceof Frame) {
            d = new JDialog((Frame) owner, "", true);
        } else if (owner instanceof Dialog) {
            d = new JDialog((Dialog) owner, "", true);
        } else {
            d = new JDialog((Frame) null, "", true);
        }
        d.setResizable(false);
        d.setSize(400, 220);
        d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(BG_WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);
        header.setPreferredSize(new Dimension(0, 52));
        header.setBorder(new EmptyBorder(0, 18, 0, 18));
        JLabel titleLbl = new JLabel("\uD83D\uDC8A " + prompt);
        titleLbl.setFont(FONT_LABEL);
        titleLbl.setForeground(Color.WHITE);
        header.add(titleLbl, BorderLayout.CENTER);

        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(new Color(242, 242, 242));
        field.setPreferredSize(new Dimension(0, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true),
                new EmptyBorder(8, 14, 8, 14)));

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_WHITE);
        body.setBorder(new EmptyBorder(22, 22, 10, 22));
        body.add(field, BorderLayout.NORTH);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(new Color(248, 248, 248));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)));

        JButton cancelBtn = createOutlineButton("Cancel");
        JButton okBtn = createAccentButton("OK", ACCENT);

        final String[] result = { null };
        cancelBtn.addActionListener(e -> d.dispose());
        okBtn.addActionListener(e -> {
            result[0] = field.getText();
            d.dispose();
        });
        d.getRootPane().setDefaultButton(okBtn);

        footer.add(cancelBtn);
        footer.add(okBtn);

        d.add(header, BorderLayout.NORTH);
        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
        return result[0];
    }

    private static JButton createAccentButton(String text, Color color) {
        final Color col = color;
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isArmed() ? col.darker()
                        : getModel().isRollover() ? col.brighter()
                                : col;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 20, 7, 20));
        return btn;
    }

    private static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isArmed() ? new Color(225, 225, 225)
                        : getModel().isRollover() ? new Color(240, 240, 240)
                                : BG_WHITE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT_SECONDARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }
}
