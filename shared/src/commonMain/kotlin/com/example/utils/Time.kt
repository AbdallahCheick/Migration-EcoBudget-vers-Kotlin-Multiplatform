package com.example.utils

/**
 * Horodatage courant en millisecondes depuis l'epoch Unix.
 *
 * Déclaration `expect` : java.lang.System est interdit dans le code commun,
 * chaque plateforme fournit son implémentation `actual` (androidMain / iosMain).
 */
expect fun getCurrentTimeMillis(): Long
