package fr.advancement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.advancement.AdvancementWidgetData;
import fr.advancement.MissingPopup;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;

/** Retient la progression de chaque succès et repère celui qui est survolé. */
@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin implements AdvancementWidgetData {
    @Unique
    private AdvancementProgress advancement$progress;

    @Inject(method = "setProgress", at = @At("HEAD"))
    private void advancement$onSetProgress(AdvancementProgress progress, CallbackInfo ci) {
        this.advancement$progress = progress;
    }

    // extractHover n'est appelé que pour le succès sous la souris
    @Inject(method = "extractHover", at = @At("HEAD"))
    private void advancement$onHover(GuiGraphicsExtractor graphics, int x, int y, float fade, int a, int b, int c,
            CallbackInfo ci) {
        MissingPopup.setHovered((AdvancementWidget) (Object) this);
    }

    @Override
    public AdvancementProgress advancement$getProgress() {
        return this.advancement$progress;
    }
}
