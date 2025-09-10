import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NeedHelpUI extends JFrame {
    public NeedHelpUI() {
        setTitle("Need Help?");
        setSize(820, 600); // Set the size of the Help window to 800x600
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a panel for the Need Help content
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(238, 14, 14)); // Light background color for the panel

        // Title Label
        JLabel titleLabel = new JLabel("Need Help?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(248, 207, 45)); // #f8cf2d
        titleLabel.setBounds(300, 20, 200, 50);
        panel.add(titleLabel);

        // JTextArea for the content
        JTextArea aboutText = new JTextArea();
        aboutText.setText("The Kitchen view displays all current orders that need to be prepared. As an employee in the kitchen:\n\n" +
                "View Orders: See the details of orders (items, quantities, customer).\n" +
                "Mark Orders as Done: Once an order is prepared, click the 'Done' button to mark it as completed. The order will be removed from the list.\n\n" +
                "This system is used to take orders from customers and process payments. Here's how to use it:\n\n" +
                "Select Menu Items: Browse through categories like Chicken, Pasta, Add-ons, and Drinks. Click on the items to add them to the order.\n" +
                "Update Order: Add items as requested by the customer and adjust quantities.\n" +
                "Confirm Order: Review the order details, then click the 'Confirm' button to complete the transaction. The order will be added to the system, and a receipt will be printed (if applicable).\n" +
                "Payment: Accept payments based on the total price. You may have the option to add discounts or special charges.");
        aboutText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        aboutText.setBounds(50, 100, 710, 350); // Set the position and size of the text area
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setEditable(false); // Make the text area non-editable
        panel.add(aboutText);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setBounds(350, 480, 100, 40); // Set position and size
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBackground(new Color(248, 207, 45)); // #f8cf2d
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and display the MainUI (Home page)
                MainUI homeFrame = new MainUI(); // Assuming no userId is required here, adjust accordingly
                homeFrame.setVisible(true);
                dispose(); // Close the Need Help frame
            }
        });
        panel.add(backButton);

        // Add the panel to the Need Help window
        add(panel);

        setVisible(true); // Show the Need Help frame
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NeedHelpUI().setVisible(true));
    }
}
