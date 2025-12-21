## Installation

### Prérequis
- **JDK** : Java 11 ou supérieur
- **IDE** : IntelliJ IDEA, Eclipse, ou VS Code (recommandé)
- **Système** : Windows, macOS, ou Linux

### Étapes d'installation

**1. Cloner le dépôt**
```bash
git clone git@git.unistra.fr:fouilleul-muharremoglu/a31-bataille-navale.git
cd a31-bataille-navale/
```

**2. Compiler le projet**
```bash
# Avec javac (ligne de commande)
javac -d bin -sourcepath src src/**/*.java

# Ou avec votre IDE (comme IntelliJ)
# Ouvrir le projet et lancer la compilation
```

**3. Lancer l'application**
```bash
java -cp bin Main
```

---

## Guide d'utilisation

### Démarrer une partie

1. **Configuration**
  - Entrez votre nom d'utilisateur
  - Choisir la taille de grille (6×6 à 10×10)
  - Sélectionner le nombre de bateaux de chaque type
  - Activer ou non le mode Île
  - Choisir la stratégie du robot (aléatoire ou intelligent)

2. **Placement**
  - Placer manuellement vos bateaux en cliquant sur la grille
  - Utiliser la rotation pour changer l'orientation (horizontal/vertical)
  - Placer vos pièges stratégiquement (si disponibles)
  - Valider pour démarrer la partie

3. **Jouer**
  - Sélectionner une arme dans le panneau de sélection
  - Cliquer sur la grille adverse pour attaquer
  - Observer le résultat et les statistiques
  - Le robot joue automatiquement après vous

### Stratégies gagnantes

**Mode classique**
- Utiliser le sonar pour détecter les zones occupées
- Conserver la bombe pour les bateaux alignés
- Placer le trou noir près de vos gros navires

**Mode Île**
- Explorer l'île en début de partie pour récupérer des bonus
- Équilibrer entre exploration et attaque
- Utiliser l'inventaire pour placer des pièges trouvés

**Contre le robot intelligent**
- Espacer vos bateaux pour éviter la traque
- Utiliser les pièges de manière défensive
- Cibler méthodiquement zone par zone

---
