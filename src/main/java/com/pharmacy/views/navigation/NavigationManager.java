package com.pharmacy.views.navigation;

import com.pharmacy.views.pages.IPage;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * DashboardView içerisindeki sayfaları (CardLayout kullanarak) yöneten sistem.
 * Sayfaların onPageEnter vb. yaşam döngüsü tetiklemelerini üstlenir.
 */
public class NavigationManager {
    private final JPanel centerWrapper;
    private final CardLayout cardLayout;
    private final Map<String, IPage> pages;
    private IPage currentPage;

    public NavigationManager(JPanel centerWrapper, CardLayout cardLayout) {
        this.centerWrapper = centerWrapper;
        this.cardLayout = cardLayout;
        this.pages = new HashMap<>();
    }

    public void registerPage(IPage page) {
        pages.put(page.getPageName(), page);
        centerWrapper.add(page.getPagePanel(), page.getPageName());
    }

    public void showPage(String pageName) {
        if (!pages.containsKey(pageName)) {
            return;
        }

        if (currentPage != null) {
            currentPage.onPageExit();
        }

        currentPage = pages.get(pageName);
        cardLayout.show(centerWrapper, pageName);
        currentPage.onPageEnter();
    }
    
    public IPage getCurrentPage() {
        return currentPage;
    }
}
