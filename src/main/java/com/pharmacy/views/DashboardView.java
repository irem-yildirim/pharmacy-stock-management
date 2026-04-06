package com.pharmacy.views;

import com.pharmacy.controllers.MedicineController;
import com.pharmacy.models.*;
import com.pharmacy.views.MedicineCard.ExpiryMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import static com.pharmacy.views.ThemeConstants.*;

public class DashboardView extends JFrame {
    private final MedicineController controller;

    private JTextField searchField;
    private Map<Integer, Brand> brandMap;
    private Map<Integer, PresType> presMap;
    private JPanel cardsPanel;
    private JLabel topTitleLabel;
    private JPanel homeWidgetsRow;

    private JPanel catItemsPanel;

    private JPanel centerWrapper;
    private CardLayout centerCards;
    private static final String CARD_HOME = "home";
    private static final String CARD_MEDICINES = "medicines";
    private static final String CARD_BRANDS = "brands";

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

        topTitleLabel = new JLabel("💊 Pharmacy Manager");
        topTitleLabel.setFont(FONT_TITLE);
        topTitleLabel.setForeground(SIDEBAR_BG);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        searchField = new StyledTextField(22, "Search medicines...");
        searchField.setPreferredSize(new Dimension(220, 36));

        JButton searchBtn = createSecondaryButton("Search");

        searchBtn.addActionListener(e -> searchMedicines());
        searchField.addActionListener(e -> searchMedicines());

        right.add(searchField);
        right.add(searchBtn);
        
        topBar.add(topTitleLabel, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private void setPageTitle(String title) {
        if (topTitleLabel != null) {
            topTitleLabel.setText("💊 " + title);
        }
    }

    private JPanel buildCenter() {
        centerCards = new CardLayout();
        centerWrapper = new JPanel(centerCards);
        centerWrapper.setBackground(BG_LIGHT);

        // 1. HOME CARD
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(BG_LIGHT);
        homePanel.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel homeTitle = new JLabel("Dashboard");
        homeTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        homeTitle.setForeground(SIDEBAR_BG);
        homePanel.add(homeTitle);
        homePanel.add(Box.createVerticalStrut(18));

        homeWidgetsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        homeWidgetsRow.setBackground(BG_LIGHT);
        homeWidgetsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePanel.add(homeWidgetsRow);

        JScrollPane homeScroll = new JScrollPane(homePanel);
        homeScroll.setBorder(null);
        homeScroll.getViewport().setBackground(BG_LIGHT);
        hideScrollBar(homeScroll);
        centerWrapper.add(homeScroll, CARD_HOME);

        // 2. MEDICINES CARD
        cardsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        cardsPanel.setBackground(BG_LIGHT);
        cardsPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setBackground(BG_LIGHT);
        cardsWrapper.add(cardsPanel, BorderLayout.NORTH);

        JScrollPane medScroll = new JScrollPane(cardsWrapper);
        medScroll.setBorder(null);
        medScroll.getViewport().setBackground(BG_LIGHT);
        hideScrollBar(medScroll);

        centerWrapper.add(medScroll, CARD_MEDICINES);
        
        centerWrapper.add(buildBrandsView(), CARD_BRANDS);

        return centerWrapper;
    }

    private void hideScrollBar(JScrollPane scroll) {
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
    }

    private JPanel buildExpiryWidget(List<Medicine> all) {
        LocalDate today = LocalDate.now();
        LocalDate threshold15 = today.plusDays(15);
        LocalDate threshold30 = today.plusDays(30);

        List<Medicine> withDate = all.stream()
                .filter(m -> m.getExpirationDate() != null)
                .filter(m -> !m.getExpirationDate().isAfter(threshold30)) // Sadece <= 30 days
                .sorted(Comparator.comparing(Medicine::getExpirationDate)).toList();

        List<Medicine> soonList = withDate.stream().limit(6).toList();
        boolean hasUrgent = !soonList.isEmpty();
        
        // Mode will be calculated dynamically per card now since some might be 15, some 30.
        // Wait, ExpiryMode is passed per widget in buildSectionWidget. Let's pass null and determine inside the hook.
        // Or MedicineCard constructor expects ExpiryMode. We can pass a generalized one since buildSectionWidget handles it statically.
        // Let's modify buildSectionWidget to calculate ExpiryMode per medicine.
        
        String hdr = "📅  Upcoming Expirations";
        Color col = new Color(240, 140, 50); // Orange by default for expiry header if it exists
        return buildSectionWidget(hdr, col, soonList, null);
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
            ExpiryMode computedMode = cardMode;
            if (cardMode == null) {
                // If it's the Expiry Widget, compute mode based on days remaining
                if (m.getExpirationDate() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), m.getExpirationDate());
                    if (days <= 15) computedMode = ExpiryMode.EXPIRY_URGENT;
                    else computedMode = ExpiryMode.EXPIRING_SOON;
                } else {
                    computedMode = ExpiryMode.EXPIRY_SAFE;
                }
            }
            grid.add(new MedicineCard(m, b != null ? b.getBrandName() : "Unknown",
                    p != null ? p.getPrescription() : "Unknown", computedMode, this::openMedicineForm));
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

