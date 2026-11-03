package com.example.model

enum class ProductCategory(val displayName: String) {
    ALL("Todo"),
    REBAJAS("🔥 Rebajas"),
    OLYMPUS("Colección Olimpo"),
    DRESSES("Vestidos de Gala"),
    SETS("Conjuntos & Trajes"),
    ADIDAS("👟 Adidas"),
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
        "prod_corset_encaje", "rebajas_corset_encaje_trio" -> com.example.R.drawable.rebajas_corset_encaje_trio
        "prod_corset_leopardo", "rebajas_corset_leopardo_negro" -> com.example.R.drawable.rebajas_corset_leopardo_negro
        "prod_conjunto_marron", "rebajas_conjunto_canale_duo" -> com.example.R.drawable.rebajas_conjunto_canale_duo
        "prod_campera_piel", "rebajas_campera_piel_duo" -> com.example.R.drawable.rebajas_campera_piel_duo
        "prod_buzo_losangeles", "rebajas_buzo_losangeles_91" -> com.example.R.drawable.rebajas_buzo_losangeles_91
        "prod_chaleco_corderito" -> com.example.R.drawable.prod_chaleco_corderito
        "prod_jogging_wide" -> com.example.R.drawable.prod_jogging_wide
        "prod_pantalon_strass" -> com.example.R.drawable.prod_pantalon_strass
        "prod_remera_crop" -> com.example.R.drawable.prod_remera_crop
        "prod_conjunto_violeta" -> com.example.R.drawable.prod_conjunto_violeta
        "prod_campera_jean_adidas" -> com.example.R.drawable.prod_campera_jean_adidas
        "prod_conjunto_3piezas_adidas" -> com.example.R.drawable.prod_conjunto_3piezas_adidas
        "prod_set_malibu_celeste" -> com.example.R.drawable.prod_set_malibu_celeste
        "prod_set_malibu_rosa" -> com.example.R.drawable.prod_set_malibu_rosa
        "prod_set_malibu_marron" -> com.example.R.drawable.prod_set_malibu_marron
        "banner_portada_rebajas" -> com.example.R.drawable.banner_portada_rebajas
        "athena_cover" -> com.example.R.drawable.athena_cover
        else -> com.example.R.drawable.athena_cover
    }
}
