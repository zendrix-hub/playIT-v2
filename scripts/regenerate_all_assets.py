"""
PlayIT Comprehensive Asset Generator
Strictly compliant with docs/engineering-package/16_ILLUSTRATION_STYLE_GUIDE.md:
- Flat-to-semi-flat 2D vector illustration
- DarkBrownOutline (#4A2E18) for all assets / DarkEspressoOutline (#3C2415) for Lily
- 3-tone cel shading (highlight top-left, base fill, shadow bottom-right)
- Generated on solid magenta background (#FF00FF)
- Automated alpha extraction via rembg with 1px edge refinement
- Negative prompt enforcement
"""

import os
import sys
import time
import random
import urllib.parse
import requests
import io
from concurrent.futures import ThreadPoolExecutor, as_completed
from PIL import Image, ImageFilter

try:
    import rembg
    REMBG_AVAILABLE = True
except ImportError:
    REMBG_AVAILABLE = False
    print("WARNING: rembg not installed. Background removal will be skipped.")

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images")
DRAWABLE_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res")

# 16_ILLUSTRATION_STYLE_GUIDE.md Universal Parameters
NEGATIVE_PROMPT = (
    "photorealistic, 3D render, CGI, complex gradients, gradient mesh, realistic fur, "
    "realistic feathers, realistic skin texture, text, watermark, signature, logo, brand marks, "
    "scary, sharp teeth, claws, weapons, blood, violence, dark muted colour palette, harsh red, "
    "neon colours, complex background clutter, busy patterns, intricate details, small fine details "
    "that disappear at small sizes, adult content, suggestive content, pure black outlines, thin hairline strokes, "
    "drop shadow, baked-in shadow on background, white background, grey background, checkerboard background, "
    "semi-transparent background, noisy edges, white halo, fringing, multiple subjects, group scene, cluttered composition, "
    "emoji, text overlays, UI elements baked into the image"
)

UNIVERSAL_MASCOT_SUFFIX = (
    "small plush pear-shaped body, oversized rounded head, giant warm hazel glossy eyes with single white specular highlight dot at top-left of each iris, "
    "soft rounded plush ears with energy-orange (#FF9800) inner glow, stubby rounded paws with no sharp claws, "
    "thick clean dark espresso vector outline (#3C2415) at 2.5dp container and 1.5dp inner detail, "
    "3-tone cel shading (cream highlight, warm tan base, soft brown shadow), Duolingo ABC and Headspace character design style, "
    "centred composition, isolated on a plain solid magenta background (#FF00FF), no text, no watermark, no signature"
)

UNIVERSAL_PICTURE_SUFFIX = (
    "flat-to-semi-flat 2D vector illustration, soft rounded shapes, thick consistent warm dark-brown outline (#4A2E18), "
    "cel-shaded 3-tone lighting with soft top-left highlight and bottom-right shadow, centred composition, "
    "simple gentle resting expression if a character, isolated on a plain solid magenta background (#FF00FF), "
    "bold clean outline, bright controlled colour palette drawn from Learning Blue (#4A90E2), Growth Green (#4CAF50), Achievement Gold (#FFC107), Energy Orange (#FF9800), Friendly Purple (#8E7DF2), "
    "designed to be instantly recognizable at 64dp in a rounded card for a children's reading app, no text or letters visible"
)

UNIVERSAL_PROP_SUFFIX = (
    "flat-to-semi-flat 2D vector illustration, soft rounded shapes, thick warm dark-brown outline (#4A2E18), "
    "cel-shaded 3-tone lighting, isolated on a plain solid magenta background (#FF00FF), "
    "whimsical oversized proportions, bold outline, bright controlled colours from PlayIT Design System palette, no text, no letters"
)

UNIVERSAL_REWARD_SUFFIX = (
    "flat-to-semi-flat 2D vector style, Achievement Gold (#FFC107), Energy Orange (#FF9800), and Friendly Purple (#8E7DF2), "
    "thick warm dark-brown outlines (#4A2E18), cel-shaded flat fills, isolated on a plain solid magenta background (#FF00FF), "
    "no characters, no text, celebratory and joyful, suitable as a transparent overlay effect"
)