    private JPanel buildBrandsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        JLabel title = new JLabel("🏷️ Managed Brands");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 18, 18));
        grid.setBackground(BG_LIGHT);
        grid.setBorder(new EmptyBorder(20, 0, 0, 0));

        for(Brand b : controller.getAllBrands()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(BG_WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(16, 16, 16, 16)));
            
            JLabel name = new JLabel(b.getBrandName());
            name.setFont(FONT_HEADER);
            // Dynamic drugs for Brand
            String drugNames = controller.getMedicinesByBrand(b.getBrandId()).stream()
                    .map(Medicine::getMedName)
                    .reduce((med1, med2) -> med1 + "<br>" + med2)
                    .orElse("No drugs");
            
            JLabel drugsLabel = new JLabel("<html><i style='color:#a0a0a0; font-size:12px;'>" + drugNames + "</i></html>");
            
            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.add(name);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(drugsLabel);
            
            card.add(infoPanel, BorderLayout.CENTER);
            
            JButton buyBtn = createPrimaryButton("Buy Stock");
            buyBtn.addActionListener(e -> {
                new PurchaseDialog(this, controller, b.getBrandId()).setVisible(true);
            });
            card.add(buyBtn, BorderLayout.SOUTH);
            grid.add(card);
        }
        
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_LIGHT);
        hideScrollBar(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane buildSidebar() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(SIDEBAR_BG);
        content.setBorder(new EmptyBorder(20, 14, 20, 14));

        JLabel navLbl = new JLabel("NAVIGATION");
        navLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        navLbl.setForeground(new Color(120, 160, 180));
        navLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(navLbl);
        content.add(Box.createVerticalStrut(10));

        JButton btnHome = createNavButton("🏠  Home");
        JButton btnAll = createNavButton("📋  All Medicines");
        JButton btnBrands = createNavButton("🏷️  Brands");

        content.add(btnHome);
        content.add(btnAll);
        content.add(btnBrands);

        // Submenu for Categories
        catItemsPanel = buildSidebarItemsPanel(controller.getAllCategories(), cat -> {
            JButton b = createNavSubItem("  " + ((MedicineCategory) cat).getCatName());
            b.addActionListener(e -> {
                setPageTitle(((MedicineCategory) cat).getCatName() + " Medicines");
                centerCards.show(centerWrapper, CARD_MEDICINES);
                filterByCategory(((MedicineCategory) cat).getCatId());
            });
            return b;
        });
        addAccordionSection(content, "📁  Categories", catItemsPanel);

        content.add(Box.createVerticalStrut(20));

        JLabel transLbl = new JLabel("TRANSACTIONS");
        transLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        transLbl.setForeground(new Color(120, 160, 180));
        transLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(transLbl);
        content.add(Box.createVerticalStrut(10));

        JButton btnSell = createNavButton("🛒  Sell Medicine");
        JButton btnAddMed = createNavButton("➕  Add Medicine");
        JButton btnAddBrand = createNavButton("🏢  Add Brand");
        JButton btnAddCat = createNavButton("📁  Add Category");

        content.add(btnSell);
        content.add(btnAddMed);
        content.add(btnAddBrand);
        content.add(btnAddCat);

        content.add(Box.createVerticalStrut(20));

        JButton btnLogout = createNavButton("🚪  Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginView(new com.pharmacy.controllers.LoginController(
                new com.pharmacy.service.UserService(new com.pharmacy.dao.UserDAO()), controller
            )).setVisible(true);
        });
        content.add(btnLogout);

        JButton btnExit = createNavButton("❌  Exit");
        btnExit.addActionListener(e -> System.exit(0));
        content.add(btnExit);

        // Attach requested explicit Sidebar Listeners via method
        initSidebarListeners(btnHome, btnAll, btnBrands, btnSell, btnAddMed, btnAddBrand, btnAddCat);

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

    private void initSidebarListeners(JButton btnHome, JButton btnAll, JButton btnBrands, JButton btnSell, JButton btnAddMed, JButton btnAddBrand, JButton btnAddCat) {
        btnHome.addActionListener(e -> {
            setPageTitle("Pharmacy Dashboard");
            centerCards.show(centerWrapper, CARD_HOME);
            showHome();
            centerWrapper.revalidate();
            centerWrapper.repaint();
        });

        btnAll.addActionListener(e -> {
            setPageTitle("All Medicines");
            centerCards.show(centerWrapper, CARD_MEDICINES);
            loadTableData();
            centerWrapper.revalidate();
            centerWrapper.repaint();
        });

        btnBrands.addActionListener(e -> {
            setPageTitle("Managed Brands");
            // Find and remove old brands view securely
            Component[] comps = centerWrapper.getComponents();
            for (Component c : comps) {
                if ("brands".equals(c.getName())) {
                    centerWrapper.remove(c);
                }
            }
            JPanel brandsView = buildBrandsView();
            brandsView.setName("brands");
            centerWrapper.add(brandsView, CARD_BRANDS);
            centerCards.show(centerWrapper, CARD_BRANDS);
            centerWrapper.revalidate();
            centerWrapper.repaint();
        });

        btnSell.addActionListener(e -> {
            new SellDrugDialog(this, controller).setVisible(true);
        });

        btnAddMed.addActionListener(e -> {
            openMedicineForm(null);
        });

        btnAddBrand.addActionListener(e -> {
            String brandName = JOptionPane.showInputDialog(this, "Enter New Brand Name:", "Add Brand", JOptionPane.PLAIN_MESSAGE);
            if (brandName != null && !brandName.trim().isEmpty()) {
                Brand newBrand = new Brand();
                newBrand.setBrandName(brandName.trim());
                controller.addBrand(newBrand);
                loadReferenceData(); // Refresh UI mappings
                ThemedDialog.showMessage(this, "Brand added successfully!", ThemedDialog.Kind.SUCCESS);
            }
        });

        btnAddCat.addActionListener(e -> {
            String catName = JOptionPane.showInputDialog(this, "Enter New Category Name:", "Add Category", JOptionPane.PLAIN_MESSAGE);
            if (catName != null && !catName.trim().isEmpty()) {
                MedicineCategory mc = new MedicineCategory(0, catName.trim(), "");
                controller.addCategory(mc);
                loadReferenceData(); // Refresh sidebar categories
                ThemedDialog.showMessage(this, "Category added successfully!", ThemedDialog.Kind.SUCCESS);
            }
        });
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
                    homeWidgetsRow.removeAll();
                    homeWidgetsRow.add(buildExpiryWidget(all));
                    homeWidgetsRow.add(buildLowStockWidget(all));
                    homeWidgetsRow.revalidate();
                    homeWidgetsRow.repaint();
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    public void loadTableData() {
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.getAllMedicines();
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {}
            }
        }.execute();
    }

    private void searchMedicines() {
        String kw = searchField.getText().trim();
        setPageTitle(kw.isEmpty() ? "All Medicines" : "Search: " + kw);
        centerCards.show(centerWrapper, CARD_MEDICINES); // Ekranı anında listeye çevir

        if (kw.isEmpty()) {
            loadTableData();
            return;
        }
        updateCardsPanel(controller.searchMedicines(kw));
    }

    private void filterByCategory(int catId) {
        new SwingWorker<List<Medicine>, Void>() {
            @Override
            protected List<Medicine> doInBackground() {
                return controller.getMedicinesByCategory(catId);
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {}
            }
        }.execute();
    }

    private void updateCardsPanel(List<Medicine> meds) {
        cardsPanel.removeAll();
        if (meds == null) return;
        
        for (Medicine m : meds) {
            Brand b = brandMap.get(m.getBrandId());
            PresType p = presMap.get(m.getPresId());
            MedicineCard card = new MedicineCard(m, b != null ? b.getBrandName() : "Unknown",
                    p != null ? p.getPrescription() : "Unknown", this::openMedicineForm);
            cardsPanel.add(card);
        }
        
        int rem = meds.size() % 3;
        if (!meds.isEmpty() && rem != 0) {
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
        btn.setFont(new Font("SansSerif", Font.PLAIN, 17));
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
        btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
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
