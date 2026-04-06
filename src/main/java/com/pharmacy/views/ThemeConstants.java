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
    public static final Color BG_CARD = new Color(0xF8F9FA);
    public static final Color BG_CARD_HOVER = new Color(0xFFFFFF);

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

    // ── Design Tokens ─────────────────────────────────────────────────────────
    public static final int CARD_RADIUS = 18;
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 15);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 26);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 17);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
}
