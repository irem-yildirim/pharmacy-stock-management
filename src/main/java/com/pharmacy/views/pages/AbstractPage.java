package com.pharmacy.views.pages;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.views.DashboardView;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Dashboard içindeki tüm sayfaların ortak şablonu (Ana Sınıf).
 * Her sayfanın controller'lara ihtiyacı oluyor; buradan miras alarak tekrar yazmıyorlar.
 * Bu tasarım kalıbının adı "Template Method" — ortak iskelet buradan gelir.
 */
public abstract class AbstractPage implements IPage {
    // Tüm alt sayfalarda kullanılabilmesi için protected yaptık
    protected final DashboardView parent;
    protected final InventoryController inventoryController;
    protected final TransactionController transactionController;
    protected final ReportController reportController;

    // Her sayfanın içeriği bu panel içine ekleniyor
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

        // Her sayfa için boş bir panel oluşturuyoruz — alt sınıflar bunu dolduracak
        this.container = new JPanel(new BorderLayout());
        this.container.setOpaque(false);
    }

    // NavigationManager bu metot ile sayfanın panel nesnesini alıyor
    @Override
    public JPanel getPagePanel() {
        return container;
    }

    // Sayfanın adı — CardLayout gösterimi için kullanılıyor
    @Override
    public String getPageName() {
        return pageName;
    }

    // Alt sınıflar kendi bileşenlerini bu container'a ekliyor
    protected JPanel getContainer() {
        return container;
    }
}
