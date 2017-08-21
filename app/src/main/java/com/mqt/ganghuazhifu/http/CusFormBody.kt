package com.mqt.ganghuazhifu.http

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util
import okio.Buffer
import okio.BufferedSink
import java.io.IOException
import java.util.*

/**
 * Created by danding1207 on 16/10/18.
 */

class CusFormBody private constructor(encodedNames: List<String>, encodedValues: List<String>) : RequestBody() {

    private val encodedNames: List<String> = Util.immutableList(encodedNames)
    private val encodedValues: List<String> = Util.immutableList(encodedValues)

    /** The number of key-value pairs in this form-encoded body.  */
    fun size(): Int {
        return encodedNames.size
    }

    fun encodedName(index: Int): String {
        return encodedNames[index]
    }

    fun name(index: Int): String {
        return percentDecode(encodedName(index), true)
    }

    fun encodedValue(index: Int): String {
        return encodedValues[index]
    }

    fun value(index: Int): String {
        return percentDecode(encodedValue(index), true)
    }

    override fun contentType(): MediaType {
        return CONTENT_TYPE
    }

    override fun contentLength(): Long {
        return writeOrCountBytes(null, true)
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        writeOrCountBytes(sink, false)
    }

    /**
     * Either writes this request to `sink` or measures its content length. We have one method
     * do double-duty to make sure the counting and content are consistent, particularly when it comes
     * to awkward operations like measuring the encoded length of header strings, or the
     * length-in-digits of an encoded integer.
     */
    private fun writeOrCountBytes(sink: BufferedSink?, countBytes: Boolean): Long {
        var byteCount = 0L

        val buffer: Buffer
        if (countBytes) {
            buffer = Buffer()
        } else {
            buffer = sink!!.buffer()
        }

        var i = 0
        val size = encodedNames.size
        while (i < size) {
            if (i > 0) buffer.writeByte('&'.toInt())
            buffer.writeUtf8(encodedNames[i])
            buffer.writeByte('='.toInt())
            buffer.writeUtf8(encodedValues[i])
            i++
        }

        if (countBytes) {
            byteCount = buffer.size()
            buffer.clear()
        }

        return byteCount
    }

    class Builder {
        private val names = ArrayList<String>()
        private val values = ArrayList<String>()

        fun add(name: String, value: String?): CusFormBody.Builder {
            if (value != null) {
                names.add(canonicalize(name, FORM_ENCODE_SET, false, false, true, true))
                values.add(canonicalize(value, FORM_ENCODE_SET, false, false, true, true))
            }
            return this
        }

        fun addEncoded(name: String, value: String): CusFormBody.Builder {
            names.add(canonicalize(name, FORM_ENCODE_SET, true, false, true, true))
            values.add(canonicalize(value, FORM_ENCODE_SET, true, false, true, true))
            return this
        }

        fun build(): CusFormBody {
            return CusFormBody(names, values)
        }
    }

    private fun percentDecode(list: List<String>, plusIsSpace: Boolean): List<String> {
        val result = ArrayList<String>(list.size)
        list.mapTo(result) { percentDecode(it, plusIsSpace) }
        return Collections.unmodifiableList(result)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        var i = 0
        val size = encodedNames.size
        while (i < size) {
            stringBuilder.append(encodedNames[i])
            stringBuilder.append("=")
            stringBuilder.append(encodedValues[i])
            stringBuilder.append("\n")
            i++
        }
        return stringBuilder.toString()
    }

    fun toGetUrlString(): String {
        val stringBuilder = StringBuilder()
        var i = 0
        val size = encodedNames.size
        while (i < size - 1) {
            stringBuilder.append(encodedNames[i])
            stringBuilder.append("=")
            stringBuilder.append(encodedValues[i])
            stringBuilder.append("&")
            i++
        }
        stringBuilder.append(encodedNames[size - 1])
        stringBuilder.append("=")
        stringBuilder.append(encodedValues[size - 1])
        return stringBuilder.toString()
    }

