package com.pharmacy.views.navigation;

import com.pharmacy.views.pages.IPage;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * DashboardView içerisindeki sayfaları (CardLayout kullanarak) yöneten sistem.
 * Hangi sayfanın görüneceğini bu sınıf belirliyor; sayfa geçişlerinde
 * onPageExit / onPageEnter çağrılarını da o tetikliyor.
 */
public class NavigationManager {
    private final JPanel centerWrapper;
    private final CardLayout cardLayout;
    // Sayfa adı → IPage nesnesini tutan harita — hızlı erişim için Map kullandık
    private final Map<String, IPage> pages;
    private IPage currentPage;

    public NavigationManager(JPanel centerWrapper, CardLayout cardLayout) {
        this.centerWrapper = centerWrapper;
        this.cardLayout = cardLayout;
        this.pages = new HashMap<>();
    }

    // Yeni bir sayfayı sisteme kaydediyoruz ve CardLayout'a ekliyoruz
    public void registerPage(IPage page) {
        pages.put(page.getPageName(), page);
        centerWrapper.add(page.getPagePanel(), page.getPageName());
    }

    // Belirtilen sayfaya geçiş yap — önce eski sayfayı uğurla, sonra yenisini karşıla
    public void showPage(String pageName) {
        if (!pages.containsKey(pageName)) {
            return; // Bilinmeyen sayfa adı gelirse sessizce geç
        }

        // Aktif sayfanın çıkış rutinini çalıştır
        if (currentPage != null) {
            currentPage.onPageExit();
        }

        // Yeni sayfayı öne al ve giriş rutinini tetikle
        currentPage = pages.get(pageName);
        cardLayout.show(centerWrapper, pageName);
        currentPage.onPageEnter();
    }

    // O an aktif olan sayfayı döndürüyoruz — DashboardView bunu kullanıyor
    public IPage getCurrentPage() {
        return currentPage;
    }
}
