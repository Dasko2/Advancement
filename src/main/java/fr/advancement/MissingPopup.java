package fr.advancement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fr.advancement.mixin.AdvancementWidgetAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

/** Fenêtre qui liste ce qu'il manque pour terminer un succès à progression. */
public final class MissingPopup {
    private static Component title;
    private static int missing;
    private static int total;
    private static List<Component> entries = List.of();
    private static boolean open;

    private MissingPopup() {
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
        AdvancementWidgetAccessor accessor = (AdvancementWidgetAccessor) widget;
        AdvancementNode node = accessor.advancement$getNode();
        AdvancementProgress progress = accessor.advancement$getProgress();
        if (node == null || progress == null) return false;

        List<String> remaining = new ArrayList<>();
        for (String key : progress.getRemainingCriteria()) remaining.add(key);
        int done = 0;
        for (String ignored : progress.getCompletedCriteria()) done++;

        int all = remaining.size() + done;
        if (all <= 1 || remaining.isEmpty()) return false;

        DisplayInfo display = node.advancement().display().orElse(null);
        title = display != null ? display.getTitle() : Component.literal(node.holder().id().toString());
        missing = remaining.size();
        total = all;

        List<Component> names = new ArrayList<>();
        for (String key : remaining) names.add(criterionName(key));
        names.sort(Comparator.comparing(Component::getString, String.CASE_INSENSITIVE_ORDER));
        entries = names;
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
            if (I18n.exists(translationKey)) return Component.translatable(translationKey);
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

    public static void render(GuiGraphicsExtractor g) {
        Font font = Minecraft.getInstance().font;
        int screenW = g.guiWidth();
        int screenH = g.guiHeight();
        int lineH = font.lineHeight + 2;
        int pad = 8;

        Component head = title.copy().withStyle(ChatFormatting.YELLOW);
        Component sub = Component.translatable("advancement.popup.missing", missing, total)
                .withStyle(ChatFormatting.RED);
        Component foot = Component.translatable("advancement.popup.close").withStyle(ChatFormatting.GRAY);

        int n = entries.size();
        int entryW = 0;
        for (Component c : entries) entryW = Math.max(entryW, font.width(c));
        entryW += font.width("• ") + 14;

        int fixedH = pad * 2 + lineH * 3 + 8;
        int maxRows = Math.max(3, (screenH - 40 - fixedH) / lineH);
        int maxCols = Math.max(1, (screenW - 40 - pad * 2) / entryW);
        int cols = Math.max(1, Math.min(maxCols, (n + maxRows - 1) / maxRows));
        int rows = Math.min(maxRows, (n + cols - 1) / cols);
        int capacity = rows * cols;
        int shown = n <= capacity ? n : capacity - 1;

        int contentW = Math.max(cols * entryW, Math.max(font.width(head), Math.max(font.width(sub), font.width(foot))));
        int w = contentW + pad * 2;
        int h = fixedH + rows * lineH;
        int x = (screenW - w) / 2;
        int y = (screenH - h) / 2;

        g.nextStratum();
        g.fill(0, 0, screenW, screenH, 0x90000000);
        g.fill(x, y, x + w, y + h, 0xF0100010);
        g.outline(x, y, w, h, 0xFFC8A000);

        int cy = y + pad;
        g.text(font, head, x + (w - font.width(head)) / 2, cy, 0xFFFFFFFF, true);
        cy += lineH;
        g.text(font, sub, x + (w - font.width(sub)) / 2, cy, 0xFFFFFFFF, true);
        cy += lineH + 4;

        for (int i = 0; i < shown; i++) {
            int col = i / rows;
            int row = i % rows;
            Component line = Component.literal("• ").withStyle(ChatFormatting.RED).append(entries.get(i));
            g.text(font, line, x + pad + col * entryW, cy + row * lineH, 0xFFFFFFFF, true);
        }
        if (shown < n) {
            int i = shown;
            Component more = Component.literal("… +" + (n - shown)).withStyle(ChatFormatting.GRAY);
            g.text(font, more, x + pad + (i / rows) * entryW, cy + (i % rows) * lineH, 0xFFFFFFFF, true);
        }

        cy += rows * lineH + 4;
        g.text(font, foot, x + (w - font.width(foot)) / 2, cy, 0xFFFFFFFF, true);
    }
}
