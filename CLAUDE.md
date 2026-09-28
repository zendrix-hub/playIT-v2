cat > CLAUDE.md << 'EOF'
@AGENTS.md

# Refactor rules (Hear It / Say It)
- Spec: docs/specs/hear-say-refactor.md wins over docs/specs/week3-changeset.md.
- docs/specs/validation-report.md is reference only; do not implement from it.
- One refactor step per session. Cite the requirement ID in commit messages.
- [confirm] or [proposed] in the spec means stop and ask me.
- Say It never removes hearts. Letter names and added vowels are foils, never accepted.
- Every change updates or adds a unit test. Run ./gradlew testDebugUnitTest before finishing.
- Offline only: no network calls.
EOF
cat CLAUDE.md