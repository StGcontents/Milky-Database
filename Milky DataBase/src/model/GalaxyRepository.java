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
public class GalaxyRepository extends UniRepository<Galaxy> {
	
	private GalaxyNameSubjectAdapter nameSubject;
	private GalaxySubjectAdapter galaxySubject;
	public GalaxyRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		nameSubject = new GalaxyNameSubjectAdapter();
		galaxySubject = new GalaxySubjectAdapter();
	}
	
	public Subject<List<AdaptableValue>> getNameSubject() { return this.nameSubject; }
	public Subject<Galaxy> getGalaxySubject() { return this.galaxySubject; }
	
	@Override
	public void persist(Galaxy galaxy) throws Exception {
		Connection connection = null;
		PreparedStatement qStatement = null, 
				statement = null,
				alterStatement = null;
		ResultSet set = null;
		boolean committed = false;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			String query = "SELECT count(*) FROM galaxy WHERE name LIKE ?";
			qStatement = connection.prepareStatement(query);
			qStatement.setString(1, galaxy.getName());
			set = qStatement.executeQuery();
			if (!set.next()) throw new Exception();
			boolean result = set.getInt(1) == 0;
			
			if (result) {
				String insert = "INSERT INTO galaxy(name, hours, minutes, seconds, "
						+ "sign, degrees, arcmin, arcsec, redshift, distance, spectre, "
						+ "lum_nev_1, lum_nev_1_flag, lum_nev_2, lum_nev_2_flag, "
						+ "lum_oiv, lum_oiv_flag, metallicity, metallicity_err) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
				statement = connection.prepareStatement(insert);
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
				
				int count;
				if ((count = galaxy.getAlternativeNames().length) > 0) {
					String alterInsert = "INSERT INTO alternative_names (name, alter_name) VALUES";
					for (int i = 0; i < count; ++i) 
						alterInsert += i == 0 ? " (?, ?)" : ", (?, ?)";
					
					alterStatement = connection.prepareStatement(alterInsert);
					for (int i = 0; i < count; ++i) {
						alterStatement.setString(2 * i + 1, galaxy.getName());
						alterStatement.setString(2 * i + 2, galaxy.getAlternativeNames()[i]);
					}	
					alterStatement.executeUpdate();
				}
			}
			
			connection.commit();
			committed = true;
		}
		finally {
			if (!committed)
				connection.rollback();
			release(set, alterStatement, statement, qStatement, connection);
		}
	}
	
	@Override
	public List<Galaxy> read(PreparedStatement statement) throws Exception {
		ResultSet set = statement.executeQuery();
		return GalaxyFactory.instance().create(set);
	}
	
	public void retrieveGalaxyByName(String name, boolean force) throws Exception {
		Galaxy galaxy = GalaxyPool.getByName(name);
		if (galaxy != null && !galaxy.isExpired()) {
			galaxySubject.setState(galaxy);
			return;
		}
		
		Connection connection = null;
		PreparedStatement statement = null,
				alterStatement = null;
		ResultSet alterSet = null;
		boolean committed = false;
		
		try {		
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			String query = "SELECT * FROM galaxy WHERE name LIKE ?";
			statement = connection.prepareStatement(query, 
					ResultSet.TYPE_FORWARD_ONLY, 
					ResultSet.CONCUR_READ_ONLY, 
					ResultSet.CLOSE_CURSORS_AT_COMMIT);
			statement.setString(1, name);
			
			galaxy = read(statement).get(0);
			
			String alterQuery = "SELECT alter_name FROM alternative_names WHERE name LIKE ?";
			alterStatement = connection.prepareStatement(alterQuery);
			alterStatement.setString(1, name);
			alterSet = alterStatement.executeQuery();
			List<String> names = new ArrayList<>();
			while (alterSet.next()) 
				names.add(alterSet.getString(1));
			
			galaxy.setAlternativeNames(names.toArray(new String[names.size()]));
		
			if (force) 			
				new FluxRepository(dataSource).retrieveGalaxyFluxes(galaxy);
			else connection.commit();
			
			committed = true;
		
			GalaxyPool.insert(galaxy);
			galaxySubject.setState(galaxy);
		}
		finally {
			if (!committed)
				connection.rollback();
			release(alterSet, alterStatement, statement, connection);
		}
	}
	
	public void retrieveGalaxyNames(String partial) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet set = null;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(true);
			
			String query = "SELECT G.name, AN.alter_name "
					 	+ "FROM galaxy G LEFT JOIN alternative_names AN ON (G.name LIKE AN.name) "
					 	+ "WHERE G.name LIKE ? OR alter_name LIKE ? "
					 	+ "ORDER BY G.name, AN.alter_name";
			statement = connection.prepareStatement(query);
			statement.setString(1, "%" + partial + "%");
			statement.setString(2, "%" + partial + "%");
			
			set = statement.executeQuery();
			List<AdaptableValue> results = new ArrayList<>();
			String last = "", str0, str1;
			while (set.next()) {
				str0 = set.getString(1);
				str1 = set.getString(2);
				if (!last.equals(str0)) {
					last = str0;
					if (str0.contains(partial))
						results.add(AdaptableValue.getNameValue(str0, null));
				}
				if (str1 != null && str1.contains(partial)) 
						results.add(AdaptableValue.getNameValue(str0, str1));
			}
		
			nameSubject.setState(results);
		}
		finally {
			release(set, statement, connection);
		}
	}
	
	public void retrieveGalaxyInRange(Coordinates center, double range, int limit) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet set = null;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(true);
			
			double ra2 = 15 * (center.getRightAscensionHours() + center.getRightAscensionMinutes() / 60 + center.getRightAscensionSeconds() / 3600);
			double dec2 = center.getDegrees() + center.getArcMinutes() / 60 + center.getArcSeconds() / 3600;
			dec2 *= center.getSign() ? 1 : -1;
			
			String nestedQuery = "SELECT * FROM (SELECT name, (ACOS(SIN(15 * (hours + minutes / 60 + seconds / 3600)) * SIN(?) + COS(15 * (hours + minutes / 60 + seconds / 3600)) * COS(?) * COS(sign * (degrees + arcmin / 60 + arcsec / 3600) - ?))) AS EXP "
					+ "FROM galaxy) AS GD WHERE GD.EXP < ? ORDER BY GD.exp LIMIT ?";
			
			statement = connection.prepareStatement(nestedQuery);
			statement.setDouble(1, ra2);
			statement.setDouble(2, ra2);
			statement.setDouble(3, dec2);
			statement.setDouble(4, range);
			statement.setInt(5, limit);
			statement.executeQuery();		
			
			set = statement.getResultSet();
			List<AdaptableValue> results = new ArrayList<>();
			while (set.next()) 
				results.add(AdaptableValue.getDistanceValue(set.getString(1), set.getDouble(2)));
		
			nameSubject.setState(results);
		}
		finally {
			release(set, statement, connection);
		}
	}
	
	public void retrieveGalaxyByRedshiftValue(double redshift, boolean higherThen, int limit) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet set = null;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(true);
			
			String query = "SELECT name, redshift FROM galaxy WHERE redshift ";
			query += higherThen ? ">= " : "<= ";
			query += "? ORDER BY redshift, name LIMIT ?";
			statement = connection.prepareStatement(query);
			statement.setDouble(1, redshift);
			statement.setInt(2, limit);
			
			set = statement.executeQuery();
			List<AdaptableValue> results = new ArrayList<>();
			while (set.next()) 
				results.add(AdaptableValue.getRedshiftValue(set.getString(1), set.getDouble(2)));
	
			nameSubject.setState(results);
		}
		finally {
			release(set, statement, connection);
		}
	}

	@Override
	public void update(Galaxy entity) {
		//TODO: no need to implement it
	}
	
	@Override
	public void delete(Galaxy galaxy) {
		//TODO: no need to implement it
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
