"""
PlayIT Phonics & Dictionary API Validator
Utilizes Public APIs (Datamuse & Free Dictionary API) to validate words for PlayIT Phonics groups.
"""

import sys
import json
import argparse
import requests

DATAMUSE_API = "https://api.datamuse.com/words"
DICTIONARY_API = "https://api.dictionaryapi.dev/api/v2/entries/en"

MARUNGKO_GROUPS = {
    1: ["m", "s", "a", "i"],
    2: ["o", "b", "e", "u"],
    3: ["t", "k", "l", "y"],
    4: ["n", "g", "ng", "p"],
    5: ["r", "d", "h", "w"],
    6: ["c", "f", "j"],
    7: ["q", "v", "x", "z"]
}

def get_cumulative_letters(max_group: int):
    letters = set()
    for g in range(1, max_group + 1):
        for l in MARUNGKO_GROUPS.get(g, []):
            if l != "ng":
                letters.add(l)
    return letters

def query_datamuse_words(available_letters, max_results=15):
    """Finds valid English words composed exclusively of available letters."""
    valid_words = []
    try:
        resp = requests.get(f"{DATAMUSE_API}?sp=???&max=100", timeout=10)
        if resp.status_code == 200:
            data = resp.json()
            for item in data:
                word = item.get('word', '').lower()
                if word.isalpha() and all(c in available_letters for c in word):
                    valid_words.append(word)
                    if len(valid_words) >= max_results:
                        break
    except Exception as e:
        print(f"[!] Warning: Datamuse API request failed: {e}")
        
    return valid_words

def verify_word_definition(word: str):
    """Verifies word pronunciation and definition using Free Dictionary API."""
    try:
        resp = requests.get(f"{DICTIONARY_API}/{word.lower()}", timeout=10)
        if resp.status_code == 200:
            data = resp.json()
            if data and isinstance(data, list):
                first_entry = data[0]
                phonetics = [p.get("text") for p in first_entry.get("phonetics", []) if p.get("text")]
                ipa = phonetics[0] if phonetics else "N/A"
                meanings = first_entry.get('meanings', [])
                first_def = "N/A"
                pos = ""
                if meanings and meanings[0].get('definitions'):
                    pos = meanings[0].get('partOfSpeech', '')
                    first_def = meanings[0]['definitions'][0].get('definition', 'N/A')
                return {
                    "valid": True,
                    "phonetic": ipa,
                    "pos": pos,
                    "definition": first_def
                }
    except Exception as e:
        pass
    return {"valid": False, "phonetic": "N/A", "pos": "", "definition": "Definition not found"}

def main():
    parser = argparse.ArgumentParser(description="Validate Phonics Word Bank using Public APIs")
    parser.add_argument("--group", type=int, default=1, help="Marungko Group Number (1-7)")
    parser.add_argument("--word", type=str, default=None, help="Optional specific word to check")
    args = parser.parse_args()

    print("=" * 80)
    print(f"[*] PlayIT Phonics Curriculum & Dictionary API Validator")
    print("=" * 80)

    if args.word:
        print(f"[*] Checking specific word: '{args.word.upper()}'")
        res = verify_word_definition(args.word.lower())
        print(f"    - Exists in Lexicon: {res['valid']}")
        print(f"    - Phonetic IPA:      {res['phonetic']}")
        print(f"    - Part of Speech:    {res['pos']}")
        print(f"    - Definition:        {res['definition']}")
        print("=" * 80)
        return

    available_letters = get_cumulative_letters(args.group)
    print(f"[*] Group {args.group} cumulative available letters: {sorted(list(available_letters))}")
    print("[*] Fetching candidate words from Public Datamuse API...")
    words = query_datamuse_words(available_letters)
    
    print(f"[+] Found {len(words)} candidate CVC words formable with Group 1..{args.group} letters:")
    for w in words:
        info = verify_word_definition(w)
        print(f"    • {w.upper():<6} | IPA: {info['phonetic']:<12} | [{info['pos']}] {info['definition'][:45]}...")
        
    print("=" * 80)
    print("[*] Validation complete.")

if __name__ == "__main__":
    main()
