package af.market.nerkhtimes.data.model

object MarketCatalog {

    private const val AFN = "؋"

    data class Meta(
        val key: String,
        val group: String,
        val name_ps: String,   // Pashto
        val unit_ps: String,
        val name_fa: String = "",   // Dari/Farsi — empty → falls back to name_ps
        val unit_fa: String = ""    // Dari/Farsi unit — empty → falls back to unit_ps
    )

    private val list = listOf(
        // ── Currency ──────────────────────────────────────────────────────────
        Meta("usd",  "currency", "دالر",  AFN, name_fa = "دلار"),
        Meta("eur",  "currency", "یورو",  AFN, name_fa = "یورو"),
        Meta("pkr",  "currency", "کلدار", AFN, name_fa = "کلدار"),

        // ── Gold ──────────────────────────────────────────────────────────────
        Meta("gold_18k",      "gold", "سره زر ۱۸ عیار",  AFN,     name_fa = "طلای ۱۸ عیار"),
        Meta("gold_21k",      "gold", "سره زر ۲۱ عیار",  AFN,     name_fa = "طلای ۲۱ عیار"),
        Meta("gold_24k",      "gold", "سره زر ۲۴ عیار",  AFN,     name_fa = "طلای ۲۴ عیار"),
        Meta("gold_per_gram", "gold", "سره زر فی ګرام",  "ګرام",  name_fa = "طلا فی‌گرام",  unit_fa = "گرام"),
        Meta("gold_per_tola", "gold", "سره زر فی توله",  "توله",  name_fa = "طلا فی‌تولہ",  unit_fa = "تولہ"),

        // ── Stones ────────────────────────────────────────────────────────────
        Meta("stone_diamond",   "stones", "الماس",    AFN,       name_fa = "الماس"),
        Meta("stone_ruby",      "stones", "یاقوت",    AFN,       name_fa = "یاقوت"),
        Meta("stone_emerald",   "stones", "زمرد",     AFN,       name_fa = "زمرد"),
        Meta("stone_lapis",     "stones", "لاجورد",   AFN,       name_fa = "لاجورد"),
        Meta("stone_agate",     "stones", "عقیق",     AFN,       name_fa = "عقیق"),
        Meta("stone_per_carat", "stones", "فی قیراط", "قیراط",   name_fa = "فی‌قیراط",       unit_fa = "قیراط"),

        // ── Food ──────────────────────────────────────────────────────────────
        Meta("food_rice",      "food", "وریجې",       "کیلو ګرام", name_fa = "برنج",             unit_fa = "کیلوگرام"),
        Meta("food_flour",     "food", "اوړه",        "کیلو ګرام", name_fa = "آرد",              unit_fa = "کیلوگرام"),
        Meta("food_sugar",     "food", "بوره",        "کیلو ګرام", name_fa = "شکر",              unit_fa = "کیلوگرام"),
        Meta("food_oil",       "food", "غوړي",        "لیټر",      name_fa = "روغن",             unit_fa = "لیتر"),
        Meta("food_tea",       "food", "چای",         "کیلو ګرام", name_fa = "چای",              unit_fa = "کیلوگرام"),
        Meta("food_eggs",      "food", "هګۍ",         "عدد",       name_fa = "تخم‌مرغ",          unit_fa = "عدد"),
        Meta("food_meat",      "food", "غوښه",        "کیلو ګرام", name_fa = "گوشت",             unit_fa = "کیلوگرام"),
        Meta("food_veg_fruit", "food", "سبزي/مېوې",  "کیلو ګرام", name_fa = "سبزیجات/میوه",    unit_fa = "کیلوگرام"),

        // ── Fuel ──────────────────────────────────────────────────────────────
        Meta("fuel_petrol", "fuel", "پټرول",    "لیټر",      name_fa = "بنزین",     unit_fa = "لیتر"),
        Meta("fuel_diesel", "fuel", "ډیزل",     "لیټر",      name_fa = "دیزل",      unit_fa = "لیتر"),
        Meta("fuel_lpg",    "fuel", "ګاز (LPG)","کیلو ګرام", name_fa = "گاز (LPG)", unit_fa = "کیلوگرام")
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

        "emerald_ct"    to "stone_emerald",
        "lapis_ct"      to "stone_lapis",
        "ruby_ct"       to "stone_ruby",
        "agate_ct"      to "stone_agate",
        "cut_stone_ct"  to "stone_per_carat",
        "raw_stone_g"   to "stone_per_carat",

        "flour_kg"      to "food_flour",
        "flour_bag"     to "food_flour",
        "rice_kg"       to "food_rice",
        "rice_bag"      to "food_rice",
        "oil_l"         to "food_oil",
        "oil_kg"        to "food_oil",
        "sugar_kg"      to "food_sugar",
        "tea_kg"        to "food_tea",
        "eggs_piece"    to "food_eggs",
        "eggs_carton"   to "food_eggs",
        "meat_kg"       to "food_meat",
        "veg_kg"        to "food_veg_fruit",
        "fruit_kg"      to "food_veg_fruit",

        "petrol_l"      to "fuel_petrol",
        "diesel_l"      to "fuel_diesel",
        "lpg_kg"        to "fuel_lpg",
        "kerosene_l"    to "fuel_diesel"
    )
        .mapKeys  { it.key.lowercase() }
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
