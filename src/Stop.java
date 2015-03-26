import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Stop {
	
	Connection con = MySQLConnection.getInstance().getConnection();
	
	public Stop() {}
	
	public boolean insertStop(int stopNum, String stopName, String location) {
		try {
			PreparedStatement stmt = con.prepareStatement("INSERT INTO stop VALUES (?, ?, ?)");
			stmt.setInt(1, stopNum);
			stmt.setString(2, stopName);
			stmt.setString(3, location);
			stmt.executeUpdate();
			stmt.close();
			return true;
		}
		catch (SQLException ex) {
			return false;
		}
	}
	
	public boolean deleteStop(int stopNum) {
		try {
			Statement stmt = con.createStatement();
			int rows = stmt.executeUpdate("DELETE FROM stop WHERE stopNumber = " + stopNum);
			stmt.close();
			return (rows != 0) ? true : false;
		}
		catch (SQLException ex) {
			return false;
		}
	}
	
	public ResultTableModel displayStops() {
		
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM stop");
			ResultTableModel rtm = new ResultTableModel(rs);
			stmt.close();
			return rtm;
		}
		catch (SQLException ex) {
			//TODO
			return null;
		}
	}
	
	public ResultTableModel searchStops(String stopName) {
		
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM stop WHERE stopName LIKE '%" + stopName + "%'");
			ResultTableModel rtm = new ResultTableModel(rs);
			stmt.close();
			return rtm;
		}
		catch (SQLException ex) {
			// TODO
			return null;
		}
	}
	
public ResultTableModel searchStops(int sid) {
		
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM stop WHERE stopNumber = " + sid);
			ResultTableModel rtm = new ResultTableModel(rs);
			stmt.close();
			return rtm;
		}
		catch (SQLException ex) {
			// TODO
			return null;
		}
	}
	
	public ResultTableModel findAllRoutes(int stopNum) {
		
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT r.routeNumber, routeName FROM has h NATURAL JOIN route r WHERE h.stopNumber = " + stopNum);
			ResultTableModel rtm = new ResultTableModel(rs);
			stmt.close();
			return rtm;
		}
		catch (SQLException ex) {
			// TODO 
			return null;
		}
	}
}
