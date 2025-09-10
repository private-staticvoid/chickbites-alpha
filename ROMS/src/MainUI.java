import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    public MainUI() {
        setTitle("Restaurant Order Management System (ROMS)");
        setSize(1000, 600); // Set the size to 1000x600
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background color of the main UI to red
        getContentPane().setBackground(Color.RED);

        // Logo
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\Aki\\Downloads\\ChessGame-master\\ROMS\\src\\image\\Logo.png"); // Replace with your logo path
        Image img = logoIcon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH); // Resize image to 150x150 pixels
        logoIcon = new ImageIcon(img);

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        add(logoLabel, BorderLayout.NORTH);

        // Panel to hold buttons with FlowLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 50)); // Use FlowLayout with centered buttons and spacing
        buttonPanel.setBackground(Color.RED);

        // Buttons
        JButton kitchenButton = createButton("Kitchen");
        JButton counterButton = createButton("Counter");
        JButton aboutUsButton = createButton("Need Help?");

        // Action listeners for buttons
        kitchenButton.addActionListener(e -> {
            new KitchenView().setVisible(true);
            this.dispose();
        });

        counterButton.addActionListener(e -> {
            new POSSystem().setVisible(true);
            this.dispose();
        });

        // Action listener for Need Help? button
        aboutUsButton.addActionListener(e -> new NeedHelpUI());

        buttonPanel.add(kitchenButton);
        buttonPanel.add(counterButton);
        buttonPanel.add(aboutUsButton); // Add About Us button to the panel

        add(buttonPanel, BorderLayout.CENTER);
    }

    public MainUI(String userId) {
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(248, 207, 45)); // #f8cf2d
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(250, 150)); // Set the preferred size to 100x50
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainUI().setVisible(true));
    }
}
