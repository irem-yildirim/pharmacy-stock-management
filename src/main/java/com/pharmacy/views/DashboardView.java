package com.pharmacy.views;

import com.pharmacy.controllers.MedicineController;
import com.pharmacy.entity.*;
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

    private CardLayout centerCards;
    private JPanel centerWrapper;
    private static final String CARD_HOME = "home";
    private static final String CARD_MEDICINES = "medicines";
    private static final String CARD_BRANDS = "brands";
    private static final String CARD_FINANCE = "finance";

    public DashboardView(MedicineController controller) {
        this.controller = controller;
        this.brandMap = new HashMap<>();
        this.presMap = new HashMap<>();
        setTitle("Pharmacy Management System");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);

        initComponents();
        loadReferenceData();
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
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(BG_LIGHT);
        homePanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        homeWidgetsRow = new JPanel(new GridBagLayout());
        homeWidgetsRow.setBackground(BG_LIGHT);
        homePanel.add(homeWidgetsRow, BorderLayout.CENTER);

        centerWrapper.add(homePanel, CARD_HOME);

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

    private void showHome() {
        centerCards.show(centerWrapper, CARD_HOME);
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() {
                List<Drug> all = controller.getAllMedicines();
                MedicineController.FinancialSummary stats = controller.getFinancialSummary();
                return new Object[] { all, stats };
            }

            @Override
            protected void done() {
                try {
                    Object[] res = get();
                    @SuppressWarnings("unchecked")
                    List<Drug> all = (List<Drug>) res[0];
                    MedicineController.FinancialSummary stats = (MedicineController.FinancialSummary) res[1];
                    updateBentoDashboard(all, stats);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateBentoDashboard(List<Drug> all, MedicineController.FinancialSummary stats) {
        homeWidgetsRow.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);
        statsRow.add(buildStatCard("Inventory", String.valueOf(stats.totalInventory), "📦", SIDEBAR_BG));
        statsRow.add(buildStatCard("Revenue", String.format("%.2f TL", stats.todayRevenue), "💰", SUCCESS));
        statsRow.add(buildStatCard("Low Stock", String.valueOf(stats.lowStockCount), "⚠️", DANGER));
        statsRow.add(buildStatCard("Expiry", String.valueOf(stats.expiryCount), "📅", new Color(240, 140, 50)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        statsRow.setPreferredSize(new Dimension(0, 95));
        homeWidgetsRow.add(statsRow, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 0.6;
        homeWidgetsRow.add(buildExpiryWidget(all), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        homeWidgetsRow.add(buildLowStockWidget(all), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        JPanel financeW = buildFullWidthFinancePanel(stats);
        financeW.setPreferredSize(new Dimension(0, 85));
        homeWidgetsRow.add(financeW, gbc);

        homeWidgetsRow.revalidate();
        homeWidgetsRow.repaint();
    }

    private JPanel buildStatCard(String title, String value, String icon, Color accent) {
        JPanel card = createBentoPanel();
        card.setLayout(new BorderLayout(15, 0));
        card.setPreferredSize(new Dimension(200, 95));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        card.add(iconLbl, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(FONT_SMALL);
        t.setForeground(TEXT_SECONDARY);
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 18));
        v.setForeground(accent);
        info.add(t);
        info.add(v);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildExpiryWidget(List<Drug> all) {
        LocalDate now = LocalDate.now();

        List<Drug> expirations = all.stream()
                .filter(m -> m.getExpiry() != null && m.getExpiry().getExpirationDate() != null)
                .filter(m -> {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(now, m.getExpiry().getExpirationDate());
                    return days <= 30; // Only show items expiring within 30 days
                })
                .sorted(Comparator.comparing(m -> m.getExpiry().getExpirationDate()))
                .collect(java.util.stream.Collectors.toList());

        return buildSectionWidget("📅  Critical Expirations (<30 Days)", new Color(240, 140, 50), expirations, null);
    }

    private JPanel buildLowStockWidget(List<Drug> all) {
        List<Drug> lowStock = all.stream().filter(m -> m.getStockQuantity() < 10)
                .sorted(Comparator.comparingInt(Drug::getStockQuantity))
                .collect(java.util.stream.Collectors.toList());

        boolean hasUrgent = !lowStock.isEmpty();
        List<Drug> toShow = hasUrgent ? lowStock
                : all.stream().sorted(Comparator.comparingInt(Drug::getStockQuantity)).limit(6)
                        .collect(java.util.stream.Collectors.toList());
        ExpiryMode cardMode = hasUrgent ? ExpiryMode.EXPIRING_SOON : ExpiryMode.EXPIRY_SAFE;

        return buildSectionWidget(hasUrgent ? "⚠️  Critical Stock" : "📦  Stock Monitor",
                hasUrgent ? DANGER : SUCCESS, toShow, cardMode);
    }

    private JPanel buildFullWidthFinancePanel(MedicineController.FinancialSummary fin) {
        JPanel panel = createBentoPanel();
        panel.setLayout(new GridLayout(1, 3, 40, 0));
        panel.setPreferredSize(new Dimension(0, 85));

        panel.add(createFinanceItem("Total Revenue", String.format("%.2f TL", fin.totalSales), SUCCESS));
        panel.add(createFinanceItem("Total Expenditure", String.format("%.2f TL", fin.totalPurchases), DANGER));
        panel.add(createFinanceItem("Net Performance", String.format("%.2f TL", fin.netProfit),
                fin.netProfit.compareTo(java.math.BigDecimal.ZERO) >= 0 ? SUCCESS : DANGER));

        return panel;
    }

    private JPanel createFinanceItem(String lbl, String val, Color col) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel l = new JLabel(lbl, SwingConstants.CENTER);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_SECONDARY);
        JLabel v = new JLabel(val, SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 20));
        v.setForeground(col);
        p.add(l);
        p.add(v);
        return p;
    }

    private JPanel buildSectionWidget(String headerText, Color headerColor, List<Drug> toShow, ExpiryMode cardMode) {
        JPanel widget = createBentoPanel();
        widget.setLayout(new BorderLayout());

        JLabel headerLbl = new JLabel(headerText);
        headerLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerLbl.setForeground(headerColor);
        widget.add(headerLbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 12));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(15, 0, 0, 0));

        for (Drug m : toShow) {
            Brand b = m.getBrand();
            PresType p = m.getPresType();
            ExpiryMode computedMode = cardMode;

            if (cardMode == null && m.getExpiry() != null && m.getExpiry().getExpirationDate() != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(),
                        m.getExpiry().getExpirationDate());

                if (days <= 15) {
                    computedMode = ExpiryMode.EXPIRY_URGENT;
                } else if (days <= 30) {
                    computedMode = ExpiryMode.EXPIRING_SOON;
                } else {
                    computedMode = ExpiryMode.EXPIRY_SAFE;
                }
            } else if (cardMode == null) {
                computedMode = ExpiryMode.EXPIRY_SAFE;
            }

            String bName = (b != null) ? b.getBrandName() : "Unknown";
            String pName = (p != null) ? p.getPrescription() : "Unknown";

            MedicineCard card = new MedicineCard(m, bName, pName, computedMode, (drug) -> this.openMedicineForm(drug));
            grid.add(card);
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        hideScrollBar(scroll);

        widget.add(scroll, BorderLayout.CENTER);
        return widget;
    }

    private JPanel createBentoPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CARD_RADIUS, CARD_RADIUS);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 20, 15, 20));
        return p;
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

        for (Brand b : controller.getAllBrands()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(BG_WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(16, 16, 16, 16)));

            JLabel name = new JLabel(b.getBrandName());
            name.setFont(FONT_HEADER);
            String drugNames = controller.getMedicinesByBrand(b.getBrandId()).stream()
                    .map(Drug::getName)
                    .reduce((med1, med2) -> med1 + "<br>" + med2)
                    .orElse("No drugs");

            JLabel drugsLabel = new JLabel(
                    "<html><i style='color:#a0a0a0; font-size:12px;'>" + drugNames + "</i></html>");

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.add(name);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(drugsLabel);

            card.add(infoPanel, BorderLayout.CENTER);

            JButton buyBtn = createPrimaryButton("Buy Stock");
            buyBtn.addActionListener(e -> new PurchaseDialog(this, controller, b.getBrandId()).setVisible(true));
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
        JButton btnFinance = createNavButton("📉  Finance");

        content.add(btnHome);
        content.add(btnAll);
        content.add(btnBrands);
        content.add(btnFinance);

        JPanel catPanel = buildSidebarItemsPanel(controller.getAllCategories(), cat -> {
            Category c = (Category) cat;
            JButton b = createNavSubItem("  " + c.getName());
            b.addActionListener(e -> {
                setPageTitle(c.getName() + " Medicines");
                centerCards.show(centerWrapper, CARD_MEDICINES);
                filterByCategory(c.getId());
            });
            return b;
        });
        addAccordionSection(content, "📁  Categories", catPanel);

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
        JButton btnManage = createNavButton("⚙️  Manage Data");

        content.add(btnSell);
        content.add(btnAddMed);
        content.add(btnAddBrand);
        content.add(btnAddCat);
        content.add(btnManage);

        content.add(Box.createVerticalStrut(20));
        JButton btnLogout = createNavButton("🚪  Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginView(new com.pharmacy.controllers.LoginController(
                    new com.pharmacy.service.UserService(new com.pharmacy.dao.UserDAO()), controller)).setVisible(true);
        });
        content.add(btnLogout);

        JButton btnExit = createNavButton("❌  Exit");
        btnExit.addActionListener(e -> System.exit(0));
        content.add(btnExit);

        btnManage.addActionListener(e -> new ManageMetadataView(this, controller).setVisible(true));

        initSidebarListeners(btnHome, btnAll, btnBrands, btnFinance, btnSell, btnAddMed, btnAddBrand, btnAddCat);
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

    private void initSidebarListeners(JButton btnHome, JButton btnAll, JButton btnBrands, JButton btnFinance,
            JButton btnSell, JButton btnAddMed, JButton btnAddBrand, JButton btnAddCat) {
        btnHome.addActionListener(e -> {
            setPageTitle("Pharmacy Dashboard");
            centerCards.show(centerWrapper, CARD_HOME);
            showHome();
        });

        btnAll.addActionListener(e -> {
            setPageTitle("All Medicines");
            centerCards.show(centerWrapper, CARD_MEDICINES);
            loadTableData();
        });

        btnFinance.addActionListener(e -> {
            setPageTitle("Financial Transactions");
            centerWrapper.add(buildFinanceView(), CARD_FINANCE);
            centerCards.show(centerWrapper, CARD_FINANCE);
        });

        btnBrands.addActionListener(e -> {
            setPageTitle("Managed Brands");
            centerWrapper.add(buildBrandsView(), CARD_BRANDS);
            centerCards.show(centerWrapper, CARD_BRANDS);
        });

        btnSell.addActionListener(e -> new SellDrugDialog(this, controller).setVisible(true));
        btnAddMed.addActionListener(e -> openMedicineForm(null));
        btnAddBrand.addActionListener(e -> {
            String brandName = JOptionPane.showInputDialog(this, "Enter New Brand Name:", "Add Brand",
                    JOptionPane.PLAIN_MESSAGE);
            if (brandName != null && !brandName.trim().isEmpty()) {
                Brand newBrand = new Brand();
                newBrand.setBrandName(brandName.trim());
                controller.addBrand(newBrand);
                loadReferenceData();
                ThemedDialog.showMessage(this, "Brand added successfully!", ThemedDialog.Kind.SUCCESS);
            }
        });
        btnAddCat.addActionListener(e -> {
            String catName = JOptionPane.showInputDialog(this, "Enter New Category Name:", "Add Category",
                    JOptionPane.PLAIN_MESSAGE);
            if (catName != null && !catName.trim().isEmpty()) {
                Category mc = new Category();
                mc.setName(catName.trim());
                controller.addCategory(mc);
                loadReferenceData();
                ThemedDialog.showMessage(this, "Category added successfully!", ThemedDialog.Kind.SUCCESS);
            }
        });
    }

    private JPanel buildSidebarItemsPanel(List<?> items, ItemButtonFactory factory) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setVisible(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Object item : items)
            panel.add(factory.make(item));
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
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        Component west = layout.getLayoutComponent(BorderLayout.WEST);
        if (west != null)
            remove(west);
        add(buildSidebar(), BorderLayout.WEST);
        revalidate();
        repaint();
    }

    public void loadTableData() {
        new SwingWorker<List<Drug>, Void>() {
            @Override
            protected List<Drug> doInBackground() {
                return controller.getAllMedicines();
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void searchMedicines() {
        String kw = searchField.getText().trim();
        setPageTitle(kw.isEmpty() ? "All Medicines" : "Search: " + kw);
        centerCards.show(centerWrapper, CARD_MEDICINES);
        if (kw.isEmpty()) {
            loadTableData();
            return;
        }
        updateCardsPanel(controller.searchMedicines(kw));
    }

    private void filterByCategory(long catId) {
        new SwingWorker<List<Drug>, Void>() {
            @Override
            protected List<Drug> doInBackground() {
                return controller.getMedicinesByCategory(catId);
            }

            @Override
            protected void done() {
                try {
                    updateCardsPanel(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateCardsPanel(List<Drug> meds) {
        cardsPanel.removeAll();
        if (meds == null)
            return;
        for (Drug m : meds) {
            Brand b = m.getBrand();
            PresType p = m.getPresType();
            cardsPanel.add(new MedicineCard(m, b != null ? b.getBrandName() : "Unknown",
                    p != null ? p.getPrescription() : "Unknown", this::openMedicineForm));
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

    public void openMedicineForm(Drug medicine) {
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

    private JPanel buildFinanceView() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_LIGHT);
        wrap.setBorder(new EmptyBorder(24, 24, 24, 24));
        String[] cols = { "Date", "Type", "Reference", "Amount (TL)" };
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setFillsViewportHeight(true);

        new SwingWorker<List<MedicineController.FinancialTransaction>, Void>() {
            @Override
            protected List<MedicineController.FinancialTransaction> doInBackground() {
                return controller.getFinancialTransactions();
            }

            @Override
            protected void done() {
                try {
                    for (MedicineController.FinancialTransaction tx : get()) {
                        model.addRow(new Object[] { tx.date, tx.type, tx.reference, String.format("%.2f", tx.amount) });
                    }
                } catch (Exception e) {
                }
            }
        }.execute();
        wrap.add(new JScrollPane(table), BorderLayout.CENTER);
        return wrap;
    }

    interface ItemButtonFactory {
        JButton make(Object item);
    }

    static class StyledTextField extends JTextField {
        StyledTextField(int cols, String hint) {
            super(cols);
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
