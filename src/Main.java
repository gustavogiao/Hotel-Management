import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String ROOMS_CSV_FILE = "dados.csv";
    private static final String BOOKINGS_CSV_FILE = "dadosReservas.csv";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowLoginForm();
            }
        });
    }

    private static void createAndShowLoginForm() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(350, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel titleLabel = new JLabel("BookingSystem");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();


        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Verificar as credenciais do usuário aqui
                // Se as credenciais estiverem corretas, criar a janela principal (Dashboard)
                // Caso contrário, exibir mensagem de erro
                if (username.equals("admin") && password.equals("admin")) {
                    loginFrame.dispose(); // Fecha o formulário de login

                    List<Room> availableRooms = readRoomsFromCSV(ROOMS_CSV_FILE);
                    List<Booking> bookings = readBookingsFromCSV(BOOKINGS_CSV_FILE);

                    // Criar a janela com a lista de quartos disponíveis e de reservas
                    createAndShowMainDashboard(availableRooms, bookings);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Credenciais inválidas", "Erro de login", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Adicionar uma tecla de atalho para o botão de login quando "Enter" é pressionado
        loginFrame.getRootPane().setDefaultButton(loginButton);

        // Adicionar os componentes ao painel de formulário
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

    private static List<Room> readRoomsFromCSV(String filename) {
        List<Room> rooms = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filename));
            // Ignorar a primeira linha, que contém os cabeçalhos das colunas
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                int roomNumber = Integer.parseInt(parts[1]);
                int adultsCapacity = Integer.parseInt(parts[2]);
                int childrenCapacity = Integer.parseInt(parts[3]);
                float price = Float.parseFloat(parts[4]);
                rooms.add(new Room(id, roomNumber, adultsCapacity, childrenCapacity, price));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    private static List<Booking> readBookingsFromCSV(String filename) {
        List<Booking> bookings = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato de data esperado no CSV

        try {
            Scanner scanner = new Scanner(new File(filename));
            // Ignorar a primeira linha, que contém os cabeçalhos das colunas
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                String guestFirstName = parts[1];
                String guestLastName = parts[2];
                Date checkInDate = dateFormat.parse(parts[3]); // Parse da data de check-in
                Date checkOutDate = dateFormat.parse(parts[4]); // Parse da data de check-out
                int numberOfAdults = Integer.parseInt(parts[5]);
                int numberOfChildren = Integer.parseInt(parts[6]);
                int roomId = Integer.parseInt(parts[7]);
                int statusId = Integer.parseInt(parts[8]);
                bookings.add(new Booking(guestFirstName, guestLastName, checkInDate, checkOutDate, numberOfAdults, numberOfChildren, roomId, statusId));
            }
            scanner.close();
        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    private static void createAndShowMainDashboard(List<Room> availableRooms, List<Booking> bookings) {
        Janela janela = new Janela(availableRooms, bookings);
        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApplication(availableRooms, bookings);
            }
        });
        janela.setVisible(true);
    }

    private static void closeApplication(List<Room> rooms, List<Booking> bookings) {
        writeRoomsToCSV(rooms, ROOMS_CSV_FILE);
        writeBookingsToCSV(bookings, BOOKINGS_CSV_FILE);
        System.exit(0);
    }

    private static void writeRoomsToCSV(List<Room> rooms, String filename) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("id;roomNumber;adultsCapacity;childrenCapacity;price");
            for (Room room : rooms) {
                writer.println(room.getId() + ";" + room.getRoomNumber() + ";" + room.getAdultsCapacity() + ";" + room.getChildrenCapacity() + ";" + room.getPrice());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void writeBookingsToCSV(List<Booking> bookings, String filename) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato de data esperado no CSV

        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("id;guestFirstName;guestLastName;checkInDate;checkOutDate;numberOfAdults;numberOfChildren;roomId;statusId");
            for (Booking booking : bookings) {
                writer.println(booking.getId() + ";" + booking.getGuestFirstName() + ";" + booking.getGuestLastName() + ";" +
                        dateFormat.format(booking.getCheckInDate()) + ";" + dateFormat.format(booking.getCheckOutDate()) + ";" +
                        booking.getNumberOfAdults() + ";" + booking.getNumberOfChildren() + ";" +
                        booking.getRoomId() + ";" + booking.getStatusId());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