    companion object {
        private val CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded")
        internal val FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~"
        internal fun percentDecode(encoded: String, plusIsSpace: Boolean): String {
            return percentDecode(encoded, 0, encoded.length, plusIsSpace)
        }
        internal fun percentDecode(encoded: String, pos: Int, limit: Int, plusIsSpace: Boolean): String {
            for (i in pos..limit - 1) {
                val c = encoded[i]
                if (c == '%' || c == '+' && plusIsSpace) {
                    // Slow path: the character at i requires decoding!
                    val out = Buffer()
                    out.writeUtf8(encoded, pos, i)
                    percentDecode(out, encoded, i, limit, plusIsSpace)
                    return out.readUtf8()
                }
            }
            // Fast path: no characters in [pos..limit) required decoding.
            return encoded.substring(pos, limit)
        }
        internal fun percentDecode(out: Buffer, encoded: String, pos: Int, limit: Int, plusIsSpace: Boolean) {
            var codePoint: Int
            var i = pos
            while (i < limit) {
                codePoint = encoded.codePointAt(i)
                if (codePoint == '%'.toInt() && i + 2 < limit) {
                    val d1 = decodeHexDigit(encoded[i + 1])
                    val d2 = decodeHexDigit(encoded[i + 2])
                    if (d1 != -1 && d2 != -1) {
                        out.writeByte((d1 shl 4) + d2)
                        i += 2
                        i += Character.charCount(codePoint)
                        continue
                    }
                } else if (codePoint == '+'.toInt() && plusIsSpace) {
                    out.writeByte(' '.toInt())
                    continue
                }
                out.writeUtf8CodePoint(codePoint)
                i += Character.charCount(codePoint)
            }
        }

        internal fun percentEncoded(encoded: String, pos: Int, limit: Int): Boolean {
            return pos + 2 < limit
                    && encoded[pos] == '%'
                    && decodeHexDigit(encoded[pos + 1]) != -1
                    && decodeHexDigit(encoded[pos + 2]) != -1
        }

        internal fun decodeHexDigit(c: Char): Int {
            if (c in '0'..'9') return c - '0'
            if (c in 'a'..'f') return c - 'a' + 10
            if (c in 'A'..'F') return c - 'A' + 10
            return -1
        }

        internal fun canonicalize(input: String?, encodeSet: String, alreadyEncoded: Boolean, strict: Boolean,
                                  plusIsSpace: Boolean, asciiOnly: Boolean): String {
            return canonicalize(
                    input, 0, input!!.length, encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly)
        }

        /**
         * Returns a substring of `input` on the range `[pos..limit)` with the following
         * transformations:
         *
         *  * Tabs, newlines, form feeds and carriage returns are skipped.
         *  * In queries, ' ' is encoded to '+' and '+' is encoded to "%2B".
         *  * Characters in `encodeSet` are percent-encoded.
         *  * Control characters and non-ASCII characters are percent-encoded.
         *  * All other characters are copied without transformation.
         *

         * @param alreadyEncoded true to leave '%' as-is; false to convert it to '%25'.
         * *
         * @param strict true to encode '%' if it is not the prefix of a valid percent encoding.
         * *
         * @param plusIsSpace true to encode '+' as "%2B" if it is not already encoded.
         * *
         * @param asciiOnly true to encode all non-ASCII codepoints.
         */
        internal fun canonicalize(input: String?, pos: Int, limit: Int, encodeSet: String,
                                  alreadyEncoded: Boolean, strict: Boolean, plusIsSpace: Boolean, asciiOnly: Boolean): String {
            var codePoint: Int
            var i = pos
            while (i < limit) {
                codePoint = input!!.codePointAt(i)
                if (codePoint < 0x20
                        || codePoint == 0x7f
                        || codePoint >= 0x80 && asciiOnly
                        || encodeSet.indexOf(codePoint.toChar()) != -1
                        || codePoint == '%'.toInt() && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
                        || codePoint == '+'.toInt() && plusIsSpace) {
                    // Slow path: the character at i requires encoding!
                    val out = Buffer()
                    out.writeUtf8(input, pos, i)
                    canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace,
                            asciiOnly)
                    return out.readUtf8()
                }
                i += Character.charCount(codePoint)
            }

            // Fast path: no characters in [pos..limit) required encoding.
            return input!!.substring(pos, limit)
        }

        internal fun canonicalize(out: Buffer, input: String, pos: Int, limit: Int, encodeSet: String,
                                  alreadyEncoded: Boolean, strict: Boolean, plusIsSpace: Boolean, asciiOnly: Boolean) {
            var utf8Buffer: Buffer? = null // Lazily allocated.
            var codePoint: Int
            var i = pos
            while (i < limit) {
                codePoint = input.codePointAt(i)
                if (alreadyEncoded && (codePoint == '\t'.toInt() || codePoint == '\n'.toInt() || codePoint == 12 || codePoint == '\r'.toInt())) {
                    // Skip this character.
                } else if (codePoint == '+'.toInt() && plusIsSpace) {
                    // Encode '+' as '%2B' since we permit ' ' to be encoded as either '+' or '%20'.
                    out.writeUtf8(if (alreadyEncoded) "+" else "%2B")
                } else if (codePoint < 0x20
                        || codePoint == 0x7f
                        || codePoint >= 0x80 && asciiOnly
                        || encodeSet.indexOf(codePoint.toChar()) != -1
                        || codePoint == '%'.toInt() && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
                    // Percent encode this character.
                    if (utf8Buffer == null) {
                        utf8Buffer = Buffer()
                    }
                    utf8Buffer.writeUtf8CodePoint(codePoint)
                    while (!utf8Buffer.exhausted()) {
                        var b: Int = utf8Buffer.readByte().toInt()
                        b = b and 0xff
                        out.writeByte('%'.toInt())
                        out.writeByte(HEX_DIGITS[b shr 4 and 0xf].toInt())
                        out.writeByte(HEX_DIGITS[b and 0xf].toInt())
                    }
                } else {
                    // This character doesn't need encoding. Just copy it over.
                    out.writeUtf8CodePoint(codePoint)
                }
                i += Character.charCount(codePoint)
            }
        }

        private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    }
}
