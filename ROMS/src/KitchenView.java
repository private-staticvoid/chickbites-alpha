import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class KitchenView extends JFrame {
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;
    private JButton serveButton, deleteButton, backButton;

    public KitchenView() {
        // Set up window properties
        setTitle("Kitchen Orders");
        setSize(1000, 600);  // Set the window size to 1000x600
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add a red border around the JFrame
        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        borderPanel.setLayout(new BorderLayout());

        // Initialize components
        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        loadOrders();

        // Buttons Panel with new color
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(Color.red);  // Set background color to #f8cf2d

        serveButton = new JButton("Mark as Served");
        serveButton.setBackground(new Color(248, 207, 45)); // Red background for buttons
        serveButton.setForeground(Color.BLACK); // White text
        serveButton.setPreferredSize(new Dimension(200, 50));
        serveButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Red border
        serveButton.addActionListener(e -> markAsServed());

        deleteButton = new JButton("Delete Order");
        deleteButton.setBackground(new Color(248, 207, 45));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setPreferredSize(new Dimension(200, 50));
        deleteButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        deleteButton.addActionListener(e -> deleteOrder());

        backButton = new JButton("Back");
        backButton.setBackground(new Color(248, 207, 45));
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.setForeground(Color.BLACK);
        backButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        backButton.addActionListener(e -> {
            new MainUI().setVisible(true);
            this.dispose();
        });

        buttonPanel.add(serveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Add components to the panel with the border
        borderPanel.add(new JScrollPane(orderList), BorderLayout.CENTER);
        borderPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the border panel to the JFrame
        add(borderPanel);

        // Center the window on the screen
        setLocationRelativeTo(null);
    }

    private void loadOrders() {
        orderListModel.clear(); // Clear previous data before reloading

        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT o.order_id, o.total_amount, oi.quantity, mi.name, oi.total_price " +
                    "FROM Orders o " +
                    "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                    "JOIN Menu_Items mi ON oi.item_id = mi.item_id " +
                    "WHERE o.status = 'Pending'";

            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String itemName = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("total_price");
                double totalAmount = rs.getDouble("total_amount");

                // Format the order display
                orderListModel.addElement("Order " + orderId + " - Item: " + itemName + " x" + quantity +
                        " - Total: ₱" + totalPrice + " - Order Total: ₱" + totalAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders!", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAsServed() {
        int selectedIndex = orderList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedOrder = orderListModel.getElementAt(selectedIndex);
            int orderId = extractOrderId(selectedOrder);

            if (orderId != -1) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("UPDATE Orders SET status = 'Completed' WHERE order_id = ?");
                    stmt.setInt(1, orderId);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Order " + orderId + " marked as served!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOrders(); // Refresh the list after update
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating order!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteOrder() {
        int selectedIndex = orderList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedOrder = orderListModel.getElementAt(selectedIndex);
            int orderId = extractOrderId(selectedOrder);

            if (orderId != -1) {
                int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete order " + orderId + "?",
                        "Delete Order", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseManager.getConnection()) {
                        // Delete the order from the database
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Orders WHERE order_id = ?");
                        stmt.setInt(1, orderId);
                        int rowsAffected = stmt.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Order " + orderId + " has been deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadOrders(); // Refresh the list after deletion
                        } else {
                            JOptionPane.showMessageDialog(this, "Error deleting order!", "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error deleting order!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int extractOrderId(String orderString) {
        try {
            String[] parts = orderString.split(" ");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return an invalid ID if parsing fails
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KitchenView().setVisible(true));
    }
}
