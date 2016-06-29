package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import controller.DataSource;
import controller.FluxFactory;

public class FluxRepository extends Repository {
	
	private static FluxRepository me;
	protected FluxRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public static synchronized FluxRepository instance(DataSource dataSource) {
		if (me == null) me = new FluxRepository(dataSource);
		return me;
	}
	
	public void persist(Galaxy galaxy, Flux... fluxes) throws Exception {
		
		Connection connection = dataSource.getConnection();
		
		for (Flux flux : fluxes) 
			persistSingleFlux(connection, galaxy, flux);
		
		release(connection);
	}
	
	private void persistSingleFlux(Connection connection, Galaxy galaxy, Flux flux) throws Exception {
		String insert = "INSERT INTO ?(galaxy, ion, aperture, flux, error) "
				+ "values (?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(insert);
		
		statement.setString(1, flux.isContinuous() ? "countinuous_flux" : "line_flux");
		statement.setString(2, galaxy.getName());
		statement.setInt(3, flux.getIon().getId());
		statement.setString(4, flux.getAperture());
		statement.setDouble(5, flux.getValue());
		statement.setDouble(6, flux.isUpperLimit() ? null : flux.getError());
		
		statement.executeUpdate();
		release(statement);
	}
	
	public void retrieveLineFluxes(Galaxy galaxy) throws Exception {
		Connection connection = dataSource.getConnection();
		String query = "SELECT CF.value, CF.error, CF.aperture, I.id, I.name, I.charges, I.line "
			    + "FROM line_flux CF JOIN ion I ON (CF.ion = I.id) "
			    + "WHERE CF.galaxy LIKE ?";
		PreparedStatement statement = templateRetriever(connection, query, galaxy);
		ResultSet set = statement.executeQuery();
		galaxy.addAll(FluxFactory.getFactoryByType(true).create(set));
		
		release(set, statement, connection);
	}
	
	public void retrieveContinuousFluxes(Galaxy galaxy) throws Exception {
		Connection connection = dataSource.getConnection();
		String query = "SELECT CF.value, CF.error, CF.aperture, I.id, I.name, I.charges, I.line "
			    + "FROM continuous_flux CF JOIN ion I ON (CF.ion = I.id) "
			    + "WHERE CF.galaxy LIKE ?";
		PreparedStatement statement = templateRetriever(connection, query, galaxy);
		ResultSet set = statement.executeQuery();
		galaxy.addAll(FluxFactory.getFactoryByType(true).create(set));
		
		release(set, statement, connection);
	}
	
	public void retrieveGalaxyFluxes(Galaxy galaxy) throws Exception {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		
		String conQuery = "SELECT CF.value, CF.error, CF.aperture, I.id, I.name, I.charges, I.line "
					    + "FROM continuous_flux CF JOIN ion I ON (CF.ion = I.id) "
					    + "WHERE CF.galaxy LIKE '" + galaxy.getName() + "'",
			  lineQuery = "SELECT CF.value, CF.error, CF.aperture, I.id, I.name, I.charges, I.line "
					    + "FROM line_flux CF JOIN ion I ON (CF.ion = I.id) "
					    + "WHERE CF.galaxy LIKE '" + galaxy.getName() + "'";
		
		PreparedStatement conStatement = templateRetriever(connection, conQuery, galaxy); 
		ResultSet conSet = conStatement.executeQuery();
		galaxy.addAll(FluxFactory.getFactoryByType(true).create(conSet));
		
		PreparedStatement lineStatement = templateRetriever(connection, lineQuery, galaxy); 
		ResultSet lineSet = lineStatement.executeQuery();
		galaxy.addAll(FluxFactory.getFactoryByType(false).create(lineSet));
		
		connection.commit();
		release(conSet, conStatement, lineSet, lineStatement, connection);
	}
	
	private PreparedStatement templateRetriever (Connection connection, String query, Galaxy galaxy) throws Exception {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, galaxy.getName());
		return statement;
	}
}
