/**
 * This is main function where user interaction occurs.
 * User is asked to input number of rows and seat (they can't be more than 9!) and than he can interact with the menu.
 */

fun main() {
    println("Enter the number of rows:")
    val rows = readln().toInt()
    println("Enter the number of seats in each row:")
    val seats = readln().toInt()

    val cinemaRoom = CinemaRoom(rows, seats)

    var userChoice: Int = -1
    while (userChoice != 0) {
        cinemaRoom.printMenu()

        when(readln().toInt()) {
            0 -> userChoice = 0
            1 -> cinemaRoom.printCinemaRoom()
            2 -> {
                var purchase = false
                while (!purchase) {
                    println("\nEnter a row number:")
                    val row = readln().toInt()
                    println("Enter a seat number in that row:")
                    val seat = readln().toInt()
                    purchase = if (cinemaRoom.printResultOfPurchasing(row, seat)) true else false
                }

            }
            3 -> cinemaRoom.printStatistics()
        }
    }
}