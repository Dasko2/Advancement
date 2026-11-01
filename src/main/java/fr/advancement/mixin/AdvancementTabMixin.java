package fr.advancement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.advancement.MissingPopup;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin {
    // Chaque image, on oublie le succès survolé ; extractHover le retrouvera s'il y en a un
    @Inject(method = "extractTooltips", at = @At("HEAD"))
    private void advancement$resetHover(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        MissingPopup.setHovered(null);
    }
}
