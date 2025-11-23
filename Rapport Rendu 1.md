# Rapport de conception - Rendu 1
## Projet Bataille Navale - A31

## Introduction
Ce rapport présente nos choix de conception pour le projet "Bataille Navale". Notre objectif est de développer un jeu mono-joueur contre l'ordinateur en respectant une architecture MVC et en appliquant les principes de conception appropriés.

Nous avons opter pour une approche consistant à :
- Concevoir dès maintenant une architecture complète qui anticipe tous les niveaux de fonctionnalités, comme demandé
- Implémenter progressivement pour garantir un jeu fonctionnel rapidement
- Respecter les principes de conception, comme la séparation des responsabilités, l'encapsulation, l'extensibilité (important pour passer d'un niveau à un autre)...
- Tout en privilégiant la clarté et l'extensibilité.

## Architecture MVC

Nous avons pour notre jeu utilisé MVC.

Les **modèles** contiennent toute la logique métier, les règles du jeu, les états, les données, les calculs et sont indépendants de la vue.

Les vues permettent l'affichage Swing, l'interaction utilisateur et ne contiennent aucune logique métier.

Enfin, le controller permet la coordination entre Model et View. De même, elle ne contient aucune logique métier.

En suivant le MVC, notre code devient facilement testable (le modèle peut être testé sans interface graphique), maintenable (chaque partie peut évoluer indépendamment) et réutilisable (le modèle pourrait être utilisé avec une interface différente : web, console, mobile …)

## Organisation du package Model
Le package model est structuré en sous-packages :

```
model/
├── players/        # Joueurs
├── enums/          # Énumérations (types, états, modes)
├── contents/       # Éléments du jeu
│   ├── fleet/      # Bateaux
│   ├── weapons/    # Armes
│   └── traps/      # Pièges
├── grid/           # Grille, cases, position, île
├── history/        # Historique des actions
└── game/           # Logique de partie et configuration
```

De cette façon, chaque package a une responsabilité bien définie. Ainsi, la navigation et savoir où ajouter de nouvelles fonctionnalités sont plus simples et les classes liées sont regroupées ensemble.


## FLUX GÉNÉRAL D'UN TOUR DE JEU :

```
GameView (clic utilisateur)
   │
   ▼
GameController (récupère action)
   │
   ▼
Game.playTurn()
   │
   ├─→ playPlayerTurn(player, robot)
   │       │
   │       ├─→ Vérifier tornado active
   │       ├─→ Récupérer case cible
   │       ├─→ Vérifier contenu (piège ?)
   │       └─→ executeAttack() ou handleTrap()
   │
   └─→ playPlayerTurn(robot, player)
   │   │
   │   └─→ [même processus]
   │
   ▼
Observer
   │
   ▼
GameView (mise à jour affichage)
```



## Choix de conceptions importants

### Classe utilitaire Position
→ Nous avons créer une classe Position pour encapsuler les coordonnées et l'orientation.

```java
public class Position {
   private int x;
   private int y;
   private Orientation orientation; // peut être null
}
```

Cette classe permet tout d'abord plus de lisibilité. La méthode placeBoat(boat, position) est plus claire que placeBoat(boat, x, y, orientation).
De plus, cela permet d'ajouter facilement des méthodes utilitaires si besoin un jour, comme distance, cases adjacentes, etc…


### Interface Content
→ Tous les éléments que l'on peut placer sur la grille implémentent l'interface **Content**.
Cela permet à la classe **Square** de contenir n'importe quel **Content** sans connaître son type, et les méthodes comme setContent fonctionne pour tous les types. De plus, de cette façon, ajouter un nouveau type de contenu ne nécessitera pas la modification de toutes les méthodes, mais seulement d'implémenter un nouveau **Content**.

### Stockage des armes dans Player
Les armes qu'un joueur possède sont stockées dans un dictionnaire **<WeaponType, Int>**. Cela permet de connaître quelle arme le joueur possède, combien de fois il peut jouer cette même arme … Dès qu'un joueur utilise une arme, on décrémente le int.


## Configuration et placement

Nous avons une classe **GameConfig** qui permet de centraliser tous les paramètres de configuration d'une partie.

```java
public class GameConfig {
   private ModeGame modeGame;      // STANDARD ou ISLAND
   private int gridSize;           // 6 à 10
   private RobotMode robotMode;    // RANDOM ou SMART
   private Map<BoatName, int> numberBoat;  // Nombre de chaque type
   private String username;
   private TrapPlacement trapPlacement; // FIXED, RANDOM, MANUAL
}
```

Comme ça, un seul objet contient toute la configuration, la transmission des paramètres entre le controller et le model est plus simple et ça permet de faire toutes les validations plus facilement (par exemple, vérifier que le total des cases est inférieur à 35).

De la même manière, nous avons une classe **GamePlacement**, qui elle, stocke toutes les positions des éléments pour les deux joueurs :

```java
public class GamePlacement {
   private Map<BoatName, Position> boatPlacementPlayer;
   private Map<TrapType, Position> trapPlacementPlayer;
   private Map<BoatName, Position> boatPlacementRobot;
   private Map<TrapType, Position> trapPlacementRobot;
}
```

De cette façon, on sépare la configuration générale du placement et nous pouvons de même valider les placement avant de les appliquer. Ça permettra également de faciliter la génération de placement aléatoire.

## Gestion des Joueurs

### Classe Player
Contrairement à une hiérarchie avec une classe abstraite, nous avons choisi une classe Player concrète.

```java
public class Player {
   private String username;
   private boolean isRobot;
   private Grid grid;
   private List<Boat> boats;
   private Map<WeaponType, int> weapons;
   private Tornado tornado; // Tornado active (ou null)
}
```

Nous avons évité la hiérarchie **Player -> HumanPlayer.RobotPlayer** car le comportement différent du robot est géré dans **Game**, donc les méthodes dans Player sont finalement les mêmes pour le robot et l'humain.

Un point important dans notre architecture est l'attribut **Tornado** dans **Player**.
Quand un joueur touche une **Tornado** :
- Le joueur est affecté pendant 3 tours.
- Ses attaques sont déviées
- Donc l'information doit être attachée au Player

Nous avons écarté l'idée de le stocker dans **Game** car ça nécessiterait deux variable **humanTornado** et **robotTornado** avec des if/else partout.

Avec **Tornado** dans **Player**, on peut voir si la tornade est active simplement avec **player.hasTornadoActive()**. Chaque joueur gère son propre état, et c'est plus extensible si on ajoute plus de joueurs.

## Grille et Cases

**Grille :**
- Contenir les cases (Square)
- Valider les placements
- Gérer l'île (mode île)

**Square :**
- Stocker son contenu
- Gérer son état (attaquée, île)


## Contenus: Bateaux, Armes, Pièges

### Bateaux avec Factory
- garantit que chaque type a le bon nombre de cases.
- évite les erreurs de création

### Armes avec méthode use()
```java
public abstract class Weapon implements Content {
   public abstract Position[] use(Position position);
}
```

Exemples :
- Missile : retourne [position] (1 case)
- Bomb : retourne [center, haut, bas, gauche, droite] (5 cases)
- Sonar : retourne les positions où le sonar détecte des bateaux (ou trou noir).

→ L'arme calcule les positions affectées
→ **Game** traite ces positions

### Mise en place d'une Factory WeaponFactory
→ évite de devoir connaître le type exact de chaque arme

## Pièges

Les pièges héritent d'une classe abstraite **Trap** qui implémente l'interface **Content**.
La classe abstraite **Trap** définit la structure commune.

Les pièges ne contiennent que leur état et quelques méthodes utilitaires. Ils ne gèrent PAS la logique d'application des effets. C'est la classe **Game** qui s'occupe de leurs utilisations.

Le **BlackHole** est une classe très simple qui ne fait que stocker son état.
Cette classe ne contient aucune méthode complexe. Lorsqu'un joueur touche un BlackHole, c'est la classe **Game** qui gère le rebond de l'attaque sur la grille de l'attaquant.


**BlackHole** n'a pas besoin de connaître les joueurs, leurs grilles ou les armes.
Sa seule responsabilité est d'exister à une position donnée.
La logique du rebond est dans Game car elle nécessite d'accéder aux deux joueurs et de gérer la tornado potentiellement active.

La **Tornado** est plus complexe car elle doit gérer les transformations de coordonnées pendant 3 tours. Elle stocke son état (activé ou non, nombre d'utilisations restantes), génère le dictionnaire des transformations, transforme une position donnée et décrémente automatiquement le compteur à chaque utilisation.

Cependant, **Tornado** ne connaît pas le joueur affecté. Elle ne décide pas quand s'appliquer et elle ne gère pas le rebond du **BlackHole**.

Comme pour les bateaux et les armes, un **TrapFactory** permet de créer les pièges.
Cette factory garantit une création cohérente des pièges et facilite l'extension si de nouveaux types de pièges sont ajoutés.

## Logique du jeu - Classe Game

La classe **Game** est la classe qui gère toute la partie. Elle coordonne les actions, applique les règles et gère les interactions entre les différents éléments du jeu.

### Algorithme de playPlayerTurn

La méthode playPlayerTurn gère le tour d'un joueur en suivant ces étapes :

1. Récupérer l'action du joueur (arme et position cible)

2. Vérifier si le joueur a une tornado active
    - Si oui : transformer la position avec tornado.getNewPosition()

3. Récupérer la case visée sur la grille du défenseur

4. Examiner le contenu de la case

   a) Si c'est un BlackHole :
    - Calculer la position de rebond (même position)
    - Si l'attaquant a une tornado active, le rebond est aussi transformé
    - Attaquer la grille de l'attaquant à cette position
    - Marquer la case du piège comme attaquée

   b) Si c'est une Tornado :
    - Activer la tornado (numberUse = 3, générer le mapping)
    - Affecter la tornado au joueur : assaillant.setTornado(tornado)
    - L'attaque actuelle touche quand même la case de la tornado

   c) Sinon (case vide ou bateau) :
    - Appeler executeAttack() pour traiter l'attaque normalement

