package com.pharmacy.views;

import com.pharmacy.controllers.LoginController;
import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Category;
import com.pharmacy.entity.Drug;
import com.pharmacy.views.components.ThemedDialog;
import com.pharmacy.views.dialogs.ManageMetadataView;
import com.pharmacy.views.dialogs.MedicineFormView;
import com.pharmacy.views.dialogs.SellDrugDialog;
import com.pharmacy.views.navigation.NavigationManager;
import com.pharmacy.views.pages.BrandsPage;
import com.pharmacy.views.pages.FinancePage;
import com.pharmacy.views.pages.HomePage;
import com.pharmacy.views.pages.InventoryPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static com.pharmacy.views.components.ThemeConstants.*;

public class DashboardView extends JFrame {

    private final InventoryController inventoryController;
    private final TransactionController transactionController;
    private final ReportController reportController;
    private final LoginController loginController;
    private final NavigationManager navigationManager;

    private JLabel topTitleLabel;
    private JPanel centerWrapper;

    public DashboardView(InventoryController inventoryC, TransactionController transC, ReportController repC,
            LoginController loginC) {
        this.inventoryController = inventoryC;
        this.transactionController = transC;
        this.reportController = repC;
        this.loginController = loginC;

        setTitle("Pharmacy Management System");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);

        CardLayout cardLayout = new CardLayout();
        centerWrapper = new JPanel(cardLayout);
        centerWrapper.setBackground(BG_LIGHT);
        navigationManager = new NavigationManager(centerWrapper, cardLayout);

        initComponents();
        loadPagesAndStart();
    }

    private void loadPagesAndStart() {
        navigationManager
                .registerPage(new HomePage(this, inventoryController, transactionController, reportController));
        navigationManager
                .registerPage(new InventoryPage(this, inventoryController, transactionController, reportController));
        navigationManager
                .registerPage(new BrandsPage(this, inventoryController, transactionController, reportController));
        navigationManager
                .registerPage(new FinancePage(this, inventoryController, transactionController, reportController));

        rebuildSidebar();
        navigationManager.showPage("Home");
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(centerWrapper, BorderLayout.CENTER);
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

        topBar.add(topTitleLabel, BorderLayout.WEST);
        return topBar;
    }

    public void setPageTitle(String title) {
        if (topTitleLabel != null) {
            topTitleLabel.setText("💊 " + title);
        }
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

        JPanel catPanel = new JPanel();
        catPanel.setLayout(new BoxLayout(catPanel, BoxLayout.Y_AXIS));
        catPanel.setBackground(SIDEBAR_BG);
        catPanel.setVisible(false);
        catPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Category c : inventoryController.getAllCategories()) {
            JButton b = createNavSubItem("  " + c.getName());
            b.addActionListener(e -> {
                setPageTitle(c.getName() + " Medicines");
                navigationManager.showPage("Inventory");
                ((InventoryPage) navigationManager.getCurrentPage()).filterByCategory(c.getId());
            });
            catPanel.add(b);
        }

        JButton catHeader = createNavButton("▶  📁  Categories");
        catHeader.addActionListener(e -> {
            boolean v = !catPanel.isVisible();
            catPanel.setVisible(v);
            catHeader.setText((v ? "▼  " : "▶  ") + "📁  Categories");
            content.revalidate();
            content.repaint();
        });
        content.add(catHeader);
        content.add(catPanel);

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
            new LoginView(loginController).setVisible(true);
        });
        content.add(btnLogout);

        JButton btnExit = createNavButton("❌  Exit");
        btnExit.addActionListener(e -> System.exit(0));
        content.add(btnExit);

        // Listeners
        btnHome.addActionListener(e -> {
            setPageTitle("Pharmacy Dashboard");
            navigationManager.showPage("Home");
        });
        btnAll.addActionListener(e -> {
            setPageTitle("All Medicines");
            navigationManager.showPage("Inventory");
        });
        btnBrands.addActionListener(e -> {
            setPageTitle("Managed Brands");
            navigationManager.showPage("Brands");
        });
        btnFinance.addActionListener(e -> {
            if (loginController.getCurrentUser() != null && "STAFF".equals(loginController.getCurrentUser().getRole())) {
                ThemedDialog.showMessage(DashboardView.this, "Access Denied: Staff role cannot access Finance section.", ThemedDialog.Kind.ERROR);
                return;
            }
            setPageTitle("Financial Transactions");
            navigationManager.showPage("Finance");
        });

        btnSell.addActionListener(
                e -> new SellDrugDialog(this, transactionController, inventoryController).setVisible(true));
        btnAddMed.addActionListener(e -> openMedicineForm(null));
        btnManage.addActionListener(e -> {
            if (loginController.getCurrentUser() != null && "STAFF".equals(loginController.getCurrentUser().getRole())) {
                ThemedDialog.showMessage(DashboardView.this, "Access Denied: Staff role cannot access Metadata Management.", ThemedDialog.Kind.ERROR);
                return;
            }
            new ManageMetadataView(this, inventoryController).setVisible(true);
        });

        btnAddBrand.addActionListener(e -> {
            String brandName = JOptionPane.showInputDialog(this, "Enter New Brand Name:", "Add Brand",
                    JOptionPane.PLAIN_MESSAGE);
            if (brandName != null && !brandName.trim().isEmpty()) {
                Brand newBrand = new Brand();
                newBrand.setBrandName(brandName.trim());
                inventoryController.addBrand(newBrand);
                rebuildSidebar();
                navigationManager.showPage("Brands");
                ThemedDialog.showMessage(this, "Brand added successfully!", ThemedDialog.Kind.SUCCESS);
            }
        });

        btnAddCat.addActionListener(e -> {
            String catName = JOptionPane.showInputDialog(this, "Enter New Category Name:", "Add Category",
                    JOptionPane.PLAIN_MESSAGE);
            if (catName != null && !catName.trim().isEmpty()) {
                Category mc = new Category();
                mc.setName(catName.trim());
                inventoryController.addCategory(mc);
                rebuildSidebar();
                ThemedDialog.showMessage(this, "Category added successfully!", ThemedDialog.Kind.SUCCESS);
            }
        });

        content.add(Box.createVerticalGlue());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(SIDEBAR_BG);
        wrapper.setPreferredSize(new Dimension(220, 0));
        wrapper.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(SIDEBAR_BG);
        return scroll;
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

    public void openMedicineForm(Drug medicine) {
        new MedicineFormView(this, inventoryController, medicine).setVisible(true);
    }

    public void loadTableData() {
        if (navigationManager.getCurrentPage() instanceof InventoryPage) {
            navigationManager.getCurrentPage().onPageEnter();
        }
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
}
