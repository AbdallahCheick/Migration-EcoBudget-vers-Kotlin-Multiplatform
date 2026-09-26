package com.example.ui

import com.example.R
import com.example.model.Category

/**
 * Association temporaire (côté Android) entre une [Category] et son libellé `R.string`,
 * depuis que l'entité partagée ne dépend plus de la classe `R`.
 */
val Category.labelResId: Int
    get() = when (this) {
        Category.TRANSPORT -> R.string.category_transport
        Category.ALIMENTATION -> R.string.category_alimentation
        Category.LOISIRS -> R.string.category_loisirs
        Category.LOGEMENT -> R.string.category_logement
    }
