import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MetropolisViewer extends JFrame {
    private final JTextField metropolisField;
    private final JTextField continentField;
    private final JTextField populationField;
    private final JComboBox<String> populationCombo;
    private final JComboBox<String> matchTypeCombo;
    private final JButton searchButton;
    private final JButton addButton;
    private final JTable resultTable;
    private final MetropolisTableModel tableModel;

    public MetropolisViewer() {
        super("Metropolis Viewer");

        tableModel = new MetropolisTableModel();
        resultTable = new JTable(tableModel);

        metropolisField = new JTextField(10);
        continentField = new JTextField(10);
        populationField = new JTextField(10);

        populationCombo = new JComboBox<>(new String[]{"Greater Than", "Less Than"});
        matchTypeCombo = new JComboBox<>(new String[]{"Exact Match", "Partial Match"});

        searchButton = new JButton("Search");
        addButton = new JButton("Add");

        searchButton.addActionListener(e -> performSearch());

        addButton.addActionListener(e -> performAdd());

        setupLayout();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void performSearch() {
        String metropolis = metropolisField.getText().trim();
        String continent = continentField.getText().trim();
        String population = populationField.getText().trim();
        String populationOption = (String) populationCombo.getSelectedItem();
        String matchOption = (String) matchTypeCombo.getSelectedItem();

        tableModel.search(metropolis, continent, population, populationOption, matchOption);
    }

    private void performAdd() {
        String metropolis = metropolisField.getText().trim();
        String continent = continentField.getText().trim();
        String population = populationField.getText().trim();

        if (metropolis.isEmpty() || continent.isEmpty() || population.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter values for all fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long.parseLong(population);
            tableModel.add(metropolis, continent, population);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Population must be a valid number",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }

            new MetropolisViewer();
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("Search/Add"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Metropolis:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(metropolisField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Continent:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(continentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Population:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(populationField, gbc);

        // Add combo boxes
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Population:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(populationCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Match Type:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(matchTypeCombo, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(searchButton);
        buttonPanel.add(addButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(new TitledBorder("Results"));
        scrollPane.setPreferredSize(new Dimension(500, 300));
        add(scrollPane, BorderLayout.CENTER);
    }
}