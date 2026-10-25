package com.sagar.japmala.data

data class Mantra(
    val id: String,
    val title: String,
    val text: String,
    val roman: String = "",
    val custom: Boolean = false,
)

object Mantras {
    val presets = listOf(
        Mantra("om_namah_shivaya", "शिव पंचाक्षरी", "ॐ नमः शिवाय", "Om Namah Shivaya"),
        Mantra("shri_ram", "श्री राम", "श्री राम जय राम जय जय राम", "Shri Ram Jai Ram Jai Jai Ram"),
        Mantra(
            "hare_krishna", "हरे कृष्ण महामंत्र",
            "हरे कृष्ण हरे कृष्ण, कृष्ण कृष्ण हरे हरे । हरे राम हरे राम, राम राम हरे हरे ॥",
            "Hare Krishna Maha Mantra",
        ),
        Mantra("radhe_radhe", "राधे राधे", "राधे राधे", "Radhe Radhe"),
        Mantra("vasudeva", "विष्णु द्वादशाक्षर", "ॐ नमो भगवते वासुदेवाय", "Om Namo Bhagavate Vasudevaya"),
        Mantra("ganesh", "गणेश", "ॐ गं गणपतये नमः", "Om Gam Ganapataye Namah"),
        Mantra("hanuman", "हनुमान", "ॐ हं हनुमते नमः", "Om Ham Hanumate Namah"),
        Mantra("durga", "दुर्गा", "ॐ दुं दुर्गायै नमः", "Om Dum Durgayai Namah"),
        Mantra("lakshmi", "लक्ष्मी", "ॐ श्रीं महालक्ष्म्यै नमः", "Om Shreem Mahalakshmyai Namah"),
        Mantra(
            "gayatri", "गायत्री मंत्र",
            "ॐ भूर्भुवः स्वः । तत्सवितुर्वरेण्यं । भर्गो देवस्य धीमहि । धियो यो नः प्रचोदयात् ॥",
            "Gayatri Mantra",
        ),
        Mantra(
            "mahamrityunjaya", "महामृत्युंजय",
            "ॐ त्र्यम्बकं यजामहे सुगन्धिं पुष्टिवर्धनम् । उर्वारुकमिव बन्धनान्मृत्योर्मुक्षीय मामृतात् ॥",
            "Mahamrityunjaya Mantra",
        ),
        Mantra("om", "प्रणव", "ॐ", "Om"),
    )
}
