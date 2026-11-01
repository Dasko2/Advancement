package fr.advancement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.advancement.MissingPopup;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void advancement$onClick(MouseButtonEvent event, boolean doubleClick,
            CallbackInfoReturnable<Boolean> cir) {
        MissingPopup.LOGGER.info("[Advancement] clic reçu : bouton={}, fenêtre ouverte={}", event.button(),
                MissingPopup.isOpen());
        // Fenêtre déjà ouverte : un clic la ferme
        if (MissingPopup.isOpen()) {
            MissingPopup.close();
            cir.setReturnValue(true);
            return;
        }
        if (event.button() != 0) return;

        AdvancementWidget hovered = MissingPopup.getHovered();
        MissingPopup.LOGGER.info("[Advancement] clic gauche, succès survolé : {}", hovered);
        if (hovered != null && MissingPopup.openFor(hovered)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void advancement$onKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (MissingPopup.isOpen() && event.key() == 256) {
            MissingPopup.close();
            cir.setReturnValue(true);
        }
    }

    // Pendant que la fenêtre est ouverte, on masque l'infobulle normale des succès
    @Inject(method = "extractTooltips", at = @At("HEAD"), cancellable = true)
    private void advancement$hideTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (MissingPopup.isOpen()) {
            MissingPopup.setHovered(null);
            ci.cancel();
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void advancement$onScroll(double mouseX, double mouseY, double scrollX, double scrollY,
            CallbackInfoReturnable<Boolean> cir) {
        if (MissingPopup.isOpen()) {
            MissingPopup.scroll(scrollY > 0 ? -1 : 1);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void advancement$drawPopup(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick,
            CallbackInfo ci) {
        if (MissingPopup.isOpen()) MissingPopup.render(graphics);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void advancement$onRemoved(CallbackInfo ci) {
        MissingPopup.close();
        MissingPopup.setHovered(null);
    }
}
