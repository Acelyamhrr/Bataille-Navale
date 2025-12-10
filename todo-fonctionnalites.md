# TODO COMPLET - Bataille Navale

## D1 - Génération de la grille
- [x] **Niveau 1** : Taille fixe 10x10
- [x] **Niveau 2** : Taille paramétrable 6x6 à 10x10

## D2 - Choix du nombre de bateaux
- [x] **Niveau 1** : Nombre fixe (1 de chaque type)
- [x] **Niveau 2** : 1 à 3 bateaux de chaque type (max 35 cases, impacte les 2 joueurs)

## D3 - Placement des bateaux de l'ordinateur
- [x] **Niveau 1** : Placement fixe ⚠️ (à refaire)
- [x] **Niveau 2** : Placement aléatoire

## D4 - Placement des bateaux du joueur humain
- [x] **Niveau 1** : Placement fixe ⚠️ (à refaire)
- [x] **Niveau 2** : Placement aléatoire
- [x] **Niveau 3** : Placement personnalisé avec choix orientation + position

## D5 - Tirs de l'ordinateur
- [x] **Niveau 1** : Tirs aléatoires
- [ ] **Niveau 2** : IA intelligente - chercher à couler quand touche un bateau

## D6 - Détection de la fin de partie
- [x] Quand un joueur a coulé tous les bateaux adverses

## D7 - Recommencer une partie
- [x] **Niveau 0** : Pas de possibilité de recommencer ⚠️ (actuellement à ce niveau)
- [ ] **Niveau 1** : Recommencer sans redémarrer l'application

## D8 - Armes supplémentaires et pièges
- [x] **Niveau 0** : Aucune arme ni piège
- [x] **Niveau 1** : 1 arme ou 1 piège
- [x] **Niveau 2** : 1 arme et 1 piège
- [x] **Niveau 3** : 3 armes ou pièges
- [x] **Niveau 4** : 2 armes (Bombe, Sonar) + 2 pièges (Tornade, Trou noir)
  - [x] Missile (infini)
  - [x] Bombe (1 usage) - implémentée mais à tester
  - [ ] Sonar (1 usage) - BUG à corriger
  - [x] Tornade (piège)
  - [x] Trou noir (piège)

## D9 - Sélection de l'arme
- [x] Interface pour sélectionner Missile / Bombe / Sonar

## D10 - Placement des pièges
- [x] **Niveau 1** : Placement fixe avant les bateaux ⚠️ (à refaire)
- [x] **Niveau 2** : Placement aléatoire après les bateaux
- [x] **Niveau 3** : Placement manuel par le joueur après les bateaux

## D11 - Choix du mode île
- [x] **Niveau 1** : Mode imposé (on joue forcément avec l'île)
- [x] **Niveau 2** : Mode au choix lors de la configuration

## D12 - Placement des armes/pièges sur l'île
- [] **Niveau 1** : Placement aléatoire
- [] **Niveau 2** : Placement manuel après placement des bateaux

## D13 - Visualisation de l'historique
- [x] **Niveau 1** : Visualisation dans la console
- [x] **Niveau 2** : Visualisation dans l'interface graphique

---

## E1 - Écran de configuration
- [x] Choix taille de la grille
- [x] Choix nombre de bateaux de chaque type
- [x] Choix mode "Île" activé ou non
- [x] Bouton pour passer à l'écran de placement

## E2 - Écran de placement
- [x] Placement fixe initial
- [x] Bouton génération placement aléatoire
- [x] Placement manuel ergonomique des bateaux
- [x] Bouton démarrer partie → écran principal

## E3 - Écran principal
### Affichage général
- [x] Grille joueur avec ses bateaux
- [x] Grille adverse pour attaquer
- [x] Numéro du tour actuel

### Pour chaque joueur
- [x] Dernier coup joué
- [x] Nombre bateaux intacts
- [x] Nombre bateaux touchés
- [x] Nombre bateaux coulés
- [x] Nombre tirs dans l'eau
- [x] Cases touchées / cases totales
- [x] Liste armes disponibles/utilisées
- [x] Nombre cases île restant à fouiller (si mode île)

### Visualisation grille
- [x] Case non touchée
- [x] Case touchée (bateau non coulé)
- [x] Case touchée (bateau coulé)
- [x] Case où arme/piège utilisé
- [x] Case île non fouillée
- [x] Case île fouillée vide
- [x] Case île fouillée avec arme/piège

### Actions joueur
- [x] Sélectionner arme à utiliser
- [x] Cliquer case pour attaquer
- [x] Cliquer case île pour fouiller

### Tour ordinateur
- [x] Visualisation ergonomique du tour robot
- [x] Comprendre action effectuée

## E4 - Écran de fin de partie
- [ ] Affichage du vainqueur
- [ ] Résumé statistiques partie
- [ ] Bouton recommencer (si D7 implémenté)

---