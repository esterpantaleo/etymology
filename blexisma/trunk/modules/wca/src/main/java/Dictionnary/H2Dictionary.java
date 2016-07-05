package Dictionnary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import acfast.Global;

public class H2Dictionary implements Dictionary {

	Connection conn;
	PreparedStatement selectWord = null;
    PreparedStatement updateTotal = null;

    String selectWordString =
        "SELECT sid, def " + 
        "FROM dict WHERE lemma = ?";

    /**
	 * Creates a new dictionary taking its content in directory dir
	 * @param dir
	 * @throws ClassNotFoundException if the H2 db driver is not available
	 * @throws SQLException 
	 */
	public H2Dictionary(String dir) {
		try {
			if (! dir.startsWith("jdbc:h2:")) { dir = "jdbc:h2:" + dir; };
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(dir, "wsa", "");
			selectWord = conn.prepareStatement(selectWordString);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception while creating H2 dictionary", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("H2 driver not found in classpath.", e);
		}
	}
	
	// TODO: add a weakhashmap for caching
	
	@Override
	public ArrayList<Word> getWords(String lemme) {
		ArrayList<Word> r = new ArrayList<Word>();
		try {
			selectWord.setString(1, lemme);
			ResultSet rs = selectWord.executeQuery();
			while (rs.next()) {
				String sid = rs.getString("sid");
				String def = rs.getString("def");
				Word w = new Word();
				w.setIDS(sid);
				w.setDef(def);
				r.add(w);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Caught an unexpected SQL Exception while retrieving a word.", e);
		}
		return r;
	}

	@Override
	public ArrayList<Word> getWords(String lemme, int categorieLex) {
		String l = lemme.toLowerCase();
		String lp = null;
		switch (categorieLex) {
		case Global.NOUN: {
			lp = l+"%n";
			break;
		}
		case Global.VERB: {
			lp = l+"%v";
			break;
		}
		case Global.ADJECTIVE: {
			lp = l+"%a";
			break;
		}
		case Global.ADJECTIVE_SATELLITE: {
			lp = l+"%a";
			break;
		}
		case Global.ADVERB: {
			lp = l+"%r";
			break;
		}
		}
		return getWords(lp);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void close() throws SQLException {
		if (null != conn)
			conn.close();
	}
	
	public static void main(String args[]) throws SQLException {
		String db = args[0];
		long t = System.currentTimeMillis();
		System.err.println("Creating dictionary instance.");
		H2Dictionary d = new H2Dictionary(db);
		System.err.println("DONE: [." + (System.currentTimeMillis() - t) + " ms]");
		t = System.currentTimeMillis();
		System.err.println("Getting 1000 lemmas.");
		
		PreparedStatement select1000 = d.conn.prepareStatement("select lemma from dict limit 1000");

		ResultSet rs = select1000.executeQuery();
		while (rs.next()) {
			String l = rs.getString(1);
			System.err.println(l);
		}
		System.err.println("DONE: [." + (System.currentTimeMillis() - t) + " ms]");
		
		System.err.println("Looking for The Cranberries");
		t = System.currentTimeMillis();
		System.err.println(d.getWords("%The_Cranberries").get(0));
		System.err.println("DONE: [." + (System.currentTimeMillis() - t) + " ms]");

	}
}
