package com.pharmacy.views.pages;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.views.DashboardView;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Dashboard sayfaları için ortak taban sınıf (Abstract Base Class).
 * Sınıflar arası bağımlılığı (Coupling) Constructor Injection ile açıkça alır.
 */
public abstract class AbstractPage implements IPage {
    protected final DashboardView parent;
    protected final InventoryController inventoryController;
    protected final TransactionController transactionController;
    protected final ReportController reportController;
    
    private final JPanel container;
    private final String pageName;

    public AbstractPage(String pageName, DashboardView parent, 
                        InventoryController inventoryController, 
                        TransactionController transactionController, 
                        ReportController reportController) {
        this.pageName = pageName;
        this.parent = parent;
        this.inventoryController = inventoryController;
        this.transactionController = transactionController;
        this.reportController = reportController;
        
        this.container = new JPanel(new BorderLayout());
        this.container.setOpaque(false);
    }

    @Override
    public JPanel getPagePanel() {
        return container;
    }

    @Override
    public String getPageName() {
        return pageName;
    }
    
    protected JPanel getContainer() {
        return container;
    }
}
