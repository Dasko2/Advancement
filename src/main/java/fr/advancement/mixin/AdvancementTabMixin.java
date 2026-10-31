package fr.advancement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.advancement.MissingPopup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin {
    // Chaque image, on oublie le succès survolé ; drawHover le retrouvera s'il y en a un
    @Inject(method = "drawTooltips", at = @At("HEAD"))
    private void advancement$resetHover(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, CallbackInfo ci) {
        MissingPopup.setHovered(null);
    }
}
