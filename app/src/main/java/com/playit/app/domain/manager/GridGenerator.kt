package com.playit.app.domain.manager

import com.playit.app.domain.model.Phoneme
import javax.inject.Inject
import javax.inject.Singleton

data class FindItPictureItem(
    val id: String,
    val phonemeLetter: String,
    val word: String,
    val imagePath: String,
    val isCorrect: Boolean
)

@Singleton
class GridGenerator @Inject constructor() {

    private val pictureBank: Map<String, List<Pair<String, String>>> = mapOf(
        "m" to listOf("Mouse" to "images/pictures/picture_mouse.webp", "Mat" to "images/pictures/blendword_mat.webp", "Map" to "images/pictures/picture_map.webp"),
        "s" to listOf("Sun" to "images/pictures/picture_sun.webp", "Star" to "images/pictures/picture_star.webp", "Snake" to "images/pictures/picture_snake.webp"),
        "a" to listOf("Apple" to "images/pictures/picture_apple.webp", "Ant" to "images/pictures/picture_ant.webp", "Axe" to "images/pictures/picture_axe.webp"),
        "i" to listOf("Insect" to "images/pictures/picture_insect.webp", "Igloo" to "images/pictures/picture_igloo.webp", "Ink" to "images/pictures/picture_ink.webp"),
        "o" to listOf("Orange" to "images/pictures/picture_orange.webp", "Owl" to "images/pictures/picture_owl.webp", "Ox" to "images/pictures/picture_ox.webp"),
        "b" to listOf("Ball" to "images/pictures/picture_ball.webp", "Bat" to "images/pictures/blendword_bat.webp", "Bus" to "images/pictures/blendword_bus.webp"),
        "e" to listOf("Elephant" to "images/pictures/picture_elephant.webp", "Egg" to "images/pictures/picture_egg.webp", "Envelope" to "images/pictures/picture_envelope.webp"),
        "u" to listOf("Umbrella" to "images/pictures/picture_umbrella.webp", "Up" to "images/pictures/blendword_sub.webp", "Uncle" to "images/pictures/picture_uncle.webp"),
        "t" to listOf("Tiger" to "images/pictures/picture_tiger.webp", "Tree" to "images/pictures/picture_tree.webp", "Top" to "images/pictures/picture_top.webp"),
        "k" to listOf("Kite" to "images/pictures/picture_kite.webp", "Kit" to "images/pictures/blendword_kit.webp", "Key" to "images/pictures/picture_key.webp"),
        "l" to listOf("Lion" to "images/pictures/picture_lion.webp", "Lit" to "images/pictures/blendword_lit.webp", "Leaf" to "images/pictures/picture_leaf.webp"),
        "y" to listOf("Yoyo" to "images/pictures/picture_yoyo.webp", "Yak" to "images/pictures/picture_yak.webp", "Yarn" to "images/pictures/picture_yarn.webp"),
        "n" to listOf("Nest" to "images/pictures/picture_nest.webp", "Nut" to "images/pictures/picture_nut.webp", "Net" to "images/pictures/picture_net.webp"),
        "g" to listOf("Goat" to "images/pictures/picture_goat.webp", "Gap" to "images/pictures/blendword_gap.webp", "Gift" to "images/pictures/picture_gift.webp"),
        "ng" to listOf("Ring" to "images/pictures/picture_ring.webp", "Wing" to "images/pictures/picture_wing.webp", "King" to "images/pictures/picture_king.webp"),
        "p" to listOf("Pig" to "images/pictures/picture_pig.webp", "Pan" to "images/pictures/blendword_pan.webp", "Pin" to "images/pictures/blendword_pin.webp"),
        "r" to listOf("Rabbit" to "images/pictures/picture_rabbit.webp", "Road" to "images/pictures/blendword_road.webp", "Rocket" to "images/pictures/picture_rocket.webp"),
        "d" to listOf("Dog" to "images/pictures/picture_dog.webp", "Draw" to "images/pictures/blendword_draw.webp", "Duck" to "images/pictures/picture_duck.webp"),
        "h" to listOf("Hat" to "images/pictures/picture_hat.webp", "Hen" to "images/pictures/blendword_hen.webp", "Hand" to "images/pictures/blendword_hand.webp"),
        "w" to listOf("Watch" to "images/pictures/picture_watch.webp", "Web" to "images/pictures/blendword_web.webp", "Worm" to "images/pictures/picture_worm.webp"),
        "c" to listOf("Cat" to "images/pictures/picture_cat.webp", "Cake" to "images/pictures/blendword_cake.webp", "Cup" to "images/pictures/blendword_cup.webp"),
        "f" to listOf("Fish" to "images/pictures/picture_fish.webp", "Fan" to "images/pictures/blendword_fan.webp", "Fox" to "images/pictures/blendword_fox.webp"),
        "j" to listOf("Jug" to "images/pictures/picture_jug.webp", "Jam" to "images/pictures/blendword_jam.webp", "Jet" to "images/pictures/picture_jet.webp"),
        "ñ" to listOf("Piña" to "images/pictures/picture_pina.webp", "Niño" to "images/pictures/picture_nino.webp", "Baño" to "images/pictures/picture_bano.webp"),
        "q" to listOf("Queen" to "images/pictures/picture_queen.webp", "Quiz" to "images/pictures/blendword_quiz.webp", "Quilt" to "images/pictures/picture_quilt.webp"),
        "v" to listOf("Van" to "images/pictures/picture_van.webp", "Vase" to "images/pictures/picture_vase.webp", "Vest" to "images/pictures/picture_vest.webp"),
        "x" to listOf("Box" to "images/pictures/picture_box.webp", "Fox" to "images/pictures/blendword_fox.webp", "Six" to "images/pictures/picture_six.webp"),
        "z" to listOf("Zebra" to "images/pictures/picture_zebra.webp", "Zoo" to "images/pictures/blendword_zoo.webp", "Zip" to "images/pictures/picture_zip.webp")
    )