5. Utiliser l'arme (décrémenter le compteur)

6. Enregistrer l'action dans l'historique (fait grâce au patron de conception Observer)

On vérifie d'abord la tornado du joueur (qui affecte toutes ses actions), puis on regarde ce qu'il touche. Cela garantit la cohérence : même le rebond d'un BlackHole peut être affecté par la tornado active.

Quand un joueur touche un BlackHole, le rebond du BlackHole subit aussi la tornado si elle est active. Cela crée des situations intéressantes où un joueur peut se tirer dessus à une position différente de celle qu'il visait initialement.

Quand un joueur touche une Tornado, Game active l'effet, l'attaque qui touche la tornado n'est pas déviée. Seules les 3 attaques suivantes le seront.


Nos choix pour la gestion des pièges reposent donc sur trois principes :

### 1. Séparation des responsabilités

- Les pièges (BlackHole, Tornado) stockent uniquement leur état
- La classe Game gère l'application des effets
- Cette séparation facilite les tests et la maintenance

### 2. Cohérence des règles

- La tornado affecte toutes les positions générées par le joueur
- Cela inclut les attaques normales et les rebonds de BlackHole
- Cette cohérence crée des interactions complexes mais logiques

### 3. Centralisation dans Game

- Game a la vision globale nécessaire (deux joueurs, deux grilles)
- Game applique les règles de manière neutre
- Toute la logique des pièges est au même endroit, facilitant la compréhension