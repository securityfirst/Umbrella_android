package org.secfirst.umbrella.whitelabel.misc

import java.util.*
import java.util.regex.Pattern

/**
 * Hashids developed to generate short hashes from numbers (like YouTube).
 * Can be used as forgotten password hashes, invitation codes, store shard numbers.
 * This is implementation of http://hashids.org
 *
 * @author leprosus <korolyov.denis@gmail.com>
 * @license MIT
 */
class Hashids(var salt: String = "", var length: Int = 0, alphabet: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890") {
    private val min: Int = 16
    private val sepsDiv: Double = 3.5
    private val guardDiv: Int = 12

    private var seps: String = "cfhistuCFHISTU"
    private var guards: String? = null

    private var alphabet: String

    init {
        this.alphabet = alphabet.unique()

        if (this.alphabet.length < min)
            throw IllegalArgumentException("Alphabet must contain at least $min unique characters")

        /**
         * seps should contain only characters present in alphabet;
         * alphabet should not contains seps
         */
        val sepsLength = seps.length - 1
        for (index in 0..sepsLength) {
            val position = this.alphabet.indexOf(seps[index])

            if (position == -1) {
                seps = seps.substring(0, index) + " " + seps.substring(index + 1)
            } else {
                this.alphabet = this.alphabet.substring(0, position) + " " + this.alphabet.substring(position + 1)
            }
        }
        this.alphabet = this.alphabet.replace("\\s+".toRegex(), "")
        seps = seps.replace("\\s+".toRegex(), "")

        seps = consistentShuffle(seps, this.salt)

        if ((seps == "") || ((this.alphabet.length / seps.length) > sepsDiv)) {
            var sepsCount = getCount(this.alphabet.length, sepsDiv)

            if (sepsCount == 1)
                sepsCount++

            if (sepsCount > seps.length) {
                val diff = sepsCount - seps.length
                seps += this.alphabet.substring(0, diff)
                this.alphabet = this.alphabet.substring(diff)
            } else {
                seps = seps.substring(0, sepsCount)
            }
        }

        this.alphabet = this.consistentShuffle(this.alphabet, this.salt)

        val guardCount = getCount(this.alphabet.length, guardDiv)

        if (this.alphabet.length < 3) {
            guards = seps.substring(0, guardCount)
            seps = seps.substring(guardCount)
        } else {
            guards = this.alphabet.substring(0, guardCount)
            this.alphabet = this.alphabet.substring(guardCount)
        }
    }

    /**
     * Encrypt numbers to string
     *
     * @param numbers the numbers to encrypt
     * @return The encrypt string
     */
    fun encode(vararg numbers: Long): String {
        if (numbers.isEmpty())
            return ""

        for (number in numbers)
            if (number > 9007199254740992)
                throw IllegalArgumentException("Number can not be greater than 9007199254740992L")

        var numberHashInt = 0
        for (i in numbers.indices)
            numberHashInt += (numbers[i] % (i + 100)).toInt()

        var alphabet = this.alphabet
        val retInt = alphabet.toCharArray()[numberHashInt % alphabet.length]

        var num: Long
        var sepsIndex: Int
        var guardIndex: Int
        var buffer: String
        var retString = retInt + ""
        var guard: Char


        for (i in numbers.indices) {
            num = numbers[i]
            buffer = retInt + salt + alphabet

            alphabet = consistentShuffle(alphabet, buffer.substring(0, alphabet.length))
            val last = hash(num, alphabet)

            retString += last

            if (i + 1 < numbers.size) {
                num %= (last.toCharArray()[0].toInt() + i)
                sepsIndex = (num % seps.length).toInt()
                retString += seps.toCharArray()[sepsIndex]
            }
        }

        if (retString.length < length) {
            guardIndex = (numberHashInt + retString.toCharArray()[0].toInt()) % guards!!.length
            guard = guards!!.toCharArray()[guardIndex]

            retString = guard + retString

            if (retString.length < length) {
                guardIndex = (numberHashInt + retString.toCharArray()[2].toInt()) % guards!!.length
                guard = guards!!.toCharArray()[guardIndex]

                retString += guard
            }
        }

        val halfLength = alphabet.length / 2
        while (retString.length < length) {
            alphabet = consistentShuffle(alphabet, alphabet)
            retString = alphabet.substring(halfLength) + retString + alphabet.substring(0, halfLength)
            val excess = retString.length - length
            if (excess > 0) {
                val position = excess / 2
                retString = retString.substring(position, position + length)
            }
        }

        return retString
    }

    /**
     * Decrypt string to numbers
     *
     * @param hash the encrypt string
     * @return Decrypted numbers
     */
    fun decode(hash: String): LongArray {
        if (hash == "")
            return LongArray(0)

        var alphabet = alphabet
        val retArray = ArrayList<Long>()

        var i = 0
        val regexp = "[$guards]".toRegex()
        var hashBreakdown = hash.replace(regexp, " ")
        var hashArray = hashBreakdown.split(" ")

        if (hashArray.size == 3 || hashArray.size == 2) {
            i = 1
        }

        hashBreakdown = hashArray[i]

        val lottery = hashBreakdown.toCharArray()[0]

        hashBreakdown = hashBreakdown.substring(1)
        hashBreakdown = hashBreakdown.replace("[$seps]".toRegex(), " ")
        hashArray = hashBreakdown.split(" ")

        var buffer: String
        for (subHash in hashArray) {
            buffer = lottery + salt + alphabet
            alphabet = consistentShuffle(alphabet, buffer.substring(0, alphabet.length))
            retArray.add(unhash(subHash, alphabet))
        }

        var arr = LongArray(retArray.size)
        for (index in retArray.indices) {
            arr[index] = retArray[index]
        }

        if (encode(*arr) != hash) {
            arr = LongArray(0)
        }

        return arr
    }

    /**
     * Encrypt hexa to string
     *
     * @param hexa the hexa to encrypt
     * @return The encrypt string
     */
    fun encodeHex(hexa: String): String {
        if (!hexa.matches("^[0-9a-fA-F]+$".toRegex()))
            return ""

        val matched = ArrayList<Long>()
        val matcher = Pattern.compile("[\\w\\W]{1,12}").matcher(hexa)

        while (matcher.find())
            matched.add(java.lang.Long.parseLong("1" + matcher.group(), 16))

        val result = LongArray(matched.size)
        for (i in matched.indices) result[i] = matched[i]

        return encode(*result)
    }

    /**
     * Decrypt string to numbers
     *
     * @param hash the encrypt string
     * @return Decrypted numbers
     */
    fun decodeHex(hash: String): String {
        var result = ""
        val numbers = decode(hash)

        for (number in numbers) {
            result += java.lang.Long.toHexString(number).substring(1)
        }

        return result
    }


    private fun getCount(length: Int, div: Double): Int = Math.ceil(length.toDouble() / div).toInt()

    private fun getCount(length: Int, div: Int): Int = getCount(length, div.toDouble())

    private fun consistentShuffle(alphabet: String, salt: String): String {
        if (salt.isEmpty())
            return alphabet

        var shuffled = alphabet

        val saltArray = salt.toCharArray()
        val saltLength = salt.length
        var integer: Int
        var j: Int
        var temp: Char

        var i = shuffled.length - 1
        var v = 0
        var p = 0

        while (i > 0) {
            v %= saltLength
            integer = saltArray[v].toInt()
            p += integer
            j = (integer + v + p) % i

            temp = shuffled[j]
            shuffled = shuffled.substring(0, j) + shuffled[i] + shuffled.substring(j + 1)
            shuffled = shuffled.substring(0, i) + temp + shuffled.substring(i + 1)

            i--
            v++
        }

        return shuffled
    }

    private fun hash(input: Long, alphabet: String): String {
        var current = input
        var hash = ""
        val length = alphabet.length
        val array = alphabet.toCharArray()

        do {
            hash = array[(current % length.toLong()).toInt()] + hash
            current /= length
        } while (current > 0)

        return hash
    }

    private fun unhash(input: String, alphabet: String): Long {
        var number: Long = 0
        var position: Long
        val inputArray = input.toCharArray()
        val length = input.length - 1

        for (i in 0..length) {
            position = alphabet.indexOf(inputArray[i]).toLong()
            number += (position.toDouble() * Math.pow(alphabet.length.toDouble(), (input.length - i - 1).toDouble())).toLong()
        }

        return number
    }

    fun getVersion(): String {
        return "1.0.0"
    }

    fun kotlin.String.unique(): kotlin.String {
        var unique = ""
        val length = this.length - 1

        for (index in 0..length) {
            val current: kotlin.String = "" + this[index]

            if (!unique.contains(current) && current != " ")
                unique += current
        }

        return unique
    }
}