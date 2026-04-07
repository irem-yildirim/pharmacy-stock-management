package com.pharmacy.views.pages;

import javax.swing.JPanel;

/**
 * Sayfaların yaşam döngüsünü ve UI component'lerini belirleyen arayüz.
 * Polimorfizm sayesinde NavigationManager tüm sayfaları IPage formunda yönetir.
 */
public interface IPage {
    JPanel getPagePanel();
    void onPageEnter();
    void onPageExit();
    String getPageName();
}
