import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom table model for the Metropolises database that extends AbstractTableModel.
 * This class provides the interface between the JTable GUI component and the MySQL database.
 */
public class MetropolisTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {"Metropolis", "Continent", "Population"};

    private final List<String[]> tableData;

    private static final String DB_URL = "jdbc:mysql://192.168.1.4:3306/my_database";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1111"; // Remember to remove before submission

    /**
     * Constructs a MetropolisTableModel with empty data.
     */
    public MetropolisTableModel() {
        tableData = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return tableData.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        String[] rowData = tableData.get(row);
        return switch (column) {
            case 0, 1 -> rowData[column];
            case 2 -> Long.parseLong(rowData[column]);
            default -> null;
        };
    }

    public void search(String metropolis, String continent, String population,
                       String populationOption, String matchOption) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM metropolises WHERE 1=1");

            if (!metropolis.isEmpty()) {
                if (matchOption.equals("Exact Match")) {
                    queryBuilder.append(" AND metropolis = ?");
                } else {
                    queryBuilder.append(" AND metropolis LIKE ?");
                    metropolis = "%" + metropolis + "%";
                }
            }

            if (!continent.isEmpty()) {
                if (matchOption.equals("Exact Match")) {
                    queryBuilder.append(" AND continent = ?");
                } else {
                    queryBuilder.append(" AND continent LIKE ?");
                    continent = "%" + continent + "%";
                }
            }

            if (!population.isEmpty()) {
                if (populationOption.equals("Greater Than")) {
                    queryBuilder.append(" AND population > ?");
                } else {
                    queryBuilder.append(" AND population < ?");
                }
            }

            stmt = conn.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            if (!metropolis.isEmpty()) {
                stmt.setString(paramIndex++, metropolis);
            }
            if (!continent.isEmpty()) {
                stmt.setString(paramIndex++, continent);
            }
            if (!population.isEmpty()) {
                stmt.setLong(paramIndex, Long.parseLong(population));
            }

            rs = stmt.executeQuery();

            tableData.clear();
            while (rs.next()) {
                String[] rowData = new String[3];
                rowData[0] = rs.getString("metropolis");
                rowData[1] = rs.getString("continent");
                rowData[2] = String.valueOf(rs.getLong("population"));
                tableData.add(rowData);
            }

            fireTableDataChanged();

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    public void add(String metropolis, String continent, String population) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "INSERT INTO metropolises (metropolis, continent, population) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(query);

            stmt.setString(1, metropolis);
            stmt.setString(2, continent);
            stmt.setLong(3, Long.parseLong(population));

            System.out.println("Executing query: " + query);
            stmt.executeUpdate();
            System.out.println("Insert successful!");

            tableData.clear();
            String[] rowData = new String[3];
            rowData[0] = metropolis;
            rowData[1] = continent;
            rowData[2] = population;
            tableData.add(rowData);

            fireTableDataChanged();

        } catch (SQLException e) {
            System.err.println("Database error during add: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }
}