# Advancement

Mod Fabric pour Minecraft **26.3** : dans le **menu des succès de base**, clique sur un succès qui a une progression (par exemple « Heure de l'aventure » à `1 / 42`) et la liste de **ce qu'il te manque** s'affiche dans la fenêtre des succès, avec le style de Minecraft.

- Pas de nouveau menu : ça se passe directement dans l'écran des succès (touche `L`).
- Clique sur un succès à progression → la liste des éléments manquants s'affiche (biomes, monstres, aliments…), triée par ordre alphabétique, en deux colonnes (molette pour défiler).
- Clique n'importe où ou appuie sur `Échap` pour fermer la fenêtre.
- **100 % client** : rien à installer sur le serveur, ça marche sur **Paper**, Spigot, vanilla, etc.

## Installation

1. Installer [Fabric Loader](https://fabricmc.net/use/) 0.20.1+ pour Minecraft 26.3 et [Fabric API](https://modrinth.com/mod/fabric-api).
2. Mettre `advancement-1.1.0.jar` dans le dossier `mods` du **client** (Java 25 requis).

## Compiler

Pousse le dépôt sur GitHub : l'onglet **Actions** compile automatiquement et fournit le `.jar` dans les artefacts du run (`advancement`).
Sur la branche `main`, le `.jar` est aussi attaché à une release.

En local : Java 25 + Gradle 9.7.0, puis `gradle build` (jar dans `build/libs/`).
