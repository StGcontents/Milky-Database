package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import controller.DataSource;
import controller.GalaxyFactory;
import model.Galaxy.Coordinates;
import model.Galaxy.Luminosity;
import pattern.Subject;
import pattern.ViewSubject;

@SuppressWarnings("rawtypes")
public class GalaxyRepository extends Repository {
	
	private GalaxyNameSubjectAdapter nameSubject;
	private GalaxySubjectAdapter galaxySubject;
	public GalaxyRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		nameSubject = new GalaxyNameSubjectAdapter();
		galaxySubject = new GalaxySubjectAdapter();
	}
	
	public Subject<List<AdaptableValue>> getNameSubject() { return this.nameSubject; }
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
					+ "sign, degrees, arcmin, arcsec, redshift, distance, spectre, "
					+ "lum_nev_1, lum_nev_1_flag, lum_nev_2, lum_nev_2_flag, "
					+ "lum_oiv, lum_oiv_flag, metallicity, metallicity_err) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setString(1, galaxy.getName());
			
			statement.setInt(2, galaxy.getCoordinates().getRightAscensionHours());
			statement.setInt(3, galaxy.getCoordinates().getRightAscensionMinutes());
			statement.setDouble(4, galaxy.getCoordinates().getRightAscensionSeconds());
			statement.setInt(5, galaxy.getCoordinates().getSign() ? 1 : -1);
			statement.setInt(6, galaxy.getCoordinates().getDegrees());
			statement.setInt(7, galaxy.getCoordinates().getArcMinutes());
			statement.setDouble(8, galaxy.getCoordinates().getArcSeconds());
			
			statement.setDouble(9, galaxy.getRedShift());
			
			if (galaxy.getDistance() == null)
				statement.setNull(10, Types.REAL);
			else statement.setDouble(10, galaxy.getDistance());
			
			statement.setString(11, galaxy.getSpectre());
			
			for (int i = 0; i < 3; ++i) {
				Luminosity lum = galaxy.getLuminosities()[i];
				if (lum == null) {
					statement.setNull(12 + i * 2, Types.REAL);
					statement.setNull(13 + i * 2, Types.BOOLEAN);
				}
				else {
					statement.setDouble(12 + i * 2, lum.getValue());
					statement.setBoolean(13 + i * 2, lum.isLimit());
				}
			}
			
			if (galaxy.getMetallicity() == null) {
				statement.setNull(18, Types.REAL);
				statement.setNull(19, Types.REAL);
			}
			else {
				statement.setDouble(18, galaxy.getMetallicity());
				if (galaxy.getMetallicityError() == null)
					statement.setNull(19, Types.REAL);
				else statement.setDouble(19, galaxy.getMetallicityError());
			}
			
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
		for (AutoCloseable ac : releaseQueue)
			release(ac);
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
	
	public void retrieveGalaxyByName(String name, boolean force) throws Exception {
		Galaxy galaxy = GalaxyPool.getByName(name);
		if (galaxy != null && !galaxy.isExpired()) {
			galaxySubject.setState(galaxy);
			return;
		}
		
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		
		String query = "SELECT * FROM galaxy WHERE name LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, name);
		
		ResultSet set = statement.executeQuery();
		galaxy = GalaxyFactory.instance().create(set).get(0);
		
		String alterQuery = "SELECT alter_name FROM alternative_names WHERE name LIKE ?";
		PreparedStatement alterStatement = connection.prepareStatement(alterQuery, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		alterStatement.setString(1, name);
		ResultSet alterSet = alterStatement.executeQuery();
		List<String> names = new ArrayList<>();
		while (alterSet.next()) 
			names.add(alterSet.getString(1));
	
		galaxy.setAlternativeNames(names.toArray(new String[names.size()]));
		
		connection.commit();
		release(alterStatement, statement, set);
		
		if (force) 			
			new FluxRepository(dataSource).retrieveGalaxyFluxes(galaxy);
		
		release(connection);
		
		GalaxyPool.insert(galaxy);
		
		galaxySubject.setState(galaxy);
	}
	
	public void retrieveGalaxyNames(String partial) throws Exception {
		Connection connection = dataSource.getConnection();
		
		String query = "SELECT G.name, AN.alter_name "
					 + "FROM galaxy G LEFT JOIN alternative_names AN ON (G.name LIKE AN.name) "
					 + "WHERE G.name LIKE ? OR alter_name LIKE ? "
					 + "ORDER BY G.name, AN.alter_name";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, "%" + partial + "%");
		statement.setString(2, "%" + partial + "%");
		
		ResultSet set = statement.executeQuery();
		List<AdaptableValue> results = new ArrayList<>();
		String last = "", str0, str1;
		boolean match = false;
		while (set.next()) {
			str0 = set.getString(1);
			str1 = set.getString(2);
			if (last.equals(str0)) {
				if (!match || str1.contains(partial)) 
					results.add(AdaptableValue.getNameValue(str0, str1));
				else continue;
			}
			else {
				last = str1 == null ? str0 : str1;
				match = last.contains(partial);
				results.add(AdaptableValue.getNameValue(str0, str1 != null && str1.contains(partial) ? str1 : null));
			}
		}
		
		release(connection, statement, set);
		
		nameSubject.setState(results);
	}
	
	public void retrieveGalaxyInRange(Coordinates center, double range, int limit) throws Exception {
		Connection connection = dataSource.getConnection();
		
		double ra2 = 15 * (center.getRightAscensionHours() + center.getRightAscensionMinutes() / 60 + center.getRightAscensionSeconds() / 3600);
		double dec2 = center.getDegrees() + center.getArcMinutes() / 60 + center.getArcSeconds() / 3600;
		dec2 *= center.getSign() ? 1 : -1;
		
		String nestedQuery = "SELECT * FROM (SELECT name, (ACOS(SIN(15 * (hours + minutes / 60 + seconds / 3600)) * SIN(?) + COS(15 * (hours + minutes / 60 + seconds / 3600)) * COS(?) * COS(sign * (degrees + arcmin / 60 + arcsec / 3600) - ?))) AS EXP "
				+ "FROM galaxy) AS GD WHERE GD.EXP < ? ORDER BY GD.exp LIMIT ?";
		
		PreparedStatement statement = connection.prepareStatement(nestedQuery);
		statement.setDouble(1, ra2);
		statement.setDouble(2, ra2);
		statement.setDouble(3, dec2);
		statement.setDouble(4, range);
		statement.setInt(5, limit);
		statement.executeQuery();		
		
		ResultSet set = statement.getResultSet();
		List<AdaptableValue> results = new ArrayList<>();
		while (set.next()) 
			results.add(AdaptableValue.getDistanceValue(set.getString(1), set.getDouble(2)));
		
		release(connection, statement, set);
		
		nameSubject.setState(results);
	}
	
	public void retrieveGalaxyByRedshiftValue(double redshift, boolean higherThen, int limit) throws Exception {
		Connection connection = dataSource.getConnection();
		
		String query = "SELECT name, redshift FROM galaxy WHERE redshift ";
		query += higherThen ? ">= " : "<= ";
		query += "? ORDER BY redshift, name LIMIT ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setDouble(1, redshift);
		statement.setInt(2, limit);
		
		ResultSet set = statement.executeQuery();
		List<AdaptableValue> results = new ArrayList<>();
		while (set.next()) 
			results.add(AdaptableValue.getRedshiftValue(set.getString(1), set.getDouble(2)));
	
		nameSubject.setState(results);
	}
	
	class GalaxyNameSubjectAdapter extends ViewSubject<List<AdaptableValue>> {
		
		List<AdaptableValue> results;
		
		protected GalaxyNameSubjectAdapter() {
			results = new ArrayList<>();
		}
		
		protected void setState(List<AdaptableValue> results) {
			this.results.clear();
			this.results.addAll(results);
			notifyObservers();
		}

		@Override
		public List<AdaptableValue> retrieveState() {
			return results;
		}
	}
	
	class GalaxySubjectAdapter extends ViewSubject<Galaxy> {
		
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
