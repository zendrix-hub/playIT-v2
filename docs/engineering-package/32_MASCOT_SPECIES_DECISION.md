# 32 — Mascot Species Decision: Lily the Tarsier

> Resolves a documentation inconsistency: `17_CHARACTER_DESIGN_GUIDE.md`
> (original engineering package) proposed a cat named "Kuting," while
> `26_MASCOT_COPLAYER_SYSTEM.md` (polish phase) treats "Lily the Tarsier" as
> the already-implemented mascot — existing `MascotState` enum,
> `DockedMascotWithBubble`, `MascotBubble`. This doc confirms which is
> correct going forward, and why. `17_CHARACTER_DESIGN_GUIDE.md` has been
> rewritten to match this decision — see that file for the full
> appearance/usage spec.

## 1. The question

Doc 17 explicitly flagged this as unresolved when it was written: it
proposed Kuting the cat as a safe default, but noted the source Competitive
Analysis research had already surfaced "regional options like a tarsier"
and left the door open for stakeholders to swap the reference description.
Somewhere between doc 17 and doc 26, that swap happened in the actual
codebase — but doc 17 itself was never updated, leaving two contradictory
"official" character specs sitting in the same package.

## 2. Why Lily the Tarsier is the right call

**Cultural relevance beats generic cuteness for this specific audience.**
General mascot-design research agrees that a consistent, expressive
companion character helps pre-literate and early-reading children — a
trusted character narrating instructions, reacting without judgment, giving
the product an emotional anchor. That principle is satisfied about equally
well by a cat or a tarsier; it doesn't decide between them. What does: the
Philippine tarsier is a genuine national symbol, closely tied to Bohol
tourism and Visayan folklore, where it's traditionally regarded as a small,
lucky forest spirit. For a phonics app built specifically for Filipino
Grade 1 learners, that's a meaningfully stronger "relatedness" hook (in the
Self-Determination Theory sense `04_RESEARCH_SUMMARY.md` already leans on)
than a cat offers — a companion that reads as authentically *theirs*, not a
generic animal that could belong to any kids' app from any country.

**The tarsier's signature trait is a design asset, not a liability.** Its
famously oversized, expressive eyes are exactly the kind of feature that
reads as endearing on a stylized children's character — the same "big eyes,
round proportions" cuteness cue that makes most successful kids'-app
mascots work in the first place — and it gives the expression system
(already built out in `26 §3`) more to work with than a cat's smaller,
less expressive eyes would.

**The name is already doing real work.** "Lily" softens what could
otherwise be an unfamiliar-looking animal into something warm and
approachable before a child ever sees her. That framing matters more for a
tarsier than it would for a cat, where the animal is already instantly
familiar without any help from a name.

## 3. The one real risk, and how it's already handled

Tarsiers are tiny, thin-limbed, and nocturnal — nothing about the real
animal is "round and soft." Left un-stylized, that combination can tip into
unsettling rather than endearing. This is purely a stylization risk, not a
reason to reconsider the species: `26`'s existing art direction (soft PNG
states, the gummy/rounded shape language shared with the rest of the `23`
UI refresh) already pushes hard in the right direction.
`17_CHARACTER_DESIGN_GUIDE.md` has been rewritten with an explicit
guardrail on this — see its §2.

## 4. One open item, not resolved here

`17`'s original 8-pose proposal included an `Excited` state (for
milestones, streak badges, unlocks) that isn't part of `26`'s current
5-state baseline (`IDLE`, `CELEBRATING`, `ENCOURAGING`, `LISTENING`,
`POINTING`) or its two new additions (`WAVING`, `THINKING`). Either
`Excited` was intentionally folded into `CELEBRATING` during
implementation, or it's a gap nobody caught. Confirm which before treating
milestone celebrations as fully speced — the rewritten `17` reflects the
7-state reality as-is and flags this rather than silently picking an
answer for you.

## Sources consulted
- Philippine tarsier cultural significance and Bohol tourism association —
  kids.kiddle.co, secret-ph.com, kgmresorts.com, destinationsunknown.com
- Children's edtech mascot design principles (consistent character,
  emotional range, cuteness cues for ages 4–8) — gapsystudio.com,
  mascoteer.com
