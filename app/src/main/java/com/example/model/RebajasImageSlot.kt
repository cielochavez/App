package com.example.model

/**
 * Represents an image slot or showcase space in the Rebajas section.
 * Can hold official boutique flyers, user-uploaded outfit photos from gallery,
 * or customizable image slots.
 */
data class RebajasImageSlot(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val imageUri: String? = null,
    val drawableResId: Int? = null,
    val isPlaceholder: Boolean = false,
    val price: Double? = null,
    val originalPrice: Double? = null,
    val associatedProductId: String? = null
)
