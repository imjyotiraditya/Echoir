package dev.jyotiraditya.echoir.domain.model

enum class MetadataField(
    val key: String,
    val displayName: String,
    val description: String
) {
    TITLE("TITLE", "Title", "Track title"),
    TRACK_NUMBER("TRACKNUMBER", "Track Number", "Position in album"),
    TRACK_TOTAL("TRACKTOTAL", "Total Tracks", "Total number of tracks"),
    DISC_NUMBER("DISCNUMBER", "Disc Number", "Current disc number"),
    DISC_TOTAL("DISCTOTAL", "Total Discs", "Total number of discs"),
    ALBUM("ALBUM", "Album", "Album title"),
    DATE("DATE", "Release Date", "Full release date"),
    YEAR("YEAR", "Year", "Release year"),
    COPYRIGHT("COPYRIGHT", "Copyright", "Copyright information"),
    LABEL("LABEL", "Label", "Record label"),
    UPC("UPC", "UPC", "Universal Product Code"),
    ISRC("ISRC", "ISRC", "International Standard Recording Code"),
    BARCODE("BARCODE", "Barcode", "Album barcode"),
    ARTIST("ARTIST", "Artists", "Track artists"),
    ALBUM_ARTIST("ALBUMARTIST", "Album Artist", "Primary album artist"),
    GENRE("GENRE", "Genre", "Music genres"),
    EXPLICIT("EXPLICIT", "Explicit", "Explicit content marker"),
    COMPOSER("COMPOSER", "Composer", "Track composers"),
    LYRICIST("LYRICIST", "Lyricist", "Track lyricists"),
    PRODUCER("PRODUCER", "Producer", "Track producers"),
    MIXER("MIXER", "Mixer", "Track mixers"),
    ENGINEER("ENGINEER", "Engineer", "Track engineers"),
    DESCRIPTION("DESCRIPTION", "URL", "Track URL");

    companion object {
        fun fromKey(key: String): MetadataField? = entries.find { it.key == key }
    }
}