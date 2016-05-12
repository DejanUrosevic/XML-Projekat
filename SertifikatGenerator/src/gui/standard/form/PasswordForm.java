package gui.standard.form;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.Sertifikat;

public class PasswordForm extends JDialog{

	private JLabel pass = new JLabel("Password :");
	private JTextField pass_text = new JTextField(10);
	
	private JButton btnOk = new JButton("Ok");
	
	private JPanel main_panel = new JPanel();
	private JPanel[] main_panels = new JPanel[3];
	
	private String CAPass;
	private X509Certificate certificat;
	private KeyPair kp;
	private String alias;
	private Sertifikat serNovi;
	private Sertifikat serStari;
	
	public PasswordForm(KeyPair keyPair, X509Certificate cert, String a, Sertifikat sertifikatNovi, String passCA, Sertifikat sertifikatStari) {
		// TODO Auto-generated constructor stub
		/*
		 * sertifikatNovi - onaj koji se kreira
		 * passCA - alias ks issuer-a
		 * sertifikatStari - issuer 
		 */
		setModal(true);
		setTitle("Password check");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		kp = keyPair;
		certificat = cert;
		alias = a;
		serNovi = sertifikatNovi; // koji se kreira
		CAPass = passCA;
		serStari = sertifikatStari; // issuer
		
		for (int i = 0; i < main_panels.length-1; i++) {
			main_panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		
		main_panels[2] = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));
		
		main_panels[0].add(pass);
		main_panels[0].add(pass_text);
		
		main_panels[1].add(btnOk);
		
		main_panel.add(main_panels[0]);
		main_panel.add(main_panels[1]);
		
		add(main_panel);
		pack();
		setLocationRelativeTo(null);
		
		btnOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(pass_text.getText().equals(CAPass)){
					try {
						KeyStoreForm ksf = new KeyStoreForm(kp, certificat, alias, serNovi, "./data/" + serStari.getKs().getAlias()+".jks", serStari);
						ksf.setVisible(true);
						setVisible(false);
					} catch (ClassNotFoundException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					JOptionPane.showMessageDialog(new JFrame(),"Password does not match!!", "Error", JOptionPane.WARNING_MESSAGE);
					//JOptionPane.showMessageDialog(this, "Password does not match!!","Error", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		for (int i = 0; i < main_panels.length; i++) {
			main_panel.add(main_panels[i]);
		}
		
		
	}
	
}
