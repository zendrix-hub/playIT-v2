"""
PlayIT Phonics & Dictionary Verifier (via Free Public APIs: Datamuse + Free Dictionary API)

Usage:
  python scripts/verify_dictionary.py --word cat
  python scripts/verify_dictionary.py --rhymes mat
  python scripts/verify_dictionary.py --validate-all
"""

import sys
import json
import argparse
import requests

DATAMUSE_API = "https://api.datamuse.com/words"
DICTIONARY_API = "https://api.dictionaryapi.dev/api/v2/entries/en"

# Standard Marungko Blend Words from PlayIT database
MARUNGKO_WORDS = [
    "sam", "sis", "aim", "bam", "bat", "box", "bus", "cake", "cat", "draw",
    "face", "fan", "fish", "fox", "gap", "hand", "kit", "lit", "mat", "mob",
    "nap", "pan", "pig", "quiz", "road", "spin", "sub", "sum", "van", "warm", "zoo"
]

def verify_word(word: str):
    print(f"\n[*] Querying Public APIs for word: '{word}'...")
    
    # 1. Query Free Dictionary API for definition and phonetics
    try:
        dict_resp = requests.get(f"{DICTIONARY_API}/{word}", timeout=10)
        if dict_resp.status_code == 200:
            data = dict_resp.json()
            phonetics = [p.get("text") for p in data[0].get("phonetics", []) if p.get("text")]
            meanings = data[0].get("meanings", [])
            print(f"  [+] Valid English Word: Yes")
            if phonetics:
                print(f"  [+] Phonetic / IPA: {', '.join(phonetics)}")
            if meanings:
                part_of_speech = meanings[0].get("partOfSpeech", "")
                definitions = meanings[0].get("definitions", [])
                if definitions:
                    print(f"  [+] Meaning ({part_of_speech}): {definitions[0].get('definition', '')}")
        else:
            print(f"  [!] Free Dictionary API: No exact match ({dict_resp.status_code})")
    except Exception as e:
        print(f"  [!] Free Dictionary API query error: {e}")

    # 2. Query Datamuse API for syllable count and pronunciation metadata
    try:
        dm_resp = requests.get(f"{DATAMUSE_API}?sp={word}&md=spfd", timeout=10)
        if dm_resp.status_code == 200 and dm_resp.json():
            item = dm_resp.json()[0]
            num_syllables = item.get("numSyllables", "Unknown")
            freq = item.get("tags", [])
            print(f"  [+] Syllable Count: {num_syllables}")
            print(f"  [+] Datamuse Tags: {freq}")
    except Exception as e:
        print(f"  [!] Datamuse API query error: {e}")

def find_rhymes(word: str, max_results: int = 6):
    print(f"\n[*] Finding Grade 1-friendly rhymes for '{word}' via Datamuse API...")
    try:
        resp = requests.get(f"{DATAMUSE_API}?rel_rhy={word}&max={max_results}", timeout=10)
        if resp.status_code == 200:
            rhymes = [item["word"] for item in resp.json()]
            print(f"  [+] Rhymes found: {', '.join(rhymes)}")
            return rhymes
    except Exception as e:
        print(f"  [!] Datamuse Rhyme error: {e}")
    return []

def validate_all_words():
    print(f"[*] Validating all {len(MARUNGKO_WORDS)} Marungko blend words across Public APIs...")
    for w in MARUNGKO_WORDS:
        verify_word(w)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="PlayIT Dictionary & Phonics Verifier")
    parser.add_argument("--word", help="Single word to verify phonetics and meaning")
    parser.add_argument("--rhymes", help="Find rhyming phonics words")
    parser.add_argument("--validate-all", action="store_true", help="Validate all Marungko blend words")
    
    args = parser.parse_args()
    if args.word:
        verify_word(args.word)
    elif args.rhymes:
        find_rhymes(args.rhymes)
    elif args.validate_all:
        validate_all_words()
    else:
        verify_word("sam")
        find_rhymes("mat")
