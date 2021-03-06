/*
 * GNU/GPL v3. Check out https://github.com/billwi/CyberTigerScoreboard for more info
 */
package cpscorereport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author 054457
 */
public class AzureServerHelper extends ServerHelper {

    @Override
    public void startServer(String dbUrl, String dbName, String username, String password, CPscorereport scorer) throws SQLException {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // Set the class to connect
            connUrl = "jdbc:sqlserver://" + dbUrl + ";database=" + dbName + ";user=" + username + "@ctsb;password=" + password + ";encrypt=true;trustServerCertificate=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            DriverManager.getConnection(connUrl); // Get the URL to connect
            myStatus = true; // Now that it's started...
            scorer.refreshData(); // Refresh data
        } catch (ClassNotFoundException | SQLException | IOException ex) { // Something happened
            String error = "An error has occured:\n" + ex.getMessage(); // Show them error stuff
            error += "\nIt is possible that your IP is blacklisted!";
            try {
                URL whatismyip = new URL("http://checkip.amazonaws.com"); // Get the IP
                BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                String ip = in.readLine(); // Get the IP from the amazon URL
                error += "\nYou are trying to connect from " + ip;
                error += "\nYour username is " + username + ", and you are trying to connect to " + dbName; // Tell them some more info
            } catch (IOException ex1) { // We couldn't get an internet connection
                error = "There was an error generating the error! Please check your internet connection:\n" + ex1; // Tell them we couldnt generate the error
            }
            myStatus = false; // Since we're catching it, something didn't run, so stop it
            throw new SQLException(error); // Throw the error
        }
    }

    @Override
    public IDatabaseConnection newDbConn() {
        return new AzureDatabaseConnection();
    }
}
