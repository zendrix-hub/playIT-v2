#!/usr/bin/env python3
"""Write the briefs for the art refresh (design branch): one items.json per batch, in the format of
docs/assets/briefs/*/items.json that tools/images/review_page.py, cutout.py, audit.py and make_release.py read.

Each item's subject keeps what today's picture shows (the picture is attached as the identity reference), in the
refined flat cartoon of the four anchors the user approved on the design board (2026-10-27). Faces only on animals
and people. Only pictures the app's code uses are listed.

Usage: python tools/design/make_refresh_briefs.py
"""
import json
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
BRIEFS = REPO / "docs" / "assets" / "briefs"
ANCHORS = "docs/assets/briefs/2026-10-27-refresh-anchors"

STYLE = ("Refined flat cartoon sticker illustration for a children's phonics app (ages 5 to 7), in exactly the style "
         "of the attached style reference pictures: a bold, even, dark chocolate-brown outline (#4A2E18) around every "
         "shape, flat bright colours with one soft darker shade on one side and one small white highlight, rounded "
         "friendly proportions, clean simple shapes, no gradients, no texture, no pure red (use a warm tomato or "
         "hibiscus red), simple enough to recognise at 64 px. Faces only on animals and people: objects, food, plants "
         "and vehicles have no face. One single subject, centered, filling about 70% of the square. Plain pure white "
         "background (#FFFFFF), no ground shadow, no frame, no text, no letters, no numbers unless the subject is a "
         "number, no watermark.")
STYLE_REFS = [f"{ANCHORS}/umbrella.png", f"{ANCHORS}/mouse.png", f"{ANCHORS}/fish.png", f"{ANCHORS}/sun.png"]

FINDIT = {  # picture_<id>: subject (letter words for Find It and Hear It)
    "ant": "a friendly dark-brown ant standing on six short legs, two antennae, smiling face",
    "apple": "a round apple in warm apple red with a short brown stem and one green leaf",
    "axe": "a small wood-chopping axe standing upright, grey metal head, wooden handle",
    "ball": "a round beach ball with blue, yellow and white panels",
    "bano": "a white bathtub with a curved rim and four short feet, a few soap bubbles on top (baño, the bath)",
    "box": "an empty open cardboard box with its four flaps up",
    "cat": "an orange cat sitting, facing the viewer, curled tail, smiling",
    "dog": "a brown puppy sitting, floppy ears, cream muzzle, smiling",
    "duck": "a yellow duckling standing, side view, orange beak and feet",
    "egg": "a single plain white egg standing upright",
    "elephant": "a lilac-grey baby elephant standing, big ears, trunk curled up, smiling",
    "envelope": "a closed cream envelope with a pointed flap, no stamp, no writing",
    "fish": "an orange goldfish swimming to the right, side view, one round eye",
    "gift": "a gift box in yellow wrapping paper with a blue ribbon and bow",
    "goat": "a white goat standing on four legs, side view, small horns, little beard, smiling",
    "hat": "a cone-shaped party hat with dots and a pom-pom on top",
    "igloo": "an igloo of pale-blue ice blocks with a round dark doorway",
    "ink": "a small glass ink bottle full of dark-blue ink, one ink drop beside it",
    "insect": "a red-orange ladybug standing upright on two legs, black spots, smiling",
    "jet": "a small light-blue passenger jet plane flying to the left",
    "jug": "a teal water jug with a handle and spout, a small white flower painted on it",
    "key": "a golden key with a heart-shaped bow",
    "king": "a smiling king, head and shoulders, gold crown, red-brown robe with a white fur collar",
    "kite": "a diamond kite in yellow and blue with a curly tail",
    "leaf": "a single green leaf with veins, gently curved",
    "lion": "a lion cub sitting, golden mane, smiling",
    "map": "a folded paper treasure map showing a green island and a dotted path, no letters",
    "mouse": "a grey mouse sitting upright, round pink ears, cream belly, curly pink tail, smiling",
    "nest": "a twig bird's nest holding three pale-blue eggs",
    "net": "a butterfly net with a round rim, green mesh and a wooden handle",
    "nino": "a smiling Filipino boy (niño), head and shoulders, brown skin, short black hair, blue cap",
    "nut": "an acorn with a textured brown cap",
    "orange": "a round orange fruit with a small green leaf",
    "owl": "a brown owl sitting on a short branch, big round eyes",
    "ox": "a brown ox, front view, curved horns, smiling",
    "pig": "a pink pig standing, side view, curly tail, smiling",
    "pina": "a whole pineapple (piña), golden body with a crisscross pattern and a spiky green crown",
    "queen": "a smiling queen, head and shoulders, gold crown, purple robe with a white fur collar",
    "quilt": "a folded patchwork quilt of soft pastel squares",
    "rabbit": "a white rabbit sitting, long ears with pink insides, smiling",
    "ring": "a gold ring with one blue gem",
    "rocket": "a white-and-blue rocket flying up to the right with a small orange flame",
    "six": "the numeral 6 in light blue beside six small dots in two columns",
    "snake": "a green snake coiled in an S shape, smiling",
    "star": "a five-point yellow star",
    "sun": "a bright yellow sun with short rounded rays",
    "tiger": "an orange tiger cub sitting, black stripes, smiling",
    "top": "a wooden spinning top with colourful stripes",
    "tree": "a round green tree with a short brown trunk",
    "umbrella": "an open umbrella with sunny yellow and sky blue panels and a curved brown handle",
    "uncle": "a smiling Filipino man (uncle), waist up, waving, short black hair, teal shirt",
    "up": "a blue balloon floating upward with its string trailing below and short motion lines under it",
    "van": "a small blue-and-cream van, side view",
    "vase": "a teal vase holding three flowers, pink, purple and yellow",
    "vest": "a light-blue vest with buttons down the front",
    "watch": "a wristwatch with an orange face, two hands and no numbers, and a blue strap",
    "wing": "a single white feathered bird wing",
    "worm": "a pink worm in an S shape, smiling",
    "yak": "a shaggy brown yak, front view, curved horns",
    "yarn": "a ball of pink yarn with one loose strand",
    "yoyo": "a purple yo-yo with its string",
    "zebra": "a zebra foal standing, black and white stripes, smiling",
    "zip": "a blue fabric zipper, half open, with a gold pull tab",
}

