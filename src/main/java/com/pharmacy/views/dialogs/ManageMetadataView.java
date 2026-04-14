package com.pharmacy.views.dialogs;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Category;
import com.pharmacy.views.DashboardView;
import com.pharmacy.views.components.ThemedDialog;
import static com.pharmacy.views.components.ThemeConstants.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Marka ve kategori kayıtlarını yönetmek için modal dialog.
 * Sadece ADMIN/PHARMACİST rolleri erişebiliyor — STAFF bu menüyü göremez.
 */
public class ManageMetadataView extends JDialog {

    private final InventoryController controller;

    // Ekranda görünen iki liste için veri modelleri
    private DefaultListModel<Brand> brandModel;
    private DefaultListModel<Category> catModel;
    private JList<Brand> brandList;
    private JList<Category> catList;

    public ManageMetadataView(DashboardView parent, InventoryController controller) {
        super(parent, "Manage Metadata", true); // true = arka planı kilitle (modal)
        this.controller = controller;
        setSize(800, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG_LIGHT);

        initComponents();
        loadData(); // Açılınca marka ve kategorileri veritabanından çek
    }

    // Ekranı oluşturuyoruz: sol = markalar, sağ = kategoriler
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(SIDEBAR_BG);
        JLabel title = new JLabel("⚙️ Metadata Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);

        add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        brandModel = new DefaultListModel<>();
        catModel = new DefaultListModel<>();

        brandList = createStyledList(brandModel);
        // Listede sadece marka adını göster
        brandList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Brand) setText(((Brand) value).getBrandName());
                return this;
            }
        });

        catList = createStyledList(catModel);
        // Listede sadece kategori adını göster
        catList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) setText(((Category) value).getName());
                return this;
            }
        });

        // Her iki liste için aynı panel yapısını kullanıyoruz — Runnable ile silme aksiyonunu geçiyoruz
        centerPanel.add(createColumn("Brands", brandList, () -> deleteSelectedBrand()));
        centerPanel.add(createColumn("Categories", catList, () -> deleteSelectedCategory()));

        add(centerPanel, BorderLayout.CENTER);
    }

    // Liste bileşeni için standart stil şablonu
    private <T> JList<T> createStyledList(DefaultListModel<T> model) {
        JList<T> list = new JList<>(model);
        list.setFont(FONT_BODY);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }

    // Başlıklı, kaydırılabilir, "Delete" butonlu panel şablonu — hem marka hem kategori için kullanılıyor
    private JPanel createColumn(String title, JList<?> list, Runnable onDeleteClick) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(BG_WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title));

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);

        if (onDeleteClick != null) {
            JButton btnDelete = new JButton("Delete Selected");
            btnDelete.setForeground(DANGER);
            btnDelete.addActionListener(e -> onDeleteClick.run());
            p.add(btnDelete, BorderLayout.SOUTH);
        }
        return p;
    }

    // Veritabanından tüm marka ve kategorileri çekip listelere yüklüyoruz
    private void loadData() {
        brandModel.clear();
        for (Brand b : controller.getAllBrands()) {
            brandModel.addElement(b);
        }

        catModel.clear();
        for (Category c : controller.getAllCategories()) {
            catModel.addElement(c);
        }
    }

    // Seçili markayı silmeden önce onay soruyor — yanlışlıkla silmelerin önüne geçmek için
    private void deleteSelectedBrand() {
        Brand sel = brandList.getSelectedValue();
        if (sel != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete brand " + sel.getBrandName() + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                String result = controller.deleteBrandSafely(sel.getBrandId());
                if ("SUCCESS".equals(result)) {
                    loadData(); // Silme başarılıysa listeyi yenile
                } else {
                    ThemedDialog.showMessage(this, result, ThemedDialog.Kind.ERROR);
                }
            }
        }
    }

    // Seçili kategoriyi silmeden önce onay soruyor — ilaçla bağlıysa silinmeye izin verilmez
    private void deleteSelectedCategory() {
        Category sel = catList.getSelectedValue();
        if (sel != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete category " + sel.getName() + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                String result = controller.deleteCategorySafely(sel.getId());
                if ("SUCCESS".equals(result)) {
                    loadData();
                } else {
                    ThemedDialog.showMessage(this, result, ThemedDialog.Kind.ERROR);
                }
            }
        }
    }
}
