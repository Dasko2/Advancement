package fr.advancement;

import net.minecraft.advancements.AdvancementProgress;

/** Ajouté par mixin à chaque succès de l'écran des succès : retient sa progression. */
public interface AdvancementWidgetData {
    AdvancementProgress advancement$getProgress();
}
