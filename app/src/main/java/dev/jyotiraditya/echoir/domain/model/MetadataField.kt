package dev.jyotiraditya.echoir.domain.model

enum class MetadataField(
    val key: String,
    val displayName: String,
    val description: String,
    val category: MetadataCategory
) {
    // Core metadata - required for basic music player functionality
    TITLE("TITLE", "Title", "Track title", MetadataCategory.CORE),
    ARTIST("ARTIST", "Artists", "Track artists", MetadataCategory.CORE),
    ALBUM("ALBUM", "Album", "Album title", MetadataCategory.CORE),
    TRACK_NUMBER("TRACKNUMBER", "Track Number", "Position in album", MetadataCategory.CORE),
    COVER("COVER", "Cover Art", "Album artwork", MetadataCategory.CORE),

    // Standard metadata - common but optional fields
    ALBUM_ARTIST("ALBUMARTIST", "Album Artist", "Primary album artist", MetadataCategory.STANDARD),
    DISC_NUMBER("DISCNUMBER", "Disc Number", "Current disc number", MetadataCategory.STANDARD),
    TRACK_TOTAL("TRACKTOTAL", "Total Tracks", "Total number of tracks", MetadataCategory.STANDARD),
    DISC_TOTAL("DISCTOTAL", "Total Discs", "Total number of discs", MetadataCategory.STANDARD),
    DATE("DATE", "Release Date", "Full release date", MetadataCategory.STANDARD),
    YEAR("YEAR", "Year", "Release year", MetadataCategory.STANDARD),
    GENRE("GENRE", "Genre", "Music genres", MetadataCategory.STANDARD),
    EXPLICIT("EXPLICIT", "Explicit", "Explicit content marker", MetadataCategory.STANDARD),

    // Extended metadata - detailed catalog information
    COPYRIGHT("COPYRIGHT", "Copyright", "Copyright information", MetadataCategory.EXTENDED),
    LABEL("LABEL", "Label", "Record label", MetadataCategory.EXTENDED),
    UPC("UPC", "UPC", "Universal Product Code", MetadataCategory.EXTENDED),
    ISRC("ISRC", "ISRC", "International Standard Recording Code", MetadataCategory.EXTENDED),
    BARCODE("BARCODE", "Barcode", "Album barcode", MetadataCategory.EXTENDED),

    // Credits metadata - production and authorship details
    COMPOSER("COMPOSER", "Composer", "Track composers", MetadataCategory.CREDITS),
    LYRICIST("LYRICIST", "Lyricist", "Track lyricists", MetadataCategory.CREDITS),
    PRODUCER("PRODUCER", "Producer", "Track producers", MetadataCategory.CREDITS),
    MIXER("MIXER", "Mixer", "Track mixers", MetadataCategory.CREDITS),
    ENGINEER("ENGINEER", "Engineer", "Track engineers", MetadataCategory.CREDITS),

    // Additional metadata
    DESCRIPTION("DESCRIPTION", "URL", "Track URL", MetadataCategory.ADDITIONAL);

    companion object {
        fun fromKey(key: String) = entries.find { it.key == key }
        fun getCoreFields() = entries.filter { it.category == MetadataCategory.CORE }
    }
}

enum class MetadataCategory {
    CORE,       // Essential fields that should always be included
    STANDARD,   // Common metadata fields
    EXTENDED,   // Detailed catalog information
    CREDITS,    // Production and authorship credits
    ADDITIONAL  // Miscellaneous additional information
}