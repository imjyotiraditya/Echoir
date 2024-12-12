![Echoir Banner](.github/assets/banner.png)

# Echoir

A Material You music downloader for Android

![Android](https://ziadoua.github.io/m3-Markdown-Badges/badges/Android/android2.svg)
![Kotlin](https://ziadoua.github.io/m3-Markdown-Badges/badges/Kotlin/kotlin2.svg)
![stars](https://m3-markdown-badges.vercel.app/stars/9/2/imjyotiraditya/echoir)

## About

Echoir (from Echo + Choir) is a music downloader that aims to provide high-quality audio formats while adhering to Material Design principles. The name also plays on the phrase "which will fall soon", reflecting the ephemeral nature of software projects.

## Features

- Multiple quality options:
  - Hi-Res Lossless (up to 24-bit/192kHz FLAC)
  - Lossless CD Quality (16-bit/44.1kHz FLAC)
  - Dolby Atmos (Enhanced AC-3/AC-4)
  - High Quality AAC (320kbps)
  - Standard Quality AAC (96kbps)

- Material You theming
  - Dynamic colors
  - Material Design 3 components

- Core functionality
  - Background downloads with progress notifications
  - Comprehensive metadata embedding
  - Download queue management
  - Customizable download location
  - Configurable file naming format
  - **Filters for quality and content:**
    - **Quality filters:** Hi-Res, Lossless, Dolby Atmos
    - **Content filters:** Explicit, Clean
  - Content filtering options (Explicit/Clean)
  - Album/track download support
  - Clear cache and reset settings options

## Available Regions

<details>
<summary>Supported Countries</summary>

- 🇦🇱 Albania (AL)
- 🇦🇷 Argentina (AR)
- 🇦🇺 Australia (AU)
- 🇦🇹 Austria (AT)
- 🇧🇪 Belgium (BE)
- 🇧🇷 Brazil (BR)
- 🇨🇦 Canada (CA)
- 🇨🇱 Chile (CL)
- 🇨🇴 Colombia (CO)
- 🇩🇴 Dominican Republic (DO)
- 🇫🇷 France (FR)
- 🇩🇪 Germany (DE)
- 🇭🇰 Hong Kong (HK)
- 🇮🇱 Israel (IL)
- 🇮🇹 Italy (IT)
- 🇯🇲 Jamaica (JM)
- 🇲🇾 Malaysia (MY)
- 🇲🇽 Mexico (MX)
- 🇳🇿 New Zealand (NZ)
- 🇳🇬 Nigeria (NG)
- 🇵🇪 Peru (PE)
- 🇵🇷 Puerto Rico (PR)
- 🇸🇬 Singapore (SG)
- 🇿🇦 South Africa (ZA)
- 🇪🇸 Spain (ES)
- 🇹🇭 Thailand (TH)
- 🇺🇬 Uganda (UG)
- 🇦🇪 United Arab Emirates (AE)
- 🇬🇧 United Kingdom (GB)
- 🇺🇸 United States (US)

</details>

## Embedded Metadata

<details>
<summary>Track Metadata</summary>

- **Basic Track Info**
  - Title
  - Track Number
  - Total Tracks
  - Disc Number
  - Total Discs

- **Album Info**
  - Album Title
  - Release Date
  - Year
  - Copyright
  - Record Label
  - UPC/Barcode

- **Artist Info**
  - Artist(s)
  - Album Artist
  - Composer
  - Lyricist
  - Producer
  - Mixer
  - Engineer
  - Mastering Engineer
  - Additional Performers
  - Additional Producers

- **Additional Metadata**
  - Genre
  - Explicit Flag
  - ISRC
  - Cover Art (1280x1280)
  - Lyrics (Synced if available)
  - Original URL

</details>

## Additional Information

<details>
<summary>Project History & Technical Details</summary>

### History
Echoir is the successor to FluidAC, reimagined with major improvements:
- Comprehensive metadata support
- Background download capabilities
- Full open-source availability
- Significantly reduced APK size

### Technical Notes
- Echoir uses a custom-built minimal version of [ffmpeg-kit](https://github.com/imjyotiraditya/ffmpeg-kit), significantly reduced from the [original by Arthenica](https://github.com/arthenica/ffmpeg-kit). Our build only includes FLAC support, drastically reducing the library size.

- **Why FFmpeg?**
  While Echoir is strictly a downloader (no transcoding), FFmpeg is essential for Hi-Res Lossless content. Here's why:
  - Hi-Res content is delivered via MPEG-DASH with segmented URLs
  - The process:
    1. Download all segments individually
    2. Use simple FFmpeg command: `-i input -c copy output`
    3. The `-c copy` flag ensures:
       - Direct stream copy without re-encoding
       - Pure concatenation of segments
       - No quality loss or modification
  - The code is open-source and can be audited to verify this simple operation

Note: For other formats like Lossless CD Quality, Dolby Atmos, and AAC, direct downloads are used without any FFmpeg processing.
</details>

## Related Projects & Recommendations

### Missing or Unsatisfactory Lyrics?

If you're not satisfied with the embedded lyrics or they're missing, try our friend project:
- [SongSync](https://github.com/Lambada10/SongSync/releases/latest) - A tool for managing and syncing lyrics

### Music Players

Looking for a great music player? Here are some recommendations:

- **Material Design 3 Experience:**
  - [Gramophone](https://github.com/AkaneTan/Gramophone) - A modern offline music player following Material Design 3 principles

- **Apple Music Design:**
  - [AccordLegacy](https://github.com/FoedusProgramme/AccordLegacy) - A music player with Apple Music-inspired design
  - Available through the [Light Summer](https://t.me/light_summer) channel
