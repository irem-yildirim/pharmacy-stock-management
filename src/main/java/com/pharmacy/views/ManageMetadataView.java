package com.pharmacy.views;

import com.pharmacy.controllers.MedicineController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.pharmacy.views.ThemeConstants.*;

/**
 * Management window for Categories and Brands with safe deletion logic.
 */
public class ManageMetadataView extends JDialog {
    private final MedicineController controller;
    private final DefaultListModel<Category> categoryModel;
    private final DefaultListModel<Brand> brandModel;
    private JList<Category> categoryList;
    private JList<Brand> brandList;

    public ManageMetadataView(Frame parent, MedicineController controller) {
        super(parent, "Manage Metadata", true);
        this.controller = controller;
        this.categoryModel = new DefaultListModel<>();
        this.brandModel = new DefaultListModel<>();

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_WHITE);

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(new EmptyBorder(0, 15, 0, 15));
        
        JLabel title = new JLabel("⚙️ Data Management");
        title.setFont(FONT_LABEL);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_LABEL);
        tabs.setBackground(BG_WHITE);

        tabs.addTab("Categories", buildCategoryTab());
        tabs.addTab("Brands", buildBrandTab());

        add(tabs, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setBackground(new Color(245, 245, 245));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        
        JButton closeBtn = createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildCategoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        categoryList = new JList<>(categoryModel);
        categoryList.setFont(FONT_BODY);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(" 📁  " + ((Category) value).getName());
                }
                setBorder(new EmptyBorder(5, 5, 5, 5));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(categoryList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scroll, BorderLayout.CENTER);

        JButton delBtn = createDangerButton("Delete Selected Category");
        delBtn.addActionListener(e -> {
            Category selected = categoryList.getSelectedValue();
            if (selected != null) {
                String result = controller.deleteCategorySafely(selected.getId());
                if ("SUCCESS".equals(result)) {
                    ThemedDialog.showMessage(this, "Category deleted successfully.", ThemedDialog.Kind.SUCCESS);
                    refreshData();
                } else {
                    ThemedDialog.showMessage(this, result, ThemedDialog.Kind.ERROR);
                }
            }
        });
        
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnPanel.add(delBtn, BorderLayout.EAST);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildBrandTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        brandList = new JList<>(brandModel);
        brandList.setFont(FONT_BODY);
        brandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        brandList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Brand) {
                    setText(" 🏷️  " + ((Brand) value).getBrandName());
                }
                setBorder(new EmptyBorder(5, 5, 5, 5));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(brandList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scroll, BorderLayout.CENTER);

        JButton delBtn = createDangerButton("Delete Selected Brand");
        delBtn.addActionListener(e -> {
            Brand selected = brandList.getSelectedValue();
            if (selected != null) {
                String result = controller.deleteBrandSafely(selected.getBrandId());
                if ("SUCCESS".equals(result)) {
                    ThemedDialog.showMessage(this, "Brand deleted successfully.", ThemedDialog.Kind.SUCCESS);
                    refreshData();
                } else {
                    ThemedDialog.showMessage(this, result, ThemedDialog.Kind.ERROR);
                }
            }
        });

        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnPanel.add(delBtn, BorderLayout.EAST);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshData() {
        categoryModel.clear();
        for (Category c : controller.getAllCategories()) {
            categoryModel.addElement(c);
        }
        brandModel.clear();
        for (Brand b : controller.getAllBrands()) {
            brandModel.addElement(b);
        }
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isArmed() ? new Color(150, 30, 30) : getModel().isRollover() ? new Color(200, 50, 50) : new Color(180, 40, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
