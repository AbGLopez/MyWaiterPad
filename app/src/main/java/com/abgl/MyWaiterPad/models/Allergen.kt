package com.abgl.MyWaiterPad

object Allergen {

    enum class AllergensList {
        CELERY,
        CRUSTACEAN,
        EGG,
        FISH,
        GLUTEN,
        LUPIN,
        MILK,
        MOLLUSC,
        MUSTARD,
        PEANUT,
        SESAMO,
        SOYA,
        SULPHITE,
        TREENUTS
    }

    fun toAllergen(allergenId: Int?) = when (allergenId) {
        0 -> AllergensList.CELERY
        1 -> AllergensList.CRUSTACEAN
        2 -> AllergensList.EGG
        3 -> AllergensList.FISH
        4 -> AllergensList.GLUTEN
        5 -> AllergensList.LUPIN
        6 -> AllergensList.MILK
        7 -> AllergensList.MOLLUSC
        8 -> AllergensList.MUSTARD
        9 -> AllergensList.PEANUT
        10 -> AllergensList.SESAMO
        11 -> AllergensList.SOYA
        12 -> AllergensList.SULPHITE
        13 -> AllergensList.TREENUTS
        else -> null
    }

    fun imageToAllergen(imageId: Int) = when (imageId) {
        R.id.celery_image -> AllergensList.CELERY
        R.id.crustacean_image -> AllergensList.CRUSTACEAN
        R.id.egg_image -> AllergensList.EGG
        R.id.fish_image -> AllergensList.FISH
        R.id.gluten_image -> AllergensList.GLUTEN
        R.id.lupin_image -> AllergensList.LUPIN
        R.id.milk_image -> AllergensList.MILK
        R.id.mollusc_image -> AllergensList.MOLLUSC
        R.id.mustard_image -> AllergensList.MUSTARD
        R.id.peanut_image -> AllergensList.PEANUT
        R.id.sesamo_image -> AllergensList.SESAMO
        R.id.soya_image -> AllergensList.SOYA
        R.id.sulphite_image -> AllergensList.SULPHITE
        R.id.treenuts_image -> AllergensList.TREENUTS
        else -> null
    }

    fun drawableProduct(image: Int?) = when (image) {
        0 -> R.drawable.beer
        1 -> R.drawable.cesar_salad
        2 -> R.drawable.food_chicken
        3 -> R.drawable.food_coke
        4 -> R.drawable.food_paella
        5 -> R.drawable.food_wine
        6 -> R.drawable.food_cheese_cake
        7 -> R.drawable.food_eggs
        else -> R.drawable.no_photo
    }
}