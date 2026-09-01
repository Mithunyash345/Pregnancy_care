---
name: Bloom Narrative
colors:
  surface: '#131411'
  surface-dim: '#131411'
  surface-bright: '#3a3936'
  surface-container-lowest: '#0e0e0c'
  surface-container-low: '#1c1c19'
  surface-container: '#20201d'
  surface-container-high: '#2a2a27'
  surface-container-highest: '#353532'
  on-surface: '#e5e2dd'
  on-surface-variant: '#bdcac1'
  inverse-surface: '#e5e2dd'
  inverse-on-surface: '#31302d'
  outline: '#87948c'
  outline-variant: '#3e4943'
  surface-tint: '#72dab0'
  primary: '#97ffd3'
  on-primary: '#003827'
  primary-container: '#7ae2b8'
  on-primary-container: '#006449'
  inverse-primary: '#006c4e'
  secondary: '#e9bacd'
  on-secondary: '#462736'
  secondary-container: '#5f3c4d'
  on-secondary-container: '#d6a9bc'
  tertiary: '#eaeaed'
  on-tertiary: '#2f3133'
  tertiary-container: '#ceced1'
  on-tertiary-container: '#56585a'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#8ef6cb'
  primary-fixed-dim: '#72dab0'
  on-primary-fixed: '#002115'
  on-primary-fixed-variant: '#00513a'
  secondary-fixed: '#ffd8e7'
  secondary-fixed-dim: '#e9bacd'
  on-secondary-fixed: '#2e1221'
  on-secondary-fixed-variant: '#5f3c4d'
  tertiary-fixed: '#e2e2e5'
  tertiary-fixed-dim: '#c6c6c9'
  on-tertiary-fixed: '#1a1c1e'
  on-tertiary-fixed-variant: '#454749'
  background: '#131411'
  on-background: '#e5e2dd'
  surface-variant: '#353532'
  deep-charcoal: '#0C0E10'
  maternity-mint: '#7AE2B8'
  soft-blush: '#F8C8DC'
  serene-white: '#F0EDE8'
  warning-amber: '#EF9F27'
  critical-rose: '#E24B4A'
  muted-sage: '#9E9B96'
typography:
  display-lg:
    fontFamily: Hanken Grotesk
    fontSize: 52px
    fontWeight: '500'
    lineHeight: '1.1'
    letterSpacing: -0.03em
  display-lg-mobile:
    fontFamily: Hanken Grotesk
    fontSize: 34px
    fontWeight: '500'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Hanken Grotesk
    fontSize: 32px
    fontWeight: '500'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  title-lg:
    fontFamily: Hanken Grotesk
    fontSize: 20px
    fontWeight: '500'
    lineHeight: '1.4'
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 17px
    fontWeight: '400'
    lineHeight: '1.6'
    letterSpacing: 0em
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 15px
    fontWeight: '400'
    lineHeight: '1.5'
    letterSpacing: 0em
  label-md:
    fontFamily: Hanken Grotesk
    fontSize: 13px
    fontWeight: '500'
    lineHeight: '1.4'
    letterSpacing: 0.02em
  eyebrow:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: 0.1em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  container-padding: 32px
  section-gap-lg: 80px
  section-gap-md: 48px
  component-gap: 16px
  stack-tight: 4px
  grid-gutter: 20px
---

## Brand & Style

The design system is centered on the concept of "Nurtured Intelligence." It balances the clinical precision required for pregnancy health tracking with a soft, supportive emotional layer. The target audience consists of expectant parents who value data-rich insights but require a calm, non-anxiety-inducing interface during a transformative life stage.

The aesthetic follows a **Modern Glassmorphic** direction. It utilizes a deep, dark canvas to reduce eye strain and create a premium "sanctuary" feel. Key characteristics include:
- **Calm Technicality:** Data is presented with professional rigor but softened by organic accents.
- **Luminous Depth:** Interfaces use subtle back-glows and frosted surfaces to create a sense of lightness despite the dark background.
- **Empathetic Clarity:** High-contrast typography ensures critical health information is legible at a glance, while rounded shapes provide a sense of safety and comfort.

## Colors

This design system employs a **Calibrated Dark** palette. The foundation is `Deep Charcoal`, providing a sophisticated, low-glare canvas that allows accent colors to vibrate softly without being harsh.

