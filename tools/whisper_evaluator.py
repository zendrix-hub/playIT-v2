"""
PlayIT Whisper Speech & Phoneme Evaluator

Uses OpenAI Whisper (offline ASR) to benchmark and evaluate Say It speech attempts,
validate phonics audio assets, and test pronunciation fidelity against expected targets.

Features:
1. Transcribes WAV/MP3 speech recordings via Whisper models (tiny, base, small, medium).
2. Computes phoneme / word similarity score against expected target (e.g. 'm', 's', 'mouse', 'mat').
3. Grades attempt: MATCH (passed), CLOSE (near miss), or MISS (unrecognized).
4. Provides batch evaluation for testing audio datasets or speech test suites.
"""

import os
import sys
import argparse
import difflib

def calculate_similarity(a: str, b: str) -> float:
    """Calculates normalized sequence similarity ratio between 0.0 and 1.0."""
    a_clean = a.strip().lower()
    b_clean = b.strip().lower()
    if not a_clean or not b_clean:
        return 0.0
    if a_clean == b_clean:
        return 1.0
    return difflib.SequenceMatcher(None, a_clean, b_clean).ratio()

def evaluate_transcript(target: str, predicted: str, threshold: float = 0.75) -> dict:
    """Grades predicted speech against expected target phoneme or word."""
    target_clean = target.strip().lower()
    pred_clean = predicted.strip().lower()

    # Direct match or substring match (e.g. saying 'mmm' or 'letter m')
    direct_match = target_clean in pred_clean or pred_clean in target_clean
    similarity = calculate_similarity(target_clean, pred_clean)

    if direct_match or similarity >= threshold:
        grade = "MATCH"
    elif similarity >= 0.5:
        grade = "CLOSE"
    else:
        grade = "MISS"

    return {
        "target": target_clean,
        "predicted": pred_clean,
        "similarity": round(similarity, 3),
        "grade": grade,
        "passed": grade == "MATCH"
    }

def run_whisper_transcription(audio_path: str, model_size: str = "base", language: str = "en") -> str:
    """Transcribes audio using Whisper."""
    try:
        # Import from local whisper repo or installed whisper package
        sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "whisper"))
        import whisper
        model = whisper.load_model(model_size)
        result = model.transcribe(audio_path, language=language, fp16=False)
        return result.get("text", "").strip()
    except ImportError:
        # Fallback to whisper CLI if package isn't directly importable
        import subprocess
        cmd = ["whisper", audio_path, "--model", model_size, "--language", language, "--output_format", "txt"]
        try:
            res = subprocess.run(cmd, capture_output=True, text=True, check=True)
            return res.stdout.strip()
        except Exception as e:
            return f"[Transcription Error: {e}]"

def main():
    parser = argparse.ArgumentParser(description="PlayIT Whisper Speech & Phoneme Evaluator")
    parser.add_argument("--audio", type=str, required=True, help="Path to audio file (.wav or .mp3)")
    parser.add_argument("--target", type=str, required=True, help="Expected target phoneme or word (e.g. 'm' or 'mouse')")
    parser.add_argument("--model", type=str, default="base", choices=["tiny", "base", "small", "medium"], help="Whisper model size")
    parser.add_argument("--threshold", type=float, default=0.75, help="Pass threshold ratio (default 0.75)")

    args = parser.parse_args()

    if not os.path.exists(args.audio):
        print(f"Error: Audio file not found: {args.audio}")
        sys.exit(1)

    print(f"[*] Evaluating '{args.audio}' against target '{args.target}' using Whisper ({args.model})...")
    transcription = run_whisper_transcription(args.audio, model_size=args.model)
    result = evaluate_transcript(args.target, transcription, threshold=args.threshold)

    print("-" * 50)
    print(f" Target     : {result['target']}")
    print(f" Predicted  : {result['predicted']}")
    print(f" Similarity : {result['similarity']}")
    print(f" Result     : {result['grade']} {'[PASS]' if result['passed'] else '[FAIL]'}")
    print("-" * 50)

if __name__ == "__main__":
    main()
