package model.enums;

public enum RestartAction {
    /**
     * Recommencer avec les mêmes placements (rejouer la même partie).
     */
    SAME_PLACEMENTS,

    /**
     * Recommencer avec la même config mais nouveaux placements.
     */
    SAME_CONFIG,

    /**
     * Recommencer complètement (nouvelle configuration).
     */
    NEW_CONFIG
}
