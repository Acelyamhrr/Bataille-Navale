# Planning du Projet Bataille Navale

Ce document définit la répartition des tâches, la planification et les objectifs par semaine pour le projet.

## Membres du binôme
- **Elora**
- **Acelya**

## Dates importantes
- **Rendu 1 : 23 novembre**
  - UML complet (tous niveaux)
  - Code squelette
  - Rapport expliquant la conception
- **Rendu 2 : 21 décembre**
  - Code final
  - INSTALL.md
  - Rapport final

---

## Semaine du 10 → 16 novembre
**Objectif :** Avoir l’UML posé + projet Java structuré

| Personne | Tâches |
|---------|--------|
| **Elora** | - Lister les classes du modèle (Bateau, Grille, Cellule, Joueur, Partie, Arme, Piège, Île)<br>- Commencer le diagramme UML (PlantUML) du modèle<br>- Inclure les niveaux supérieurs dans l’UML même si non codés |
| **Acelya** | - Créer le projet Java avec packages `model / view / controller`<br>- Créer les squelettes des 4 écrans (Config, Placement, Jeu, Fin)<br>- Mettre en place le dépôt Git + branches `develop`, `rendu1`, `rendu2` |
| **Ensemble** | - Réunion de validation UML (1h) |

---

## Semaine du 17 → 23 novembre (Rendu 1)
**Objectif :** Finaliser UML + rapport + squelettes propres

| Personne | Tâches |
|---------|--------|
| **Elora** | - Finaliser UML complet<br>- Écrire la partie conception du rapport<br>- Créer les classes modèle (constructeurs & attributs) |
| **Acelya** | - Faire la navigation entre les écrans (sans logique de jeu)<br>- Écrire le rapport (mise en page, intro, conclusion)<br>- Faire un README simple |
| **Ensemble (22–23 nov)** | - Vérifier rapport + UML + code<br>- Push final sur `rendu1` avant 23:59 |

---

## Semaine du 24 novembre → 1 décembre
**Objectif :** Jeu simple (sans armes) jouable

| Personne | Tâches |
|---------|--------|
| **Elora** | - Implémenter Grille, Bateaux, Placement fixe<br>- Touché / Manqué / Coulé + fin de partie |
| **Acelya** | - Afficher les deux grilles dans l’interface<br>- Clic → tir → affichage résultat |
| **Ensemble** | - Tester une partie complète |

---

## Semaine du 2 → 8 décembre
**Objectif :** Améliorer expérience + IA simple

| Personne | Tâches |
|---------|--------|
| **Elora** | - IA simple qui cherche à finir un bateau touché |
| **Acelya** | - Améliorer interface (couleurs, infos, fin de partie propre) |
| **Ensemble** | - Tests & corrections |

---

## Semaine du 9 → 15 décembre
**Objectif :** Ajouter Bombe, Sonar, Trou noir, Tornade (sans île)

| Personne | Tâches |
|---------|--------|
| **Elora** | - Implémenter les effets des armes/pièges |
| **Acelya** | - Interface de sélection de l’arme + affichage des usages |
| **Ensemble** | - Tester chaque arme, puis ensemble |

---

## Semaine du 16 → 21 décembre (Rendu final)
**Objectif :** Mode île + Documentation + Rendu final

| Personne | Tâches |
|---------|--------|
| **Elora** | - Implémenter mode Île + mise à jour UML final |
| **Acelya** | - Écrire `INSTALL.md` + terminer le rapport final |
| **Ensemble (20–21 déc)** | - Tests finaux<br>- Push final sur `rendu2` avant 23:59 |

---

## Résumé
| Période | Objectif |
|---|---|
| 10-16 nov | UML complet + structure projet |
| 17-23 nov | Rapport + Rendu1 |
| 24 nov - 1 déc | Jeu simple fonctionnel |
| 2 - 8 déc | IA + interface propre |
| 9 - 15 déc | Armes & pièges |
| 16 - 21 déc | Mode île + docs + rendu final |
