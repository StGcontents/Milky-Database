package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import controller.DataSource;
import controller.FluxFactory;
import pattern.Subject;

public class FluxRepository extends Repository {
	
	public FluxRepository(DataSource dataSource) { 
		this.dataSource = dataSource;
		statSubject = new StatisticsSubject();
		fillerSubject = new FillerSubject();
	}
	
	private StatisticsSubject statSubject;
	private FillerSubject fillerSubject;
	
	public Subject<Statistics> getStatSubject() { return statSubject; }
	public Subject<Void> getFillerSubject() { return fillerSubject; }
	
	public void persist(Galaxy galaxy) throws Exception {
		persist(galaxy, galaxy.getFluxes());
	}
	
	public void persist(Galaxy galaxy, List<Flux> fluxes) throws Exception {
		Connection connection = dataSource.getConnection();
		
		for (Flux flux : fluxes) 
			persistSingleFlux(connection, galaxy, flux);
		
		release(connection);
	}
	
	private void persistSingleFlux(Connection connection, Galaxy galaxy, Flux flux) throws Exception {
		String table = flux.isContinuous() ? "continuous_flux" : "line_flux";
		String query = "SELECT count(*) FROM " + table + " WHERE galaxy LIKE ? AND ion = ? AND aperture LIKE ?";
		String insert = "INSERT INTO " + table + "(galaxy, ion, aperture, flux, error) VALUES (?, ?, ?, ?, ?)";
		String update = "UPDATE " + table + " SET flux = ?, error = ? WHERE galaxy LIKE ? AND ion = ? AND aperture LIKE ?";
		
		PreparedStatement queryStatement = connection.prepareStatement(query);
		queryStatement.setString(1, galaxy.getName());
		queryStatement.setInt(2, flux.getIon().getId());
		queryStatement.setString(3, flux.getAperture());
		ResultSet set = queryStatement.executeQuery();
		set.next();
		int i = set.getInt(1);
		
		PreparedStatement statement;
		if (i == 0) {
			statement = connection.prepareStatement(insert);
		
			statement.setString(1, galaxy.getName());
			statement.setInt(2, flux.getIon().getId());
			statement.setString(3, flux.getAperture());
		
			statement.setDouble(4, flux.getValue());
			if (flux.isUpperLimit())
				statement.setNull(5,Types.REAL);
			else statement.setDouble(5, flux.getError());		
		
			statement.executeUpdate();
		}
		else {
			statement = connection.prepareStatement(update);
			
			statement.setDouble(1, flux.getValue());
			if (flux.isUpperLimit())
				statement.setNull(2,Types.REAL);
			else statement.setDouble(2, flux.getError());
			
			statement.setString(3, galaxy.getName());
			statement.setInt(4, flux.getIon().getId());
			statement.setString(5, flux.getAperture());		
		
			statement.executeUpdate();
		}
		
		release(queryStatement, set, statement);
	}
	
	public void retrieveGalaxyFluxes(Galaxy galaxy) throws Exception {
		retrieveLineFluxes(galaxy);
		retrieveContinuousFluxes(galaxy);
	}
	
	public void retrieveLineFluxes(Galaxy galaxy) throws Exception {
		Connection connection = dataSource.getConnection();
		String query = "SELECT CF.flux, CF.error, CF.aperture, I.id, I.name, I.charges, I.line "
			    + "FROM line_flux CF JOIN ion I ON (CF.ion = I.id) "
			    + "WHERE CF.galaxy LIKE ?";
		PreparedStatement statement = templateRetriever(connection, query, galaxy);
		ResultSet set = statement.executeQuery();
		galaxy.addAll(FluxFactory.getFactoryByType(false).create(set));
		fillerSubject.notifyObservers();
		
		release(set, statement, connection);
	}
	
	public void retrieveContinuousFluxes(Galaxy galaxy) throws Exception {
		Connection connection = dataSource.getConnection();
		String query = "SELECT CF.flux, CF.error, CF.aperture, I.id, I.name, I.charges, I.line "
			    + "FROM continuous_flux CF JOIN ion I ON (CF.ion = I.id) "
			    + "WHERE CF.galaxy LIKE ?";
		PreparedStatement statement = templateRetriever(connection, query, galaxy);
		ResultSet set = statement.executeQuery();
		galaxy.addAll(FluxFactory.getFactoryByType(true).create(set));
		fillerSubject.notifyObservers();
		
		release(set, statement, connection);
	}
	
