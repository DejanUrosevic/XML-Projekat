package gui.standard.form;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import security.KeyStoreWriter;
import util.KeyStore;
import util.Sertifikat;


public class KeyStoreForm extends JDialog{

	private JLabel aliasName = new JLabel("Alias Name: ");
	private JLabel keyPass = new JLabel("Keystore password :");
	private JLabel aliasPass = new JLabel("Alias password");
	
	private JTextField alias_text = new JTextField(10);
	private JTextField key_pass_text = new JTextField(10);
	private JTextField alias_pass = new JTextField(10);
	
	private JPanel main_panel = new JPanel();
	private JPanel[] main_panels = new JPanel[4];
	
	private JButton btnOk = new JButton("Ok");
	//private JButton btnCancel = new JButton("Cancel");
	private X509Certificate c;
	private KeyPair kp;
	private String alias;
	private Sertifikat ser = new Sertifikat();
	private String filePath;
	private Sertifikat serStari = new Sertifikat();
	private Sertifikat serNovi;
	private Map<String, Sertifikat> sertifikati = new HashMap<String, Sertifikat>(); 
	
	public KeyStoreForm(KeyPair keyPair, X509Certificate cert, String a, Sertifikat sertifikatNovi, String ksFilePath, Sertifikat sertifikatStari) throws FileNotFoundException, ClassNotFoundException, IOException{
		/*
		 * sertifikatNovi - onaj koji se kreira
		 * sertifikatStari - issuer
		 */
		
		setModal(true);
		setTitle("New keystore");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		sertifikati = ser.load();
		
		
		
		c = cert;
		kp = keyPair;
		alias = a;
		serStari= sertifikatStari; //issuer
		filePath = ksFilePath;
		serNovi = sertifikatNovi;	//koji se kreira
		
		for (int i = 0; i < main_panels.length-1; i++) {
			main_panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		
		main_panels[3] = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));
		
		main_panels[0].add(aliasName);
		main_panels[0].add(alias_text);
		
		main_panels[1].add(keyPass);
		main_panels[1].add(key_pass_text);
		
		main_panels[2].add(aliasPass);
		main_panels[2].add(alias_pass);
		
		main_panels[3].add(btnOk);
		//main_panels[3].add(btnCancel);
		
		for (int i = 0; i < main_panels.length; i++) {
			main_panel.add(main_panels[i]);
		}
		
		
		add(main_panel);
		pack();
		setLocationRelativeTo(null);
		
		btnOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
				
				if(alias_text.getText().trim().equals("") || key_pass_text.getText().trim().equals("") || alias_text.getText().equals("")){
					JOptionPane.showMessageDialog(new JFrame(), "Empty field!!", "Ok", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if(filePath == null){
					keyStoreWriter.loadKeyStore(filePath, key_pass_text.getText().toCharArray());
				}else{
					keyStoreWriter.loadKeyStore(filePath, serStari.getKs().getPassKS().toCharArray());
				}
				keyStoreWriter.write(alias, kp.getPrivate(), alias_pass.getText().toCharArray(), c);
				keyStoreWriter.saveKeyStore("./data/" + alias_text.getText() + ".jks", key_pass_text.getText().toCharArray());
				
				KeyStore keyStore = new KeyStore(alias_text.getText(), key_pass_text.getText(), alias, alias_pass.getText());
				
				for(Entry<String, Sertifikat> entry : sertifikati.entrySet()){
					if(serNovi.getCommon_name().equals(entry.getKey())){
						entry.getValue().setKs(keyStore);
						break;
					}
				}
				
				if(serStari != null){
					for(Entry<String, Sertifikat> entry : sertifikati.entrySet()){
						if(serStari.getCommon_name().equals(entry.getKey())){
							entry.getValue().addKS(keyStore);
							break;
						}
					}
				}
				
				try {
					ser.save(sertifikati);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				setVisible(false);
				//JOptionPane.showMessageDialog(new JFrame(), "Sertifikat je uspesno kreiran.", "Potvrda sertifikata", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
}
