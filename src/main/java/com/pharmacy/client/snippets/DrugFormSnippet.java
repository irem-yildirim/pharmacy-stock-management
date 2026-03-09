package com.pharmacy.client.snippets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.client.ApiClient;
import com.pharmacy.entity.Drug;
import com.pharmacy.pattern.DrugBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Swing event logic snippets for the Drug Management form.
 *
 * <p>
 * <b>Usage:</b> Paste the relevant method body into your IntelliJ GUI Builder
 * button
 * click event handler. Do NOT copy class/method declarations — only the body.
 * </p>
 *
 * <p>
 * All API calls use {@link SwingWorker} to avoid freezing the UI thread (EDT).
 * </p>
 *
 * <p>
 * Assumes your form has these fields (named exactly):
 * <ul>
 * <li>JTextField: txtBarcode, txtName, txtType, txtDose, txtCostPrice,
 * txtSellingPrice, txtStock</li>
 * <li>JTextField: txtProductionDate, txtExpirationDate (format:
 * yyyy-MM-dd)</li>
 * <li>JTable: tblDrugs (with DefaultTableModel)</li>
 * </ul>
 * </p>
 */
public class DrugFormSnippet {

    // ── Button: Load All Drugs ────────────────────────────────────────────────

    /**
     * Paste this into your "Load / Refresh" button's actionPerformed body.
     * Replace 'tblDrugs' with your JTable variable name.
     */
    public void btnLoadDrugs_actionPerformed(JTable tblDrugs) {
        new SwingWorker<List<Drug>, Void>() {
            @Override
            protected List<Drug> doInBackground() {
                String json = ApiClient.get("/drugs");
                return ApiClient.parseJsonList(json, new TypeReference<>() {
                });
            }

            @Override
            protected void done() {
                try {
                    List<Drug> drugs = get();
                    DefaultTableModel model = (DefaultTableModel) tblDrugs.getModel();
                    model.setRowCount(0); // Clear existing rows

                    for (Drug d : drugs) {
                        model.addRow(new Object[] {
                                d.getBarcode(),
                                d.getName(),
                                d.getType(),
                                d.getDose(),
                                d.getCostPrice(),
                                d.getSellingPrice(),
                                d.getStockQuantity(),
                                d.getExpirationDate()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading drugs: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Button: Save Drug (Add or Update) ────────────────────────────────────

    /**
     * Paste into your "Save" button's actionPerformed body.
     * Uses DrugBuilder (Builder Pattern) to construct the Drug object.
     */
    public void btnSaveDrug_actionPerformed(
            JTextField txtBarcode, JTextField txtName, JTextField txtType, JTextField txtDose,
            JTextField txtCostPrice, JTextField txtSellingPrice, JTextField txtStock,
            JTextField txtProductionDate, JTextField txtExpirationDate, JTable tblDrugs) {

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                // Use DrugBuilder — Builder Pattern
                Drug drug = new DrugBuilder()
                        .barcode(txtBarcode.getText().trim())
                        .name(txtName.getText().trim())
                        .type(txtType.getText().trim())
                        .dose(txtDose.getText().trim())
                        .costPrice(new BigDecimal(txtCostPrice.getText().trim()))
                        .sellingPrice(new BigDecimal(txtSellingPrice.getText().trim()))
                        .stockQuantity(Integer.parseInt(txtStock.getText().trim()))
                        .productionDate(LocalDate.parse(txtProductionDate.getText().trim()))
                        .expirationDate(LocalDate.parse(txtExpirationDate.getText().trim()))
                        .build();

                return ApiClient.post("/drugs", ApiClient.toJson(drug));
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(null, "Drug saved successfully!");
                    btnLoadDrugs_actionPerformed(tblDrugs); // Refresh table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error saving drug: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Button: Delete Drug ───────────────────────────────────────────────────

    /**
     * Paste into your "Delete" button's actionPerformed body.
     */
    public void btnDeleteDrug_actionPerformed(JTextField txtBarcode, JTable tblDrugs) {
        String barcode = txtBarcode.getText().trim();
        if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter or select a barcode.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Delete drug: " + barcode + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                return ApiClient.delete("/drugs/" + barcode);
            }

            @Override
            protected void done() {
                try {
                    int status = get();
                    if (status == 204) {
                        JOptionPane.showMessageDialog(null, "Drug deleted.");
                        btnLoadDrugs_actionPerformed(tblDrugs);
                    } else {
                        JOptionPane.showMessageDialog(null, "Delete failed. Status: " + status);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Load Categories into JComboBox ────────────────────────────────────────

    /**
     * Call this inside your form's constructor or a WindowOpened event
     * to populate your Category dropdown.
     */
    public void loadCategories(JComboBox<String> cmbCategory) {
        new SwingWorker<List<com.pharmacy.entity.Category>, Void>() {
            @Override
            protected List<com.pharmacy.entity.Category> doInBackground() {
                String json = ApiClient.get("/categories");
                return ApiClient.parseJsonList(json, new TypeReference<>() {
                });
            }

            @Override
            protected void done() {
                try {
                    List<com.pharmacy.entity.Category> categories = get();
                    cmbCategory.removeAllItems();
                    for (com.pharmacy.entity.Category c : categories) {
                        // In a real app, you might use a custom wrapper or ListCellRenderer
                        // to store the Category object but display its name.
                        cmbCategory.addItem(c.getName());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading categories: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
