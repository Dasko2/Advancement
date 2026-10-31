package fr.advancement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;

/** Donne accès au succès et à sa progression derrière un widget de l'écran des succès. */
@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {
    @Accessor("advancementNode")
    AdvancementNode advancement$getNode();

    @Accessor("progress")
    AdvancementProgress advancement$getProgress();
}