BLENDIT = {  # blendword_<id>: subject (Blend It words)
    "am": "a smiling boy pointing at himself with both thumbs (I am)",
    "bat": "a wooden baseball bat leaning on a white baseball",
    "bed": "a wooden bed with a purple blanket and a white pillow",
    "bib": "a baby bib in purple and yellow with a small apple on it, ties at the top",
    "box": "an open cardboard box with its flaps up",
    "bug": "an orange beetle with black spots, side view, smiling",
    "bus": "a yellow school bus, side view",
    "cake": "a birthday cake with white icing, sprinkles and one lit candle",
    "cap": "a blue baseball cap with a yellow brim",
    "cat": "an orange tabby cat sitting, smiling",
    "cup": "a yellow cup with green dots and a little steam",
    "dog": "a light-brown puppy sitting, smiling",
    "draw": "an open sketchbook with a crayon drawing of a flower, two crayons beside it",
    "fan": "an open folding hand fan in teal and yellow",
    "fox": "a red-orange fox sitting, white chest, smiling",
    "gap": "a short wooden fence with a gap where one plank is missing",
    "hand": "an open hand waving, palm facing the viewer",
    "hat": "a yellow sun hat with a blue ribbon bow",
    "hen": "a brown hen sitting, red-orange comb, smiling",
    "jam": "a jar of strawberry jam with a checked cloth lid",
    "kit": "a first-aid kit: a tomato-red case with a white cross and a handle",
    "lit": "a lit candle in a small round holder",
    "mat": "a flat woven mat with rainbow stripes",
    "mom": "a smiling Filipino mother waving, short dark hair, green top",
    "nap": "a baby napping on a soft purple cloud pillow",
    "pan": "a frying pan with a sunny-side-up egg",
    "pig": "a pink pig standing, side view, smiling",
    "pin": "a closed safety pin",
    "quiz": "a clipboard holding a quiz sheet with three ticked boxes and a gold star, no letters",
    "road": "a winding road through green hills",
    "sam": "a smiling boy named Sam waving, curly brown hair, green shorts",
    "sis": "a smiling girl (a sister) with two pigtails and a purple dress",
    "sub": "a yellow submarine with round windows and a periscope",
    "sum": "three toy number blocks in a row showing 1 + 2 = 3",
    "tub": "a metal washtub with soap bubbles and a yellow rubber duck",
    "van": "a teal van, side view",
    "web": "a spider web between two twigs with a tiny friendly spider",
    "yam": "a purple yam (ube), one whole and one cut half showing the purple inside",
    "zip": "a blue zipper with a gold pull tab",
    "zoo": "a green zoo entrance arch with a giraffe and a lion peeking over it, no letters",
}

