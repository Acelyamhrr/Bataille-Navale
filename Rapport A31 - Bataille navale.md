**RAPPORT DE CONCEPTION \- RENDU 2**

Projet Bataille Navale \- A31  
**__________________________________________________________________________________________________**

**Introduction et bilan global**

**Vue d’ensemble du projet**

Ce rapport présente le rendu final de notre projet “Bataille Navale”. Depuis le premier rendu, nous avons beaucoup enrichi et finalisé notre application, en respectant rigoureusement, du mieux qu’on aie pu, l’architecture MVC et en appliquant les patterns de conception appropriés.

Notre objectif initial était de concevoir une architecture complète dès le départ, capable d’accueillir tous les niveaux de fonctionnalités. Nous avons atteint cet objectif : l’application implémente tous les niveaux de toutes les fonctionnalités demandées, et propose même plusieurs fonctionnalités bonus, qui améliorent l’expérience utilisateur.

**Fonctionnalités bonus développées**

Au-delà des exigences, nous avons implémentés plusieurs améliorations : 

1. Menu principal.  
2. Système de légendes expliquant les couleurs et abréviations.  
3. Triple mode de recommencement : rejouer la même partie / nouveaux placements / nouvelle configuration  
4. Inventaire de pièges dynamique : pièges trouvés sur l’île peuvent être stockés et placés ultérieurement.  
5. Dialogues visuels avancés : effets de pièges avec couleurs adaptés (vert \= bénéfique, rouge \= néfaste), résultat sonar avec grille 3x3 visuelle, dialogue de choix lors de découverte de pièges  
6. Preview visuel lors du placement (vert \= valide, rouge \= invalide)

**Points à améliorer identifiés :** 

- Game view trop volumineuse : pourrait être allégé avec d’autres classes supplémentaires, en plus de celles que l’on a déjà. Nous avons remarqué ce problème quelques semaines avant la fin du projet. Nous voulions y revenir plus tard, pensant que ce n’était pas très long, mais finalement, il s’est avéré beaucoup plus complexe, et nécessitant beaucoup plus de temps. C’est pour cela qu’il est encore améliorable. Mais par manque de temps, nous en sommes ici.  
- Méthodes d’initialisation trop longues : initComponents dans ConfigurationView fait beaucoup trop de lignes pour une seule méthode. De même que la GameView, elle aurait pu être allégée.

Ces points d’améliorations n’impactent pas la fonctionnalité. Mais niveau code, c’est un peu moyen. Avec plus de temps, et surtout en anticipant mieux, nous aurions refactorisé davantage la gameView en introduisant une autre classe, et en extrayant la logique Observer dans une classe dédiée.

**Évolution depuis le rendu 1**

**Architecture MVC.**  
L’architecture MVC définie au rendu 1 a été maintenue et consolidée. Nous avons ajouté : 

**Dans le Model :** 

-  Package **game.Results** avec **TurnResult** et **AttackResult** pour encapsuler les résultats d'actions  
- Classe **GameStats** pour calculer les statistiques de fin de partie  
- Classe **TrapActivation** pour tracer les activations de pièges  
- Classes **SelectionState** et **PreviewInfo** dans placement pour la communication avec la vue

**Dans le Controller :** 

- **EndController** pour gérer l'écran de fin  
- Méthodes de recommencement dans **CentralController** (3 modes)  
- Gestion de l'inventaire de pièges dans **GameController**

**Dans la View :** 

- Tous les panels réutilisables (**GameGridPanel**, **PlayerStatsPanel**, etc.)  
- Package dialogs avec **GameDialogs** pour centraliser les dialogues  
- Package utils avec **GameColors** pour centraliser les couleurs

La règle fondamentale a été respectée : les vues ne contiennent aucune logique métier, elles se contentent d’afficher et de transmettre les événements au contrôleur.

**Changement majeurs**

**Résultats encapsulés (TurnTesult, AttackResult)**  
Au lieu de retourner des booléens ou des valeurs primitives, nous avons créé des classes de résultats qui encapsulent toute l'information d'une action :

```
public class TurnResult {  
   private boolean _success;  
   private String _errorMessage;  
   private AttackResult _attackResult;  
   private boolean _tornadoActivated;  
   private Position _redirectedTo;  
   private WeaponType _weaponFound;  
   private TrapType _trapFound;  
   // ...  
}
```

