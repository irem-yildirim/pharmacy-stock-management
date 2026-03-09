package com.pharmacy.client.snippets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.client.ApiClient;
import com.pharmacy.entity.Purchase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

/**
 * Swing event logic snippets for the Purchase / Stock Restock form.
 *
 * <p>
 * Assumes your form has:
 * <ul>
 * <li>JTextField: txtBarcode — drug barcode</li>
 * <li>JTextField: txtQuantity — quantity to add</li>
 * <li>JTable: tblPurchases — purchase history table</li>
 * </ul>
 * </p>
 */
public class PurchaseFormSnippet {

    // ── Button: Add Purchase ──────────────────────────────────────────────────

    public void btnAddPurchase_actionPerformed(
            JTextField txtBarcode, JTextField txtQuantity, JTable tblPurchases) {

        String barcode = txtBarcode.getText().trim();
        String quantityStr = txtQuantity.getText().trim();

        if (barcode.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter barcode and quantity.");
            return;
        }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                // Build the request body as JSON manually (simple map)
                String body = "{\"barcode\":\"" + barcode + "\",\"quantity\":" + quantityStr + "}";
                return ApiClient.post("/purchases", body);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    if (!result.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Purchase recorded successfully!");
                        txtQuantity.setText("");
                        btnLoadPurchases_actionPerformed(tblPurchases);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: Drug not found or server error.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Button: Load Purchase History ─────────────────────────────────────────

    public void btnLoadPurchases_actionPerformed(JTable tblPurchases) {
        new SwingWorker<List<Purchase>, Void>() {
            @Override
            protected List<Purchase> doInBackground() {
                String json = ApiClient.get("/purchases");
                return ApiClient.parseJsonList(json, new TypeReference<>() {
                });
            }

            @Override
            protected void done() {
                try {
                    List<Purchase> purchases = get();
                    DefaultTableModel model = (DefaultTableModel) tblPurchases.getModel();
                    model.setRowCount(0);

                    for (Purchase p : purchases) {
                        model.addRow(new Object[] {
                                p.getId(),
                                p.getDrug() != null ? p.getDrug().getBarcode() : "N/A",
                                p.getQuantityAdded(),
                                p.getPurchaseDate()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading purchases: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
