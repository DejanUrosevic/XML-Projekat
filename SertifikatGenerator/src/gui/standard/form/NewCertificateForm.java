package gui.standard.form;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import security.CertificateGenerator;
import security.IssuerData;
import security.SubjectData;
import util.Sertifikat;
/*
 * PITANJA
 * 1. UID da li treba da bude razlicit kod builder-a?
 * 2. Da li treba da bude po dva kljuca, jedan za issuer, drugi za subject? (digitalni potpis)
 * 3. CerticicateGenerator da li je SHA1WithRSAEncryption RSA od 1024 bita?
 * 4. Hijerarhija sertifikata?
 */

public class NewCertificateForm extends JDialog{

	private CertificateGenerator cg;
	
	private JLabel potpis = new JLabel("Issuer: ");
	//private JLabel validity_days = new JLabel("Validity (months):");
	private JLabel name = new JLabel("Common Name (CN):");
	private JLabel oUnit = new JLabel("Surname (SN):");
	private JLabel sName = new JLabel("Given Name (GN):");
	private JLabel oName = new JLabel("Organisation Name (ON):");
	private JLabel lName = new JLabel("Locality Name (L):");
	private JLabel country = new JLabel("Country (C):");
	private JLabel email = new JLabel("Email (E):");
	
	
	private JComboBox issuer = new JComboBox();
	//private JComboBox validity_days_text = new JComboBox();
	private JTextField name_text = new JTextField(20);
	private JTextField oUnit_text = new JTextField(20);
	private JTextField oName_text = new JTextField(20);
	private JTextField lName_text = new JTextField(20);
	private JTextField sName_text = new JTextField(20);
	private JTextField country_text = new JTextField(20);
	private JTextField email_text = new JTextField(20);

	private JButton save = new JButton("Ok");
	private JButton cancel = new JButton("Cancel");
	
	private JPanel main_panel = new JPanel();
	private JPanel[] main_panels = new JPanel[9];
	
	private Map<String, Sertifikat> sertifikati = new HashMap<String, Sertifikat>();
	private Sertifikat s = new Sertifikat();
	
