package com.example.viewmodel

import com.example.model.Category
import com.example.shared.resources.Res
import com.example.shared.resources.category_alimentation
import com.example.shared.resources.category_logement
import com.example.shared.resources.category_loisirs
import com.example.shared.resources.category_transport
import org.jetbrains.compose.resources.StringResource

/**
 * Libellé localisé d'une [Category], issu du catalogue de ressources partagé.
 *
 * Remplace l'ancien `labelResId: Int` (identifiant `R.string` Android) : l'entité du domaine
 * reste neutre et la présentation résout le texte via `stringResource(category.labelRes)`.
 */
val Category.labelRes: StringResource
    get() = when (this) {
        Category.TRANSPORT -> Res.string.category_transport
        Category.ALIMENTATION -> Res.string.category_alimentation
        Category.LOISIRS -> Res.string.category_loisirs
        Category.LOGEMENT -> Res.string.category_logement
    }
