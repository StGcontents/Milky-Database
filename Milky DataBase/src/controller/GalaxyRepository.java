package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Galaxy;

public class GalaxyRepository extends Repository {
	
	private static GalaxyRepository me;
	protected GalaxyRepository() {
		this.dataSource = new DataSource();
	}
	
	public static synchronized GalaxyRepository instance() {
		if (me == null) me = new GalaxyRepository();
		return me;
	}
	
	public void persist(Galaxy galaxy) throws Exception {
		
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		List<AutoCloseable> releaseQueue = new ArrayList<>();
		
		String query = "SELECT count(*) FROM galaxy WHERE name LIKE ?";
		PreparedStatement qStatement = connection.prepareStatement(query, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		releaseQueue.add(qStatement);
		qStatement.setString(1, galaxy.getName());
		ResultSet set = qStatement.executeQuery();
		if (!set.next()) throw new Exception();
		boolean result = set.getInt(1) == 0;
		
		if (result) {
			String insert = "INSERT INTO galaxy(name, hours, minutes, seconds, "
					+ "sign, degress, arcmin, arcsec, redshift, distance, spectre) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setString(1, galaxy.getName());
			statement.setInt(2, galaxy.getCoordinates().getRightAscensionHours());
			statement.setInt(3, galaxy.getCoordinates().getRightAscensionMinutes());
			statement.setDouble(4, galaxy.getCoordinates().getRightAscensionSeconds());
			statement.setString(5, galaxy.getCoordinates().getSign() ? "+" : "-");
			statement.setInt(6, galaxy.getCoordinates().getDegrees());
			statement.setInt(7, galaxy.getCoordinates().getArcMinutes());
			statement.setDouble(8, galaxy.getCoordinates().getArcSeconds());
			//etc
			statement.executeUpdate();
			releaseQueue.add(statement);
			
			for (String alterName : galaxy.getAlternativeNames()) {
				String alterInsert = "INSERT INTO alternative_names (name, alter_name) VALUES (?, ?)";
				PreparedStatement alterStatement = connection.prepareStatement(alterInsert);
				releaseQueue.add(alterStatement);
				alterStatement.setString(1, galaxy.getName());
				alterStatement.setString(2, alterName);
				alterStatement.executeUpdate();
			}
		}
		
		connection.commit();
		release((AutoCloseable[]) releaseQueue.toArray());
		release(connection);
	}
	
	public void delete(Galaxy galaxy) throws Exception {
		Connection connection = dataSource.getConnection();
		String delete = "DELETE FROM galaxy WHERE name LIKE ?";
		PreparedStatement statement = connection.prepareStatement(delete);
		statement.setString(1, galaxy.getName());
		statement.executeUpdate();
		
		release(connection, statement);
	}
	
	public Galaxy retrieveGalaxy(String name, boolean force) throws Exception {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		
		String query = "SELECT * FROM galaxy WHERE name LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, name);
		
		ResultSet set = statement.executeQuery();
		Galaxy galaxy = GalaxyFactory.instance().create(set).get(0);
		
		if (force) {
			String alterQuery = "SELECT alter_name FROM alternative_names WHERE name LIKE ?";
			PreparedStatement alterStatement = connection.prepareStatement(alterQuery, ResultSet.CLOSE_CURSORS_AT_COMMIT);
			ResultSet alterSet = alterStatement.executeQuery();
			String[] names = new String[alterSet.getFetchSize()];
			int i = 0;
			while (alterSet.next()) {
				names[i] = alterSet.getString(1);
				++i;
			}
			galaxy.setAlternativeNames(names);
			
			connection.commit();
			release(alterStatement);
			
			FluxRepository.instance().retrieveGalaxyFluxes(galaxy);
		}
		
		release(connection, statement, set);
		
		return galaxy;
	}
}