    /**
     * Generates a 5-item grid with EXACTLY 3 correct target pictures and 2 distractors.
     */
    fun generate5ItemGrid(targetPhonemeLetter: String, availablePhonemes: List<Phoneme> = emptyList()): List<FindItPictureItem> {
        val cleanTarget = targetPhonemeLetter.lowercase().trim()
        val targetCandidates = pictureBank[cleanTarget] ?: listOf(
            "Target 1" to "images/pictures/picture_mouse.webp",
            "Target 2" to "images/pictures/picture_sun.webp",
            "Target 3" to "images/pictures/picture_apple.webp"
        )

        val correctItems = targetCandidates.take(3).mapIndexed { idx, (word, img) ->
            FindItPictureItem(
                id = "${cleanTarget}_correct_$idx",
                phonemeLetter = cleanTarget,
                word = word,
                imagePath = img,
                isCorrect = true
            )
        }

        // Pick 2 distractors from other phonemes
        val distractorLetters = pictureBank.keys.filter { it != cleanTarget }.shuffled()
        val distractorItems = mutableListOf<FindItPictureItem>()
        for (distLetter in distractorLetters) {
            val distList = pictureBank[distLetter] ?: continue
            val distPair = distList.firstOrNull() ?: continue
            distractorItems.add(
                FindItPictureItem(
                    id = "${distLetter}_distractor_${distractorItems.size}",
                    phonemeLetter = distLetter,
                    word = distPair.first,
                    imagePath = distPair.second,
                    isCorrect = false
                )
            )
            if (distractorItems.size == 2) break
        }

        return (correctItems + distractorItems).shuffled()
    }

    fun generateGrid(targetPhonemeId: Int, availablePhonemes: List<Phoneme>): List<Phoneme> {
        val target = availablePhonemes.find { it.id == targetPhonemeId }
            ?: return availablePhonemes.take(4)

        val distractors = availablePhonemes.filter { it.id != targetPhonemeId }
        val gridItems = mutableListOf<Phoneme>()
        gridItems.add(target)

        if (distractors.size >= 3) {
            gridItems.addAll(distractors.shuffled().take(3))
        } else {
            gridItems.addAll(distractors)
            val fallbackCandidates = listOf(
                Phoneme(2, "s", "audio/phonemes/phoneme_s.mp3", "images/pictures/picture_sun.webp", "Sun"),
                Phoneme(3, "a", "audio/phonemes/phoneme_a.mp3", "images/pictures/picture_apple.webp", "Apple"),
                Phoneme(4, "i", "audio/phonemes/phoneme_i.mp3", "images/pictures/picture_insect.webp", "Insect")
            )
            for (candidate in fallbackCandidates) {
                if (gridItems.size < 4 && gridItems.none { it.id == candidate.id }) {
                    gridItems.add(candidate)
                }
            }
        }

        return gridItems.shuffled()
    }
}
