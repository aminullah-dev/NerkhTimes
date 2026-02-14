package af.market.nerkhtimes.data.model

/**
 * Local catalog for:
 * 1) Grouping (currency / gold / stones / food / fuel)
 * 2) Localized names + units
 *
 * IMPORTANT:
 * - Keys must match what you store in Google Sheet (prices.key)
 * - This file also contains an alias map for backward compatibility.
 *
 * ✅ AFN pinned:
 *   Afghan Afghani symbol: ؋  (Code: AFN)
 */
object MarketCatalog {

    private const val AFN = "؋"

    data class Meta(
        val key: String,
        val group: String,
        val name_ps: String,
        val unit_ps: String
    )

    private val list = listOf(
        // ---- Currency
        Meta("usd", "currency", "دالر", AFN),
        Meta("eur", "currency", "یورو", AFN),
        Meta("pkr", "currency", "کلدار", AFN),

        // ---- Gold
        Meta("gold_18k", "gold", "سره زر ۱۸ عیار", AFN),
        Meta("gold_21k", "gold", "سره زر ۲۱ عیار", AFN),
        Meta("gold_24k", "gold", "سره زر ۲۴ عیار", AFN),
        Meta("gold_per_gram", "gold", "سره زر فی ګرام", "ګرام"),
        Meta("gold_per_tola", "gold", "سره زر فی توله", "توله"),

        // ---- Stones
        Meta("stone_diamond", "stones", "الماس", AFN),
        Meta("stone_ruby", "stones", "یاقوت", AFN),
        Meta("stone_emerald", "stones", "زمرد", AFN),
        Meta("stone_lapis", "stones", "لاجورد", AFN),
        Meta("stone_agate", "stones", "عقیق", AFN),
        Meta("stone_per_carat", "stones", "فی قیراط", "قیراط"),

        // ---- Food
        Meta("food_rice", "food", "وریجې", "کیلو ګرام"),
        Meta("food_flour", "food", "اوړه", "کیلو ګرام"),
        Meta("food_sugar", "food", "بوره", "کیلو ګرام"),
        Meta("food_oil", "food", "غوړي", "لیټر"),
        Meta("food_tea", "food", "چای", "کیلو ګرام"),
        Meta("food_eggs", "food", "هګۍ", "عدد"),
        Meta("food_meat", "food", "غوښه", "کیلو ګرام"),
        Meta("food_veg_fruit", "food", "سبزي/مېوې", "کیلو ګرام"),

        // ---- Fuel
        Meta("fuel_petrol", "fuel", "پټرول", "لیټر"),
        Meta("fuel_diesel", "fuel", "ډیزل", "لیټر"),
        Meta("fuel_lpg", "fuel", "ګاز (LPG)", "کیلو ګرام")
    )

    private val metaNew: Map<String, Meta> =
        list.associateBy { it.key.lowercase() }

    private val aliases: Map<String, String> = mapOf(
        "gold_24g_gram" to "gold_per_gram",
        "gold_24g_tola" to "gold_per_tola",
        "gold_22g_gram" to "gold_per_gram",
        "gold_22g_tola" to "gold_per_tola",
        "gold_21g_gram" to "gold_21k",
        "gold_21g_tola" to "gold_21k",
        "gold_18g_gram" to "gold_18k",
        "gold_18g_tola" to "gold_18k",

        "emerald_ct" to "stone_emerald",
        "lapis_ct" to "stone_lapis",
        "ruby_ct" to "stone_ruby",
        "agate_ct" to "stone_agate",
        "cut_stone_ct" to "stone_per_carat",
        "raw_stone_g" to "stone_per_carat",

        "flour_kg" to "food_flour",
        "flour_bag" to "food_flour",
        "rice_kg" to "food_rice",
        "rice_bag" to "food_rice",
        "oil_l" to "food_oil",
        "oil_kg" to "food_oil",
        "sugar_kg" to "food_sugar",
        "tea_kg" to "food_tea",
        "eggs_piece" to "food_eggs",
        "eggs_carton" to "food_eggs",
        "meat_kg" to "food_meat",
        "veg_kg" to "food_veg_fruit",
        "fruit_kg" to "food_veg_fruit",

        "petrol_l" to "fuel_petrol",
        "diesel_l" to "fuel_diesel",
        "lpg_kg" to "fuel_lpg",
        "kerosene_l" to "fuel_diesel"
    )
        .mapKeys { it.key.lowercase() }
        .mapValues { it.value.lowercase() }

    val metaByKey: Map<String, Meta> by lazy {
        val merged = metaNew.toMutableMap()
        for ((oldKey, newKey) in aliases) {
            val meta = metaNew[newKey]
            if (meta != null) merged[oldKey] = meta
        }
        merged
    }

    fun allKeysForChart(): List<String> = listOf(
        "usd", "eur", "pkr",
        "gold_per_gram", "gold_per_tola",
        "fuel_petrol", "fuel_diesel"
    )
}
