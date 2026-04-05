package com.pharmacy.views;

import java.awt.*;

/**
 * Shared design tokens for the Pharmacy Management System UI.
 * Accent palette: pastel orange — warm, professional, consistent.
 */
public class ThemeConstants {

    // ── Backgrounds ──────────────────────────────────────────────────────────
    public static final Color BG_WHITE = new Color(0xFFFFFF);
    public static final Color BG_LIGHT = new Color(0xF5F7FA);
    public static final Color BG_CARD = new Color(0xFFFFFF);

    // ── Pastel Orange Accent ──────────────────────────────────────────────────
    public static final Color ACCENT = new Color(255, 167, 80);
    public static final Color ACCENT_HOVER = new Color(255, 145, 50);
    public static final Color ACCENT_DARK = new Color(230, 120, 30);
    public static final Color ACCENT_LIGHT = new Color(255, 237, 210);

    // ── Sidebar ───────────────────────────────────────────────────────────────
    public static final Color SIDEBAR_BG = new Color(0x1B3A4B);
    public static final Color SIDEBAR_HOVER = new Color(0x254F65);
    public static final Color SIDEBAR_SECTION = new Color(0xFF, 0xFF, 0xFF, 120);

    // ── Text ─────────────────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY = new Color(40, 40, 40);
    public static final Color TEXT_SECONDARY = new Color(110, 110, 110);
    public static final Color TEXT_WHITE = Color.WHITE;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
}