De cette façon, le contrôleur reçoit toute l’information en une fois.  
Il est plus facile d’ajouter de nouvelles informations sans changer les signatures.  
Le modèle construit des résultats riches et explicites.

**Statistiques calculées (GameStats)**  
Plutôt que de passer 15 paramètres à EndView, nous avons créé GameStats qui calcule et encapsule toutes les statistiques :

```
GameStats playerStats = GameStats.calculate(_game.getPlayer(), _game.getRobot(), gridSize);
```

Un seul objet à passer à la vue.  
Calculs centralisés et réutilisables.  
Facilite les tests unitaires.

#### **Inventaire de pièges**

Initialement non prévu, nous avons ajouté un système d'inventaire pour les pièges trouvés sur l'île :

* Player a une **Map\<TrapType, Integer\>** **\_trapInventory**  
* **InventoryPanel** affiche l'inventaire et permet de placer les pièges  
* **GameController** gère le mode "placement de piège depuis inventaire"

Cela enrichit considérablement le gameplay du mode île.

**Package Model**

**Structure générale**

Le package model est organisé en sous-packages :

```
model/  
├── players/           // Joueurs (Player)  
├── enums/             // Énumérations (types, états, modes)  
├── contents/          // Éléments du jeu  
│   ├── fleet/         // Bateaux (Boat, BoatFactory)  
│   ├── weapons/       // Armes (Weapon, Bomb, Missile, Sonar, WeaponFactory)  
│   └── traps/         // Pièges (Trap, BlackHole, Tornado, TrapFactory)  
├── grid/              // Grille, cases, position, île  
├── placement/         // Logique de placement  
└── game/              // Logique de partie et configuration  
   ├── RobotStrategy/  //Stratégies du robot  
   └── Results/        // Résultats d'actions
```

Cette organisation garantit que chaque package a une responsabilité unique et facilite la navigation.

**Quelques classes importantes**

```
Position  
Classe utilitaire qui encapsule coordonnées ET orientation :  
public class Position {  
   private int _x;  
   private int _y;  
   private Orientation _orientation;  
}
```

**Pourquoi ?**

* Plus lisible : placeBoat(boat, position) vs placeBoat(boat, x, y, orientation)  
* Facilite l'ajout de méthodes utilitaires (distance, adjacence...)  
* Utilisée partout : grille, bateaux, pièges, attaques

#### **Content (classe abstraite)**

Tous les éléments placés sur la grille implémentent Content :

**Avantages :**

* Square peut contenir n'importe quel Content sans connaître son type  
* Facile d'ajouter de nouveaux types de contenu  
* Polymorphisme utilisé dans toute la grille

**Game : la logique du jeu**  
La classe Game coordonne toute la partie. Elle ne contient que de la logique métier, aucun affichage.  
Elle : 

- Initialise les joueurs et leurs grilles  
- Gère les tours (joueur puis robot)  
- Exécute les attaques (missile, bombe, sonar)  
- Gère les pièges (activation, effets)  
- Détecte la fin de partie

Algorithme d'un tour :

```
1. Récupérer l'action du joueur (arme \+ position)  
2. Vérifier si tornado active → transformer la position  
3. Récupérer la case visée  
4. Examiner le contenu :  
    a) BlackHole → rebond sur l'attaquant  
    b) Tornado → activer (3 tours)  
    c) Autre → attaque normale  
5. Utiliser l'arme (décrémenter compteur)  
6. Retourner TurnResult avec toutes les infos
```

Game gère les interactions complexes entre pièges. Par exemple, si un joueur avec une tornado active touche un trou noir, le rebond est aussi affecté par la tornado. Cette cohérence est garantie car tout passe par Game.

**Grid et Square**

**Grid** : contient les cases et gère les validations de placement.  
**Square** : représente une case de la grille

Les Square sont observables : quand elles sont attaquées ou fouillées, elles notifient les observateurs (la vue).

**Bateaux avec Observer**  
Les Boat implémentent le pattern Observer pour notifier la vue de leurs changements d'état.

Avantages :  
La vue se met à jour automatiquement quand un bateau change  
Pas besoin de rafraîchissement manuel  
Séparation complète : le bateau ne connaît pas la vue

**Armes avec méthode use()**

