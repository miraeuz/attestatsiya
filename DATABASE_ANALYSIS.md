# Mirae database analysis

Source: `Mirae_Informatika_Attestatsiya_Savollar_Bazasi (1).sqlite3`

## Inventory

- Active questions: 528
- Subjects: 3
- Topics: 15
- Options: 2,112 (4 per question)
- Solutions: 528
- Sources: 183
- Document pages: 3,031

## Subjects

| Subject | Questions |
|---|---:|
| Informatika va axborot texnologiyalari | 458 |
| Kasb standarti | 11 |
| Pedagogik mahorat | 59 |

## Topics

The topic table includes official slot ranges used by the source database:

- Axborot va raqamli savodxonlik — 1–3
- Kompyuter texnik va dasturiy taʼminoti — 4–5
- Word, Excel va PowerPoint — 6–10
- Mantiqiy mulohazalar — 11–13
- Sanoq sistemalari — 14–15
- Algoritmlash — 16–18
- Scratch va LOGO — 19–21
- Python va JavaScript — 22–24
- Maʼlumotlar bazasi va Access — 25–26
- Grafika va veb texnologiyalar — 27–31
- Tarmoqlar va IP manzillash — 32–33
- Xavfsizlik va raqamli xizmatlar — 34–35
- Pedagogning kasb standarti — 36–40
- Umumiy pedagogika — 41–47
- Informatika o‘qitish metodikasi — 48–50

## Difficulty

- Medium: 512
- Easy: 13
- Hard: 3

## Provenance

- original_spec_aligned: 246
- user_material: 272
- past_paper_candidate: 10

Important: several records explicitly say that their actual official-exam status is unconfirmed. The Mirae UI therefore treats the database as a practice/question bank, not as an official government exam archive.

## Implementation choice

The Android app copies the supplied SQLite database from `assets/mirae_questions.sqlite3` into app-private storage on first launch and reads it through Android's read-only `SQLiteDatabase` API. This keeps the exact supplied data intact and avoids a risky schema conversion.
