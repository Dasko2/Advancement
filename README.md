# Advancement

Mod Fabric pour Minecraft **1.21.11** : dans le **menu des succès de base**, clique sur un succès qui a une progression (par exemple « Une aventure, la belle vie » à `1 / 42`) et une fenêtre te montre **tout ce qu'il te manque**.

- Pas de nouveau menu : ça se passe directement dans l'écran des succès (touche `L`).
- Clique sur un succès à progression → la liste des éléments manquants s'affiche (biomes, monstres, aliments…), triée par ordre alphabétique et rangée en colonnes.
- Clique n'importe où ou appuie sur `Échap` pour fermer la fenêtre.
- **100 % client** : rien à installer sur le serveur, ça marche sur **Paper**, Spigot, vanilla, etc.

## Installation

1. Installer [Fabric Loader](https://fabricmc.net/use/) 0.18.6+ pour Minecraft 1.21.11 et [Fabric API](https://modrinth.com/mod/fabric-api).
2. Mettre `advancement-1.0.0.jar` dans le dossier `mods` du **client** (Java 21 requis).

## Compiler

Pousse le dépôt sur GitHub : l'onglet **Actions** compile automatiquement et fournit le `.jar` dans les artefacts du run (`advancement`).
Sur la branche `main`, le `.jar` est aussi attaché à une release.

En local : Java 21 + Gradle 9.2.1, puis `gradle build` (jar dans `build/libs/`).
