package com.example.focal

import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.io.StreamTokenizer


/*

Class to parse the file

Uses StreamTokenizer - StringTokenizer could also be used

*/
object FileParser {
    //We will create a FileReader to the file and wrap it with a StreamTokenizer
    var frs: FileReader? = null
    var `in`: StreamTokenizer? = null

    //array to store each ranking wich is read from the file 366 days in a leap year,
    //no value stored in days[0] so array needs to be sized at 367
    var days = IntArray(367)

    //store the max number of days in each month
    var daysInMonths = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    //var to determine where each value which is read from the file is stored in the array
    var day = 0
    fun fillArray(): IntArray {
        readFile() // read contents of file into array
        return days //return array
    } //end fillArray()

    // method to read contents of file into array
    private fun readFile() {
        try {
            // Create file input and output streams
            frs = FileReader("users.txt")

            // Create a stream tokenizer wrapping file input stream
            `in` = StreamTokenizer(frs)
        } catch (ex: FileNotFoundException) {
            println(ex)
        } //end catch
        try {

            //read the 2nd row 1st col value (i.e "Jan")
            `in`!!.nextToken()

            //while not EOF
            while (`in`!!.ttype != StreamTokenizer.TT_EOF) {

                //loop through the 12 months of the year
                for (month in 0..11) {
                    //read the popularity rankings for each day in the current month
                    readDaysInMonth(month)

                    //read next token => this line will read the remainder of the row headings
                    //E.G. "Feb", "Mar"...."Dec"
                    `in`!!.nextToken()
                } //end for
            } //end while
        } catch (ex: Exception) {
            println(ex)
            System.exit(0)
        } //end catch
    } //end readFile()

    //method to read the rankings for a given month
    private fun readDaysInMonth(month: Int) {
        for (i in 0 until daysInMonths[month]) {
            day++ //day stores the current day of the year => 01/01 is 1, 31/12 is 366 etc
            try {
                `in`!!.nextToken() //read the next token
            } catch (ex: IOException) {
                println(ex)
            }
            val ranking = `in`!!.nval.toInt() //read ranking from the stream
            days[day] = ranking //store the ranking in the array at index day
        } //end for
    } //end readDaysInMonth()
} //end class
