package it.polito.tdp.nyc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.nyc.model.Adiacenza;
import it.polito.tdp.nyc.model.Hotspot;

public class NYCDao {
	
	public List<Hotspot> getAllHotspot(){
		String sql = "SELECT * FROM nyc_wifi_hotspot_locations";
		List<Hotspot> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Hotspot(res.getInt("OBJECTID"), res.getString("Borough"),
						res.getString("Type"), res.getString("Provider"), res.getString("Name"),
						res.getString("Location"),res.getDouble("Latitude"),res.getDouble("Longitude"),
						res.getString("Location_T"),res.getString("City"),res.getString("SSID"),
						res.getString("SourceID"),res.getInt("BoroCode"),res.getString("BoroName"),
						res.getString("NTACode"), res.getString("NTAName"), res.getInt("Postcode")));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	public List<String> getAllProvider(){
		String sql = "select distinct provider "
				+ "FROM `nyc_wifi_hotspot_locations` "
				+ "order by provider ";
		List<String> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(res.getString("provider"));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}

	public List<String> getVertici(String provider){
		String sql = "select distinct `City` "
				+ "FROM `nyc_wifi_hotspot_locations` "
				+ "where provider=? ";
				
		List<String> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, provider);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(res.getString("City"));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}

	public List<Adiacenza> getArchi(String provider){
		String sql = "select distinct n.`City`as c1,n2.`City`as c2,avg(n.`Longitude`) as c1lo,avg(n.`Latitude`) as c1la,avg(n2.`Longitude`)as c2lo,avg(n2.`Latitude`) as c2la "
				+ "FROM `nyc_wifi_hotspot_locations`n,`nyc_wifi_hotspot_locations`n2 "
				+ "where n.provider=? and n.provider=n2.provider and n.City>n2.City "
				+ "group by n.`City`,n2.`City` ";
			
				
		List<Adiacenza> result = new ArrayList<>();
		LatLng pnt1,pnt2;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, provider);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				pnt1=new LatLng(res.getDouble("c1la"),res.getDouble("c1lo"));
				pnt2=new LatLng(res.getDouble("c2la"),res.getDouble("c2lo"));
				double peso=LatLngTool.distance(pnt1, pnt2, LengthUnit.KILOMETER);
				result.add(new Adiacenza(res.getString("c1"),res.getString("c2"),peso));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}

}
