package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import controller.DataSource;
import controller.GalaxyFactory;
import pattern.Subject;

public class GalaxyRepository extends Repository {
	
	private GalaxyNameAdapter nameSubject;
	private GalaxyAdapter galaxySubject;
	public GalaxyRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		nameSubject = new GalaxyNameAdapter();
		galaxySubject = new GalaxyAdapter();
	}
	
	public Subject<List<String[]>> getNameSubject() { return this.nameSubject; }
	public Subject<Galaxy> getGalaxySubject() { return this.galaxySubject; }
	
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
	
	public void retrieveGalaxyNames(String partial) throws Exception {
		Connection connection = dataSource.getConnection();
		
		String query = "SELECT G.name, AN.alter_name "
					 + "FROM galaxy G LEFT JOIN alternative_names AN ON (G.name LIKE AN.name) "
					 + "WHERE G.name LIKE ? OR alter_name LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, "%" + partial + "%");
		statement.setString(2, "%" + partial + "%");
		
		ResultSet set = statement.executeQuery();
		List<String[]> results = new ArrayList<>();
		while (set.next()) {
			results.add(new String[] { set.getString(1), set.getString(2) });
		}
		
		release(connection, statement, set);
		
		nameSubject.setState(results);
	}
	
	public void retrieveGalaxyByName(String name, boolean force) throws Exception {
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
			
			FluxRepository.instance(dataSource).retrieveGalaxyFluxes(galaxy);
		}
		else connection.commit();
		
		release(connection, statement, set);
		
		galaxySubject.setState(galaxy);
	}
	
	class GalaxyNameAdapter extends Subject<List<String[]>> {
		
		List<String[]> results;
		
		protected GalaxyNameAdapter() {
			results = new ArrayList<>();
		}
		
		protected void setState(List<String[]> results) {
			this.results.clear();
			this.results.addAll(results);
			notifyObservers();
		}

		@Override
		public List<String[]> retrieveState() {
			return new ArrayList<>(results);
		}
	}
	
	class GalaxyAdapter extends Subject<Galaxy> {
		
		Galaxy result;
		
		protected void setState(Galaxy result) {
			this.result = result;
			notifyObservers();
		}

		@Override
		public Galaxy retrieveState() {
			return result;
		}
	}
}
