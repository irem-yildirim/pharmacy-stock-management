package com.pharmacy.client.snippets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.client.ApiClient;
import com.pharmacy.entity.Sale;
import com.pharmacy.entity.SaleItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing event logic snippets for the Sales Management form.
 *
 * <p>
 * Assumes your form has:
 * <ul>
 * <li>JTable: tblCart — columns: barcode, quantity, unitPrice</li>
 * <li>JTable: tblSaleHistory — for displaying past sales</li>
 * <li>JLabel: lblTotal — displays computed total</li>
 * </ul>
 * </p>
 */
public class SaleFormSnippet {

    // ── Button: Complete Sale ─────────────────────────────────────────────────

    /**
     * Reads items from the cart JTable and submits a sale to the API.
     */
    public void btnCompleteSale_actionPerformed(JTable tblCart, JLabel lblTotal, JTable tblSaleHistory) {
        DefaultTableModel cartModel = (DefaultTableModel) tblCart.getModel();
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Cart is empty. Add items first.");
            return;
        }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                List<SaleItem> items = new ArrayList<>();

                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    String barcode = cartModel.getValueAt(i, 0).toString();
                    int qty = Integer.parseInt(cartModel.getValueAt(i, 1).toString());
                    BigDecimal price = new BigDecimal(cartModel.getValueAt(i, 2).toString());

                    SaleItem item = new SaleItem();
                    // Set drug reference (just barcode needed — backend resolves full entity)
                    com.pharmacy.entity.Drug drug = new com.pharmacy.entity.Drug();
                    drug.setBarcode(barcode);
                    item.setDrug(drug);
                    item.setQuantity(qty);
                    item.setUnitPrice(price);
                    items.add(item);
                }

                return ApiClient.post("/sales", ApiClient.toJson(items));
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    Sale sale = ApiClient.parseJson(result, Sale.class);
                    JOptionPane.showMessageDialog(null,
                            "Sale completed! ID: " + (sale != null ? sale.getId() : "?")
                                    + " | Total: " + (sale != null ? sale.getTotalAmount() : "?"));
                    cartModel.setRowCount(0); // Clear cart
                    lblTotal.setText("Total: 0.00");
                    btnLoadSaleHistory_actionPerformed(tblSaleHistory); // Refresh history
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error completing sale: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Button: Load Sale History ─────────────────────────────────────────────

    public void btnLoadSaleHistory_actionPerformed(JTable tblSaleHistory) {
        new SwingWorker<List<SaleItem>, Void>() {
            @Override
            protected List<SaleItem> doInBackground() {
                String json = ApiClient.get("/sales/history");
                return ApiClient.parseJsonList(json, new TypeReference<>() {
                });
            }

            @Override
            protected void done() {
                try {
                    List<SaleItem> history = get();
                    DefaultTableModel model = (DefaultTableModel) tblSaleHistory.getModel();
                    model.setRowCount(0);

                    for (SaleItem item : history) {
                        model.addRow(new Object[] {
                                item.getId(),
                                item.getDrug() != null ? item.getDrug().getBarcode() : "N/A",
                                item.getQuantity(),
                                item.getUnitPrice()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading history: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
