package com.pharmacy.views;

import com.pharmacy.controllers.LoginController;
import com.pharmacy.controllers.MedicineController;
import com.pharmacy.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.pharmacy.views.ThemeConstants.*;

public class LoginView extends JFrame {
    private final LoginController loginController;
    private JComboBox<User> userCombo;
    private JPasswordField passwordField;
    private RoundedButton loginButton;
    private JLabel roleLabel;

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        setTitle("Pharmacy Management System - Login");
        setSize(360, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(SIDEBAR_BG);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("💊 Pharmacy Login");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User label
        JLabel userLabel = new JLabel("Select User");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        userLabel.setForeground(new Color(180, 200, 210));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userCombo = new StyledUserCombo();
        userCombo.setMaximumSize(new Dimension(300, 45));
        userCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (User u : loginController.getAllUsers()) {
            userCombo.addItem(u);
        }

        // Role badge — updates dynamically when user selection changes
        roleLabel = new JLabel("");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        roleLabel.setForeground(ACCENT);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateRoleLabel();

        userCombo.addActionListener(e -> updateRoleLabel());

        // Password label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        passLabel.setForeground(new Color(180, 200, 210));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new RoundedPasswordField(20, "Enter password...");
        passwordField.setMaximumSize(new Dimension(300, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton = new RoundedButton("Login");
        loginButton.setMaximumSize(new Dimension(300, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(this::handleLogin);
        getRootPane().setDefaultButton(loginButton);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(userLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(userCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(roleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        mainPanel.add(passLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 26)));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    private void updateRoleLabel() {
        User selected = (User) userCombo.getSelectedItem();
        if (selected != null && selected.getRole() != null) {
            roleLabel.setText("\uD83D\uDD11 Role: " + selected.getRole());
        } else {
            roleLabel.setText("");
        }
    }

    // SWINGWORKER (ASYNC EDT PROTECTION)
    private void handleLogin(ActionEvent e) {
        User selectedUser = (User) userCombo.getSelectedItem();
        String password = new String(passwordField.getPassword());

        if (selectedUser == null || password.isEmpty()) {
            ThemedDialog.showMessage(this, "Valid User and Password required.", ThemedDialog.Kind.ERROR);
            return;
        }

        loginButton.setText("Connecting...");
        loginButton.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                // Saf veritabanı isteği arkaplanda çalışır
                return loginController.login(selectedUser.getUsername(), password);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        LoginView.this.dispose();
                        MedicineController medController = loginController.getMedicineController();
                        DashboardView dashboard = new DashboardView(medController);
                        dashboard.setVisible(true);
                    } else {
                        ThemedDialog.showMessage(LoginView.this, "Invalid credentials.", ThemedDialog.Kind.ERROR);
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    ThemedDialog.showMessage(LoginView.this, "System error.", ThemedDialog.Kind.ERROR);
                } finally {
                    loginButton.setText("Login");
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }

    static class StyledUserCombo extends JComboBox<User> {
        public StyledUserCombo() {
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setForeground(new Color(60, 60, 60));
            setBorder(new EmptyBorder(0, 0, 0, 0));
            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                            cellHasFocus);
                    if (value instanceof User) {
                        lbl.setText(((User) value).getName() + " (" + ((User) value).getRole() + ")");
                    } else if (value != null) {
                        lbl.setText(value.toString());
                    }
                    lbl.setBorder(new EmptyBorder(6, 44, 6, 10));
                    lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    if (isSelected) {
                        lbl.setBackground(ACCENT);
                        lbl.setForeground(Color.WHITE);
                    } else {
                        lbl.setBackground(Color.WHITE);
                        lbl.setForeground(new Color(60, 60, 60));
                    }
                    return lbl;
                }
            });
            setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton btn = new JButton("▾");
                    btn.setBorder(new EmptyBorder(0, 4, 0, 10));
                    btn.setContentAreaFilled(false);
                    return btn;
                }

                @Override
                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(242, 242, 242));
                    g2.fillRoundRect(bounds.x, bounds.y, bounds.width + 20, bounds.height, 30, 30);
                    g2.dispose();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(242, 242, 242));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedPasswordField extends JPasswordField {
        private final String hint;

        public RoundedPasswordField(int columns, String hint) {
            super(columns);
            this.hint = hint;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 42, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(242, 242, 242));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            super.paintComponent(g);

            // Draw placeholder hint when empty and not focused
            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setColor(new Color(160, 160, 160));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(hint, 42, getHeight() / 2 + fm.getAscent() / 2 - 1);
            }
            g2.dispose();
        }
    }

    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 15));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isArmed() ? ACCENT_DARK : getModel().isRollover() ? ACCENT_HOVER : ACCENT);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}
