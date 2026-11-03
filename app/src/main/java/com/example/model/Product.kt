package com.example.model

enum class ProductCategory(val displayName: String) {
    ALL("Todo"),
    REBAJAS("🔥 Rebajas"),
    OLYMPUS("Colección Olimpo"),
    DRESSES("Vestidos de Gala"),
    SETS("Conjuntos & Trajes"),
    TOPS("Blusas & Sedas"),
    ACCESSORIES("Calzado & Accesorios")
}

data class ProductColor(
    val name: String,
    val hexValue: Long
)

data class Product(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val price: Double,
    val originalPrice: Double? = null,
    val sizes: List<String> = listOf("XS", "S", "M", "L"),
    val colors: List<ProductColor> = listOf(
        ProductColor("Dorado Atenas", 0xFFD4AF37),
        ProductColor("Negro Obsidiana", 0xFF1A1A1E),
        ProductColor("Crema Seda", 0xFFF5EBE0)
    ),
    val description: String,
    val fabric: String,
    val careInstructions: String = "Lavar en seco o ciclo delicado con agua fría. No usar blanqueador.",
    val stock: Int = 12,
    val sku: String,
    val rating: Float = 4.9f,
    val reviewsCount: Int = 28,
    val isNew: Boolean = false,
    val isTrending: Boolean = false,
    val imageResName: String = "athena_hero_banner",
    val imageUrl: String? = null,
    val isActive: Boolean = true
) {
    val discountPercent: Int?
        get() = if (originalPrice != null && originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else null

    val isLowStock: Boolean
        get() = stock in 1..4

    val isOutOfStock: Boolean
        get() = stock <= 0
}

fun Product.getDrawableResId(): Int {
    return when (imageResName) {
        "prod_corset_encaje" -> com.example.R.drawable.prod_corset_encaje
        "prod_corset_leopardo" -> com.example.R.drawable.prod_corset_leopardo
        "prod_conjunto_marron" -> com.example.R.drawable.prod_conjunto_marron
        "prod_campera_piel" -> com.example.R.drawable.prod_campera_piel
        "prod_chaleco_corderito" -> com.example.R.drawable.prod_chaleco_corderito
        "prod_jogging_wide" -> com.example.R.drawable.prod_jogging_wide
        "prod_pantalon_strass" -> com.example.R.drawable.prod_pantalon_strass
        "prod_remera_crop" -> com.example.R.drawable.prod_remera_crop
        "prod_buzo_losangeles" -> com.example.R.drawable.prod_buzo_losangeles
        "prod_conjunto_violeta" -> com.example.R.drawable.prod_conjunto_violeta
        "banner_portada_rebajas" -> com.example.R.drawable.banner_portada_rebajas
        "athena_cover" -> com.example.R.drawable.athena_cover
        else -> com.example.R.drawable.athena_cover
    }
}
