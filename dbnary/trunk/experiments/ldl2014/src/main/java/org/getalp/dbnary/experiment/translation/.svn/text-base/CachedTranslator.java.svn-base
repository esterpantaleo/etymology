package org.getalp.dbnary.experiment.translation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class CachedTranslator implements Translator {

	private String dir;
	private Connection db;
	private Translator t;
	private boolean doUpdate;
	
	private Logger log = LoggerFactory.getLogger(CachedTranslator.class);
			
    private static String getTranslationMemory = "SELECT target FROM tm WHERE source = ? AND slang = ? AND tlang = ?";
    private static String setTranslationMemory = "INSERT INTO tm (SOURCE, SLANG, TARGET, TLANG) VALUES (?, ?, ?, ?)";

	public CachedTranslator(String dir, Translator t, boolean update) {
		try {
			if (! dir.startsWith("jdbc:h2:")) { dir = "jdbc:h2:" + dir; };
			this.dir = dir + ";CACHE_SIZE=4000000";
			Class.forName("org.h2.Driver");
			db = DriverManager.getConnection(dir, "dbnary", "");

			// System.err.println("Checking if tm table exists.");
			boolean tmTableExists = false;
			DatabaseMetaData meta = db.getMetaData();
			java.sql.ResultSet res = meta.getTables(null, null, null, new String[] {"TABLE"});
			while (res.next()) {
				String tn = res.getString("TABLE_NAME");
				if ("TM".equalsIgnoreCase(tn)) {
					tmTableExists = true;
				}
			}

			if ( ! tmTableExists) {
				log.debug("Creating translation Memory table.");

				String createString;
				createString = "create table TM (" +
						"source VARCHAR(2048), " +
						"slang VARCHAR(3), " +
						"target VARCHAR(2048), " + 
						"tlang VARCHAR(3)" + 
						")";

				Statement stmt = db.createStatement();
				stmt.executeUpdate(createString);
				stmt.close();
				
				String createIndex;
                createIndex = "create index idx_source on TM (source, slang)";

                Statement cindex = db.createStatement();
                cindex.executeUpdate(createIndex);
                cindex.close();

			}
			
			this.t = t;
			doUpdate = update;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("H2 driver not found in classpath.", e);
		} catch (SQLException e) {
			throw new RuntimeException("Could not allocate connection to dictionary.", e);
		}
	}
	
	@Override
	public String translate(String source, String slang, String tlang) {
		
		String target;
		try {
			PreparedStatement getCachedTranslation = db.prepareStatement(getTranslationMemory);
			getCachedTranslation.setString(1, source);
			getCachedTranslation.setString(2, slang);
			getCachedTranslation.setString(3, tlang);
			
			ResultSet rs = getCachedTranslation.executeQuery();
			if (rs.first()) {
				target = rs.getString("target");
				log.debug("Got cached translation:[{}] {}", slang, target);
			} else {
				// Ask for translation and update cache if asked to !
				target = t.translate(source, slang, tlang);
				log.debug("Computed translation:[{}] {}", tlang, target);

				if (target != null && doUpdate) {
					PreparedStatement cacheTranslation = db.prepareStatement(setTranslationMemory);
					cacheTranslation.setString(1, source);
					cacheTranslation.setString(2, slang);
					cacheTranslation.setString(3, target);
					cacheTranslation.setString(4, tlang);
					
					cacheTranslation.executeUpdate();
					log.debug("Updated cached translation:[{}] {}", slang, target);

				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Caught an unexpected SQL Exception while retrieving a word.", e);
		}
		return target;
	}

}
