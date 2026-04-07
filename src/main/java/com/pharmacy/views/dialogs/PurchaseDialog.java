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

public class PurchaseDialog extends JDialog {

    private final TransactionController transactionController;
    private final InventoryController inventoryController;
    private final DashboardView parent;
    private final int presetBrandId;

    private JComboBox<Drug> medicineCombo;
    private JSpinner quantitySpinner;

    public PurchaseDialog(DashboardView parent, TransactionController transC, InventoryController invC, int brandId) {
        super(parent, "Purchase Stock", true);
        this.parent = parent;
        this.transactionController = transC;
        this.inventoryController = invC;
        this.presetBrandId = brandId;

        setSize(380, 250);
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

        JLabel lbl = new JLabel("📥 Stock Purchase");
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

        JLabel medLabel = new JLabel("Select Medicine from Brand");
        medLabel.setFont(FONT_LABEL);
        medLabel.setForeground(TEXT_PRIMARY);
        medLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(medLabel);
        form.add(Box.createVerticalStrut(4));

        medicineCombo = new JComboBox<>();
        List<Drug> brandDrugs = inventoryController.getMedicinesByBrand(presetBrandId);
        for (Drug d : brandDrugs) {
            medicineCombo.addItem(d);
        }

        medicineCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Drug) {
                    setText(((Drug) value).getName() + " (" + ((Drug) value).getStockQuantity() + " in stock)");
                }
                return this;
            }
        });

        medicineCombo.setFont(FONT_BODY);
        medicineCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        medicineCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(medicineCombo);
        form.add(Box.createVerticalStrut(16));

        JLabel qtyLabel = new JLabel("Purchase Quantity");
        qtyLabel.setFont(FONT_LABEL);
        qtyLabel.setForeground(TEXT_PRIMARY);
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(qtyLabel);
        form.add(Box.createVerticalStrut(4));

        quantitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 10));
        quantitySpinner.setFont(FONT_BODY);
        quantitySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        quantitySpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(quantitySpinner);
        
        if(brandDrugs.isEmpty()) {
            medicineCombo.setEnabled(false);
            quantitySpinner.setEnabled(false);
        }

        return form;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(new Color(248, 248, 248));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(FONT_BODY);
        cancelBtn.addActionListener(e -> dispose());

        JButton purchaseBtn = new JButton("Buy Stock") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isArmed() ? SUCCESS.darker() : getModel().isRollover() ? SUCCESS.brighter() : SUCCESS);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        purchaseBtn.setFont(FONT_LABEL);
        purchaseBtn.setForeground(Color.WHITE);
        purchaseBtn.setContentAreaFilled(false);
        purchaseBtn.setBorderPainted(false);
        purchaseBtn.setFocusPainted(false);
        purchaseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        purchaseBtn.setBorder(new EmptyBorder(7, 20, 7, 20));
        purchaseBtn.addActionListener(e -> handlePurchase());
        
        if (medicineCombo.getItemCount() == 0) {
            purchaseBtn.setEnabled(false);
        }

        footer.add(cancelBtn);
        footer.add(purchaseBtn);
        return footer;
    }

    private void handlePurchase() {
        Object selected = medicineCombo.getSelectedItem();
        if (!(selected instanceof Drug)) return;

        Drug drug = (Drug) selected;
        int qty = (int) quantitySpinner.getValue();

        boolean success = transactionController.purchaseDrug(drug.getBarcode(), qty);
        if (success) {
            ThemedDialog.showMessage(this, "Stock purchased successfully!", ThemedDialog.Kind.SUCCESS);
            dispose();
            parent.loadTableData();
        } else {
            ThemedDialog.showMessage(this, "Failed to purchase stock.", ThemedDialog.Kind.ERROR);
        }
    }
}