	public void calculate(String spectralGroup, String apertureSize) throws Exception {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		
		String viewQuery = 
				"CREATE TEMP VIEW group_galaxy AS "
				+ "SELECT name "
				+ "FROM galaxy "
				+ "WHERE spectre LIKE '" + spectralGroup + "';"
			  + "CREATE TEMP VIEW group_fluxes AS "
			  	+ "SELECT galaxy, ion, aperture, flux "
			  	+ "FROM line_flux F "
			  	+ "WHERE EXISTS (SELECT * "
			  				  + "FROM group_galaxy GG "
			  				  + "WHERE GG.name LIKE F.galaxy);"
			 + "CREATE TEMP VIEW flux_ratio AS "
			 	+ "SELECT LF1.flux / LF2.flux as RATIO "
			 	+ "FROM group_fluxes LF1, group_fluxes LF2 "
			 	+ "WHERE ";
		if (apertureSize != null) 
			viewQuery += "LF1.aperture LIKE '" + apertureSize 
				 + "' AND LF2.aperture LIKE '" + apertureSize + "' AND ";
		viewQuery += "(LF1.galaxy <> LF2.galaxy OR LF1.ion <> LF2.ion OR LF1.aperture <> LF2.aperture);";

		Statement viewStatement = connection.createStatement();
		viewStatement.execute(viewQuery);
		release(viewStatement);
		
		String avgQuery = "SELECT avg(RATIO), stddev(RATIO), count(DISTINCT RATIO) FROM flux_ratio";
		PreparedStatement avgStatement = connection.prepareStatement(avgQuery);
		
		ResultSet avgSet = avgStatement.executeQuery();
		double avg, stddev;
		int cnt;
		avgSet.next();
		if ((cnt = avgSet.getInt(3)) == 0) {
			
			String drop = "DROP VIEW flux_ratio; DROP VIEW group_fluxes; DROP VIEW group_galaxy;";
			Statement dropStatement = connection.createStatement();
			dropStatement.execute(drop);
			release(dropStatement);
			
			release(connection, avgStatement, avgSet);
			statSubject.setState(null);
			return;
		}
		avg = avgSet.getDouble(1);
		stddev = avgSet.getDouble(2);
		release(avgStatement, avgSet);
		
		String medQuery = 
				"SELECT RATIO "
			  + "FROM (SELECT row_number() OVER (ORDER BY RATIO) AS ROW, RATIO "
			  		+ "FROM flux_ratio "
			  		+ "ORDER BY RATIO) AS RR "
			  + "WHERE RR.ROW IN (?, ?)";
		PreparedStatement medStatement = connection.prepareStatement(medQuery);
		
		boolean isOdd = cnt % 2 == 1; 
		int row1 = isOdd ? (cnt + 1) / 2 : cnt / 2, row2 = isOdd ? row1 : row1 + 1;
		medStatement.setInt(1, row1);
		medStatement.setInt(2, row2);
		double med;
		ResultSet medSet = medStatement.executeQuery();
		medSet.next();
		med = medSet.getDouble(1);
		if (medSet.next()) med = (med + medSet.getDouble(1)) / 2.0;
		release(medStatement, medSet);
		
		String madQuery = 
				"SELECT M.MAD FROM "
				+ "(SELECT RA.MAD, row_number() OVER (ORDER BY RA.MAD) AS ROW "
				+ "FROM (SELECT ABS(RATIO - ?) AS MAD FROM flux_ratio ORDER BY MAD) AS RA) AS M "
			  + "WHERE M.ROW IN (?, ?)";
		PreparedStatement madStatement = connection.prepareStatement(madQuery);
		
		madStatement.setDouble(1, med);
		madStatement.setInt(2, row1);
		madStatement.setInt(3, row2);
		double mad;
		ResultSet madSet = madStatement.executeQuery();
		madSet.next();
		mad = madSet.getDouble(1);
		if (madSet.next()) mad = (mad + madSet.getDouble(1)) / 2.0;
		release(madStatement, madSet);
		
		String drop = "DROP VIEW flux_ratio; DROP VIEW group_fluxes; DROP VIEW group_galaxy;";
		Statement dropStatement = connection.createStatement();
		dropStatement.execute(drop);
		release(dropStatement);
		
		connection.commit();		
		release(connection);
		
		statSubject.setState(new Statistics(avg, stddev, med, mad));
	}
	
	private PreparedStatement templateRetriever (Connection connection, String query, Galaxy galaxy) throws Exception {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, galaxy.getName());
		return statement;
	}
	
	protected class StatisticsSubject extends Subject<Statistics> {

		private Statistics stats;
		protected void setState(Statistics stats) {
			this.stats = stats;
			notifyObservers();
		}
		
		@Override
		public Statistics retrieveState() {
			return stats;
		}
	}
	
	protected class FillerSubject extends Subject<Void> {
		@Override public Void retrieveState() { return null; }
	}
}
