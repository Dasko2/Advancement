package fr.advancement;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;

/** Ajouté par mixin à chaque succès de l'écran des succès : retient son noeud, son affichage et sa progression. */
public interface AdvancementWidgetData {
    AdvancementNode advancement$getNode();

    DisplayInfo advancement$getDisplay();

    AdvancementProgress advancement$getProgress();
}
