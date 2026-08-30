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
        "m" to listOf("Mouse" to "images/pictures/picture_mouse.png", "Mat" to "images/pictures/blendword_mat.png", "Map" to "images/pictures/picture_map.png"),
        "s" to listOf("Sun" to "images/pictures/picture_sun.png", "Star" to "images/pictures/picture_star.png", "Snake" to "images/pictures/picture_snake.png"),
        "a" to listOf("Apple" to "images/pictures/picture_apple.png", "Ant" to "images/pictures/picture_ant.png", "Axe" to "images/pictures/picture_axe.png"),
        "i" to listOf("Insect" to "images/pictures/picture_insect.png", "Igloo" to "images/pictures/picture_igloo.png", "Ink" to "images/pictures/picture_ink.png"),
        "o" to listOf("Orange" to "images/pictures/picture_orange.png", "Owl" to "images/pictures/picture_owl.png", "Ox" to "images/pictures/picture_ox.png"),
        "b" to listOf("Ball" to "images/pictures/picture_ball.png", "Bat" to "images/pictures/blendword_bat.png", "Bus" to "images/pictures/blendword_bus.png"),
        "e" to listOf("Elephant" to "images/pictures/picture_elephant.png", "Egg" to "images/pictures/picture_egg.png", "Envelope" to "images/pictures/picture_envelope.png"),
        "u" to listOf("Umbrella" to "images/pictures/picture_umbrella.png", "Up" to "images/pictures/blendword_sub.png", "Uncle" to "images/pictures/picture_uncle.png"),
        "t" to listOf("Tiger" to "images/pictures/picture_tiger.png", "Tree" to "images/pictures/picture_tree.png", "Top" to "images/pictures/picture_top.png"),
        "k" to listOf("Kite" to "images/pictures/picture_kite.png", "Kit" to "images/pictures/blendword_kit.png", "Key" to "images/pictures/picture_key.png"),
        "l" to listOf("Lion" to "images/pictures/picture_lion.png", "Lit" to "images/pictures/blendword_lit.png", "Leaf" to "images/pictures/picture_leaf.png"),
        "y" to listOf("Yoyo" to "images/pictures/picture_yoyo.png", "Yak" to "images/pictures/picture_yak.png", "Yarn" to "images/pictures/picture_yarn.png"),
        "n" to listOf("Nest" to "images/pictures/picture_nest.png", "Nut" to "images/pictures/picture_nut.png", "Net" to "images/pictures/picture_net.png"),
        "g" to listOf("Goat" to "images/pictures/picture_goat.png", "Gap" to "images/pictures/blendword_gap.png", "Gift" to "images/pictures/picture_gift.png"),
        "ng" to listOf("Ring" to "images/pictures/picture_ring.png", "Wing" to "images/pictures/picture_wing.png", "King" to "images/pictures/picture_king.png"),
        "p" to listOf("Pig" to "images/pictures/picture_pig.png", "Pan" to "images/pictures/blendword_pan.png", "Pin" to "images/pictures/blendword_pin.png"),
        "r" to listOf("Rabbit" to "images/pictures/picture_rabbit.png", "Road" to "images/pictures/blendword_road.png", "Rocket" to "images/pictures/picture_rocket.png"),
        "d" to listOf("Dog" to "images/pictures/picture_dog.png", "Draw" to "images/pictures/blendword_draw.png", "Duck" to "images/pictures/picture_duck.png"),
        "h" to listOf("Hat" to "images/pictures/picture_hat.png", "Hen" to "images/pictures/blendword_hen.png", "Hand" to "images/pictures/blendword_hand.png"),
        "w" to listOf("Watch" to "images/pictures/picture_watch.png", "Web" to "images/pictures/blendword_web.png", "Worm" to "images/pictures/picture_worm.png"),
        "c" to listOf("Cat" to "images/pictures/picture_cat.png", "Cake" to "images/pictures/blendword_cake.png", "Cup" to "images/pictures/blendword_cup.png"),
        "f" to listOf("Fish" to "images/pictures/picture_fish.png", "Fan" to "images/pictures/blendword_fan.png", "Fox" to "images/pictures/blendword_fox.png"),
        "j" to listOf("Jug" to "images/pictures/picture_jug.png", "Jam" to "images/pictures/blendword_jam.png", "Jet" to "images/pictures/picture_jet.png"),
        "ñ" to listOf("Piña" to "images/pictures/picture_pina.png", "Niño" to "images/pictures/picture_nino.png", "Baño" to "images/pictures/picture_bano.png"),
        "q" to listOf("Queen" to "images/pictures/picture_queen.png", "Quiz" to "images/pictures/blendword_quiz.png", "Quilt" to "images/pictures/picture_quilt.png"),
        "v" to listOf("Van" to "images/pictures/picture_van.png", "Vase" to "images/pictures/picture_vase.png", "Vest" to "images/pictures/picture_vest.png"),
        "x" to listOf("Box" to "images/pictures/picture_box.png", "Fox" to "images/pictures/blendword_fox.png", "Six" to "images/pictures/picture_six.png"),
        "z" to listOf("Zebra" to "images/pictures/picture_zebra.png", "Zoo" to "images/pictures/blendword_zoo.png", "Zip" to "images/pictures/picture_zip.png")
    )

    /**
     * Generates a 5-item grid with EXACTLY 3 correct target pictures and 2 distractors.
     */
    fun generate5ItemGrid(targetPhonemeLetter: String, availablePhonemes: List<Phoneme> = emptyList()): List<FindItPictureItem> {
        val cleanTarget = targetPhonemeLetter.lowercase().trim()
        val targetCandidates = pictureBank[cleanTarget] ?: listOf(
            "Target 1" to "images/pictures/picture_mouse.png",
            "Target 2" to "images/pictures/picture_sun.png",
            "Target 3" to "images/pictures/picture_apple.png"
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
                Phoneme(2, "s", "audio/phonemes/phoneme_s.mp3", "images/pictures/picture_sun.png", "Sun"),
                Phoneme(3, "a", "audio/phonemes/phoneme_a.mp3", "images/pictures/picture_apple.png", "Apple"),
                Phoneme(4, "i", "audio/phonemes/phoneme_i.mp3", "images/pictures/picture_insect.png", "Insect")
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
