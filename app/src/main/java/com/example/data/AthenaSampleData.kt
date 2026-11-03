package com.example.data

import com.example.model.AppNotification
import com.example.model.CartItem
import com.example.model.NotificationType
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductColor
import com.example.model.TrackingTimelineEvent

object AthenaSampleData {

    val sampleProducts = listOf(
        // PRENDAS REALES EN REBAJAS (FOTOS OFICIALES ATHENA STYLE)
        Product(
            id = "reb-corset-1",
            name = "Corset Encaje Floral",
            category = ProductCategory.TOPS,
            price = 38.0,
            originalPrice = 55.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Blanco Nácar", 0xFFFFFFFF),
                ProductColor("Azul Cobalto", 0xFF003399),
                ProductColor("Rosa Palo", 0xFFC9848B)
            ),
            description = "Corset strapless confeccionado en fino encaje bordado con varillas de soporte estilizadoras y copa preformada. Cierre trasero con ganchillos regulables.",
            fabric = "Encaje bordado con forro de tul de seda y elastano",
            stock = 7,
            sku = "ATH-REB-001",
            rating = 4.92f,
            reviewsCount = 38,
            isNew = true,
            isTrending = true,
            imageResName = "prod_corset_encaje"
        ),
        Product(
            id = "reb-corset-2",
            name = "Corset Animal Print & Black",
            category = ProductCategory.TOPS,
            price = 42.0,
            originalPrice = 60.0,
            sizes = listOf("S", "M", "L"),
            colors = listOf(
                ProductColor("Estampado Leopardo", 0xFFB87333),
                ProductColor("Negro Satinado", 0xFF18181A)
            ),
            description = "Corset entallado con copa bustier y paneles transparentes laterales que resaltan la silueta. Tirantes regulables y ballenas reforzadas.",
            fabric = "Satén estampado con paneles de tul elastizado",
            stock = 4,
            sku = "ATH-REB-002",
            rating = 4.88f,
            reviewsCount = 29,
            isNew = true,
            isTrending = true,
            imageResName = "prod_corset_leopardo"
        ),
        Product(
            id = "reb-conjunto-3",
            name = "Conjunto Marrón & Burdeos",
            category = ProductCategory.SETS,
            price = 65.0,
            originalPrice = 89.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Marrón Chocolate", 0xFF4A3525),
                ProductColor("Burdeos Vino", 0xFF6B1D2F)
            ),
            description = "Conjunto de dos piezas en canalé elástico de alta densidad. Top crop de manga corta con cierre blanco frontal y pantalón flare con detalle de tiras cruzadas.",
            fabric = "Canalé de algodón peinado con spandex",
            stock = 9,
            sku = "ATH-REB-003",
            rating = 4.95f,
            reviewsCount = 45,
            isNew = true,
            isTrending = true,
            imageResName = "prod_conjunto_marron"
        ),
        Product(
            id = "set-malibu-celeste",
            name = "Set Malibú Celeste",
            category = ProductCategory.SETS,
            price = 68.0,
            originalPrice = 95.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Celeste Malibú", 0xFFAEC6CF)
            ),
            description = "Conjunto exclusivo Set Malibú de dos piezas. Top halter con escote pronunciado, cierre frontal regulable y pantalón flare tiro medio con pretina ancha doblada estilo boho chic.",
            fabric = "Tejido modal elastizado de alta densidad con caída fluida",
            stock = 8,
            sku = "ATH-MAL-CEL",
            rating = 4.98f,
            reviewsCount = 54,
            isNew = true,
            isTrending = true,
            imageResName = "prod_set_malibu_celeste"
        ),
        Product(
            id = "set-malibu-rosa",
            name = "Set Malibú Rosa Pastel",
            category = ProductCategory.SETS,
            price = 68.0,
            originalPrice = 95.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Rosa Pastel", 0xFFF4C2C2)
            ),
            description = "Conjunto Set Malibú en tono rosa dulce. Top halter entallado con cierre metálico frontal y pantalón oxford con pretina elastizada que resalta y estiliza la figura.",
            fabric = "Modal stretch transpirable y suave al tacto",
            stock = 6,
            sku = "ATH-MAL-ROSA",
            rating = 4.95f,
            reviewsCount = 42,
            isNew = true,
            isTrending = true,
            imageResName = "prod_set_malibu_rosa"
        ),
        Product(
            id = "set-malibu-marron",
            name = "Set Malibú Chocolate",
            category = ProductCategory.SETS,
            price = 68.0,
            originalPrice = 95.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Marrón Chocolate", 0xFF3E2723)
            ),
            description = "Conjunto Set Malibú en tono chocolate profundo. Top escote halter con cremallera frontal y pantalón acampanado flare con cintura doblada super favorecedora.",
            fabric = "Modal stretch acanalado premium",
            stock = 9,
            sku = "ATH-MAL-BRW",
            rating = 4.97f,
            reviewsCount = 49,
            isNew = true,
            isTrending = true,
            imageResName = "prod_set_malibu_marron"
        ),
        Product(
            id = "reb-campera-4",
            name = "Campera con Piel Térmica",
            category = ProductCategory.TOPS,
            price = 78.0,
            originalPrice = 110.0,
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Marrón Caramelo", 0xFF5C3A21),
                ProductColor("Beige Nieve", 0xFFE8DFC8)
            ),
            description = "Campera corta con capucha totalmente forrada con sherpa piel sintética de tacto ultrasuave. Abrigo premium con bolsillos frontales y cierre metálico reforzado.",
            fabric = "Pana acanalada térmica con forro de sherpa corderito",
            stock = 5,
            sku = "ATH-REB-004",
            rating = 5.0f,
            reviewsCount = 52,
            isNew = true,
            isTrending = true,
            imageResName = "prod_campera_piel"
        ),
        Product(
            id = "reb-chaleco-5",
            name = "Chaleco Corderito Vintage",
            category = ProductCategory.TOPS,
            price = 54.0,
            originalPrice = 75.0,
            sizes = listOf("S", "M", "L"),
            colors = listOf(
                ProductColor("Marrón Cuero", 0xFF472D1B),
                ProductColor("Beige Tostado", 0xFFC9AD8A)
            ),
            description = "Chaleco de gamuza con terminaciones y cuello alto en corderito piel. Cierre de cremallera frontal y bolsillos térmicos laterales.",
            fabric = "Antílope sintético gamuzado y ribetes de corderito",
            stock = 6,
            sku = "ATH-REB-005",
            rating = 4.85f,
            reviewsCount = 33,
            isNew = true,
            isTrending = false,
            imageResName = "prod_chaleco_corderito"
        ),
        Product(
            id = "reb-jogging-6",
            name = "Jogging Wide Leg Relaxed",
            category = ProductCategory.SETS,
            price = 45.0,
            originalPrice = 65.0,
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Terracota Rústico", 0xFF8A3B28),
                ProductColor("Beige Arena", 0xFFD8C7A5),
                ProductColor("Blanco Óptico", 0xFFF5F5F5),
                ProductColor("Negro Noche", 0xFF1C1C1E)
            ),
            description = "Pantalón jogger de corte ancho confeccionado en algodón rústico frisado pesado con cintura elastizada y cordón ajustable.",
            fabric = "100% Algodón frisado rústico de alto gramaje",
            stock = 15,
            sku = "ATH-REB-006",
            rating = 4.89f,
            reviewsCount = 61,
            isNew = true,
            isTrending = true,
            imageResName = "prod_jogging_wide"
        ),
        Product(
            id = "reb-strass-7",
            name = "Pantalón de Strass & Hoodie Set",
            category = ProductCategory.SETS,
            price = 85.0,
            originalPrice = 125.0,
            sizes = listOf("S", "M", "L"),
            colors = listOf(
                ProductColor("Marrón Glitter", 0xFF4E3728),
                ProductColor("Gris Topo", 0xFF585452)
            ),
            description = "Conjunto exclusivo con microcristales strass brillantes aplicados artesanalmente en toda la prenda. Campera con capucha y pantalón jogger a juego.",
            fabric = "Algodón perchado premium con apliques de strass termofijados",
            stock = 3,
            sku = "ATH-REB-007",
            rating = 4.96f,
            reviewsCount = 41,
            isNew = true,
            isTrending = true,
            imageResName = "prod_pantalon_strass"
        ),
        Product(
            id = "adi-campera-jean-1",
            name = "Campera de Jean Adidas 3 Tiras",
            category = ProductCategory.ADIDAS,
            price = 98.0,
            originalPrice = 135.0,
            sizes = listOf("XS", "S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Denim Lavado Vintage", 0xFF4A6B8C)
            ),
            description = "Campera bomber de jean denim con las clásicas 3 tiras celestes en hombros y brazos. Detalle de logo Trefoil bordado en el pecho, cremallera completa y pretina elástica.",
            fabric = "100% Denim de algodón lavado premium",
            stock = 7,
            sku = "ATH-ADI-DENIM-01",
            rating = 4.98f,
            reviewsCount = 64,
            isNew = true,
            isTrending = true,
            imageResName = "prod_campera_jean_adidas"
        ),
        Product(
            id = "adi-conjunto-3piezas-2",
            name = "Conjunto 3 Piezas Adidas Trefoil",
            category = ProductCategory.ADIDAS,
            price = 89.0,
            originalPrice = 125.0,
            sizes = listOf("S", "M", "L"),
            colors = listOf(
                ProductColor("Negro Noche", 0xFF141416),
                ProductColor("Bordeaux Vino", 0xFF6B1D2F),
                ProductColor("Beige Avena", 0xFFDFD7C7)
            ),
            description = "Conjunto deportivo de tres piezas de alta tendencia: campera crop con capucha y cierre, top bandeau elastizado y short con cordón ajustable. All-over mini logos Trefoil.",
            fabric = "Algodón peinado con elastano y felpa suave",
            stock = 10,
            sku = "ATH-ADI-SET-02",
            rating = 4.96f,
            reviewsCount = 53,
            isNew = true,
            isTrending = true,
            imageResName = "prod_conjunto_3piezas_adidas"
        ),
        Product(
            id = "adi-conjunto-3",
            name = "Pantalón Jogger Adidas 3 Tiras Wide",
            category = ProductCategory.ADIDAS,
            price = 62.0,
            originalPrice = 85.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Negro Profundo", 0xFF121214),
                ProductColor("Gris Melange", 0xFF7D7D82)
            ),
            description = "Pantalón deportivo wide leg con cintura ajustable y caída relajada. Comodidad absoluta para look streetwear y entrenamiento.",
            fabric = "Algodón frisado y felpa francesa suave",
            stock = 12,
            sku = "ATH-ADI-003",
            rating = 4.90f,
            reviewsCount = 37,
            isNew = true,
            isTrending = false,
            imageResName = "prod_jogging_wide"
        ),
        Product(
            id = "reb-remera-8",
            name = "Remera Crop Encaje Escote V",
            category = ProductCategory.TOPS,
            price = 28.0,
            originalPrice = 40.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Marrón Ébano", 0xFF3D281E),
                ProductColor("Negro Clásico", 0xFF18181A)
            ),
            description = "Remera crop entallada de tejido canalé suave con escote en V pronunciado y detalle de puntilla de encaje delicado.",
            fabric = "Rib acanalado de viscosa y algodón elastizado",
            stock = 11,
            sku = "ATH-REB-008",
            rating = 4.78f,
            reviewsCount = 27,
            isNew = true,
            isTrending = false,
            imageResName = "prod_remera_crop"
        ),
        Product(
            id = "reb-buzo-9",
            name = "Buzo Los Angeles 91 Oversize",
            category = ProductCategory.TOPS,
            price = 35.0,
            originalPrice = 55.0,
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Negro Vintage", 0xFF1E1E22),
                ProductColor("Gris Melange", 0xFF8A8A8E)
            ),
            description = "Buzo cuello redondo de silueta oversize con estampa universitaria 'LOS ANGELES 91' en felpa suave y puños acanalados. Precio promocional en Rebajas: $13.000.",
            fabric = "Algodón con frisa interior abrigada",
            stock = 8,
            sku = "ATH-REB-009",
            rating = 4.91f,
            reviewsCount = 39,
            isNew = true,
            isTrending = true,
            imageResName = "prod_buzo_losangeles"
        ),
        Product(
            id = "reb-violeta-10",
            name = "Conjunto Violeta & Baya Flare",
            category = ProductCategory.SETS,
            price = 62.0,
            originalPrice = 88.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Violeta Profundo", 0xFF4B1E4A),
                ProductColor("Marrón Moca", 0xFF523B2B),
                ProductColor("Frambuesa Magenta", 0xFF8B1E42)
            ),
            description = "Conjunto dos piezas deportivo y urbano con cintura cruzada en contraste blanco y pernera campana de tiro alto con top manga larga.",
            fabric = "Supplex compresivo de alta flexibilidad",
            stock = 6,
            sku = "ATH-REB-010",
            rating = 4.94f,
            reviewsCount = 36,
            isNew = true,
            isTrending = true,
            imageResName = "prod_conjunto_violeta"
        ),
        Product(
            id = "prod-1",
            name = "Vestido Afrodita Seda Dorada",
            category = ProductCategory.OLYMPUS,
            price = 189.0,
            originalPrice = 240.0,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Dorado Champán", 0xFFD4AF37),
                ProductColor("Negro Ónix", 0xFF18181A),
                ProductColor("Blanco Perla", 0xFFF8F6F0)
            ),
            description = "Elegante vestido de gala confeccionado en satén de seda fluida de morera pura. Caída escultural drapeada en el busto y espalda descubierta con tirantes entrecruzados bañados en oro mate.",
            fabric = "100% Satén de Seda Natural de 22 Momme",
            careInstructions = "Limpieza profesional en seco únicamente. Planchar al vapor por el revés a baja temperatura.",
            stock = 3,
            sku = "ATH-DRE-001",
            rating = 4.95f,
            reviewsCount = 42,
            isNew = true,
            isTrending = true,
            imageResName = "athena_hero_banner"
        ),
        Product(
            id = "prod-2",
            name = "Blazer Atenea Haute Couture",
            category = ProductCategory.SETS,
            price = 225.0,
            originalPrice = null,
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Negro Obsidiana", 0xFF14121E),
                ProductColor("Gris Perla Imperial", 0xFFB0B0BA),
                ProductColor("Verde Laurel", 0xFF4E7057)
            ),
            description = "Saco sastre estructurado con hombreras geométricas pronunciadas y solapa de smoking. Botones metálicos grabados a mano con el emblema de la lechuza de Atenea.",
            fabric = "Lana virgen fría con forro interior en cupro transpirable",
            careInstructions = "Lavar en tintorería especializada. Guardar en percha ancha para preservar la hombrera.",
            stock = 8,
            sku = "ATH-BLZ-002",
            rating = 4.88f,
            reviewsCount = 31,
            isNew = false,
            isTrending = true,
            imageResName = "athena_hero_banner"
        ),
        Product(
            id = "prod-3",
            name = "Vestido Noche Artemisa",
            category = ProductCategory.DRESSES,
            price = 270.0,
            originalPrice = 330.0,
            sizes = listOf("XS", "S", "M"),
            colors = listOf(
                ProductColor("Azul Noche Helénica", 0xFF0D1B2A),
                ProductColor("Burdeos Imperial", 0xFF4A0E17),
                ProductColor("Verde Esmeralda", 0xFF0B3B24)
            ),
            description = "Vestido largo de fiesta con corpiño ajustado y abertura lateral sutil. Tul bordado con micro-pedrería que refleja la luz en cada movimiento.",
            fabric = "Georgette de seda con forro de crepé elastizado",
            careInstructions = "Solo limpieza en seco especializada para prendas de alta costura.",
            stock = 2,
            sku = "ATH-ART-003",
            rating = 5.0f,
            reviewsCount = 18,
            isNew = true,
            isTrending = true,
            imageResName = "athena_hero_banner"
        ),
        Product(
            id = "prod-4",
            name = "Blusa Quimono Ninfa Marfil",
            category = ProductCategory.TOPS,
            price = 98.0,
            originalPrice = null,
            sizes = listOf("XS", "S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Marfil Puro", 0xFFFFFFF0),
                ProductColor("Rosa Polvo", 0xFFD8B4B8),
                ProductColor("Terracota Suave", 0xFFB07D62)
            ),
            description = "Blusa envolvente fluida de corte asimétrico con mangas murciélago y lazo ajustable a la cintura. Sofisticada tanto para cóctel como para oficina premium.",
            fabric = "Gasa de seda y viscosa ecológica sostenible",
            careInstructions = "Lavado a mano con detergente neutro para prendas delicadas. No estrujar.",
            stock = 14,
            sku = "ATH-TOP-004",
            rating = 4.8f,
            reviewsCount = 57,
            isNew = false,
            isTrending = false,
            imageResName = "athena_hero_banner"
        ),
        Product(
            id = "prod-5",
            name = "Pantalón Palazzo Calipso",
            category = ProductCategory.SETS,
            price = 115.0,
            originalPrice = 140.0,
            sizes = listOf("S", "M", "L"),
            colors = listOf(
                ProductColor("Arena Cálida", 0xFFD2B48C),
                ProductColor("Blanco Marfil", 0xFFFAF0E6),
                ProductColor("Negro Grafito", 0xFF2B2B2B)
            ),
            description = "Pantalón de tiro alto con pernera ultra ancha y pliegues frontales. Aporta estilismo y movimiento etéreo al caminar.",
            fabric = "100% Lino europeo orgánico prelavado con textura suave",
            careInstructions = "Lavar a máquina en programa suave a 30°C. Secar al aire a la sombra.",
            stock = 7,
            sku = "ATH-PLZ-005",
            rating = 4.75f,
            reviewsCount = 22,
            isNew = false,
            isTrending = true,
            imageResName = "athena_hero_banner"
        ),
        Product(
            id = "prod-6",
            name = "Sandalias Hephaestus de Oro Viejo",
            category = ProductCategory.ACCESSORIES,
            price = 135.0,
            originalPrice = null,
            sizes = listOf("36", "37", "38", "39", "40"),
            colors = listOf(
                ProductColor("Oro Champán", 0xFFD4AF37),
                ProductColor("Cuero Natural", 0xFF8B5A2B),
                ProductColor("Negro Mate", 0xFF1C1C1C)
            ),
            description = "Sandalias romanas con correas finas de cuero vacuno curtido al vegetal. Hebillas de latón pulido y plantilla ergonómica amortiguada.",
            fabric = "Cuero genuino curtido vegetal y suela de cuero flexible",
            careInstructions = "Nutrir con crema neutra especial para calzado de cuero.",
            stock = 5,
            sku = "ATH-ACC-006",
            rating = 4.9f,
            reviewsCount = 39,
            isNew = true,
            isTrending = false,
            imageResName = "athena_hero_banner"
        ),
        Product(
            id = "prod-7",
            name = "Corset Olimpo Esculpido",
            category = ProductCategory.OLYMPUS,
            price = 155.0,
            originalPrice = 195.0,
            sizes = listOf("XS", "S", "M"),
            colors = listOf(
                ProductColor("Oro Fundido", 0xFFCCA43B),
                ProductColor("Plata Lunar", 0xFFC0C0C0),
                ProductColor("Negro Satinado", 0xFF101014)
            ),
            description = "Corset arquitectónico con varillas flexibles de soporte perfecto. Cierre trasero con cordones de raso que estilizan la silueta femenina.",
            fabric = "Brocado jacquard con hilos dorados de lurex de alta resistencia",
            careInstructions = "No planchar sobre los herrajes metálicos. Lavar a mano.",
            stock = 1, // Alert low stock
            sku = "ATH-COR-007",
            rating = 4.98f,
            reviewsCount = 45,
            isNew = true,
            isTrending = true,
            imageResName = "athena_hero_banner"
        ),
        Product(
            id = "prod-8",
            name = "Bolso Pouch Hera en Cuero Alabastro",
            category = ProductCategory.ACCESSORIES,
            price = 165.0,
            originalPrice = null,
            sizes = listOf("Única"),
            colors = listOf(
                ProductColor("Alabastro Marfil", 0xFFF2EBD9),
                ProductColor("Coñac Vintage", 0xFF9E4714),
                ProductColor("Negro Medianoche", 0xFF1A1A1A)
            ),
            description = "Clutch de mano con marco magnético fruncido en napa ultrasuave. Cadena desmontable de eslabones gruesos en baño de oro de 18k.",
            fabric = "Piel de napa italiana con herrajes dorados antioxidantes",
            careInstructions = "Limpiar con un paño seco suave y guardar en su funda guardapolvo.",
            stock = 4,
            sku = "ATH-BAG-008",
            rating = 4.85f,
            reviewsCount = 27,
            isNew = false,
            isTrending = false,
            imageResName = "athena_hero_banner"
        )
    )

    fun createInitialOrder(): Order {
        val prod1 = sampleProducts[0]
        val prod2 = sampleProducts[5]

        val items = listOf(
            CartItem(
                product = prod1,
                selectedSize = "S",
                selectedColor = prod1.colors[0],
                quantity = 1
            ),
            CartItem(
                product = prod2,
                selectedSize = "38",
                selectedColor = prod2.colors[0],
                quantity = 1
            )
        )

        val subtotal = 189.0 + 135.0
        val discount = 32.40 // ATHENA10 coupon
        val shipping = 0.0 // Free Express VIP
        val total = subtotal - discount + shipping

        return Order(
            orderId = "ATH-9284",
            dateFormatted = "Hoy, 14:15 PM",
            items = items,
            subtotal = subtotal,
            discount = discount,
            shippingFee = shipping,
            total = total,
            status = OrderStatus.OUT_FOR_DELIVERY,
            trackingNumber = "ATH-EXP-7500-AR",
            customerName = "Camila Restrepo (Athena VIP)",
            deliveryAddress = "Av. Moreno 350, Centro",
            deliveryCity = "Tres Arroyos, Buenos Aires (CP 7500)",
            deliveryNotes = "Timbrar en recepción. Entregar en caja rosa flúor.",
            paymentMethodName = "Tarjeta Athena Gold Platinum •••• 4242",
            courierName = "Alejandro Valdés - VIP Express",
            courierPhone = "+54 9 2983 52-9883",
            courierVehicle = "Moto Reparto Express Athena #04",
            courierProgress = 0.65f,
            estimatedMinutes = 8,
            remainingDistanceKm = 1.8,
            postalCode = "7500",
            latitude = -38.3752,
            longitude = -60.2783,
            isOrderAccepted = true,
            courierStatusText = "Repartidor en camino por Av. Moreno: Faltan 8 min (1.8 km) para llegar",
            timeline = listOf(
                TrackingTimelineEvent(
                    title = "Pedido Confirmado",
                    description = "Atelier Athena Style ha validado el pago y reservado las prendas exclusivas.",
                    time = "10:30 AM",
                    isCompleted = true,
                    isCurrent = false
                ),
                TrackingTimelineEvent(
                    title = "Control de Calidad & Empaque de Lujo",
                    description = "Prendas revisadas meticulosamente y perfumadas con la fragancia signature Athena.",
                    time = "11:45 AM",
                    isCompleted = true,
                    isCurrent = false
                ),
                TrackingTimelineEvent(
                    title = "En Tránsito hacia Hub Express",
                    description = "Paquete despachado y sellado con lacre dorado de seguridad.",
                    time = "01:20 PM",
                    isCompleted = true,
                    isCurrent = false
                ),
                TrackingTimelineEvent(
                    title = "En Reparto en tu Zona",
                    description = "El mensajero Alejandro Valdés está a menos de 2 km de tu dirección.",
                    time = "02:15 PM",
                    isCompleted = false,
                    isCurrent = true
                ),
                TrackingTimelineEvent(
                    title = "Entregado con Éxito",
                    description = "Firma del cliente y entrega personal en tus manos.",
                    time = "Estimado 02:45 PM",
                    isCompleted = false,
                    isCurrent = false
                )
            )
        )
    }

    val sampleNotifications = listOf(
        AppNotification(
            id = "notif-1",
            title = "¡Tu pedido está en camino!",
            message = "Tu orden #ATH-9284 con el Vestido Afrodita ya está en reparto en tu zona con mensajero VIP.",
            timeAgo = "Hace 15 min",
            type = NotificationType.ORDER_UPDATE,
            isRead = false,
            linkedOrderId = "ATH-9284"
        ),
        AppNotification(
            id = "notif-2",
            title = "Alerta de Stock Exclusivo",
            message = "¡Solo queda 1 unidad del 'Corset Olimpo Esculpido' en talla S! Asegura la tuya antes de que se agote.",
            timeAgo = "Hace 1 hora",
            type = NotificationType.INVENTORY_ALERT,
            isRead = false
        ),
        AppNotification(
            id = "notif-3",
            title = "Lanzamiento Privado: Colección Olimpo",
            message = "Acceso anticipado de 48 horas a la nueva línea inspirada en el Partenón. Usa código GOLDEN20.",
            timeAgo = "Hace 3 horas",
            type = NotificationType.PROMO_DROP,
            isRead = true
        ),
        AppNotification(
            id = "notif-4",
            title = "Regalo por Membresía VIP",
            message = "Has alcanzado el estatus Diosa Athena. Tienes envíos express ilimitados gratis este mes.",
            timeAgo = "Ayer",
            type = NotificationType.VIP_EXCLUSIVE,
            isRead = true
        )
    )
}
