import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class POSSystem extends JFrame {
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;
    private JPanel categoryPanel;
    private JPanel menuPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JLabel logoLabel;
    private JButton returnButton;
    private JLabel totalLabel;

    private Map<String, Integer> cart = new HashMap<>();
    private double totalAmount = 0.0;

    public POSSystem() {
        setTitle("POS System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Panel - Orders List + Logo + Return Button
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 600));
        leftPanel.setBackground(Color.RED);
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 8)); // Thicker red border

        // Logo
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\Aki\\Downloads\\ChessGame-master\\ROMS\\src\\image\\Logo.png"); // Replace with your logo path
        Image img = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize image to 200x100 pixels
        logoIcon = new ImageIcon(img);

        logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        leftPanel.add(logoLabel);


        // Order List
        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        leftPanel.add(new JScrollPane(orderList));

        // Total Price Label
        totalLabel = new JLabel("Total: ₱" + totalAmount);
        totalLabel.setBackground(Color.white);
        totalLabel.setForeground(Color.white);

// Set a bigger font size
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));  // Change 18 to the size you want

        leftPanel.add(totalLabel);


        // Discount & Place Order Panel
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new GridLayout(3, 1, 3, 3));
        orderPanel.setPreferredSize(new Dimension(50, 10));

        JButton discountButton = createButton("Apply Discount");
        discountButton.setPreferredSize(new Dimension(200, 50));
        discountButton.addActionListener(e -> {
            String discountInput = JOptionPane.showInputDialog(this, "Enter discount percentage:");
            try {
                double discountPercent = Double.parseDouble(discountInput);
                if (discountPercent < 0 || discountPercent > 100) {
                    JOptionPane.showMessageDialog(this, "Invalid discount percentage!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                totalAmount -= totalAmount * (discountPercent / 100);
                totalLabel.setText("Total: ₱" + totalAmount);
                JOptionPane.showMessageDialog(this, "Discount applied! New Total: ₱" + totalAmount);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton placeOrderButton = createButton("Place Order");
        placeOrderButton.setPreferredSize(new Dimension(200, 50));
        placeOrderButton.addActionListener(e -> saveOrderToDatabase());
        // Return Button
        returnButton = createButton("Return");
        returnButton.setPreferredSize(new Dimension(200, 50));
        returnButton.addActionListener(e -> {
            new MainUI().setVisible(true);
            this.dispose();
        });

        orderPanel.add(discountButton);
        orderPanel.add(placeOrderButton);
        orderPanel.add(returnButton);
        leftPanel.add(orderPanel);

        add(leftPanel, BorderLayout.WEST);

        // Right Panel - Menu Items with Red Border
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 15)); // Thicker red border

        // Category Panel
        categoryPanel = new JPanel();
        categoryPanel.setLayout(new GridLayout(1, 4, 10, 10));

        JButton chickenButton = createButton("Chicken");
        JButton pastaButton = createButton("Pasta");
        JButton addonsButton = createButton("Add-ons");
        JButton drinksButton = createButton("Drinks");

        categoryPanel.add(chickenButton);
        categoryPanel.add(pastaButton);
        categoryPanel.add(addonsButton);
        categoryPanel.add(drinksButton);

        rightPanel.add(categoryPanel, BorderLayout.NORTH);

        // Menu Panel (Dynamically updated)
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 2, 10, 10));
        rightPanel.add(new JScrollPane(menuPanel), BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

        // Load default menu
        loadMenuItems("Chicken");

        // Add category button listeners
        chickenButton.addActionListener(e -> loadMenuItems("Chicken"));
        pastaButton.addActionListener(e -> loadMenuItems("Pasta"));
        addonsButton.addActionListener(e -> loadMenuItems("Add-ons"));
        drinksButton.addActionListener(e -> loadMenuItems("Drinks"));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 50)); // Smaller button size (150x75)
        button.setBackground(new Color(248, 207, 45)); // #f8cf2d
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    private void loadMenuItems(String category) {
        menuPanel.removeAll();
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT item_id, name, price FROM Menu_Items WHERE category = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("name");
                double price = rs.getDouble("price");

                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                // Load Image and Resize

                ImageIcon itemImage = new ImageIcon("C:\\Users\\Aki\\Downloads\\ChessGame-master\\ROMS\\src\\image\\CB" + itemId + ".png"); // Adjust for image naming convention
                Image img = itemImage.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Resize image
                itemImage = new ImageIcon(img);
                JLabel imageLabel = new JLabel(itemImage);
                itemPanel.add(imageLabel, BorderLayout.CENTER);

                // Item Button
                JButton itemButton = createButton(itemName + " - ₱" + price);
                itemButton.addActionListener(new AddToOrderListener(itemId, itemName, price));
                itemPanel.add(itemButton, BorderLayout.SOUTH);

                menuPanel.add(itemPanel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        menuPanel.revalidate();
        menuPanel.repaint();
    }


    private class AddToOrderListener implements ActionListener {
        private int itemId;
        private String itemName;
        private double price;

        public AddToOrderListener(int itemId, String itemName, double price) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.price = price;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cart.put(itemName, cart.getOrDefault(itemName, 0) + 1);
            totalAmount += price;
            totalLabel.setText("Total: ₱" + totalAmount);

            orderListModel.addElement(itemName + " - ₱" + price);
        }
    }

    private void saveOrderToDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Insert Order
            String orderQuery = "INSERT INTO Orders (total_amount, status) VALUES (?, 'Pending')";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStmt.setDouble(1, totalAmount);
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);

                // Insert Order Items
                for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                    int itemId = getItemIdByName(conn, entry.getKey());
                    if (itemId != -1) {
                        String itemQuery = "INSERT INTO Order_Items (order_id, item_id, quantity, total_price) VALUES (?, ?, ?, ?)";
                        PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, itemId);
                        itemStmt.setInt(3, entry.getValue());
                        itemStmt.setDouble(4, entry.getValue() * getItemPriceById(conn, itemId));
                        itemStmt.executeUpdate();
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Order placed successfully!");

            // Reset order
            cart.clear();
            orderListModel.clear();
            totalAmount = 0.0;
            totalLabel.setText("Total: ₱" + totalAmount);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getItemIdByName(Connection conn, String itemName) throws SQLException {
        String query = "SELECT item_id FROM Menu_Items WHERE name = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, itemName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("item_id");
        }
        return -1;
    }

    private double getItemPriceById(Connection conn, int itemId) throws SQLException {
        String query = "SELECT price FROM Menu_Items WHERE item_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, itemId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("price");
        }
        return 0.0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new POSSystem().setVisible(true));
    }
}
