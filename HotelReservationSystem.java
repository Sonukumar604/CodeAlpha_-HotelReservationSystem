import java.io.*;
import java.util.*;

enum RoomType { STANDARD, DELUXE, SUITE }

class Room implements Serializable {
    int roomNumber;
    RoomType type;
    boolean isAvailable = true;

    Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ") - " + (isAvailable ? "Available" : "Booked");
    }
}

class Reservation implements Serializable {
    String guestName;
    int roomNumber;
    RoomType type;
    Date date;
    boolean paid;

    Reservation(String guestName, int roomNumber, RoomType type, boolean paid) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.type = type;
        this.date = new Date();
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Reservation for " + guestName + ": Room " + roomNumber + " (" + type + ") on " + date + " | Paid: " + paid;
    }
}

class Hotel implements Serializable {
    List<Room> rooms = new ArrayList<>();
    List<Reservation> reservations = new ArrayList<>();
    static final String DATA_FILE = "hotel_data.ser";

    Hotel() {
        // Initialize rooms if empty
        if (rooms.isEmpty()) {
            for (int i = 1; i <= 5; i++) rooms.add(new Room(i, RoomType.STANDARD));
            for (int i = 6; i <= 8; i++) rooms.add(new Room(i, RoomType.DELUXE));
            for (int i = 9; i <= 10; i++) rooms.add(new Room(i, RoomType.SUITE));
        }
    }

    void showAvailableRooms(RoomType type) {
        System.out.println("\nAvailable " + type + " rooms:");
        for (Room room : rooms) {
            if (room.type == type && room.isAvailable) {
                System.out.println(room);
            }
        }
    }

    Room findAvailableRoom(RoomType type) {
        for (Room room : rooms) {
            if (room.type == type && room.isAvailable) return room;
        }
        return null;
    }

    void bookRoom(String guestName, RoomType type) {
        Room room = findAvailableRoom(type);
        if (room == null) {
            System.out.println("No available rooms of type " + type);
            return;
        }
        // Simulate payment
        System.out.print("Simulating payment... ");
        boolean paid = true; // Always successful for simulation
        System.out.println("Payment successful!");

        room.isAvailable = false;
        Reservation res = new Reservation(guestName, room.roomNumber, type, paid);
        reservations.add(res);
        System.out.println("Booking successful! " + res);
    }

    void cancelReservation(String guestName, int roomNumber) {
        Iterator<Reservation> it = reservations.iterator();
        boolean found = false;
        while (it.hasNext()) {
            Reservation res = it.next();
            if (res.guestName.equalsIgnoreCase(guestName) && res.roomNumber == roomNumber) {
                it.remove();
                for (Room room : rooms) {
                    if (room.roomNumber == roomNumber) room.isAvailable = true;
                }
                System.out.println("Reservation cancelled for " + guestName + " in room " + roomNumber);
                found = true;
                break;
            }
        }
        if (!found) System.out.println("No reservation found for " + guestName + " in room " + roomNumber);
    }

    void showReservations() {
        System.out.println("\n--- All Reservations ---");
        for (Reservation res : reservations) {
            System.out.println(res);
        }
    }

    void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error saving hotel data: " + e.getMessage());
        }
    }

    static Hotel loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                return (Hotel) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading hotel data: " + e.getMessage());
            }
        }
        return new Hotel();
    }
}

public class HotelReservationSystem {
    public static void main(String[] args) {
        Hotel hotel = Hotel.loadData();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Search Rooms\n2. Book Room\n3. Cancel Reservation\n4. View Reservations\n5. Save & Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter room type (STANDARD, DELUXE, SUITE): ");
                RoomType type = RoomType.valueOf(sc.nextLine().trim().toUpperCase());
                hotel.showAvailableRooms(type);
            } else if (choice == 2) {
                System.out.print("Enter your name: ");
                String name = sc.nextLine();
                System.out.print("Enter room type (STANDARD, DELUXE, SUITE): ");
                RoomType type = RoomType.valueOf(sc.nextLine().trim().toUpperCase());
                hotel.bookRoom(name, type);
            } else if (choice == 3) {
                System.out.print("Enter your name: ");
                String name = sc.nextLine();
                System.out.print("Enter room number to cancel: ");
                int roomNum = sc.nextInt();
                sc.nextLine();
                hotel.cancelReservation(name, roomNum);
            } else if (choice == 4) {
                hotel.showReservations();
            } else if (choice == 5) {
                hotel.saveData();
                System.out.println("Data saved. Exiting.");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
        sc.close();
    }
}