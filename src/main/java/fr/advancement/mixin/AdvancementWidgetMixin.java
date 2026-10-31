package fr.advancement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.advancement.AdvancementWidgetData;
import fr.advancement.MissingPopup;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;

/** Retient les infos de chaque succès et repère celui qui est survolé. */
@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin implements AdvancementWidgetData {
    @Unique
    private AdvancementNode advancement$node;
    @Unique
    private DisplayInfo advancement$display;
    @Unique
    private AdvancementProgress advancement$progress;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void advancement$onInit(AdvancementTab tab, Minecraft minecraft, AdvancementNode node,
            DisplayInfo display, CallbackInfo ci) {
        this.advancement$node = node;
        this.advancement$display = display;
    }

    @Inject(method = "setProgress", at = @At("HEAD"))
    private void advancement$onSetProgress(AdvancementProgress progress, CallbackInfo ci) {
        this.advancement$progress = progress;
    }

    // drawHover n'est appelé que pour le succès sous la souris
    @Inject(method = "drawHover", at = @At("HEAD"))
    private void advancement$onHover(GuiGraphics graphics, int x, int y, float fade, int screenWidth,
            int screenHeight, CallbackInfo ci) {
        MissingPopup.setHovered((AdvancementWidget) (Object) this);
    }

    @Override
    public AdvancementNode advancement$getNode() {
        return this.advancement$node;
    }

    @Override
    public DisplayInfo advancement$getDisplay() {
        return this.advancement$display;
    }

    @Override
    public AdvancementProgress advancement$getProgress() {
        return this.advancement$progress;
    }
}
