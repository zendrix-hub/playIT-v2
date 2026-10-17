# PlayIT diagrams

UML and ERD diagrams for the IT411 documents, drawn from SRS v3.0 (`docs/SRS_v3.0_Refactored.md`), SDD v2.0 (`docs/SDD_v2.0_Refactored.md`) and the code on `refactor/hear-say-it` (checked 2026-10-11). Each diagram has a PlantUML source in `src/`, a PNG in `png/` (for Word and PDF) and an SVG in `svg/` (zooms without blur).

Grey parts marked `<<planned>>` are in the SRS or SDD but not in the app yet, following SDD §2.0. Examples: `AudioComposer`, `LearnerModel`, `TelemetryLogger`, `CsvExportManager`, and the schema v4 tables. Everything else matches the current code.

## Use case diagram (SRS modules)

| Diagram | Covers |
|---|---|
| `usecase_srs_modules` | All six functional modules of SRS §3.2. Actors: the child learner, parent or caregiver, teacher or facilitator, and two system actors: Vosk ASR (on-device) and Android OS (PdfDocument, share sheet). Planned use cases: warm-up and recall checks (FR-NEW-REC) and the telemetry CSV export (FR-NEW-TEL). |

## Class and sequence diagrams (SDD modules)

| Module | Class diagram | Sequence diagram | SDD sections |
|---|---|---|---|
| 1. Hear It | `class_m1_hear_it` | `seq_m1_hear_it`: the modeling sequence, Ear-button replay, 10 s idle re-prompt | §3.1, §3.6.2, §4.1 |
| 2. Say It | `class_m2_say_it` | `seq_m2_say_it`: one attempt, the Vosk grammar, `TutorPolicy`, and the 3-step prompt ladder | §3.2, §3.3, §4.2 |
| 3. Find It | `class_m3_find_it` | `seq_m3_find_it`: a picture tap, hearts, heart recovery, and the 3-heart restart | §2.0 |
| 4. Blend It | `class_m4_blend_it` | `seq_m4_blend_it`: building and checking a word, the sound-out, and group stars | §3.4 |
| 5. Progression and profiles | `class_m5_progression` | `seq_m5_progression`: profile pick, letter stars, streak, unlock, live star total | §2.0, §3.5 |
| 6. Parent dashboard | `class_m6_parent_dashboard` | `seq_m6_parent_dashboard`: arithmetic gate, dashboard, PDF report, and the planned CSV export | §2.0, §3.5.2 |

## ERD

| Diagram | Covers |
|---|---|
| `erd_system` | The Room database `playit_db`, schema version 3: 11 tables with keys and foreign keys, all ON DELETE CASCADE. The two planned schema v4 tables (`letter_progress`, `telemetry_events`) are grey. |

## Re-rendering

You need Java 17, the PlantUML jar (1.2024.7 was used), and Graphviz (`dot`) for the use case, class and ER layouts. Sequence diagrams do not need Graphviz.

```bash
cd docs/diagrams
export GRAPHVIZ_DOT=/path/to/dot          # Windows: ...\Graphviz\bin\dot.exe
java -jar plantuml.jar -charset UTF-8 -tpng -o ../png src/*.puml
java -jar plantuml.jar -charset UTF-8 -tsvg -o ../svg src/*.puml
```

`src/_style.iuml` holds the shared colours and the grey `<<planned>>` style. Edit it to change the look of every diagram at once.
