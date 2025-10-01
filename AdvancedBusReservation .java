import java.io.*;
import java.util.*;

class Bus {
    String busNumber;
    int totalSeats;
    String[] passengers;
    boolean[] seatStatus;

    Bus(String busNumber, int totalSeats) {
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.passengers = new String[totalSeats];
        this.seatStatus = new boolean[totalSeats];
    }
}

public class AdvancedBusReservation {
    static Scanner sc = new Scanner(System.in);
    static List<Bus> buses = new ArrayList<>();
    static final String FILE_NAME = "busBookings.txt";

    // Load data from file
    static void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            Bus bus = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Bus:")) {
                    String[] parts = line.split(" ");
                    bus = new Bus(parts[1], Integer.parseInt(parts[2]));
                    buses.add(bus);
                } else if (line.startsWith("Seat:") && bus != null) {
                    String[] parts = line.split(" ");
                    int seat = Integer.parseInt(parts[1]);
                    String name = parts[2];
                    bus.passengers[seat] = name.equals("null") ? null : name;
                    bus.seatStatus[seat] = name.equals("null") ? false : true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    // Save data to file
    static void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Bus bus : buses) {
                bw.write("Bus:" + bus.busNumber + " " + bus.totalSeats);
                bw.newLine();
                for (int i = 0; i < bus.totalSeats; i++) {
                    bw.write("Seat:" + i + " " + (bus.passengers[i] == null ? "null" : bus.passengers[i]));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    // Login method
    static boolean login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        if(username.equals("admin") && password.equals("1234")) {
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Invalid credentials!");
            return false;
        }
    }

    // Display buses
    static void displayBuses() {
        System.out.println("\nAvailable Buses:");
        for (int i = 0; i < buses.size(); i++) {
            System.out.println((i+1) + ". " + buses.get(i).busNumber + " (Seats: " + buses.get(i).totalSeats + ")");
        }
    }

    // Add new bus
    static void addBus() {
        System.out.print("Enter bus number: ");
        String busNum = sc.nextLine();
        System.out.print("Enter total seats: ");
        int seats = sc.nextInt();
        sc.nextLine();
        buses.add(new Bus(busNum, seats));
        System.out.println("Bus added successfully!");
    }

    // Book tickets
    static void bookTickets() {
        displayBuses();
        System.out.print("Select bus number to book: ");
        int choice = sc.nextInt() - 1;
        sc.nextLine();
        if(choice < 0 || choice >= buses.size()) {
            System.out.println("Invalid bus choice!");
            return;
        }

        Bus bus = buses.get(choice);

        System.out.print("Enter number of tickets to book: ");
        int tickets = sc.nextInt();
        sc.nextLine();

        for(int i = 0; i < tickets; i++) {
            System.out.print("Enter seat number (1-" + bus.totalSeats + "): ");
            int seat = sc.nextInt() - 1;
            sc.nextLine();
            if(seat < 0 || seat >= bus.totalSeats) {
                System.out.println("Invalid seat number!");
                i--;
            } else if(bus.seatStatus[seat]) {
                System.out.println("Seat already booked!");
                i--;
            } else {
                System.out.print("Enter passenger name: ");
                String name = sc.nextLine();
                bus.passengers[seat] = name;
                bus.seatStatus[seat] = true;
                System.out.println("Ticket booked for " + name + " on seat " + (seat+1));
            }
        }
        saveData();
    }

    // Cancel tickets
    static void cancelTicket() {
        displayBuses();
        System.out.print("Select bus number to cancel ticket: ");
        int choice = sc.nextInt() - 1;
        sc.nextLine();
        if(choice < 0 || choice >= buses.size()) {
            System.out.println("Invalid bus choice!");
            return;
        }

        Bus bus = buses.get(choice);

        System.out.print("Enter seat number to cancel: ");
        int seat = sc.nextInt() - 1;
        sc.nextLine();
        if(seat < 0 || seat >= bus.totalSeats || !bus.seatStatus[seat]) {
            System.out.println("Seat is already empty!");
        } else {
            System.out.println("Ticket for " + bus.passengers[seat] + " canceled.");
            bus.passengers[seat] = null;
            bus.seatStatus[seat] = false;
            saveData();
        }
    }

    // Display bus status
    static void displayBusStatus() {
        displayBuses();
        System.out.print("Select bus number to view status: ");
        int choice = sc.nextInt() - 1;
        sc.nextLine();
        if(choice < 0 || choice >= buses.size()) {
            System.out.println("Invalid bus choice!");
            return;
        }

        Bus bus = buses.get(choice);
        System.out.println("\nSeat Status for " + bus.busNumber + ":");
        for(int i = 0; i < bus.totalSeats; i++) {
            if(bus.seatStatus[i]) {
                System.out.println("Seat " + (i+1) + ": Booked by " + bus.passengers[i]);
            } else {
                System.out.println("Seat " + (i+1) + ": Available");
            }
        }
    }

    public static void main(String[] args) {
        loadData();
        if(!login()) return;

        while(true) {
            System.out.println("\n--- Advanced Bus Reservation System ---");
            System.out.println("1. Add Bus");
            System.out.println("2. Book Tickets");
            System.out.println("3. Cancel Ticket");
            System.out.println("4. View Bus Status");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch(choice) {
                case 1: addBus(); break;
                case 2: bookTickets(); break;
                case 3: cancelTicket(); break;
                case 4: displayBusStatus(); break;
                case 5: System.out.println("Exiting..."); saveData(); return;
                default: System.out.println("Invalid choice!");
            }
        }
    }
}
