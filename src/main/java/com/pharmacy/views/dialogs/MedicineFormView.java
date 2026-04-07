package com.pharmacy.views.dialogs;

import static com.pharmacy.views.components.ThemeConstants.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Category;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.PresType;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.components.ThemedDialog;

/**
 * Yeni ilaç ekleme veya mevcut olanı güncelleme ekranı.
 * Ana pencerenin üzerine açılan (Modal) temiz bir form yapısı.
 */
public class MedicineFormView extends JDialog {

    private final InventoryController controller;
    private final Drug medicine; // null = add mode
    private final DashboardView parent;

    private JTextField nameField, costField, priceField, qtyField, barcodeField, expiryField;
    private JComboBox<Category> categoryCombo;
    private JComboBox<Brand> brandCombo;
    private JComboBox<PresType> presCombo;

    public MedicineFormView(DashboardView parent, InventoryController controller, Drug medicine) {
        super(parent, medicine == null ? "Add Medicine" : "Edit Medicine", true);
        this.parent = parent;
        this.controller = controller;
        this.medicine = medicine;

        setSize(420, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);
        header.setPreferredSize(new Dimension(0, 52));
        header.setBorder(new EmptyBorder(0, 18, 0, 18));

        String title = medicine == null ? "💊 Add New Medicine"
                : "📝 Edit: " + medicine.getName();
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(Color.WHITE);
        header.add(lbl, BorderLayout.CENTER);
        return header;
    }

    private JScrollPane buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG_WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        barcodeField = addField(form, "Barcode / ID");
        nameField = addField(form, "Medicine Name");
        costField = addField(form, "Cost Price");

