package fr.advancement.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.advancement.MissingPopup;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * Greffe sur le menu des succès de base : un clic sur un succès avec une progression
 * (ex. 1 / 42) ouvre la liste de ce qu'il manque.
 */
@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin {
    // Position de la zone intérieure de la fenêtre (valeurs de Minecraft)
    private static final int ADV$INSIDE_X = 9;
    private static final int ADV$INSIDE_Y = 18;

    @Shadow
    private AdvancementTab selectedTab;
    @Shadow
    private int leftPos;
    @Shadow
    private int topPos;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void advancement$onClick(MouseButtonEvent event, boolean doubleClick,
            CallbackInfoReturnable<Boolean> cir) {
        // Fenêtre déjà ouverte : un clic la ferme
        if (MissingPopup.isOpen()) {
            MissingPopup.close();
            cir.setReturnValue(true);
            return;
        }
        if (event.button() != 0 || this.selectedTab == null) return;

        // Le clic doit tomber dans la zone des succès (pas sur les onglets ni les bords)
        double mx = event.x() - (this.leftPos + ADV$INSIDE_X);
        double my = event.y() - (this.topPos + ADV$INSIDE_Y);
        if (mx < 0 || my < 0 || mx >= AdvancementsScreen.WINDOW_INSIDE_WIDTH
                || my >= AdvancementsScreen.WINDOW_INSIDE_HEIGHT) {
            return;
        }

        AdvancementWidget hovered = ((AdvancementTabAccessor) this.selectedTab).advancement$getHovered();
        if (hovered != null && MissingPopup.openFor(hovered)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void advancement$onKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (MissingPopup.isOpen() && event.key() == GLFW.GLFW_KEY_ESCAPE) {
            MissingPopup.close();
            cir.setReturnValue(true);
        }
    }

    // Pendant que la fenêtre est ouverte, on masque l'infobulle normale des succès
    @Inject(method = "extractTooltips", at = @At("HEAD"), cancellable = true)
    private void advancement$hideTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (MissingPopup.isOpen()) ci.cancel();
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void advancement$drawPopup(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick,
            CallbackInfo ci) {
        if (MissingPopup.isOpen()) MissingPopup.render(graphics);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void advancement$onRemoved(CallbackInfo ci) {
        MissingPopup.close();
    }
}
