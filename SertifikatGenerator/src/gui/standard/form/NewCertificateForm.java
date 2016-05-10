package gui.standard.form;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import security.CertificateGenerator;
import security.IssuerData;
import security.SubjectData;


public class NewCertificateForm extends JDialog{
	private static final String signature_algorithm = "SHA1withRSA";
	private static final String key_algorithm = "RSA";
	private static final int key_size = 1024;
	private CertificateGenerator cg;
	
	private JLabel validity_days = new JLabel("Validity (days):");
	private JLabel name = new JLabel("Common Name (CN):");
	private JLabel oUnit = new JLabel("Surname (SN):");
	private JLabel sName = new JLabel("Given Name (GN):");
	private JLabel oName = new JLabel("Organisation Name (ON):");
	private JLabel lName = new JLabel("Locality Name (L):");
	private JLabel country = new JLabel("Country (C):");
	private JLabel email = new JLabel("Email (E):");
	
	private JTextField validity_days_text = new JTextField(20);
	private JTextField name_text = new JTextField(20);
	private JTextField oUnit_text = new JTextField(20);
	private JTextField oName_text = new JTextField(20);
	private JTextField lName_text = new JTextField(20);
	private JTextField sName_text = new JTextField(20);
	private JTextField country_text = new JTextField(20);
	private JTextField email_text = new JTextField(20);

	private JButton save = new JButton("Save");
	private JButton cancel = new JButton("Cancel");
	
	private JPanel main_panel = new JPanel();
	private JPanel[] main_panels = new JPanel[9];
	
	
	public NewCertificateForm(){
	//	setSize(new Dimension(500,400));
		setTitle("New");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		for (int i = 0; i < main_panels.length-1; i++) {
			main_panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		main_panels[8] = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));

		main_panels[0].add(validity_days);
		main_panels[0].add(validity_days_text);

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
					//par kljuceva
					KeyPair keyPair = cg.generateKeyPair();
					
					//datumi
					SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
					
					
					Date startDate = new Date();
					long milisekunde = startDate.getTime();
					milisekunde += (Integer.parseInt(validity_days_text.getText())*86400000);
					Date endDate = new Date(milisekunde);
					//Date endDateParse = iso8601Formater.parse(endDate.);
					
					//podaci o vlasniku i izdavacu posto je self signed 
					//klasa X500NameBuilder pravi X500Name objekat koji nam treba
					X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
				    builder.addRDN(BCStyle.CN, name_text.getText());
				    builder.addRDN(BCStyle.SURNAME, oUnit_text.getText());
				    builder.addRDN(BCStyle.GIVENNAME, oUnit_text.getText());
				    builder.addRDN(BCStyle.O, oName_text.getText());
				    builder.addRDN(BCStyle.OU, oUnit_text.getText());
				    builder.addRDN(BCStyle.C, country_text.getText());
				    builder.addRDN(BCStyle.E, email_text.getText());
				    //UID (USER ID) je ID korisnika
				    builder.addRDN(BCStyle.UID, "123445");
					
					
					//Serijski broj sertifikata
				    int randomNum = 0 + (int)(Math.random() * 10000000); 
					String sn = String.valueOf(randomNum);
					//kreiraju se podaci za issuer-a
					IssuerData issuerData = new IssuerData(keyPair.getPrivate(), builder.build());
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
					
					
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchProviderException e) {
					e.printStackTrace();
				} catch (SignatureException e) {
					e.printStackTrace();
				}
			
				
				
			}
		});
		
	}
	

}