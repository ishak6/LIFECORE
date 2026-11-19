# LIFECORE Design System — M1
Status: Implemented in the M1 branch; CI verification pending.

## Product direction
- Native Android application using Kotlin and Jetpack Compose.
- iOS-inspired visual language; no WebView app shell.
- Appearance modes: Light, Dark and System.
- Calm, focused, accessible UI rather than dense decorative screens.

## Design tokens
- Light background: #F6F7FA; light surface: #FFFFFF; light text: #171820.
- Dark background: #101116; dark surface: #1B1D25; dark text: #F5F5F7.
- Primary: #7667F5; secondary: #45B8A6.
- Success: #299D73; warning: #E6A23C; error: #D95757.
- Corner radii: 8, 12, 16, 20 and 24 dp.
- Standard screen horizontal inset: 20 dp; card inset: 16 dp.
- Standard component spacing: 8–16 dp.

## Typography
Use Material 3 typography roles consistently: display/headline for page-level messages, title for sections/cards, body for content, and label for compact controls and metadata. Do not communicate status by font size alone.

## Interaction principles
- Native Compose controls and Android accessibility semantics.
- Visible selected state for theme controls.
- Selected theme survives activity recreation.
- Planned modules must not be presented as working features.
- Data modules add loading, empty, error and offline states in their owning milestone.
- Destructive actions require confirmation when implemented.

## Reusable UI foundation
- LifeCoreTheme: central Material 3 color and shape theme.
- LifeCoreCard: standard surface container.
- LifeCoreSectionTitle: standard section heading.

## M1 acceptance checklist
- [x] Compose Android project scaffold created.
- [x] Light and dark color schemes defined.
- [x] System appearance mode supported.
- [x] Interactive Light/Dark/System preview controls added.
- [x] Reusable card and section-title components added.
- [x] Android CI workflow added.
- [ ] CI build and tests pass.
- [ ] APK artifact uploaded by a successful Actions run.

## Dependency baseline
Pinned versions checked against official documentation on 2026-11-19:
- Android Gradle Plugin 9.5.0; Gradle 9.6.0; JDK 17.
- Kotlin 2.4.21 and Compose compiler plugin 2.4.21.
- Compose BOM 2026.09.00.
- Compile/target SDK 36; min SDK 26.
Re-check official compatibility guidance before changing these versions.
