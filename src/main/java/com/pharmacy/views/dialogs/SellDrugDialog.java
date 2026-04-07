package com.pharmacy.views.dialogs;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.entity.Drug;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.components.ThemedDialog;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class SellDrugDialog extends JDialog {

    private final TransactionController transactionController;
    private final InventoryController inventoryController;
    private final DashboardView parent;

    private JComboBox<Drug> medicineCombo;
    private JSpinner quantitySpinner;
    private JLabel totalLabel;

    public SellDrugDialog(DashboardView parent, TransactionController transC, InventoryController invC) {
        super(parent, "Sell Medicine (POS)", true);
        this.parent = parent;
        this.transactionController = transC;
        this.inventoryController = invC;

        setSize(400, 360);
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

        JLabel lbl = new JLabel("🛒 Sell Medicine");
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(Color.WHITE);
        header.add(lbl, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG_WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        JLabel medLabel = new JLabel("Select Medicine");
        medLabel.setFont(FONT_LABEL);
        medLabel.setForeground(TEXT_PRIMARY);
        medLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(medLabel);
        form.add(Box.createVerticalStrut(4));

        medicineCombo = new JComboBox<>();
        List<Drug> allMeds = inventoryController.getAllMedicines();
        for (Drug m : allMeds) {
            if (m.getStockQuantity() > 0) {
                medicineCombo.addItem(m);
            }
        }
        
        medicineCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Drug) {
                    Drug m = (Drug) value;
                    setText(m.getName() + " (" + m.getDose() + ") - Stock: " + m.getStockQuantity());
                }
                return this;
            }
        });
        
        medicineCombo.setFont(FONT_BODY);
        medicineCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        medicineCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        medicineCombo.addActionListener(e -> updateTotal());
        form.add(medicineCombo);
        form.add(Box.createVerticalStrut(16));

        JLabel qtyLabel = new JLabel("Quantity");
        qtyLabel.setFont(FONT_LABEL);
        qtyLabel.setForeground(TEXT_PRIMARY);
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(qtyLabel);
        form.add(Box.createVerticalStrut(4));

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantitySpinner.setFont(FONT_BODY);
        quantitySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        quantitySpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantitySpinner.addChangeListener(e -> updateTotal());
        form.add(quantitySpinner);
        form.add(Box.createVerticalStrut(24));

        totalLabel = new JLabel("Total: 0.00 TL");
        totalLabel.setFont(FONT_TITLE);
        totalLabel.setForeground(ACCENT_DARK);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(totalLabel);

        updateTotal();

        return form;
    }

    private void updateTotal() {
        Object selected = medicineCombo.getSelectedItem();
        if (selected instanceof Drug) {
            Drug drug = (Drug) selected;
            int qty = (int) quantitySpinner.getValue();
            double total = drug.getSellingPrice().doubleValue() * qty;
            totalLabel.setText(String.format("Total: %.2f TL", total));
            
            SpinnerNumberModel model = (SpinnerNumberModel) quantitySpinner.getModel();
            model.setMaximum(drug.getStockQuantity());
            if (qty > drug.getStockQuantity()) {
                quantitySpinner.setValue(drug.getStockQuantity());
            }
        }
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(new Color(248, 248, 248));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(FONT_BODY);
        cancelBtn.addActionListener(e -> dispose());

        JButton sellBtn = new JButton("Complete Sale") {
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
        sellBtn.setFont(FONT_LABEL);
        sellBtn.setForeground(Color.WHITE);
        sellBtn.setContentAreaFilled(false);
        sellBtn.setBorderPainted(false);
        sellBtn.setFocusPainted(false);
        sellBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sellBtn.setBorder(new EmptyBorder(7, 20, 7, 20));
        sellBtn.addActionListener(e -> handleSale());

        footer.add(cancelBtn);
        footer.add(sellBtn);
        return footer;
    }

    private void handleSale() {
        Object selected = medicineCombo.getSelectedItem();
        if (!(selected instanceof Drug)) {
            ThemedDialog.showMessage(this, "No medicine selected.", ThemedDialog.Kind.ERROR);
            return;
        }

        Drug drug = (Drug) selected;
        int qty = (int) quantitySpinner.getValue();
        
        boolean success = transactionController.sellDrug(drug.getBarcode(), qty);
        
        if (success) {
            ThemedDialog.showMessage(this, "Sale completed successfully!", ThemedDialog.Kind.SUCCESS);
            dispose();
            parent.loadTableData();
        } else {
            ThemedDialog.showMessage(this, "Sale failed. Please check stock levels.", ThemedDialog.Kind.ERROR);
        }
    }
}
