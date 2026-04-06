package com.pharmacy.views;

import com.pharmacy.controllers.MedicineController;
import com.pharmacy.models.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import static com.pharmacy.views.ThemeConstants.*;

/**
 * Modal dialog form for adding or editing a Medicine.
 */
public class MedicineFormView extends JDialog {

    private final MedicineController controller;
    private final Medicine medicine; // null = add mode
    private final DashboardView parent;

    private JTextField nameField, doseField, costField, priceField, qtyField, barcodeField;
    private JComboBox<MedicineCategory> categoryCombo;
    private JComboBox<Brand> brandCombo;

    public MedicineFormView(DashboardView parent, MedicineController controller, Medicine medicine) {
        super(parent, medicine == null ? "Add Medicine" : "Edit Medicine", true);
        this.parent = parent;
        this.controller = controller;
        this.medicine = medicine;

        setSize(420, 520);
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

        String title = medicine == null ? "\uD83D\uDC8A Add New Medicine"
                : "\u270F\uFE0F Edit: " + medicine.getMedName();
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
        doseField = addField(form, "Dose");
        costField = addField(form, "Cost Price");
        
        if (medicine != null) {
            // Quick Update Box for Price & Stock
            JPanel quickPanel = new JPanel();
            quickPanel.setLayout(new BoxLayout(quickPanel, BoxLayout.Y_AXIS));
            quickPanel.setBackground(BG_CARD);
            quickPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1, true),
                new EmptyBorder(12, 16, 4, 16)
            ));
            quickPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel quickLabel = new JLabel("⚡ Quick Update");
            quickLabel.setFont(FONT_HEADER);
            quickLabel.setForeground(ACCENT_DARK);
            quickPanel.add(quickLabel);
            quickPanel.add(Box.createVerticalStrut(10));
            
            priceField = addField(quickPanel, "Selling Price");
            qtyField = addField(quickPanel, "Quantity");
            
            form.add(quickPanel);
            form.add(Box.createVerticalStrut(12));
        } else {
            priceField = addField(form, "Selling Price");
            qtyField = addField(form, "Quantity");
        }

        // Category combo
        JLabel catLabel = new JLabel("Category");
        catLabel.setFont(FONT_LABEL);
        catLabel.setForeground(TEXT_PRIMARY);
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(catLabel);
        form.add(Box.createVerticalStrut(4));

        categoryCombo = new JComboBox<>();
        List<MedicineCategory> cats = controller.getAllCategories();
        for (MedicineCategory c : cats)
            categoryCombo.addItem(c);
        categoryCombo.setFont(FONT_BODY);
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(categoryCombo);
        form.add(Box.createVerticalStrut(12));

        // Brand combo
        JLabel brandLabel = new JLabel("Brand");
        brandLabel.setFont(FONT_LABEL);
        brandLabel.setForeground(TEXT_PRIMARY);
        brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(brandLabel);
        form.add(Box.createVerticalStrut(4));

        brandCombo = new JComboBox<>();
        List<Brand> brands = controller.getAllBrands();
        for (Brand b : brands) {
            brandCombo.addItem(b);
        }
        brandCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Brand) {
                    setText(((Brand) value).getBrandName());
                }
                return this;
            }
        });
        brandCombo.setFont(FONT_BODY);
        brandCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        brandCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(brandCombo);
        form.add(Box.createVerticalStrut(12));

        // Populate if editing
        if (medicine != null) {
            barcodeField.setText(String.valueOf(medicine.getMedId()));
            barcodeField.setEditable(false);
            nameField.setText(medicine.getMedName());
            doseField.setText(medicine.getDose());
            costField.setText(String.valueOf(medicine.getCost()));
            priceField.setText(String.valueOf(medicine.getPrice()));
            qtyField.setText(String.valueOf(medicine.getQuantity()));

            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                if (categoryCombo.getItemAt(i).getCatId() == medicine.getCatId()) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < brandCombo.getItemCount(); i++) {
                if (brandCombo.getItemAt(i).getBrandId() == medicine.getBrandId()) {
                    brandCombo.setSelectedIndex(i);
                    break;
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
        saveBtn.setFocusPainted(false);
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
            Medicine med = new Medicine();
            med.setMedId(Integer.parseInt(barcodeField.getText().trim()));
            med.setMedName(nameField.getText().trim());
            med.setDose(doseField.getText().trim());
            med.setCost(Double.parseDouble(costField.getText().trim()));
            med.setPrice(Double.parseDouble(priceField.getText().trim()));
            med.setQuantity(Integer.parseInt(qtyField.getText().trim()));

            MedicineCategory selectedCat = (MedicineCategory) categoryCombo.getSelectedItem();
            if (selectedCat != null)
                med.setCatId(selectedCat.getCatId());

            Brand selectedBrand = (Brand) brandCombo.getSelectedItem();
            if (selectedBrand != null)
                med.setBrandId(selectedBrand.getBrandId());

            med.setPresId(1); // Defaulting presId unless explicitly requested to be a combo too

            boolean success;
            if (medicine == null) {
                success = controller.addMedicine(med);
            } else {
                success = controller.updateMedicine(med);
            }

            if (success) {
                ThemedDialog.showMessage(this, medicine == null ? "Medicine added!" : "Medicine updated!",
                        ThemedDialog.Kind.SUCCESS);
                dispose();
                parent.loadTableData();
            }
        } catch (NumberFormatException ex) {
            ThemedDialog.showMessage(this, "Please enter valid numbers for price, cost and quantity.",
                    ThemedDialog.Kind.ERROR);
        }
    }

    private void handleDelete() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this medicine?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteMedicine(medicine.getMedId());
            ThemedDialog.showMessage(this, "Medicine deleted.", ThemedDialog.Kind.SUCCESS);
            dispose();
            parent.loadTableData();
        }
    }
}
