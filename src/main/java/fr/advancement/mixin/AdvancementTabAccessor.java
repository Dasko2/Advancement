package fr.advancement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;

/** Donne accès au succès actuellement survolé dans un onglet. */
@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {
    @Accessor("hovered")
    AdvancementWidget advancement$getHovered();
}