	public NewCertificateForm(){
		//setSize(new Dimension(500,400));
		setModal(true);
		setTitle("New certificate");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		//*****************************************************
		//ucitavanje sertifikata koji ce biti ponudjeni za potpisicanje drugih sertifikata
		try {
			sertifikati = s.load();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		issuer.addItem("");
		
		/*
		validity_days_text.addItem("3");
		validity_days_text.addItem("6");
		validity_days_text.addItem("12");
		validity_days_text.addItem("24");
		validity_days_text.addItem("48");
		*/
		
		if(sertifikati.size() == 0){
			System.out.println("prazna lista");
		}else if(sertifikati == null){
			System.out.println("null");
		}else{
			for(Entry<String, Sertifikat> entry : sertifikati.entrySet()){
				issuer.addItem(entry.getKey());
				System.out.println("Br. izdatih sertifikata : " + entry.getValue().getKeyset().size());
				
			}			
		}
		
		//****************************************************
		
		for (int i = 0; i < main_panels.length-1; i++) {
			main_panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		//main_panels[8] = new JPanel(new FlowLayout(FlowLayout.CENTER));
		main_panels[8] = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));

		main_panels[0].add(potpis);
		main_panels[0].add(issuer);

		main_panels[1].add(name);
		main_panels[1].add(name_text);

		main_panels[2].add(oUnit);
		main_panels[2].add(oUnit_text);

		main_panels[3].add(oName);
		main_panels[3].add(oName_text);

		main_panels[4].add(lName);
		main_panels[4].add(lName_text);

		main_panels[5].add(sName);
		main_panels[5].add(sName_text);

		main_panels[6].add(country);
		main_panels[6].add(country_text);

		main_panels[7].add(email);
		main_panels[7].add(email_text);

		main_panels[8].add(save);
		main_panels[8].add(cancel);
		
		


		
		for (int i = 0; i < main_panels.length; i++) {
			main_panel.add(main_panels[i]);
		}
		
		
		add(main_panel);
		pack();
		setLocationRelativeTo(null);
	
		cg = new CertificateGenerator();
		
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e1) {
				// TODO Auto-generated method stub
				
				try {
										
					if(name_text.getText().trim().equals("") || oUnit_text.getText().trim().equals("") || oName_text.getText().trim().equals("") ||
							lName_text.getText().trim().equals("") || sName_text.getText().trim().equals("") || country_text.getText().trim().equals("") || email_text.getText().trim().equals("")){
						JOptionPane.showMessageDialog(new JFrame(), "Empty field!!", "Ok", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					Sertifikat sertifikat;
					//par kljuceva
					KeyPair keyPair = cg.generateKeyPair();
					
					//datumi
					SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
					Date startDate = new Date();
					
					Calendar c = Calendar.getInstance(); 
					c.setTime(new Date());
					c.add(Calendar.YEAR, 1);  
					
					Date endDate = c.getTime();
					//Date endDateParse = iso8601Formater.parse(endDate.);
					
					//podaci o vlasniku i izdavacu posto je self signed 
					//klasa X500NameBuilder pravi X500Name objekat koji nam treba
					X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
					X500NameBuilder issuerBuilder = new X500NameBuilder(BCStyle.INSTANCE);
					
					String nameOfCA = (String) issuer.getSelectedItem();
					String uid = UUID.randomUUID().toString();
					Sertifikat sertifikatIssuer = new Sertifikat();
					
					for(Entry<String, Sertifikat> entry : sertifikati.entrySet()){
						if(nameOfCA.equals(entry.getKey())){
							sertifikatIssuer = entry.getValue();
							break;
						}
					}
					
					if(!nameOfCA.equals("")){
						issuerBuilder.addRDN(BCStyle.CN, sertifikatIssuer.getCommon_name());
						issuerBuilder.addRDN(BCStyle.SURNAME, sertifikatIssuer.getSurname());
						issuerBuilder.addRDN(BCStyle.GIVENNAME, sertifikatIssuer.getGivenname());
						issuerBuilder.addRDN(BCStyle.O, sertifikatIssuer.getoName());
						issuerBuilder.addRDN(BCStyle.OU, sertifikatIssuer.getoUnit());
						issuerBuilder.addRDN(BCStyle.C, sertifikatIssuer.getCountry_text());
						issuerBuilder.addRDN(BCStyle.E, sertifikatIssuer.getEmail());
					    //UID (USER ID) je ID korisnika
						issuerBuilder.addRDN(BCStyle.UID, sertifikatIssuer.getUid());
					}else{
						issuerBuilder.addRDN(BCStyle.CN, name_text.getText());
						issuerBuilder.addRDN(BCStyle.SURNAME, oUnit_text.getText());
						issuerBuilder.addRDN(BCStyle.GIVENNAME, oUnit_text.getText());
						issuerBuilder.addRDN(BCStyle.O, oName_text.getText());
						issuerBuilder.addRDN(BCStyle.OU, oUnit_text.getText());
						issuerBuilder.addRDN(BCStyle.C, country_text.getText());
						issuerBuilder.addRDN(BCStyle.E, email_text.getText());
					    //UID (USER ID) je ID korisnika
						issuerBuilder.addRDN(BCStyle.UID, uid);
					}
					
					
					
					builder.addRDN(BCStyle.CN, name_text.getText());
				    builder.addRDN(BCStyle.SURNAME, oUnit_text.getText());
				    builder.addRDN(BCStyle.GIVENNAME, oUnit_text.getText());
				    builder.addRDN(BCStyle.O, oName_text.getText());
				    builder.addRDN(BCStyle.OU, oUnit_text.getText());
				    builder.addRDN(BCStyle.C, country_text.getText());
				    builder.addRDN(BCStyle.E, email_text.getText());
				    //UID (USER ID) je ID korisnika
				    builder.addRDN(BCStyle.UID, uid);
				    /********************************************************
				     * pravljenje sertifikata koji ce biti sacuvan i ponudjen za potpisicanje drugog
				     */
				    sertifikat = new Sertifikat(name_text.getText(), oUnit_text.getText(), oUnit_text.getText(), 
				    							oName_text.getText(), oUnit_text.getText(), 
				    							country_text.getText(), email_text.getText(), uid);
					
				    sertifikati.put(name_text.getText(), sertifikat);
				    try {
						sertifikatIssuer.save(sertifikati);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				    /*
				     * *******************************************************************
				     */
				    
					//Serijski broj sertifikata
				    int randomNum = 0 + (int)(Math.random() * 10000000); 
					String sn = String.valueOf(randomNum);
					//kreiraju se podaci za issuer-a
					IssuerData issuerData = new IssuerData(keyPair.getPrivate(), issuerBuilder.build());
					//kreiraju se podaci za vlasnika
					SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);
					
					//generise se sertifikat
					X509Certificate cert = cg.generateCertificate(issuerData, subjectData);
					
					System.out.println("ISSUER: " + cert.getIssuerX500Principal().getName());
					System.out.println("SUBJECT: " + cert.getSubjectX500Principal().getName());
					System.out.println("Sertifikat:");
					System.out.println("-------------------------------------------------------");
					System.out.println(cert);
					System.out.println("-------------------------------------------------------");
					//ako validacija nije uspesna desice se exception
					
					//ovde bi trebalo da prodje
					cert.verify(keyPair.getPublic());
					System.out.println("VALIDACIJA USPESNA....");
					
					//ovde bi trebalo da se desi exception, jer validaciju vrsimo drugim kljucem
					//KeyPair anotherPair = generateKeyPair();
					//cert.verify(anotherPair.getPublic());
					
					cg.saveCert(cert, name_text.getText());
					//JOptionPane.showMessageDialog(new JFrame(), "Sertifikat * " + name_text.getText() + " * je uspesno kreiran.", "Potvrda sertifikata", JOptionPane.INFORMATION_MESSAGE);
					if(!nameOfCA.equals("")){
																							//ser kori kreiram, alias ks onog ko potpisuje, ser onog ko potpisuje
						PasswordForm pf = new PasswordForm(keyPair, cert, name_text.getText(), sertifikat, sertifikatIssuer.getKs().getPassAlias(), sertifikatIssuer);
						pf.setVisible(true);
					}else{																	//ser koji se kreira
						KeyStoreForm ksf = new KeyStoreForm(keyPair, cert, name_text.getText(), sertifikat, null, null);
						ksf.setVisible(true);
					}
				} catch (InvalidKeyException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), "Doslo je do sledece greske: " + e.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
				} catch (CertificateException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), "Doslo je do sledece greske: " + e.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), "Doslo je do sledece greske: " + e.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
				} catch (NoSuchProviderException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), "Doslo je do sledece greske: " + e.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
				} catch (SignatureException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), "Doslo je do sledece greske: " + e.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				setVisible(false);
				
			}
		});
		
	}
	

}
