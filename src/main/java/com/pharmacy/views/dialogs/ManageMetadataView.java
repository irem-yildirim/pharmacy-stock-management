package com.pharmacy.views.dialogs;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Category;
import com.pharmacy.entity.PresType;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.components.ThemedDialog;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ManageMetadataView extends JFrame {

    private final InventoryController controller;
    
    private DefaultListModel<Brand> brandModel;
    private DefaultListModel<Category> catModel;
    private DefaultListModel<PresType> presModel;
    
    private JList<Brand> brandList;
    private JList<Category> catList;
    private JList<PresType> presList;

    public ManageMetadataView(DashboardView parent, InventoryController controller) {
        this.controller = controller;
        setTitle("Manage Metadata");
        setSize(800, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG_LIGHT);
        
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(SIDEBAR_BG);
        JLabel title = new JLabel("⚙️ Metadata Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        
        add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        brandModel = new DefaultListModel<>();
        catModel = new DefaultListModel<>();
        presModel = new DefaultListModel<>();

        brandList = createStyledList(brandModel);
        catList = createStyledList(catModel);
        presList = createStyledList(presModel);

        centerPanel.add(createColumn("Brands", brandList, () -> deleteSelectedBrand()));
        centerPanel.add(createColumn("Categories", catList, () -> deleteSelectedCategory()));
        centerPanel.add(createColumn("Prescription Types", presList, null));

        add(centerPanel, BorderLayout.CENTER);
    }

    private <T> JList<T> createStyledList(DefaultListModel<T> model) {
        JList<T> list = new JList<>(model);
        list.setFont(FONT_BODY);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }

    private JPanel createColumn(String title, JList<?> list, Runnable onDeleteClick) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(BG_WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title));
        
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);

        if(onDeleteClick != null) {
            JButton btnDelete = new JButton("Delete Selected");
            btnDelete.setForeground(DANGER);
            btnDelete.addActionListener(e -> onDeleteClick.run());
            p.add(btnDelete, BorderLayout.SOUTH);
        }
        return p;
    }

    private void loadData() {
        brandModel.clear();
        for(Brand b : controller.getAllBrands()) {
            brandModel.addElement(b);
        }
        
        catModel.clear();
        for(Category c : controller.getAllCategories()) {
            catModel.addElement(c);
        }

        presModel.clear();
        for(PresType p : controller.getAllPresTypes()) {
            presModel.addElement(p);
        }
    }

    private void deleteSelectedBrand() {
        Brand sel = brandList.getSelectedValue();
        if(sel != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete brand " + sel.getBrandName() + "?");
            if(confirm == JOptionPane.YES_OPTION) {
                String result = controller.deleteBrandSafely(sel.getBrandId());
                if("SUCCESS".equals(result)) {
                    loadData();
                } else {
                    ThemedDialog.showMessage(this, result, ThemedDialog.Kind.ERROR);
                }
            }
        }
    }

    private void deleteSelectedCategory() {
        Category sel = catList.getSelectedValue();
        if(sel != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete category " + sel.getName() + "?");
            if(confirm == JOptionPane.YES_OPTION) {
                String result = controller.deleteCategorySafely(sel.getId());
                if("SUCCESS".equals(result)) {
                    loadData();
                } else {
                    ThemedDialog.showMessage(this, result, ThemedDialog.Kind.ERROR);
                }
            }
        }
    }
}
