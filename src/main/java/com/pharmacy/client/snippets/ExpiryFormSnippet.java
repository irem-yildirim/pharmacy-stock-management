package com.pharmacy.client.snippets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.client.ApiClient;
import com.pharmacy.entity.Expiry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Swing event logic snippets for the Expiry Tracking form.
 *
 * <p>
 * Assumes your form has:
 * <ul>
 * <li>JTable: tblExpiry — columns: barcode, drugName, daysRemaining,
 * status</li>
 * <li>Status is color-coded in the UI (red=EXPIRED, orange=CRITICAL,
 * green=OK)</li>
 * </ul>
 * </p>
 */
public class ExpiryFormSnippet {

    // ── Button: Refresh Expiry ────────────────────────────────────────────────

    /**
     * Triggers backend to recalculate all expiry records, then reloads the table.
     */
    public void btnRefreshExpiry_actionPerformed(JTable tblExpiry) {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return ApiClient.post("/expiry/refresh", "");
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(null, "Expiry records refreshed!");
                    btnLoadAllExpiry_actionPerformed(tblExpiry);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error refreshing: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Button: Show All Expiry ───────────────────────────────────────────────

    public void btnLoadAllExpiry_actionPerformed(JTable tblExpiry) {
        loadExpiry("/expiry", tblExpiry);
    }

    // ── Button: Show Expired Only ─────────────────────────────────────────────

    public void btnShowExpired_actionPerformed(JTable tblExpiry) {
        loadExpiry("/expiry/expired", tblExpiry);
    }

    // ── Button: Show Critical Only ────────────────────────────────────────────

    public void btnShowCritical_actionPerformed(JTable tblExpiry) {
        loadExpiry("/expiry/critical", tblExpiry);
    }

    // ── Private Helper ────────────────────────────────────────────────────────

    private void loadExpiry(String endpoint, JTable tblExpiry) {
        new SwingWorker<List<Expiry>, Void>() {
            @Override
            protected List<Expiry> doInBackground() {
                String json = ApiClient.get(endpoint);
                return ApiClient.parseJsonList(json, new TypeReference<>() {
                });
            }

            @Override
            protected void done() {
                try {
                    List<Expiry> records = get();
                    DefaultTableModel model = (DefaultTableModel) tblExpiry.getModel();
                    model.setRowCount(0);

                    for (Expiry e : records) {
                        model.addRow(new Object[] {
                                e.getId(),
                                e.getDaysRemaining(),
                                e.getStatus()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading expiry: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
