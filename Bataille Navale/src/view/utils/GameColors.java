package view.utils;

import java.awt.Color;

/**
 * Classe utilitaire contenant toutes les couleurs utilisées dans le jeu.
 * Centralise les couleurs pour éviter la duplication et faciliter les modifications.
 * Toutes les constantes sont publiques, statiques et finales pour un accès global.
 */
public class GameColors {

    // Couleurs de base

    /** Couleur de l'eau (bleu clair) */
    public static final Color WATER_COLOR = new Color(100, 150, 200);

    /** Couleur des bateaux (gris foncé) */
    public static final Color BOAT_COLOR = new Color(80, 80, 80);

    /** Couleur de l'île (beige/sable) */
    public static final Color ISLAND_COLOR = new Color(210, 180, 140);

    // Couleurs d'attaque

    /** Couleur d'un bateau touché mais non coulé (rouge clair) */
    public static final Color HIT_COLOR = new Color(255, 100, 100);

    /** Couleur d'un bateau complètement coulé (rouge foncé) */
    public static final Color SUNK_COLOR = new Color(150, 50, 50);

    /** Couleur d'un tir manqué dans l'eau (gris clair) */
    public static final Color MISS_COLOR = new Color(200, 200, 200);

    // Couleurs île fouillée

    /** Couleur d'une case d'île fouillée sans rien trouver (beige foncé) */
    public static final Color ISLAND_SEARCHED_EMPTY = new Color(190, 160, 120);

    /** Couleur d'une case d'île fouillée avec découverte d'arme (doré) */
    public static final Color ISLAND_SEARCHED_FOUND = new Color(255, 215, 0);

    // Couleurs pièges et armes

    /** Couleur des pièges/mines (orange-rouge) */
    public static final Color TRAP_COLOR = new Color(243, 88, 48);

    /** Couleur des armes (violet/magenta) */
    public static final Color WEAPON_COLOR = new Color(218, 14, 232);

    // Couleurs d'aperçu (preview)

    /** Couleur d'aperçu pour un placement valide (vert clair) */
    public static final Color PREVIEW_OK = new Color(100, 200, 100);

    /** Couleur d'aperçu pour un placement invalide (rouge clair) */
    public static final Color PREVIEW_BAD = new Color(200, 100, 100);
}