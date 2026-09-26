package com.example.utils

/**
 * Génère un identifiant unique (UUID v4) sous forme textuelle.
 *
 * Déclaration `expect` : java.util.UUID n'existe pas dans le code commun ni sur iOS,
 * chaque plateforme fournit son implémentation `actual` (androidMain / iosMain).
 */
expect fun generateUUID(): String
