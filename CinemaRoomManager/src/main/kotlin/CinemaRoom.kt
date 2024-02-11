/**
 *This class has the main logic of the project. In accordance with the number
 * of rows and seats entered by the user, further calculations necessary for the operation of the program are performed
 */

class CinemaRoom(private val rows: Int, private val seats: Int) {

    private val cinemaRoom = mutableMapOf<Pair<Int, Int>, String>()

    init {
        for (i in 0 until rows) {
            for (j in 0 until seats) {
                cinemaRoom[i to j] = FREE_SEAT
            }
        }
    }

    private val numberOfSeats = rows * seats
    private var purchasedTickets = 0
    private var currentIncome = 0
    private val totalIncome = getTotalIncome()

    private fun getTotalIncome(): Int {
        return if (numberOfSeats <= 60) {
            numberOfSeats * PRICE_OF_ONE_TICKET_IN_FIRST_ROWS
        } else {
            if (rows % 2 == 0) {
                (rows / 2 * seats * PRICE_OF_ONE_TICKET_IN_FIRST_ROWS) +
                        (rows / 2 * seats * PRICE_OF_ONE_TICKET_IN_LAST_ROWS)
            } else {
                (rows / 2 * seats * PRICE_OF_ONE_TICKET_IN_FIRST_ROWS) +
                        (((rows / 2) + 1) * seats * PRICE_OF_ONE_TICKET_IN_LAST_ROWS)
            }
        }
    }

    fun printCinemaRoom() {
        println()
        println("Cinema:")
        print("  ")
        for (seat in 1..seats) {
            print("$seat ")
        }
        println()
        for (row in 1..rows) {
            print("$row ")
            for (seat in 1..seats) {
                print("${cinemaRoom[row - 1 to seat - 1]} ")
            }
            println()
        }
    }

    fun printResultOfPurchasing(row: Int, seat: Int): Boolean {
        return if (cinemaRoom[row - 1 to seat - 1] == BOOKED_SEAT) {
            println("\nThat ticket has already been purchased!")
            false
        } else if (row - 1 !in 0 until rows || seat - 1 !in 0 until seats) {
            println("\nWrong input!")
            false
        } else {
            cinemaRoom[row - 1 to seat - 1] = BOOKED_SEAT
            if (numberOfSeats < 60) {
                println("\nTicket price: $$PRICE_OF_ONE_TICKET_IN_FIRST_ROWS")
                currentIncome += PRICE_OF_ONE_TICKET_IN_FIRST_ROWS
            } else {
                if (row > rows / 2) {
                    println("Ticket price: $$PRICE_OF_ONE_TICKET_IN_LAST_ROWS")
                    currentIncome += PRICE_OF_ONE_TICKET_IN_LAST_ROWS
                } else {
                    println("\nTicket price: $$PRICE_OF_ONE_TICKET_IN_FIRST_ROWS")
                    currentIncome += PRICE_OF_ONE_TICKET_IN_FIRST_ROWS
                }
            }
            purchasedTickets++
            true
        }
    }


    fun printMenu() {
        println(
            """
            
            1. Show the seats
            2. Buy a ticket
            3. Statistics
            0. Exit
        """.trimIndent()
        )
    }

    fun printStatistics() {
        println(
            """
            
            Number of purchased tickets: $purchasedTickets
            Percentage: ${"%.2f".format(getPercentageOfPurchasedTickets())}%
            Current income: $$currentIncome
            Total income: $$totalIncome
        """.trimIndent()
        )
    }

    private fun getPercentageOfPurchasedTickets(): Double {
        return purchasedTickets / numberOfSeats.toDouble() * 100.0
    }

    companion object {
        const val PRICE_OF_ONE_TICKET_IN_FIRST_ROWS = 10
        const val PRICE_OF_ONE_TICKET_IN_LAST_ROWS = 8
        const val FREE_SEAT = "S"
        const val BOOKED_SEAT = "B"
    }
}