WORLD = {  # (app_file, subject)
    "map_prop_bush": ("backgrounds/map_prop_bush.png", "a round leafy green bush"),
    "map_prop_rock": ("backgrounds/map_prop_rock.png", "two round grey rocks with a little moss"),
    "map_prop_tree_small": ("backgrounds/map_prop_tree_small.png", "a small round tree with a brown trunk on a tuft of grass"),
    "mapprop_backpack": ("backgrounds/mapprop_backpack.png", "a blue school backpack with a yellow side pocket"),
    "mapprop_book_stack": ("backgrounds/mapprop_book_stack.png", "a stack of three closed books, purple, yellow and green"),
    "mapprop_crayon_bridge": ("backgrounds/mapprop_crayon_bridge.png", "a small arched bridge made of colourful crayons"),
    "mapprop_flower": ("backgrounds/mapprop_flower.png", "one big orange-pink flower with green leaves"),
    "mapprop_globe": ("backgrounds/mapprop_globe.png", "a desk globe on a wooden stand"),
    "mapprop_magnifying_glass": ("backgrounds/mapprop_magnifying_glass.png", "a magnifying glass with an orange handle"),
    "mapprop_nipa_hut": ("backgrounds/mapprop_nipa_hut.png", "a Filipino nipa hut (bahay kubo) on short stilts with a thatched roof"),
    "mapprop_paint_palette": ("backgrounds/mapprop_paint_palette.png", "a wooden paint palette with dabs of paint and a brush"),
    "mapprop_palm_tree": ("backgrounds/mapprop_palm_tree.png", "a coconut palm tree on a tuft of grass"),
    "mapprop_paper_airplane": ("backgrounds/mapprop_paper_airplane.png", "a white paper airplane gliding to the right"),
    "mapprop_pencil_tower": ("backgrounds/mapprop_pencil_tower.png", "a tall yellow pencil standing like a tower, a little door and window"),
    "reward_heart": ("rewards/reward_heart.png", "a glossy hibiscus-red heart"),
    "reward_star": ("rewards/reward_star.png", "a glossy golden five-point star"),
    "reward_streak": ("rewards/reward_streak.png", "a curling orange and yellow flame"),
    "avatar_01_cat": ("characters/avatar_01_cat.png", "the head of an orange cat, front view, smiling (keep the animal and its colour)"),
    "avatar_02_monkey": ("characters/avatar_02_monkey.png", "the head of a brown monkey, front view, smiling (keep the animal and its colour)"),
    "avatar_03_bunny": ("characters/avatar_03_bunny.png", "the head of a white bunny with pink inner ears, front view, smiling (keep the animal and its colour)"),
    "avatar_04_bear": ("characters/avatar_04_bear.png", "the head of a brown bear, front view, smiling (keep the animal and its colour)"),
    "avatar_05_frog": ("characters/avatar_05_frog.png", "the head of a green frog, front view, smiling (keep the animal and its colour)"),
    "avatar_06_owl": ("characters/avatar_06_owl.png", "the head of a purple owl, front view, smiling (keep the animal and its colour)"),
}


def item(item_id, word, app_file, subject, why):
    return {
        "id": item_id,
        "word": word,
        "why": why,
        "app_file": app_file,
        "refs": [f"app/src/main/assets/images/{app_file}"],
        "subject": subject,
        "prompt": subject[0].upper() + subject[1:] + ". " + STYLE,
    }


def brief(batch, items, notes):
    return {"batch": batch, "size": "1024x1024", "variants_per_round": 2, "app_images": "app/src/main/assets/images",
            "style_prompt": STYLE, "style_refs": STYLE_REFS, "notes": notes, "items": items}


NOTES = ("Design refresh (branch design/visual-refresh). Attach the 4 style_refs and the item's own ref, today's picture, "
         "as the identity reference: keep what it shows, redraw it in the anchors' style. Lily is not in this refresh.")

BATCHES = {
    "2026-10-27-refresh-findit": [item(f"picture_{k}", k, f"pictures/picture_{k}.png", v, "Find It and Hear It picture")
                                  for k, v in FINDIT.items()],
    "2026-10-27-refresh-blendit": [item(f"blendword_{k}", k, f"pictures/blendword_{k}.png", v, "Blend It word picture")
                                   for k, v in BLENDIT.items()],
    "2026-10-27-refresh-world": [item(k, k.split("_", 1)[1].replace("prop_", "").replace("_", " "), f, v, "map prop, reward or avatar") for k, (f, v) in WORLD.items()],
}


def main():
    for batch, items in BATCHES.items():
        folder = BRIEFS / batch
        folder.mkdir(parents=True, exist_ok=True)
        for it in items:
            assert (REPO / it["refs"][0]).exists(), it["refs"][0]
        (folder / "items.json").write_text(json.dumps(brief(batch, items, NOTES), indent=1, ensure_ascii=False) + "\n",
                                           encoding="utf-8", newline="\n")
        print(f"{batch}: {len(items)} items")


if __name__ == "__main__":
    main()