Les armes calculent les positions affectées.  

Exemples :  
```
Missile.use() → retourne \[position\] (1 case)  
Bomb.use() → retourne \[center, haut, bas, gauche, droite\] (5 cases)  
Sonar.use() → retourne les 9 positions du carré 3×3  
```
**Pourquoi ?**  
L'arme connaît sa zone d'effet  
Game traite ensuite ces positions (vérifier contenu, appliquer dégâts)  
Facile d'ajouter de nouvelles armes avec des effets différents

Pièges : logique déléguée à Game  
Les pièges sont simples et stockent uniquement leur état.

La tornado génère un mapping aléatoire de toutes les positions lors de son activation. Chaque attaque transforme la position cible selon ce mapping.  
La tornado est attachée au joueur (Player.\_tornado) car elle affecte toutes ses actions pendant 3 tours, pas seulement une case.

**Player**  
La classe Player regroupe tout ce qui concerne un joueur.

Pourquoi pas de hiérarchie HumanPlayer/RobotPlayer ?  
Le comportement différent du robot est géré par la Strategy  
Les méthodes de Player sont les mêmes pour humain et robot  
Plus simple et plus flexible

Tornado dans Player : choix de conception important. La tornado affecte le joueur (pas une case), donc elle doit être attachée au joueur.

**Stratégies du robot**  
Nous avons utilisé le pattern STrategy pour gérer les modes du robot.  
Nous avons une interface RobotStrategy, ainsi que deux classes RandomRobotStrategy et SmartRobotStrategy qui étendent cette interface.  
De cette manière, il est plus facile d’ajouter de nouvelles modes, le robot n’a pas besoin des détails de sa stratégie et c’est testable facilement.

**Configuration et placement**  
GameConfig : centralise TOUS les paramètres d'une partie.  
GamePlacement : stocke les positions de tous les éléments  
Cette séparation permet de valider la config avant le placement et de réutiliser la même config avec différents placements. Elle permet également de simplifier les signatures de méthodes.

**PACKAGE CONTROLLER**

Le package controller contient 5 contrôleurs.  
Chaque contrôleur gère un seul écran et coordonne les interactions entre son modèle et sa vue.

Comme indique leurs noms : 

- CentralController gère la navigation entre les écrans. Elle crée et détruit les vues, permet de passer d'un écran à l'autre, de conserver la config et le placement entre les écrans et de gérer les 3 modes de recommencement. Elle a une vision globale de l’application. Il est de cette manière plus facile d’ajouter de nouveaux écrans.  
- ConfigurationController est minimal car toute la logique est dans GameConfig. Le contrôleur récupère les données brutes de la vue, construit le modèle, et retourne le résultat.  
- PlacementController implémente le pattern Observer entre Placement (modèle) et PlacementView (vue).  
```
  User clique → PlacementView.onGridClick()  
             → PlacementController.onGridClick()  
             → Placement.tryPlaceBoat()  
             → notifyObservers()  
             → PlacementView.onGridChanged()  
             → Mise à jour graphique  
```
    
- GameController est le plus gros contrôleur car il gère les clics sur les grilles (joueur et robot), la sélection d'armes, la fouille de l'île, le placement de pièges depuis l'inventaire, les tours du joueur et du robot, l’affichage des résultats (attaques, pièges, sonar), les statistiques, la fin de partie  
  handleWeaponAttack(target)  
```
   → _game.playerAttack(weapon, target)  // Modèle  
   → TurnResult result  
   → displayPlayerTurnResult(result)     // Vue  
       → _view.setPlayerAction(...)  
       → _view.updateRobotStats(...)  
       → _view.showSonarResult(...) si sonar  
       → _view.showTrapEffect(...) si piège  
   → endPlayerTurn()                     // Tour robot  
```
  Le contrôleur ne fait aucun calcul, il délègue tout au modèle et transmet les résultats à la vue.  
  GameController enregistre la vue comme observateur sur tous les bateaux et toutes les cases. Ainsi, quand un bateau est touché ou une case attaquée, la vue se met à jour automatiquement.  
- EndController est le plus simple : il récupère le choix du joueur pour la façon de recommencer et délègue immédiatement au CentralController qui gère la navigation.

**PACKAGE VIEW**  
Le package view est organisé ainsi :  

