package fr.advancement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

/** Fenêtre qui liste ce qu'il manque pour terminer un succès à progression. */
public final class MissingPopup {
    private static Component title;
    private static int missing;
    private static int total;
    private static List<String> entries = List.of();
    private static boolean open;
    private static AdvancementWidget hovered;
    private static int scroll;
    private static int maxScroll;

    // Zone intérieure de la fenêtre des succès de Minecraft
    private static final int INSIDE_X = 9;
    private static final int INSIDE_Y = 18;
    private static final int INSIDE_W = 234;
    private static final int INSIDE_H = 113;

    private MissingPopup() {
    }

    public static AdvancementWidget getHovered() {
        return hovered;
    }

    public static void setHovered(AdvancementWidget widget) {
        hovered = widget;
    }

    public static boolean isOpen() {
        return open;
    }

    public static void close() {
        open = false;
        entries = List.of();
        title = null;
    }

    /** Ouvre la fenêtre pour ce succès. Renvoie false s'il n'y a rien à afficher (pas de progression). */
    public static boolean openFor(AdvancementWidget widget) {
        AdvancementWidgetData data = (AdvancementWidgetData) (Object) widget;
        AdvancementProgress progress = data.advancement$getProgress();
        if (progress == null) return false;

        List<String> remaining = new ArrayList<>();
        for (String key : progress.getRemainingCriteria()) remaining.add(key);
        int done = 0;
        for (String ignored : progress.getCompletedCriteria()) done++;

        int all = remaining.size() + done;
        if (all <= 1 || remaining.isEmpty()) return false;

        DisplayInfo display = widget.getDisplay();
        title = display != null ? display.title() : Component.literal(widget.getAdvancement().id().toString());
        missing = remaining.size();
        total = all;

        List<String> names = new ArrayList<>();
        for (String key : remaining) names.add(criterionName(key).getString());
        names.sort(String.CASE_INSENSITIVE_ORDER);
        entries = names;
        scroll = 0;
        open = true;
        return true;
    }

    /** Transforme un nom de critère ("minecraft:plains", "apple"...) en nom lisible. */
    private static Component criterionName(String key) {
        String namespace = "minecraft";
        String path = key;
        int colon = key.indexOf(':');
        if (colon >= 0) {
            namespace = key.substring(0, colon);
            path = key.substring(colon + 1);
        }
        String dotted = path.replace('/', '.');
        for (String prefix : new String[] { "biome", "entity", "item", "block", "effect" }) {
            String translationKey = prefix + "." + namespace + "." + dotted;
            if (Language.getInstance().has(translationKey)) return Component.translatable(translationKey);
        }
        return Component.literal(prettify(path));
    }

    private static String prettify(String path) {
        StringBuilder out = new StringBuilder();
        boolean upper = true;
        for (char c : path.toCharArray()) {
            if (c == '_' || c == '/') {
                out.append(' ');
                upper = true;
            } else {
                out.append(upper ? String.valueOf(c).toUpperCase(Locale.ROOT) : String.valueOf(c));
                upper = false;
            }
        }
        return out.toString();
    }

    public static void scroll(int direction) {
        scroll = Math.max(0, Math.min(maxScroll, scroll + direction));
    }

    /** Dessine la liste dans la zone intérieure de la vraie fenêtre des succès, avec le style des infobulles de Minecraft. */
    public static void render(GuiGraphicsExtractor g) {
        Font font = Minecraft.getInstance().font;
        int wx = (g.guiWidth() - 252) / 2;
        int wy = (g.guiHeight() - 140) / 2;
        int x = wx + INSIDE_X;
        int y = wy + INSIDE_Y;
        int pad = 6;
        int lineH = 10;
        int cols = 2;
        int listTop = y + 32;
        int rows = Math.max(1, (y + INSIDE_H - 16 - listTop) / lineH);
        int colW = (INSIDE_W - pad * 2 - 6) / cols;

        int n = entries.size();
        int totalRows = (n + cols - 1) / cols;
        maxScroll = Math.max(0, totalRows - rows);
        scroll = Math.max(0, Math.min(maxScroll, scroll));

        g.nextStratum();
        g.fill(x, y, x + INSIDE_W, y + INSIDE_H, 0xF0100010);
        g.outline(x, y, INSIDE_W, INSIDE_H, 0xA05000FF);

        // En-tête : nom du succès et ce qu'il reste
        g.text(font, font.plainSubstrByWidth(title.getString(), INSIDE_W - pad * 2), x + pad, y + 6, 0xFFFFFFFF, true);
        Component sub = Component.translatable("advancement.popup.missing", missing, total);
        g.text(font, sub, x + pad, y + 18, 0xFFAAAAAA, true);
        g.fill(x + pad, y + 29, x + INSIDE_W - pad, y + 30, 0x40FFFFFF);

        // Liste des éléments manquants
        int bulletW = font.width("• ");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = (scroll + row) * cols + col;
                if (index >= n) break;
                int ex = x + pad + col * colW;
                int ey = listTop + row * lineH;
                g.text(font, "• ", ex, ey, 0xFFAAAAAA, true);
                g.text(font, font.plainSubstrByWidth(entries.get(index), colW - bulletW - 4), ex + bulletW, ey,
                        0xFFFFFFFF, true);
            }
        }

        // Barre de défilement
        if (maxScroll > 0) {
            int trackH = rows * lineH;
            int tx = x + INSIDE_W - 5;
            int thumbH = Math.max(8, trackH * rows / totalRows);
            int thumbY = listTop + (trackH - thumbH) * scroll / maxScroll;
            g.fill(tx, listTop, tx + 2, listTop + trackH, 0x40FFFFFF);
            g.fill(tx, thumbY, tx + 2, thumbY + thumbH, 0xFFAAAAAA);
        }

        Component foot = Component.translatable("advancement.popup.close");
        g.text(font, foot, x + pad, y + INSIDE_H - 12, 0xFF777777, true);
    }
}
