package com.pharmacy.views;

import com.pharmacy.controllers.MedicineController;
import com.pharmacy.models.*;
import com.pharmacy.views.MedicineCard.ExpiryMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import static com.pharmacy.views.ThemeConstants.*;

public class DashboardView extends JFrame {
    private final MedicineController controller;
    private JPanel cardsPanel;
    private JTextField searchField;
    private Map<Integer, Brand> brandMap;
    private Map<Integer, PresType> presMap;

    private JPanel supplierInfoPanel;
    private JLabel supplierNameLabel;
    private JLabel supplierContactLabel;

    private JPanel catItemsPanel;
    private JPanel brandItemsPanel;
    private JPanel suppItemsPanel;

    private JPanel centerWrapper;
    private CardLayout centerCards;
    private static final String CARD_HOME = "home";
    private static final String CARD_MEDICINES = "medicines";

    public DashboardView(MedicineController controller) {
        this.controller = controller;
        this.brandMap = new HashMap<>();
        this.presMap = new HashMap<>();
        setTitle("Pharmacy Management System");
        setSize(1200, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);

        initComponents();
        loadReferenceData(); // Asynchronous loading
    }

    private void loadReferenceData() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (Brand b : controller.getAllBrands())
                    brandMap.put(b.getBrandId(), b);
                for (PresType p : controller.getAllPresTypes())
                    presMap.put(p.getPresId(), p);
                return null;
            }

            @Override
            protected void done() {
                rebuildSidebar();
                showHome();
            }
        }.execute();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildCenter(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG_WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(230, 230, 230));
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 65));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("💊 Pharmacy Manager");
        title.setFont(FONT_TITLE);
        title.setForeground(SIDEBAR_BG);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        searchField = new StyledTextField(22, "Search medicines...");
        searchField.setPreferredSize(new Dimension(220, 36));

        JButton searchBtn = createSecondaryButton("Search");
        JButton refreshBtn = createSecondaryButton("↺ Refresh");
        JButton addBtn = createPrimaryButton("+ Add Medicine");

        searchBtn.addActionListener(e -> searchMedicines());
        refreshBtn.addActionListener(e -> loadReferenceData());
        addBtn.addActionListener(e -> openMedicineForm(null));

        right.add(searchField);
        right.add(searchBtn);
        right.add(refreshBtn);
        right.add(addBtn);
        topBar.add(title, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JPanel buildCenter() {
        centerCards = new CardLayout();
        centerWrapper = new JPanel(centerCards);
        centerWrapper.setBackground(BG_LIGHT);

        cardsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        cardsPanel.setBackground(BG_LIGHT);
        cardsPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        supplierInfoPanel = new JPanel(new BorderLayout());
        supplierInfoPanel.setBackground(new Color(230, 240, 250));
        supplierInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(24, 24, 0, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 210, 230), 1, true),
                        new EmptyBorder(16, 20, 16, 20))));
        supplierInfoPanel.setVisible(false);

        JPanel suppInner = new JPanel();
        suppInner.setLayout(new BoxLayout(suppInner, BoxLayout.Y_AXIS));
        suppInner.setOpaque(false);

        supplierNameLabel = new JLabel("");
        supplierNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        supplierNameLabel.setForeground(new Color(40, 80, 120));

        supplierContactLabel = new JLabel("");
        supplierContactLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        supplierContactLabel.setForeground(new Color(60, 100, 140));

        suppInner.add(supplierNameLabel);
        suppInner.add(supplierContactLabel);
        supplierInfoPanel.add(suppInner, BorderLayout.CENTER);

        JPanel cardsContent = new JPanel(new BorderLayout());
        cardsContent.setBackground(BG_LIGHT);
        cardsContent.add(supplierInfoPanel, BorderLayout.NORTH);
        cardsContent.add(cardsPanel, BorderLayout.CENTER);

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setBackground(BG_LIGHT);
        cardsWrapper.add(cardsContent, BorderLayout.NORTH);

        JScrollPane medScroll = new JScrollPane(cardsWrapper);
        medScroll.setBorder(null);
        medScroll.getViewport().setBackground(BG_LIGHT);
        hideScrollBar(medScroll);
        centerWrapper.add(medScroll, CARD_MEDICINES);

        return centerWrapper;
    }

    private void hideScrollBar(JScrollPane scroll) {
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
    }

    private JPanel buildExpiryWidget(List<Medicine> all) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(30);

        List<Medicine> withDate = all.stream()
                .filter(m -> m.getExpirationDate() != null)
                .sorted(Comparator.comparing(Medicine::getExpirationDate)).toList();

        List<Medicine> soonList = withDate.stream().filter(m -> !m.getExpirationDate().isAfter(threshold)).toList();
        boolean hasUrgent = !soonList.isEmpty();
        List<Medicine> toShow = hasUrgent ? soonList : withDate.stream().limit(6).toList();
        ExpiryMode cardMode = hasUrgent ? ExpiryMode.EXPIRING_SOON : ExpiryMode.EXPIRY_SAFE;

        String hdr = hasUrgent ? "⚠️  Expiring Within 30 Days" : "📅  Upcoming Expirations";
        Color col = hasUrgent ? new Color(195, 50, 50) : new Color(40, 140, 80);
        return buildSectionWidget(hdr, col, toShow, cardMode);
    }

    private JPanel buildLowStockWidget(List<Medicine> all) {
        List<Medicine> lowStock = all.stream().filter(m -> m.getQuantity() < 10)
                .sorted(Comparator.comparingInt(Medicine::getQuantity)).toList();

        boolean hasUrgent = !lowStock.isEmpty();
        List<Medicine> toShow = hasUrgent ? lowStock
                : all.stream().sorted(Comparator.comparingInt(Medicine::getQuantity)).limit(6).toList();
        ExpiryMode cardMode = hasUrgent ? ExpiryMode.EXPIRING_SOON : ExpiryMode.EXPIRY_SAFE;

        String hdr = hasUrgent ? "⚠️  Low Stock (< 10 units)" : "📦  Stock Levels";
        Color col = hasUrgent ? new Color(195, 50, 50) : new Color(40, 140, 80);
        return buildSectionWidget(hdr, col, toShow, cardMode);
    }

    private JPanel buildSectionWidget(String headerText, Color headerColor, List<Medicine> toShow,
            ExpiryMode cardMode) {
        JPanel widget = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 18, 18);
                g2.dispose();
            }
        };
        widget.setOpaque(false);
        widget.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel headerLbl = new JLabel(headerText);
        headerLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerLbl.setForeground(headerColor);
        widget.add(headerLbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 14));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(14, 0, 0, 0));

        for (Medicine m : toShow) {
            Brand b = brandMap.get(m.getBrandId());
            PresType p = presMap.get(m.getPresId());
            grid.add(new MedicineCard(m, b != null ? b.getBrandName() : "Unknown",
                    p != null ? p.getPrescription() : "Unknown", cardMode, med -> controller.updateMedicine(med)));
        }
        for (int i = toShow.size(); i < 3; i++) {
            JPanel ghost = new JPanel();
            ghost.setOpaque(false);
            ghost.setPreferredSize(new Dimension(240, 160));
            grid.add(ghost);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(grid, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        hideScrollBar(scroll);
        widget.add(scroll, BorderLayout.CENTER);

        int fixedH = 160 * 3 + 14 * 2 + 16 + 14 + 30;
        widget.setPreferredSize(new Dimension(290, fixedH));
        widget.setMaximumSize(new Dimension(290, fixedH));
        return widget;
    }

    private JScrollPane buildSidebar() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(SIDEBAR_BG);
        content.setBorder(new EmptyBorder(20, 14, 20, 14));

        JLabel menuLbl = new JLabel("MENU");
        menuLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        menuLbl.setForeground(new Color(120, 160, 180));
        menuLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(menuLbl);
        content.add(Box.createVerticalStrut(10));

        JButton btnHome = createNavButton("🏠  Home");
        btnHome.addActionListener(e -> showHome());
        content.add(btnHome);

        JButton btnAll = createNavButton("📋  All Medicines");
        btnAll.addActionListener(e -> {
            centerCards.show(centerWrapper, CARD_MEDICINES);
            loadTableData();
        });
        content.add(btnAll);

        catItemsPanel = buildSidebarItemsPanel(controller.getAllCategories(), cat -> {
            JButton b = createNavSubItem("  " + ((MedicineCategory) cat).getCatName());
            b.addActionListener(e -> {
                centerCards.show(centerWrapper, CARD_MEDICINES);
                filterByCategory(((MedicineCategory) cat).getCatId());
            });
            return b;
        });
        addAccordionSection(content, "📁  Categories", catItemsPanel);

        brandItemsPanel = buildSidebarItemsPanel(controller.getAllBrands(), b -> {
            JButton btn = createNavSubItem("  " + ((Brand) b).getBrandName());
            btn.addActionListener(e -> {
                centerCards.show(centerWrapper, CARD_MEDICINES);
                filterByBrand(((Brand) b).getBrandId());
            });
            return btn;
        });
        addAccordionSection(content, "🏷️  Brands", brandItemsPanel);

        suppItemsPanel = buildSidebarItemsPanel(controller.getAllSuppliers(), s -> {
            JButton btn = createNavSubItem("  " + ((Supplier) s).getSupplierName());
            btn.addActionListener(e -> {
                centerCards.show(centerWrapper, CARD_MEDICINES);
                filterBySupplier(((Supplier) s).getSupplierId());
            });
            return btn;
        });
        addAccordionSection(content, "🚚  Suppliers", suppItemsPanel);

        content.add(Box.createVerticalGlue());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(SIDEBAR_BG);
        wrapper.setPreferredSize(new Dimension(220, 0));
        wrapper.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(SIDEBAR_BG);
        hideScrollBar(scroll);
        return scroll;
    }

    interface ItemButtonFactory {
        JButton make(Object item);
    }

    private JPanel buildSidebarItemsPanel(List<?> items, ItemButtonFactory factory) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setVisible(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Object item : items) {
            panel.add(factory.make(item));
        }
        return panel;
    }

    private void addAccordionSection(JPanel parent, String title, JPanel itemsPanel) {
        JButton header = createNavButton("▶  " + title);
        header.addActionListener(e -> {
            boolean v = !itemsPanel.isVisible();
            itemsPanel.setVisible(v);
            header.setText((v ? "▼  " : "▶  ") + title);
            parent.revalidate();
            parent.repaint();
        });
        parent.add(header);
        parent.add(itemsPanel);
    }

    private void rebuildSidebar() {
        if (((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.WEST) != null) {
            remove(((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.WEST));
        }
        add(buildSidebar(), BorderLayout.WEST);
        revalidate();
        repaint();
    }

    // =====================================================================
    // ⬇️ UI DONMASINI ENGELLEYEN ASENKRON ÇAĞRILAR (SWINGWORKER) ⬇️
    // =====================================================================

    private void showHome() {
        if (centerWrapper.getComponentCount() > 0)
            centerWrapper.remove(0);

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(BG_LIGHT);
        homePanel.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel homeTitle = new JLabel("Dashboard");
        homeTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        homeTitle.setForeground(SIDEBAR_BG);
        homePanel.add(homeTitle);
        homePanel.add(Box.createVerticalStrut(18));

        JPanel widgetsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        widgetsRow.setBackground(BG_LIGHT);
        widgetsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePanel.add(widgetsRow);

        JScrollPane homeScroll = new JScrollPane(homePanel);
        homeScroll.setBorder(null);
        homeScroll.getViewport().setBackground(BG_LIGHT);
        hideScrollBar(homeScroll);
        centerWrapper.add(homeScroll, CARD_HOME, 0);
        centerCards.show(centerWrapper, CARD_HOME);

        // Async Data Load for home panels
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.getAllMedicines();
            }

            @Override
            protected void done() {
                try {
                    List<Medicine> all = get();
                    widgetsRow.removeAll();
                    widgetsRow.add(buildExpiryWidget(all));
                    widgetsRow.add(buildLowStockWidget(all));
                    widgetsRow.revalidate();
                    widgetsRow.repaint();
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    public void loadTableData() {
        if (supplierInfoPanel != null)
            supplierInfoPanel.setVisible(false);
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.getAllMedicines();
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void searchMedicines() {
        if (supplierInfoPanel != null)
            supplierInfoPanel.setVisible(false);
        centerCards.show(centerWrapper, CARD_MEDICINES);
        final String kw = searchField.getText();
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.searchMedicines(kw);
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void filterByCategory(int catId) {
        if (supplierInfoPanel != null)
            supplierInfoPanel.setVisible(false);
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.getAllMedicines().stream().filter(m -> m.getCatId() == catId).toList();
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void filterByBrand(int brandId) {
        if (supplierInfoPanel != null)
            supplierInfoPanel.setVisible(false);
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.getAllMedicines().stream().filter(m -> m.getBrandId() == brandId).toList();
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void filterBySupplier(int suppId) {
        Supplier s = controller.getAllSuppliers().stream().filter(sup -> sup.getSupplierId() == suppId).findFirst()
                .orElse(null);
        if (s != null && supplierInfoPanel != null) {
            supplierNameLabel.setText("🏢 Supplier: " + s.getSupplierName());
            supplierContactLabel.setText("📞 Phone: " + s.getPhoneNumber());
            supplierInfoPanel.setVisible(true);
        }
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.getAllMedicines().stream().filter(m -> m.getSupplierId() == suppId).toList();
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    // =====================================================================

    private void updateCardsPanel(List<Medicine> medicines) {
        cardsPanel.removeAll();
        for (Medicine m : medicines) {
            Brand b = brandMap.get(m.getBrandId());
            PresType p = presMap.get(m.getPresId());
            MedicineCard card = new MedicineCard(m, b != null ? b.getBrandName() : "Unknown",
                    p != null ? p.getPrescription() : "Unknown", med -> controller.updateMedicine(med));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openMedicineForm(m);
                }
            });
            cardsPanel.add(card);
        }
        int rem = medicines.size() % 3;
        if (!medicines.isEmpty() && rem != 0) {
            for (int i = 0; i < 3 - rem; i++) {
                JPanel g = new JPanel();
                g.setOpaque(false);
                g.setPreferredSize(new Dimension(240, 160));
                cardsPanel.add(g);
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    public void openMedicineForm(Medicine medicine) {
        new MedicineFormView(this, controller, medicine).setVisible(true);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover() || getModel().isArmed()) {
                    g2.setColor(SIDEBAR_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setFont(FONT_BODY);
        btn.setForeground(new Color(220, 235, 240));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createNavSubItem(String text) {
        JButton btn = createNavButton(text);
        btn.setFont(FONT_SMALL);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btn.setBorder(new EmptyBorder(0, 14, 0, 0));
        return btn;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
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
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 18, 6, 18));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isArmed() ? new Color(215, 215, 215)
                        : getModel().isRollover() ? new Color(235, 235, 235) : new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    static class StyledTextField extends JTextField {
        private final String hint;

        StyledTextField(int cols, String hint) {
            super(cols);
            this.hint = hint;
            setOpaque(false);
            setBorder(new EmptyBorder(6, 14, 6, 14));
            setFont(ThemeConstants.FONT_BODY);
            setForeground(ThemeConstants.TEXT_PRIMARY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