# Asset Manifest Definitions
ASSETS = {
    # -------------------------------------------------------------
    # BATCH 1: Mascot Poses (Lily)
    # -------------------------------------------------------------
    "mascot/lily_idle.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, standing calmly facing forward gentle friendly resting smile relaxed upright companion posture, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),
    "mascot/lily_waving.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, welcoming cheerful one-paw wave body leaning slightly forward big open joyful smile, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),
    "mascot/lily_listening.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, attentive focused expression head tilted 10 degrees one rounded paw cupped near ear, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),
    "mascot/lily_pointing.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, friendly directional gaze one rounded arm paw extended pointing to the right side, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),
    "mascot/lily_encouraging.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, warm empathetic forward lean one rounded paw open in a supportive reassuring gesture kind caring smile, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),
    "mascot/lily_thinking.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, thoughtful inquisitive posture one rounded paw gently touching chin head tilted upward curiously, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),
    "mascot/lily_celebrating.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, explosive double-paw skyward hop big open smile eyes crinkled with pure delight high energy celebration, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),
    "mascot/splash_tarsier_headspace.png": (
        f"Pediatric vector illustration of Lily the cute Philippine tarsier mascot, sitting happily with open arms in joyful welcome surrounded by gentle floating star sparkles, {UNIVERSAL_MASCOT_SUFFIX}",
        1024
    ),

    # -------------------------------------------------------------
    # BATCH 2 & 3: Avatars & Companion Avatars
    # -------------------------------------------------------------
    "characters/avatar_01_cat.png": (
        f"A single friendly round cartoon orange kitten head avatar, exaggerated round ears, big glossy hazel eyes, gentle smile, {UNIVERSAL_PICTURE_SUFFIX}",
        512
    ),
    "characters/avatar_02_monkey.png": (
        f"A single friendly round cartoon brown monkey head avatar, soft rounded ears, cute peach face, gentle smile, {UNIVERSAL_PICTURE_SUFFIX}",
        512
    ),
    "characters/avatar_03_bunny.png": (
        f"A single friendly round cartoon white bunny rabbit head avatar, long soft rounded upright ears, gentle smile, {UNIVERSAL_PICTURE_SUFFIX}",
        512
    ),
    "characters/avatar_04_bear.png": (
        f"A single friendly round cartoon honey-brown teddy bear head avatar, rounded ears, cute light-tan muzzle, gentle smile, {UNIVERSAL_PICTURE_SUFFIX}",
        512
    ),
    "characters/avatar_05_frog.png": (
        f"A single friendly round cartoon bright green frog head avatar, big rounded eyes on top, wide happy gentle smile, {UNIVERSAL_PICTURE_SUFFIX}",
        512
    ),
    "characters/avatar_06_owl.png": (
        f"A single friendly round cartoon cute purple owl head avatar, giant round eyes with golden iris, tiny beak, gentle expression, {UNIVERSAL_PICTURE_SUFFIX}",
        512
    ),

    # -------------------------------------------------------------
    # BATCH 4: Phoneme Picture Cards (pictures/)
    # -------------------------------------------------------------
    "pictures/picture_ant.png": (f"A single friendly red-brown ant, distinctive three rounded body segments and curved antennae, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_apple.png": (f"A single fresh red apple, clean round silhouette with small green leaf and curved stem at top, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_axe.png": (f"A single friendly toy woodcutter axe, smooth wooden handle with curved silver blade, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_ball.png": (f"A single colorful play ball, bold curved stripes in Achievement Gold and Learning Blue, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_bano.png": (f"A single clean wash tub basin filled with bubbly blue water and small rubber duck, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_box.png": (f"A single open friendly cardboard cube box with folded flaps, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_cat.png": (f"A single friendly sitting ginger cat, pointed triangular ears and curled tail, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_dog.png": (f"A single friendly sitting puppy dog, floppy brown ears and wagging tail, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_drum.png": (f"A single colorful toy drum with two crossed wooden drumsticks, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_duck.png": (f"A single cute bright yellow rubber duck with smooth orange bill, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_egg.png": (f"A single clean smooth white egg nestled in a tiny twig nest, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_elephant.png": (f"A single friendly blue-gray elephant, curved trunk raised upward and large round ears, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_envelope.png": (f"A single postal letter envelope with sealed flap and tiny heart seal, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_fish.png": (f"A single friendly swimming goldfish, rounded fins and tiny air bubbles, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_gift.png": (f"A single wrapped gift present box in Energy Orange with big purple ribbon bow, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_goat.png": (f"A single friendly standing cartoon goat, short rounded curved horns and tiny beard, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_hat.png": (f"A single colorful sun hat with rounded dome crown and wide circular brim, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_igloo.png": (f"A single snowy dome igloo with arched rounded entrance tunnel, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_ink.png": (f"A single glass inkwell bottle filled with blue ink and a white feather quill, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_insect.png": (f"A single friendly red ladybug insect with black polka dots and rounded shell, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_jet.png": (f"A single sleek friendly toy airplane jet with rounded wings and blue tail fin, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_jug.png": (f"A single ceramic water jug pitcher with curved handle and rounded pour spout, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_key.png": (f"A single shiny golden key with rounded circular head and clean notched teeth, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_king.png": (f"A single friendly cartoon king character wearing a golden crown and royal cape, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_kite.png": (f"A single diamond-shaped flying kite in Learning Blue and Gold with fluttering ribbon tail, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_leaf.png": (f"A single vibrant green botanical leaf with clean smooth rounded contour and stem, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_lion.png": (f"A single friendly cartoon lion with fluffy golden mane and sweet gentle face, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_map.png": (f"A single rolled-out adventure treasure map with dotted trail and tiny X mark, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_mouse.png": (f"A single friendly little gray mouse, oversized round ears and long thin curled tail, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_nest.png": (f"A single cozy woven bird nest with three small blue speckled eggs resting inside, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_net.png": (f"A single butterfly net with long wooden handle and soft mesh bag, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_nino.png": (f"A single friendly cartoon boy child waving happily in casual t-shirt, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_nut.png": (f"A single smooth brown acorn nut with textured cap and tiny stem, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_orange.png": (f"A single juicy round orange fruit with small green leaf and stem, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_owl.png": (f"A single friendly perched owl with giant round eyes and feathered tufts, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_ox.png": (f"A single gentle cartoon ox bull with sturdy rounded body and curved horns, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_pig.png": (f"A single cute pink piglet with round snout disc and curly tail, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_pina.png": (f"A single tropical pineapple fruit with golden geometric body and spiky green leafy crown, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_queen.png": (f"A single friendly cartoon queen character wearing a sparkling tiara crown, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_quilt.png": (f"A single folded patchwork quilt blanket with colorful square patterns, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_rabbit.png": (f"A single fluffy white bunny rabbit with tall upright ears and pink nose, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_ring.png": (f"A single sparkling golden band ring with a glowing blue gemstone, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_rocket.png": (f"A single retro toy space rocket ship with rounded cockpit window and red tail fins, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_six.png": (f"A single giant stylized 3D numeral number 6 block in Achievement Gold, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_snake.png": (f"A single friendly smiling green snake coiled gently in a smooth spiral, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_star.png": (f"A single glossy five-pointed golden star with smiling friendly face, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_sun.png": (f"A single smiling bright yellow sun with gentle radiating rounded rays, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_tiger.png": (f"A single friendly cartoon tiger with bold black stripes and soft rounded ears, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_top.png": (f"A single colorful spinning top toy with striped bands and pointed tip, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_tree.png": (f"A single lush green cartoon tree with fluffy cloud-shaped foliage canopy and brown trunk, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_umbrella.png": (f"A single open colorful rain umbrella with curved canopy and hooked J handle, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_uncle.png": (f"A single friendly cartoon grandfather uncle character with round glasses and warm smile, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_van.png": (f"A single cute rounded camper van vehicle with headlights and roof rack, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_vase.png": (f"A single ceramic vase with smooth curves holding a single blooming flower, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_vest.png": (f"A single colorful buttoned fabric vest jacket with front pockets, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_watch.png": (f"A single modern wristwatch with circular dial face and flexible strap band, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_wing.png": (f"A single soft feathered bird wing with smooth rounded layered feathers, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_worm.png": (f"A single friendly smiling green inchworm crawling in an arched curve, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_yak.png": (f"A single fluffy shaggy cartoon yak with curved horns and gentle smile, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_yarn.png": (f"A single round ball of soft purple yarn wool with loose curved thread, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_yoyo.png": (f"A single classic round wooden yoyo toy with looped white string, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_zebra.png": (f"A single friendly cartoon zebra with bold black and white stripes and upright mane, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/picture_zip.png": (f"A single metallic zipper pull with interlocking teeth track, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/word_apple.png": (f"A single crisp red apple with green leaf on stem, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/word_insect.png": (f"A single friendly colorful ladybug beetle, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/word_mouse.png": (f"A single friendly little gray mouse, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/word_sun.png": (f"A single joyful bright sun with warm rays, {UNIVERSAL_PICTURE_SUFFIX}", 1024),

    # -------------------------------------------------------------
    # BATCH 5: Blend It Word Illustrations (pictures/)
    # -------------------------------------------------------------
    "pictures/blendword_aim.png": (f"A single archery bullseye target board with an arrow hitting the center, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bam.png": (f"An energetic cartoon starburst pop effect with small sparkle stars, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bat.png": (f"A single cute friendly cartoon bat with spread wings, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bed.png": (f"A cozy single wooden bed with soft fluffy pillow and folded blue quilt, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bee.png": (f"A single cute buzzing cartoon bee with yellow and black stripes, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bib.png": (f"A single baby feeding bib with gentle polka dots, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bird.png": (f"A single cute blue songbird perched on a small branch, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_box.png": (f"A single neat square cardboard box with closed lid, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_boy.png": (f"A single cheerful cartoon boy smiling happily, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bug.png": (f"A single cute friendly green beetle bug, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_bus.png": (f"A single bright yellow school bus vehicle with rounded windows, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_cake.png": (f"A single delicious frosted birthday cake with single glowing candle, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_cap.png": (f"A single sporty baseball cap hat with curved brim visor, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_cat.png": (f"A single cute friendly sitting ginger cat, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_cup.png": (f"A single ceramic hot cocoa mug cup with gentle steam wisps, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_dog.png": (f"A single happy puppy dog sitting with wagging tail, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_draw.png": (f"A single sketchpad sheet of paper with three colorful crayons, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_face.png": (f"A single friendly smiling round cartoon face with rosy cheeks, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_fan.png": (f"A single desk electric fan with safe rounded blade cage, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_fish.png": (f"A single friendly orange goldfish swimming, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_fox.png": (f"A single friendly orange cartoon fox with fluffy white-tipped tail, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_gap.png": (f"Two green stepping stones with a clear stepping gap between them, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_hand.png": (f"A single friendly open hand waving in greeting, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_hat.png": (f"A single colorful hat with round crown and brim, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_hen.png": (f"A single friendly white mother hen chicken, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_jam.png": (f"A single glass jar of sweet red strawberry jam with spoon, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_kit.png": (f"A single compact first aid kit briefcase box with medical cross, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_lit.png": (f"A single glowing warm candle with bright shining flame, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_mat.png": (f"A single colorful woven floor mat with decorative stripes, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_mob.png": (f"A group of three cute round animal friends standing happily together, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_mom.png": (f"A single caring mother character smiling warmly, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_nap.png": (f"A single cozy kitten sleeping peacefully curled up with tiny Zzz bubbles, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_pan.png": (f"A single metal frying pan skillet with a sunny-side-up egg inside, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_pig.png": (f"A single cute pink piglet sitting happily, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_pin.png": (f"A single shiny metal safety pin, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_quiz.png": (f"A single clipboard with checklist and glowing question mark bubble, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_road.png": (f"A single winding country road path between green hills, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_sam.png": (f"A single cheerful cartoon boy character named Sam waving enthusiastically, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_sis.png": (f"A single cheerful little sister girl character smiling with pigtails, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_spin.png": (f"A single colorful spinning top toy whirling with curved motion lines, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_sub.png": (f"A single bright yellow toy submarine with periscope, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_sum.png": (f"A wooden chalkboard tile with plus sign and number blocks 1 + 2 = 3, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_toy.png": (f"A single classic wooden toy train engine with wheels, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_van.png": (f"A single friendly compact camper van vehicle, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_warm.png": (f"A steaming cup of tea with warm golden sun rays shining down, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_web.png": (f"A single dewy symmetrical spider web with soft glistening drops, {UNIVERSAL_PICTURE_SUFFIX}", 1024),
    "pictures/blendword_zoo.png": (f"A whimsical wooden zoo entrance arch gate with palm leaves, {UNIVERSAL_PICTURE_SUFFIX}", 1024),

    # -------------------------------------------------------------
    # BATCH 6: Map Background Props (backgrounds/)
    # -------------------------------------------------------------
    "backgrounds/map_prop_bush.png": (f"A single round puffy green garden bush shrub, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/map_prop_rock.png": (f"A single smooth rounded river rock stone, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/map_prop_tree_small.png": (f"A single cute small rounded lollipop fruit tree, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_backpack.png": (f"A single colorful school backpack bag with front zipper pocket, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_book_stack.png": (f"A neat stack of three colorful hardcover children storybooks, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_crayon_bridge.png": (f"A whimsical arched walking bridge constructed of colorful giant wax crayons, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_eraser_shrub.png": (f"A cute pink rubber eraser block shaped like a garden hedge topiary, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_flower.png": (f"A single cheerful blooming daisy flower with round yellow center and white petals, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_globe.png": (f"A small colorful desktop globe showing world continents on a curved stand, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_magnifying_glass.png": (f"A handheld detective magnifying glass with thick rounded wooden handle, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_nipa_hut.png": (f"A traditional Philippine bahay kubo nipa hut on wooden stilts with thatched roof, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_paint_palette.png": (f"A wooden artist paint palette with colorful paint blobs and wooden brush, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_palm_tree.png": (f"A tropical palm tree with curved trunk and lush green fronds, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_paper_airplane.png": (f"A single clean folded white paper airplane flying on a gentle loop trail, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_pencil_tower.png": (f"A tall whimsical watchtower made of sharpened colorful pencils with a bell roof, {UNIVERSAL_PROP_SUFFIX}", 512),
    "backgrounds/mapprop_ruler_ramp.png": (f"A giant wooden measuring ruler acting as a fun slide ramp with millimeter marks, {UNIVERSAL_PROP_SUFFIX}", 512),

    # -------------------------------------------------------------
    # BATCH 7: Reward / Gamification Assets (rewards/)
    # -------------------------------------------------------------
    "rewards/reward_confetti_burst.png": (f"Celebration confetti burst with gold stars ribbons and sparkles radiating outwards, {UNIVERSAL_REWARD_SUFFIX}", 512),
    "rewards/reward_heart.png": (f"A glossy 3D ruby-coral heart token with bright top-left specular highlight, {UNIVERSAL_REWARD_SUFFIX}", 512),
    "rewards/reward_star.png": (f"A glossy 3D golden five-pointed achievement star token with sparkles, {UNIVERSAL_REWARD_SUFFIX}", 512),
    "rewards/reward_streak.png": (f"An energetic glowing orange fire flame streak token with joyful spark embers, {UNIVERSAL_REWARD_SUFFIX}", 512),
}

def refine_alpha_edge(img: Image.Image) -> Image.Image:
    """
    Applies 1px edge contraction / threshold refinement to eliminate any sub-pixel fringe/halo.
    """
    if img.mode != "RGBA":
        img = img.convert("RGBA")
    
    r, g, b, a = img.split()
    a_clean = a.point(lambda p: 255 if p > 80 else 0)
    a_smooth = a_clean.filter(ImageFilter.GaussianBlur(radius=0.5))
    
    img.putalpha(a_smooth)
    return img

def generate_single_asset(rel_path: str, prompt: str, target_size: int, retries: int = 5) -> bool:
    target_path = os.path.join(ASSETS_DIR, rel_path)
    os.makedirs(os.path.dirname(target_path), exist_ok=True)
    
    models = ["turbo", "flux"]
    
    for attempt in range(1, retries + 1):
        model = models[(attempt - 1) % len(models)]
        seed = random.randint(10, 9999)
        encoded_prompt = urllib.parse.quote(prompt)
        url = f"https://image.pollinations.ai/prompt/{encoded_prompt}?width=1024&height=1024&model={model}&nologo=true&seed={seed}"
        
        try:
            print(f"[{rel_path}] Attempt {attempt}/{retries} (model: {model})...")
            # Rate limiting sleep between requests
            time.sleep(random.uniform(1.5, 3.0))
            
            resp = requests.get(url, timeout=90)
            if resp.status_code == 429:
                wait_s = 5 * attempt
                print(f"[{rel_path}] Rate limited (429), backing off for {wait_s}s...")
                time.sleep(wait_s)
                continue
            elif resp.status_code != 200:
                print(f"[{rel_path}] HTTP status {resp.status_code}, retrying...")
                time.sleep(3)
                continue
                
            raw_img = Image.open(io.BytesIO(resp.content)).convert("RGBA")
            
            if REMBG_AVAILABLE:
                cutout = rembg.remove(raw_img)
            else:
                cutout = raw_img
                
            # Apply alpha edge refinement
            cutout = refine_alpha_edge(cutout)
            
            # Resize with high quality Lanczos to target_size
            cutout.thumbnail((target_size, target_size), Image.Resampling.LANCZOS)
            
            # Center on target_size x target_size canvas
            canvas = Image.new("RGBA", (target_size, target_size), (0, 0, 0, 0))
            offset = ((target_size - cutout.width) // 2, (target_size - cutout.height) // 2)
            canvas.paste(cutout, offset, mask=cutout)
            
            canvas.save(target_path, "PNG", optimize=True)
            print(f"[+] Successfully generated: {rel_path} ({target_size}x{target_size})")
            
            # Special copy for companion/avatar aliases
            if "characters/avatar_" in rel_path:
                num = rel_path.split("avatar_")[1].split("_")[0] # e.g. "01"
                animal = rel_path.split(f"avatar_{num}_")[1].split(".png")[0] # e.g. "cat"
                
                mascot_avatar_path = os.path.join(ASSETS_DIR, "mascot", f"avatar_{num}.png")
                canvas.save(mascot_avatar_path, "PNG", optimize=True)
                
                companion_path = os.path.join(ASSETS_DIR, "mascot", f"companion_avatar_{num}_{animal}.png")
                canvas.save(companion_path, "PNG", optimize=True)
                
            # Special copy for lily_celebrating in drawables
            if rel_path == "mascot/lily_celebrating.png":
                for bucket in ["drawable-mdpi", "drawable-hdpi", "drawable-xhdpi", "drawable-xxhdpi", "drawable-xxxhdpi"]:
                    d_path = os.path.join(DRAWABLE_DIR, bucket, "lily_celebrating.png")
                    if os.path.exists(os.path.dirname(d_path)):
                        canvas.save(d_path, "PNG", optimize=True)
                        
            return True
            
        except Exception as e:
            print(f"[{rel_path}] Error during attempt {attempt}: {e}")
            time.sleep(4)
            
    print(f"[!] FAILED after {retries} attempts: {rel_path}")
    return False

def main():
    import argparse
    parser = argparse.ArgumentParser(description="PlayIT Asset Generator")
    parser.add_argument("--batch", choices=["mascot", "characters", "pictures", "blendwords", "backgrounds", "rewards", "all"], default="all")
    parser.add_argument("--workers", type=int, default=1, help="Concurrent workers")
    args = parser.parse_args()
    
    to_process = {}
    for path, (prompt, size) in ASSETS.items():
        if args.batch == "all":
            to_process[path] = (prompt, size)
        elif args.batch == "mascot" and "mascot/" in path:
            to_process[path] = (prompt, size)
        elif args.batch == "characters" and "characters/" in path:
            to_process[path] = (prompt, size)
        elif args.batch == "pictures" and ("pictures/picture_" in path or "pictures/word_" in path):
            to_process[path] = (prompt, size)
        elif args.batch == "blendwords" and "pictures/blendword_" in path:
            to_process[path] = (prompt, size)
        elif args.batch == "backgrounds" and "backgrounds/" in path:
            to_process[path] = (prompt, size)
        elif args.batch == "rewards" and "rewards/" in path:
            to_process[path] = (prompt, size)
            
    total = len(to_process)
    print(f"==================================================")
    print(f"Starting PlayIT Asset Generation: {total} items")
    print(f"Batch filter: {args.batch} | Workers: {args.workers}")
    print(f"Style Guide: 16_ILLUSTRATION_STYLE_GUIDE.md")
    print(f"==================================================")
    
    start_time = time.time()
    completed = 0
    failed = 0
    
    with ThreadPoolExecutor(max_workers=args.workers) as executor:
        futures = {
            executor.submit(generate_single_asset, path, prompt, size): path
            for path, (prompt, size) in to_process.items()
        }
        
        for future in as_completed(futures):
            path = futures[future]
            try:
                success = future.result()
                if success:
                    completed += 1
                else:
                    failed += 1
            except Exception as exc:
                print(f"[!] {path} generated an exception: {exc}")
                failed += 1
                
            progress = (completed + failed) / total * 100
            print(f"--- Progress: {completed + failed}/{total} ({progress:.1f}%) | Success: {completed} | Failed: {failed} ---")
            
    elapsed = time.time() - start_time
    print(f"==================================================")
    print(f"Generation Complete in {elapsed:.1f}s")
    print(f"Total: {total} | Succeeded: {completed} | Failed: {failed}")
    print(f"==================================================")

if __name__ == "__main__":
    main()