- **Primary (Maternity Mint):** Used for growth indicators, positive health trends, and primary actions. It symbolizes vitality and health.
- **Secondary (Soft Blush):** Used for emotional tracking, fetal development milestones, and secondary interactive elements. It provides a warm, human contrast to the technical mint.
- **Neutral (Serene White):** Reserved for high-priority typography and icons to ensure maximum readability against the dark background.
- **Semantic Colors:** `Warning Amber` and `Critical Rose` are used sparingly for medical alerts, ensuring they stand out immediately within the otherwise calm palette.

## Typography

The system uses **Hanken Grotesk** across all levels to maintain a clean, high-tech, yet approachable feel. It is a highly legible sans-serif that scales beautifully from dense data tables to large editorial headlines.

**Usage Guidelines:**
- **Display & Headlines:** Use tighter letter-spacing and medium weights to create a distinct, customized look.
- **Body Text:** Use `Serene White` for primary content and `Muted Sage` for secondary descriptions to establish a clear information hierarchy.
- **Eyebrows:** Always use uppercase with increased letter-spacing to categorize sections without adding visual weight.
- **Readability:** Maintain high contrast between text and background. Avoid using the secondary Soft Blush for long-form body text.

## Layout & Spacing

The design system utilizes a **8px Base Grid** to ensure mathematical harmony across all components.

- **Grid Strategy:** A 12-column fluid grid is used for desktop, transitioning to a single-column layout with 32px side margins for mobile. 
- **Bento Logic:** Complex data should be organized into "Bento-style" grids where cards have varying heights but aligned widths, creating a structured yet dynamic information display.
- **Vertical Rhythm:** Large sections are separated by 80px to provide breathing room ("white space" in a dark context), emphasizing the calm nature of the app.
- **Mobile Reflow:** On smaller screens, horizontal "overflow" scrolling is preferred for metric cards to keep the vertical scroll length manageable.

## Elevation & Depth

Hierarchy is established through **Luminance and Translucency** rather than traditional heavy shadows.

- **Surface Layers:**
    - **Base:** The primary background color (`Deep Charcoal`).
    - **Secondary Surface:** A slightly lighter charcoal (#16191C) used for card backgrounds.
    - **Glass Layer:** Semi-transparent overlays with a 12px-20px backdrop blur and a 0.5px `Serene White` border at 10% opacity.
- **Glow Effects:** Use soft radial gradients behind key metrics or primary buttons. The glow should use the `Maternity Mint` color at 10-15% opacity with a large blur radius (60px-80px) to simulate a gentle "aura."
- **Borders:** Use hairline borders (0.5px to 1px) with low-opacity white to define edges. This maintains a crisp, modern look without the clutter of heavy shadows.

## Shapes

The shape language is **Organic & Enclosing**. Rounded corners are used to evoke a sense of protection and comfort.

- **Core Elements:** Cards and containers use a 1rem (16px) to 1.5rem (24px) radius.
- **Interactive Elements:** Buttons and tags use a "Pill" shape (fully rounded) to differentiate them from static content containers.
- **Consistency:** Avoid sharp 90-degree angles. Even the smallest UI elements, like checkboxes or icons containers, should have a minimum of 4px roundedness to maintain the "soft" brand personality.

## Components

- **Buttons:** Primary buttons use a solid `Maternity Mint` fill with `Deep Charcoal` text. Secondary buttons are "Ghost" style with a `Soft Blush` border and text. All buttons should be pill-shaped.
- **Cards (Glass):** Cards must feature a subtle background tint, a backdrop blur, and a thin, low-opacity white border. They should feel like "frosted glass" resting on the dark background.
- **Inputs:** Text fields use a dark, inset appearance with a 1px border that glows `Maternity Mint` when focused. Labels should always sit above the input in the `eyebrow` style.
- **Chips & Tags:** Use small, pill-shaped containers with a low-opacity background of the color they represent (e.g., a Mint chip with 10% opacity background and 100% opacity text).
- **Progress Indicators:** Use soft, rounded bars. For pregnancy progress, use a gradient transitioning from `Soft Blush` to `Maternity Mint` to symbolize the journey and growth.
- **Data Visualizations:** Charts should use thin lines (1.5px to 2px) and include a subtle glow/shadow under the line path to give it a "neon-medical" aesthetic.