```
view/  
├── dialogs/            	\# Boîtes de dialogue réutilisables  
│   ├── BoatCustomizationDialog	\# choisir les bateaux  
│   └── GameDialogs  
├── panels/             	\# Composants de panels réutilisables  
│   ├── GameGridPanel 	\# affiche une grille de jeu  
│   ├── PlayerStatsPanel	\# statistiques d'un joueur  
│   ├── WeaponSelectionPanel	\# sélectionner l'arme à utiliser  
│   ├── ActionHistoryPanel		\# historique en 3 zones  
│   ├── InventoryPanel		\# inventaire de pièges  
│   ├── PlacementGridPanel	  
│   ├── PlacementControlPanel  
│   └── TrapPlacementPanel  
├── utils/              \# Utilitaires (couleurs)  
│   └── GameColors		\# regroupe toutes les couleurs utilisés  
├── MenuView  
├── ConfigurationView	\# configurer tous les paramètres de la partie  
├── PlacementView  
├── GameView  
└── EndView			\# résultat de la partie avec stats
```

Toutes les vues sont naturellement dans le package **view/** organisé en sous-packages : 

Cette organisation facilite la réutilisation et la maintenance du code graphique.

Chaque vue a une responsabilité unique et bien définie : 

- **MenuView** : Menu principal  
- **ConfigurationView** : Configuration de la partie  
- **PlacementView** : Placement des éléments  
- **GameView** : Jeu  
- **EndView** : Écran de fin

Nous avons extrait les composants réutilisables dans le package **panels/.**  
Cette approche évite la duplication de code et facilite les modifications.

Les vues ont uniquement des méthodes publiques nécessaires au contrôleur, tout en gardant leurs composants internes privés. Par exemple, GameView expose **updatePlayerStats()** mais garde **\_pnlPlayerStats** privé.

**Les vues principales** 

**MenuView \- Menu principal**  
La vue la plus simple, servant de point d'entrée à l'application. Elle propose deux boutons : "Nouvelle Partie" et "Quitter".

**ConfigurationView \- Configuration de partie**  
Cette vue permet de configurer tous les paramètres d'une partie avant de commencer.

Ce qui pourrait être amélioré sur cette vue : 

- La méthode initComponents() fait environ 150 lignes, elle pourrait être découpée en sous-méthodes privées comme createPlayerSection(), createGridSection(), etc. Mais nous n’avons pas pu le faire par manque de temps.  
- Le dialogue BoatCustomizationDialog est créé à chaque ouverture, on pourrait le réutiliser

**PlacementView \- Placement des éléments**  
Cette vue gère la phase de placement des bateaux, pièges et armes. Elle implémente PlacementObserver pour être notifiée des changements dans le modèle.

Détails d'implémentation améliorable:

```
@Override  
public void onGridChanged(Grid grid) {  
   _pnlGrid.resetAllCells();  
   if (grid.hasIsland()) drawIsland(grid.getSize());  
   drawBoats(grid);  
   drawTraps(grid);  
   drawWeapons(grid);  
   drawPreview();  
}
```

Cette méthode redessine complètement la grille à chaque changement. C'est simple mais pourrait être optimisé en ne redessinant que les cases modifiées.

**GameView \- Vue principale du jeu**

Cette vue est la plus complexe de l'application. Elle affiche les deux grilles (joueur et robot), les statistiques, l'historique et gère toutes les interactions pendant la partie. Elle implémente Observer pour être notifiée des événements du jeu.

Architecture de GameView :
```
GameView  
├── TopPanel (Menu \+ Numéro de tour)  
├── CenterPanel  
│   ├── PlayerStatsPanel (+ InventoryPanel intégré)  
│   ├── GameGridPanel (joueur)  
│   ├── GameGridPanel (robot)  
│   └── PlayerStatsPanel (robot)  
├── ActionHistoryPanel  
└── WeaponSelectionPanel
```

GameView délègue à 6 panels spécialisés au lieu de tout gérer elle-même.  
La mise à jour de l’affichage est automatisée via boatAttacked(), boatSunk(), squareAttacked(), etc.

Nous avons rajouté un menu pour quitter, et voir les légendes (afin de mieux comprendre les couleurs/textes utilisés sur la grille). Nous avons opté pour un affichage par couleur plutôt que beaucoup de texte dans la fenêtre pour ne pas alourdir l’affichage.

Le problème principal : la longueur du code.  
Malgré la refactorisation et l'extraction de nombreux panels, GameView fait encore environ 400 lignes. C'est beaucoup pour une classe de vue.

Les points qui posent problème sont qu’il y a trop de responsabilités dans la GameView. Même après avoir extrait les panels, GameView fait encore :

- l’initialisation de tous les composants  
- gestion du menu popup  
- coordination entre les panels  
- méthodes de mise à jour pour chaque statistique  
- implémentation complète de l'interface Observer  
- dialogue de pièges trouvés

Il y a également trop de méthodes de mises à jour.

Ces méthodes sont des délégations simples vers les panels, mais elles alourdissent GameView. Elles sont nécessaires pour que le contrôleur n'accède pas directement aux panels (encapsulation), mais cela crée beaucoup de code "passe-plat".

**Ce qui aurait pu être amélioré (mais manque de temps) :**

Créer un GameViewMediator.  
Au lieu d'avoir toutes les méthodes de mise à jour dans GameView, on aurait pu créer une classe GameViewMediator qui coordonne les panels.  
GameView déléguerait tout au médiateur, réduisant sa taille d’une centaine de lignes.

Extraire la logique Observer  
Créer une classe GameViewObserverHandler qui implémente Observer et modifie les grilles.  
Cela aurait réduit GameView d’une centaine de lignes supplémentaires.

Simplifier l'initialisation  
La méthode initComponents() est très longue. On aurait pu la découper.

Pourquoi ces améliorations n'ont pas été faites ?  
Par manque de temps, nous avons priorisé :

- La fonctionnalité : le jeu marche complètement  
- La correction : pas de bugs majeurs  
- La refactorisation essentielle : extraction des panels réutilisables

Nous voulions revenir sur ce point plus tard, mais finalement, nous avons mal anticipé cette tâche, qui était bien plus compliquée et longue que prévu.

Nous avons néanmoins pris soin de bien refactoriser la vue de configuration et de placement.

**EndView \- Écran de fin**  
Vue affichant le résultat de la partie et les statistiques finales.  
La GameView reçoit des objets GameStats au lieu de 15 paramètres séparés.  
Elle calcule automatiquement la précision des tirs.  
L’affichage est comparatif entre les stats des joueurs et du robot.  
Nous avons rajouté un dialogue de choix pour recommencer. Avec notre version, nous avons le choix de recommencer la même partie, avec de nouveaux placements, ou avec une nouvelle configuration.  
	  
**PATTERNS DE CONCEPTION**

**Factory Pattern :**   
Utilisé pour créer les contenus (bateaux, armes, pièges) de manière centralisée.  
WeaponFactory et TrapFactory : même principe.

**Strategy Pattern**  
Utilisé pour le mode de jeu du robot et les placements sur la grille.

**Observer Pattern**  
Utilisé pour notifier automatiquement les vues des changements du modèle.

## **CONCLUSION**

Ce projet de Bataille Navale nous a permis d'appliquer concrètement les principes de conception orientée objet dans une application complète et fonctionnelle.

**Réalisations :** Nous avons implémenté l'intégralité des 13 fonctionnalités demandées sur leurs 3 niveaux, en respectant strictement l'architecture MVC. Les patterns de conception (Factory, Strategy, Observer) ont été appliqués, et l'interface graphique complète offre une expérience utilisateur fluide avec des fonctionnalités bonus (preview de placement, inventaire visuel, triple option de recommencement).

**Difficultés et apprentissages :** Les principales difficultés rencontrées concernent la complexité des vues (GameView très long malgré l'extraction de 6 panels) et la gestion des interactions complexes entre éléments (Tornade \+ Trou noir). Ces défis nous ont appris l'importance d'anticiper la refactorisation dès la conception et de prévoir des tests unitaires pour valider la logique métier.

**Points forts du projet :** L'architecture avec des panels réutilisables, la séparation claire des responsabilités, et le robot intelligent (SmartRobotStrategy) constituent nos principales réussites. Le système de validation centralisé dans le modèle et la gestion robuste des événements via Observer garantissent la fiabilité de l'application.

**Améliorations identifiées :** Avec du recul, nous aurions intégré des tests unitaires dès le début, et aurions anticipé la refactorisation des vues.