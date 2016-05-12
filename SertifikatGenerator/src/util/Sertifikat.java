package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sertifikat implements Serializable{

	private String common_name;
	private String surname;
	private String givenname;
	private String oName;
	private String oUnit;
	private String country_text;
	private String email;
	private String uid;
	private KeyStore ks;
	private Map<Integer, KeyStore> keyset = new HashMap<Integer, KeyStore>();
	
	public KeyStore getKs() {
		return ks;
	}

	public void setKs(KeyStore ks) {
		this.ks = ks;
	}

	public Map<Integer, KeyStore> getKeyset() {
		return keyset;
	}

	public void setKeyset(Map<Integer, KeyStore> keyset) {
		this.keyset = keyset;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCommon_name() {
		return common_name;
	}
	
	public void setCommon_name(String common_name) {
		this.common_name = common_name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGivenname() {
		return givenname;
	}

	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}
	
	public String getoName() {
		return oName;
	}
	
	public void setoName(String oName) {
		this.oName = oName;
	}
	
	public String getoUnit() {
		return oUnit;
	}
	
	public void setoUnit(String oUnit) {
		this.oUnit = oUnit;
	}
	
	public String getCountry_text() {
		return country_text;
	}
	
	public void setCountry_text(String country_text) {
		this.country_text = country_text;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void addKS(KeyStore ks){
		keyset.put(keyset.size()+1, ks);
	}
	
	public Sertifikat(String common_name, String surname, String givenname, String oName,
			String oUnit, String country_text, String email, String uid) {
		super();
		this.common_name = common_name;
		this.surname = surname;
		this.givenname = givenname;
		this.oName = oName;
		this.oUnit = oUnit;
		this.country_text = country_text;
		this.email = email;
		this.uid = uid;
	}
	
	public Sertifikat() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		return "Sertifikat [common_name=" + common_name + ", surname="
				+ surname + ", givenname=" + givenname +", oName=" + oName + ", oUnit=" + oUnit
				+ ", country_text=" + country_text + ", email=" + email + "]";
	}
	
	public void save(Map<String, Sertifikat> sertifikati) throws FileNotFoundException, IOException{
		ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream("./data/sertifikati.dat"));
		out1.writeObject(sertifikati);
		out1.flush();
		out1.close();
	}
	
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Sertifikat> load() throws FileNotFoundException, IOException, ClassNotFoundException{
		Map<String, Sertifikat> sertifikati = new HashMap<String, Sertifikat>();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("./data/sertifikati.dat"));
		sertifikati =  (Map<String, Sertifikat>) in.readObject();
		in.close();
		return (HashMap<String, Sertifikat>) sertifikati;
	}
}
