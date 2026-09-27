# Mirae UI / UX direction

## Visual language

The first reference image is used as the primary visual direction:

- soft lavender page background
- large rounded 22–30dp cards
- purple/violet gradient hero surfaces
- neon cyan primary action
- pink accent for error / secondary emphasis
- soft shadows rather than heavy borders
- compact pill/filter controls
- rounded icon containers
- spacious mobile-first layouts
- friendly, premium "cloud app" visual character

The second image is used as the Mirae brand mark / launcher icon.

## Information architecture

1. **Bosh sahifa**
   - Mirae branding
   - primary "Testni boshlash" CTA
   - three source subjects
   - question count
   - progress mini-card

2. **Mashq**
   - subject
   - topic
   - difficulty
   - question count
   - start test

3. **Fan detail**
   - quick mixed test
   - topic list
   - source slot range
   - topic question count

4. **Quiz**
   - question progress
   - XP
   - four-option answer cards
   - instant correctness feedback
   - stored solution explanation

5. **Result**
   - percentage
   - correct / incorrect
   - XP earned
   - retry
   - return home

6. **Natijalar**
   - accuracy ring
   - XP
   - tests
   - answered count
   - best score

7. **Sozlamalar**
   - dark mode
   - language
   - question bank information

## Design tokens

Primary purple: `#6E42F5`  
Violet: `#9B4DFF`  
Cyan: `#2FE5D0`  
Pink: `#FF5BBE`  
Lavender surface: `#F1ECFF`  
Ink: `#171429`

## Engineering

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Android SQLite read-only question repository
- ViewModel + StateFlow
- portrait-first responsive UI
- no network requirement for the question bank
