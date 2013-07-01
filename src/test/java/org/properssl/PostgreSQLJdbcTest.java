package org.properssl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.postgresql.util.PSQLException;

public class PostgreSQLJdbcTest {
	String host = "localhost";
	int port = 15432;
	String database = "proper_ssl";
	String username = "proper_ssl";
	String password = "real_passwords_should_be_random";

	private Connection getConnection(Properties info) throws SQLException {
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
		info.setProperty("user", username);
		info.setProperty("password", password);
		return DriverManager.getConnection(url, info);
	}

	private void testConnect(Properties info) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection(info);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT 1");
			rs.next();
			Assert.assertEquals(1, rs.getInt(1));
			rs.close();
			stmt.close();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Connect using no SSL. This is bad as passwords are sent over as
	 * plaintext.
	 */
	@Test
	public void connectNoSSL() throws SQLException {
		Properties info = new Properties();
		testConnect(info);
	}

	/**
	 * Connect using SSL without any server certificate validation. This is bad
	 * as this connection is vulnerable to a man in the middle attack.
	 */
	@Test
	public void connectSSLWithoutValidation() throws SQLException {
		Properties info = new Properties();
		info.setProperty("ssl", "true");
		info.setProperty("sslfactory",
				"org.postgresql.ssl.NonValidatingFactory");
		testConnect(info);
	}

	/**
	 * Connect using SSL and attempt to validate the server's certificate but
	 * don't actually provide it. This connection attempt should *fail* as the
	 * client should reject the server.
	 */
	@Test(expected = PSQLException.class)
	public void connectSSLWithValidationNoCert() throws SQLException {
		Properties info = new Properties();
		info.setProperty("ssl", "true");
		testConnect(info);
	}

	/**
	 * Connect using SSL and attempt to validate the server's certificate
	 * against the wrong pre shared certificate. This test uses a pre generated
	 * certificate that will *not* match the test PostgreSQL server (the
	 * certificate is for properssl.example.com).
	 * 
	 * This connection uses a custom SSLSocketFactory using a custom trust
	 * manager that validates the remote server's certificate against the pre
	 * shared certificate.
	 * 
	 * This test should throw an exception as the client should reject the
	 * server since the certificate does not match.
	 * 
	 * @throws SQLException
	 */
	@Test(expected = PSQLException.class)
	public void connectSSLWithValidationWrongCert() throws SQLException,
			IOException {
		Properties info = new Properties();
		info.setProperty("ssl", "true");
		info.setProperty("sslfactory",
				"org.properssl.SingleCertValidatingFactory");
		info.setProperty("sslfactoryarg", Utils.getClasspathFile("invalid-server.crt"));
		testConnect(info);
	}

	/**
	 * Connect using SSL and attempt to validate the server's certificate
	 * against the proper pre shared certificate. Make sure to copy the server's
	 * certificate to src/test/resources prior to running this. Instructions are
	 * in the README file.
	 * 
	 * This connection uses a custom SSLSocketFactory using a custom trust
	 * manager that validates the remote server's certificate against a pre
	 * shared certificate.
	 * 
	 * NOTE: If you're connecting to a remote server that uses a self signed
	 * certificate this is how a connection should be made.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void connectSSLWithValidationProperCert() throws SQLException,
			IOException {
		Properties info = new Properties();
		info.setProperty("ssl", "true");
		info.setProperty("sslfactory",
				"org.properssl.SingleCertValidatingFactory");
		info.setProperty("sslfactoryarg", Utils.getClasspathFile("server.crt"));
		testConnect(info);
	}
}