        if (medicine != null) {
            JPanel quickPanel = new JPanel();
            quickPanel.setLayout(new BoxLayout(quickPanel, BoxLayout.Y_AXIS));
            quickPanel.setBackground(BG_CARD);
            quickPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1, true),
                    new EmptyBorder(12, 16, 4, 16)));
            quickPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel quickLabel = new JLabel("⚡ Quick Update");
            quickLabel.setFont(FONT_HEADER);
            quickLabel.setForeground(ACCENT_DARK);
            quickPanel.add(quickLabel);
            quickPanel.add(Box.createVerticalStrut(10));

            priceField = addField(quickPanel, "Selling Price");
            qtyField = addField(quickPanel, "Stock Quantity");
            expiryField = addField(quickPanel, "Expiry Date (YYYY-MM-DD)");

            form.add(quickPanel);
            form.add(Box.createVerticalStrut(12));
        } else {
            priceField = addField(form, "Selling Price");
            qtyField = addField(form, "Stock Quantity");
            expiryField = addField(form, "Expiry Date (YYYY-MM-DD)");
        }

        form.add(new JLabel("Category") {
            {
                setFont(FONT_LABEL);
                setForeground(TEXT_PRIMARY);
            }
        });
        form.add(Box.createVerticalStrut(4));
        categoryCombo = new JComboBox<>();
        for (Category c : controller.getAllCategories())
            categoryCombo.addItem(c);
        categoryCombo.setFont(FONT_BODY);
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category)
                    setText(((Category) value).getName());
                return this;
            }
        });
        form.add(categoryCombo);
        form.add(Box.createVerticalStrut(12));

        form.add(new JLabel("Brand") {
            {
                setFont(FONT_LABEL);
                setForeground(TEXT_PRIMARY);
            }
        });
        form.add(Box.createVerticalStrut(4));
        brandCombo = new JComboBox<>();
        for (Brand b : controller.getAllBrands())
            brandCombo.addItem(b);
        brandCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Brand)
                    setText(((Brand) value).getBrandName());
                return this;
            }
        });
        brandCombo.setFont(FONT_BODY);
        brandCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        brandCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(brandCombo);
        form.add(Box.createVerticalStrut(12));

        form.add(new JLabel("Prescription Type") {
            {
                setFont(FONT_LABEL);
                setForeground(TEXT_PRIMARY);
            }
        });
        form.add(Box.createVerticalStrut(4));
        presCombo = new JComboBox<>();
        for (PresType p : controller.getAllPresTypes())
            presCombo.addItem(p);
        presCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PresType)
                    setText(((PresType) value).getPrescription());
                return this;
            }
        });
        presCombo.setFont(FONT_BODY);
        presCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        presCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(presCombo);
        form.add(Box.createVerticalStrut(12));

        if (medicine != null) {
            barcodeField.setText(medicine.getBarcode());
            barcodeField.setEditable(false);
            nameField.setText(medicine.getName());
            costField.setText(medicine.getCostPrice().toString());
            priceField.setText(medicine.getSellingPrice().toString());
            qtyField.setText(String.valueOf(medicine.getStockQuantity()));
            if (medicine.getExpiry() != null && medicine.getExpiry().getExpirationDate() != null) {
                expiryField.setText(medicine.getExpiry().getExpirationDate().toString());
            }
            if (medicine.getCategory() != null && medicine.getCategory().getId() != null) {
                for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                    Category cat = categoryCombo.getItemAt(i);
                    // NullPointerException Önlemi: Combo'dan gelen nesne veya id null olabilir
                    if (cat != null && cat.getId() != null && cat.getId().equals(medicine.getCategory().getId())) {
                        categoryCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (medicine.getBrand() != null && medicine.getBrand().getBrandId() != 0) {
                for (int i = 0; i < brandCombo.getItemCount(); i++) {
                    Brand b = brandCombo.getItemAt(i);
                    // NullPointerException Önlemi
                    if (b != null && b.getBrandId() == medicine.getBrand().getBrandId()) {
                        brandCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (medicine.getPresType() != null && medicine.getPresType().getPresId() != 0) {
                for (int i = 0; i < presCombo.getItemCount(); i++) {
                    PresType p = presCombo.getItemAt(i);
                    if (p != null && p.getPresId() == medicine.getPresType().getPresId()) {
                        presCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JTextField addField(JPanel form, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lbl);
        form.add(Box.createVerticalStrut(4));
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        form.add(field);
        form.add(Box.createVerticalStrut(12));
        return field;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(new Color(248, 248, 248));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(FONT_BODY);
        cancelBtn.addActionListener(e -> dispose());
        JButton saveBtn = new JButton(medicine == null ? "Add" : "Save") {
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
        saveBtn.setFont(FONT_LABEL);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setContentAreaFilled(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setBorder(new EmptyBorder(7, 20, 7, 20));
        saveBtn.addActionListener(e -> handleSave());

        if (medicine != null) {
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setFont(FONT_BODY);
            deleteBtn.setForeground(new Color(195, 50, 50));
            deleteBtn.addActionListener(e -> handleDelete());
            footer.add(deleteBtn);
        }
        footer.add(cancelBtn);
        footer.add(saveBtn);
        return footer;
    }

    private void handleSave() {
        try {
            String bcode = barcodeField.getText().trim();
            if (bcode.isEmpty()) {
                ThemedDialog.showMessage(this, "Barcode cannot be empty!", ThemedDialog.Kind.ERROR);
                return;
            }

            Drug d = (medicine == null) ? new Drug() : medicine;
            d.setBarcode(bcode);
            d.setName(nameField.getText().trim());
            d.setCostPrice(new BigDecimal(costField.getText().trim()));
            d.setSellingPrice(new BigDecimal(priceField.getText().trim()));
            d.setStockQuantity(Integer.parseInt(qtyField.getText().trim()));
            d.setCategory((Category) categoryCombo.getSelectedItem());
            d.setBrand((Brand) brandCombo.getSelectedItem());
            d.setPresType((PresType) presCombo.getSelectedItem());

            String expTxt = expiryField.getText().trim();
            if (!expTxt.isEmpty()) {
                try {
                    java.time.LocalDate expDate = java.time.LocalDate.parse(expTxt);
                    com.pharmacy.entity.Expiry exp = new com.pharmacy.entity.Expiry();
                    exp.setExpirationDate(expDate);
                    d.setExpiry(exp);
                } catch (java.time.format.DateTimeParseException ex) {
                    ThemedDialog.showMessage(this, "Please enter date in YYYY-MM-DD format (e.g. 2026-10-15)",
                            ThemedDialog.Kind.ERROR);
                    return;
                }
            }

            if (medicine == null) {
                controller.addMedicine(d);
            } else {
                controller.updateMedicine(d);
            }
            ThemedDialog.showMessage(this, "Success!", ThemedDialog.Kind.SUCCESS);
            dispose();
            parent.loadTableData();
        } catch (Exception ex) {
            ThemedDialog.showMessage(this, "Valid numerical values required.", ThemedDialog.Kind.ERROR);
        }
    }

    private void handleDelete() {
        if (JOptionPane.showConfirmDialog(this, "Delete this item?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.deleteMedicine(medicine.getBarcode());
            ThemedDialog.showMessage(this, "Deleted.", ThemedDialog.Kind.SUCCESS);
            dispose();
            parent.loadTableData();
        }
    }
}
