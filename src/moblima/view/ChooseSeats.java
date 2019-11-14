package moblima.view;

import moblima.controller.BookingController;
import moblima.controller.Navigation;
import moblima.model.StackArg;

public class ChooseSeats {
	
	public ChooseSeats() {
	}
	
	public void display(Navigation navigation) {
		System.out.println(
				"=====================================\n"
			  + "------Booking: Choose your Seat------\n"
			  + "=====================================\n");
		System.out.printf("'%s', %s, %s:%s%s, Cinema Class: %s, Type: %s\n\n",
				BookingController.getChosenMovie().getTitle(),
				BookingController.getChosenShowtime().getDate(),
				BookingController.getChosenShowtime().getTime()/100,
				(BookingController.getChosenShowtime().getTime()/10)%10,
				BookingController.getChosenShowtime().getTime()%10,
				BookingController.getCinemaClass(BookingController.getChosenShowtime().getCinemaId()),
				BookingController.getChosenShowtime().getType());

		getNoOfSeats(navigation);
	}

	private void getNoOfSeats(Navigation navigation) {
		int noOfSeats;
		BookingController.getChosenShowtime().getSeatLayout();
		BookingController.setSeatLayout(BookingController.getChosenShowtime().getSeatLayout());
		BookingController.printSeatLayout();
		BookingController.clearSeatSelected();
		BookingController.setTotalPrice(0);
		BookingController.setNoOfSeats(0);
		int index = 1;
		int input = navigation.getChoice("\nInput number of seats (0 - Back): ");
		System.out.println();
		BookingController.setNoOfSeats(input);

		if (input == 0) {
			navigation.goBack();
		}
		else if (input > BookingController.getNoOfSeatsLeft(BookingController.getChosenShowtime())) {
			System.out.println("Unable to book seats as number of seats exceeded number of seats left");
			System.out.println("No. of seats left: " + BookingController.getNoOfSeatsLeft(BookingController.getChosenShowtime()));
			getNoOfSeats(navigation);
		}
		else if (input > 5) {
			System.out.println("Maximum 5 seats allowed");
			getNoOfSeats(navigation);
		}
		else if (input > 0) {
			noOfSeats = input;
			getTicketType(navigation, 0, 0, index, noOfSeats);
		}
	}

	private void getTicketType(Navigation navigation, double prevPrice, double curPrice, int index, int noOfSeats) {
		BookingController.setTotalPrice(BookingController.getTotalPrice() - prevPrice);
		BookingController.clearSeat();
		System.out.println(
				"=====================================\n"
						+ "-----------Booking: Seat " + index + "----------\n"
						+ "=====================================");
		int ticketType = navigation.getChoice("Input ticket type (0 - Back, 1 - Adult, 2 - Student, 3 - SeniorCitizen): ");
		if (ticketType == 0 && index == 1) {
			getNoOfSeats(navigation);
		}
		else if (ticketType == 0) {
			index--;
			Integer[] oldSeat;
			oldSeat = BookingController.removeSeatSelected();
			BookingController.getSeatLayout()[oldSeat[0]][oldSeat[1]] = "0";
			getTicketType(navigation, curPrice, 0, index, noOfSeats);
		}
		else if (ticketType > 3 || ticketType < 0) {
			System.out.println("Please enter a valid input");
			getTicketType(navigation, 0, 0, index, noOfSeats);
		} else {
			BookingController.addSeat(ticketType);
			getRowAndColumn(navigation, index, noOfSeats);
		}
	}

	private void getRowAndColumn(Navigation navigation, int index, int noOfSeats) {
		System.out.println("Input 0 to go back to choosing ticket type");
		int row = navigation.getChoice("Input row: ");
		if (row == 0) {
			getTicketType(navigation, 0, 0, index, noOfSeats);
		}
		else {
			int col = navigation.getChoice("Input column: ");
			if (col == 0) {
				getTicketType(navigation, 0, 0, index, noOfSeats);
			}
			else if (col > 0 && row > 0 && row < 9 && col < 10) {
				if (BookingController.getSeatLayout()[row - 1][col - 1].contentEquals("0")) {
					BookingController.getSeatLayout()[row - 1][col - 1] = "1";
					BookingController.addSeat(row);
					BookingController.addSeat(col);
					BookingController.printSeatLayout();
					double price = BookingController.calcPrice();
					System.out.printf("Price for that seat: $%.2f\n", price);
					BookingController.addSeatSelected(BookingController.getSeat());
					System.out.println("\nChosen Seats");
					BookingController.printSeatSelected(BookingController.getSeatSelected());
					System.out.println();
					if (index == noOfSeats) {
						getConfirmation(navigation, price, index, noOfSeats);
					}
					else {
						index++;
						getTicketType(navigation, 0, price, index, noOfSeats);
					}
				} else if (BookingController.getSeatLayout()[row - 1][col - 1].contentEquals("1")) {
					System.out.println("Seat is occupied. PLease input a different row and column");
					getRowAndColumn(navigation, index, noOfSeats);
				} else {
					System.out.println("Please enter a valid input");
					getRowAndColumn(navigation, index, noOfSeats);
				}
			} else {
				System.out.println("Please enter a valid input");
				getRowAndColumn(navigation, index, noOfSeats);
			}
		}
	}

	private void getConfirmation(Navigation navigation, double price, int index, int noOfSeats) {
		System.out.printf("The total price of booking: $%.2f\n", BookingController.getTotalPrice());
		int confirm = navigation.getChoice("Input 1 to confirm your booking (0 - Back): ");
		if (confirm == 1) {
			System.out.println();
			navigation.goTo(new StackArg("enterParticulars", navigation.getLastView().getUserType()));
		}
		else {
			Integer[] oldSeat;
			oldSeat = BookingController.removeSeatSelected();
			BookingController.getSeatLayout()[oldSeat[0]][oldSeat[1]] = "0";
			getTicketType(navigation, price, 0, index, noOfSeats);
		}
	}

}
