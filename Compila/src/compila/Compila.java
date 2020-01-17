package compila;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Compila extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane = new JPanel();
	
	private JTextArea txtA;
	
	
	/*des listes pour détecter l'utilisation d'une variable non déclaré
	 *et pour respecter le type de variables dans les instructions : affectation d'une variable à une variable,
				affectation d'une valeur à une variable et dans la comparaison dans la condition "If". 
	 */
	private ArrayList<String> ListeString = new ArrayList<String>(); //stocke les identicateurs de type chaîne de caractère
	private ArrayList<String> ListeInt = new ArrayList<String>();	//stocke les identicateurs de type entier 
	private ArrayList<String> ListeReel = new ArrayList<String>(); //stocke les identicateurs de type reel
	private ArrayList<String> ListeErrDcl = new ArrayList<String>(); //stocke les erreurs qui s'affichent à la fin du compilation du fichier
	
	//liste pour stocker tous les mots du fichiers 
	static ArrayList<String> mots = new ArrayList<String>();
	//liste pour stocker tous les lignes du fichiers 
	static ArrayList<String> lignes = new ArrayList<String>();
	
	//liste pour stocker les resultats de l'analyse lexical
	private ArrayList<String> sortie_lexic = new ArrayList<String>();
	
	//listes pour stocker les mots d'une chaîne de caractère passée en parametre 
	private String[] mot;
	private String[] mot1;
	
	
	private int ligneCode = 0; //numero de la ligne à analyser sémantiquement
	private int nbr_erreur = 0; 
	private int nbr_start = 0;
	private int nbr_finish = 0;
	private int nbr_start_prgm = 0;
	private int nbr_end_prgm = 0;
	private int nbr_If = 0;
	
	/*
	 * 
	 * Compila :)
	 * 
	 */
	public Compila() {

		//création de la fenêtre
		this.setTitle("Compila");	 
		this.setSize(650, 550);
		this.setLocationRelativeTo(null);
		this.setResizable(false); 
		this.setIconImage(new ImageIcon("logo2.png").getImage()); 

		
		//titre
		JPanel pan1 =new JPanel();
		pan1.setLayout(null);
		pan1.setLocation(0, 0);
		pan1.setSize(650, 100);
		pan1.setBackground(new Color(0,68,93,200));
		contentPane.add(pan1);
		
		JLabel logo = new JLabel(new ImageIcon("logo1.png"));
		logo.setBounds(30, 0, 100, 100);
		pan1.add(logo);
		
		JLabel titre = label(" Mon analyseur lexicale, ",270, 0, 500, 50,new Color(255,255,191),25);
		pan1.add(titre);
		
		JLabel titre1 = label(" syntaxique et sémantique ",300,45,500,50,new Color(255,255,191),25);
		pan1.add(titre1);

		JLabel compila = label("COMPILA",110,30,200,100,new Color(0,18,55),20);
		compila.setFont(new Font("Chiller",Font.BOLD,40));
		pan1.add(compila);
		
		//---
		JPanel pan2 =new JPanel();
		pan2.setLocation(0, 470);
		pan2.setSize(650, 80);
		pan2.setBackground(new Color(0,68,93,200));

		JLabel footer = label(" BRAIKIA Houria - HALOUI Sarra ",this.getWidth()/2-50,470,100,50,new Color(255,255,255),15);
		pan2.add(footer);
		
		contentPane.add(pan2);


		//Affichage du resultat de compilation
		txtA = new JTextArea();
		txtA.setEditable(false);
		txtA.setForeground(Color.BLACK);
		txtA.setBackground(Color.WHITE);
		txtA.setFont(new Font("Comic sans MS", 13, 13));
		txtA.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,68,93,200)), " Sortie ", 0, 0, new Font("Comic sans MS", Font.BOLD, (int) 15.), new Color(0,68,93,200)));
		
		contentPane.add(txtA);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(200, 110, 430, 350);
		scrollPane.setViewportView(txtA);
		contentPane.add(scrollPane);

		//----
		
		//les boutons 
		JButton charg=  bouton("charger",25, 110, 140, 80 ,new ImageIcon("rech.png"));
		charg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					txtA.setText("");
					chargerFichier();
					int i = 0;
					while (i < lignes.size()) {
						txtA.setText(txtA.getText()+lignes.get(i)+"\n");
						i++;
						}
				} catch (FileNotFoundException e1) {
				
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(charg);
		
		//--------
		
		JButton lex= bouton("lexicale",25, 200, 140, 80,  new ImageIcon("lex.png"));
		lex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nbr_erreur = 0;
				txtA.setText("");
				analyseLexicale(); //remplissage de la liste sortie_lexic
				
				int i = 0;
				while (i < mots.size()) {
					if(sortie_lexic.get(i) == "Erreur Lexical")  nbr_erreur++; //compter le nbr d erreurs
					//affichage d'un mot de code + son analyse lexical 
					txtA.setText(txtA.getText()+mots.get(i)+"		=> " + sortie_lexic.get(i)+"\n");
					i++;
					}
				txtA.setText(txtA.getText()+"\n-------------------------------------------------------------"); 
				
				if(nbr_erreur==0)
					JOptionPane.showMessageDialog( null," Analyse sémantique terminer ! aucune erreur detecter :)");
				else
					txtA.setText(txtA.getText() + "\n Nombre d erreurs lexical = " + nbr_erreur); 
			}
		});
		contentPane.add(lex);
		
		//--------
		
		JButton syn= bouton("syntaxique",25, 290, 140, 80,   new ImageIcon("syn.png"));
		syn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nbr_erreur = 0;
				txtA.setText("");
				
				int i = 0;
				while (i < lignes.size()) {
					
					//vérifier les lignes vides au début du prgm
					while(lignes.get(i).isEmpty()) {
						i++;
					}
					String res = analyseSyntaxique(lignes.get(i));
					if(res=="Erreur Syntaxique!") nbr_erreur++; //compter le nbr d erreurs
					//affichage d'une ligne de code + son analyse syntaxique
					txtA.setText(txtA.getText()+i+") "+lignes.get(i) + "	=> " +res+"\n");
					i++;
				}
				
				txtA.setText(txtA.getText()+"\n-------------------------------------------------------------"); 
				
				if(nbr_erreur==0)
					JOptionPane.showMessageDialog( null,"\n Analyse syntaxique terminer ! aucune erreur detecter :)");
				else
					txtA.setText(txtA.getText() + "\n Nombre d erreurs syntaxiques = "+nbr_erreur);
			}
		});
		contentPane.add(syn);
		
		JButton sem= bouton("sémantique",25, 380, 140, 80,  new ImageIcon("sem.png"));
		sem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ligneCode = 0;
				
				nbr_erreur = 0;
				
				nbr_start = 0;
				nbr_finish = 0;
				nbr_start_prgm = 0;
				nbr_end_prgm = 0;
				txtA.setText("");
				
				while (ligneCode < lignes.size()) {
					//vérifier les lignes vides au début du prgm
					while(lignes.get(ligneCode).isEmpty()) {
						ligneCode++; 
					}
					
					if(lignes.get(ligneCode).equals("Start_Program")||lignes.get(ligneCode).equals("End_Program")) {
						/*
						 * compter le nbr de "Start_Program" ou "End_Program".
						 */
						analyseSemantique(lignes.get(ligneCode));
						ligneCode++;
					}
					else if(lignes.get(ligneCode).equals("Start")||lignes.get(ligneCode).equals("Finish")) {
						/*
						 * Compte le nombre de "Start" et "Finish".
						 */
						analyseSemantique(lignes.get(ligneCode));
						ligneCode++;
					}
					else {
						String res =analyseSemantique(lignes.get(ligneCode));
						if(res== "Erreur Sémentique!") nbr_erreur++;  //compter le nbr d erreurs semantique
						txtA.setText(txtA.getText()+ligneCode+") "+lignes.get(ligneCode) + "	=> " +res+"\n");
						ligneCode++;
					}
				}
				
				txtA.setText(txtA.getText()+"\n-------------------------------------------------------------"); 
				
				/*
				 * verifier si le programme commence par Start_Program et se termine par  End_Program
				 */
				if(!start_end_prgm()) {
					nbr_erreur++;
					txtA.setText(txtA.getText()+lignes.get(ligneCode) + "\n Erreur sémantique Start_Program ; End_Program !");
				}
				 
				
				/*
				 *  verifier si le fichier contient pusieurs "Start_Program"
				 */
				if(nbr_start_prgm>1) {
					nbr_erreur++;
					txtA.setText(txtA.getText() + "\n Erreur sémantique nombre de Start_Program = " + nbr_start_prgm);
				}
				
				/*
				 *  verifier si le fichier contient pusieurs "End_Program" 
				 */	
				if(nbr_end_prgm>1) {
					nbr_erreur++;
					txtA.setText(txtA.getText() + "\n Erreur sémantique nombre de End_Program = " + nbr_end_prgm);
				}
				
				//--------
				if(nbr_start!=nbr_finish){
					nbr_erreur++;
					txtA.setText(txtA.getText() + "\n Erreur sémantique Start ; Finish !");
				}
				
				//afficher le nbr d erreur
				if(nbr_erreur==0)
					JOptionPane.showMessageDialog( null,"Analyse sémantique terminer ! aucune erreur detecter :)");
				else
					txtA.setText(txtA.getText() + "\n Nombre d erreurs sémantiques = " + nbr_erreur);
				
				//afficher les erreurs
				for(String ele : ListeErrDcl)
			       {
			       	txtA.setText(txtA.getText() + "\n" +ele);
			       }
				
				//vider les listes
				ListeErrDcl.clear();
				ListeString.clear();
				ListeInt.clear();
				ListeReel.clear();
			}
			
		});
		contentPane.add(sem);
		

		contentPane.setLayout(null);
		this.setContentPane(contentPane);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setVisible(true);
		
	}
	
	//methode pour creer un label
	public JLabel label(String str,int x,int y,int langeur, int longueur,Color couleur,int size) {
		Font font = new Font("Comic sans MS", Font.BOLD, size);
		JLabel lab= new JLabel(str);
		lab.setFont(font);
		lab.setForeground(couleur);
		lab.setSize(langeur, longueur);
		lab.setLocation(x, y);
		return lab;
	}
	
	//methode pour creer un bouton
	public JButton bouton(String titre, int posX, int posY, int largeur, int longueur , ImageIcon image) {
		JButton btn = new JButton(titre, image);
		btn.setBounds(posX, posY, largeur, longueur);
		btn.setBorderPainted(false);
		btn.setBackground(Color.white); 
		Font font = new Font("Comic sans MS", Font.ITALIC, 13);
		btn.setFont(font);
		return btn;
	}
	
	/*******************************************Charcher un fichier*******************************************************/
	public static void chargerFichier() throws FileNotFoundException {
		JFileChooser choice = new JFileChooser("C:\\Users\\pcstar\\Desktop");
		FileNameExtensionFilter filter = new FileNameExtensionFilter ("", "compila");
		choice.addChoosableFileFilter(filter);
		
		if(choice.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			File file = choice.getSelectedFile();
			Scanner sc_lignes = new Scanner(file);
			Scanner sc_mots = new Scanner(file);
			mots.clear();
			lignes.clear();
			
				while(sc_lignes.hasNextLine()){
					lignes.add(sc_lignes.nextLine());  //remplir tous les lignes du code dans la liste lignes
				}
				while(sc_mots.hasNext()){
					 mots.add(sc_mots.next());   //remplir tous les mots du code dans la liste mots
					}
				
			sc_mots.close();
			sc_lignes.close();
			}
	}
	
	/*************************************************Analyse lexicale*********************************************************/
	//---------------------------------------------//
	public boolean isNumber(String chaine, int i) {
		char[] nombre = {'0','1', '2', '3', '4', '5', '6', '7', '8', '9' };
		int j = 0;
		while (j < nombre.length) {
			if (chaine.charAt(i) == nombre[j]) {
				return true;
			}
			j++;
		}

		return false;
	}

	public String nombre(String chaine) { 
		int i = 0;
		int token_position = 0;
		boolean point_unique = true;
		while (i < chaine.length()) {
			if (isNumber(chaine, i)) token_position++;
			else if(point_unique & chaine.charAt(token_position)=='.') {
				token_position++;
				point_unique = false;
			}
			i++;
		}

		if (token_position == chaine.length() && !chaine.contains(".")) return "Nombre entier";
		else if (token_position == chaine.length() && !point_unique) return "Nombre reel";
		return null;

	}
	//---------------------------------------------//

	public boolean isChar(String chaine, int i) {
		char[] alphabet = { 'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i',
				'J', 'j', 'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P', 'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T',
				't', 'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z' };
		int c = 0;
		while (c < alphabet.length) {
			if (chaine.charAt(i) == alphabet[c]) {
				return true;
			}
			c++;
		}
		return false;

	}

	public String id(String chaine) {
		boolean verifier_Premier = false;
		boolean tiret_unique = true;
		int token_pososition = 0;
		int i = 0;
		
		if (isChar(chaine, 0)) {
			token_pososition++;
			verifier_Premier = true;
		}
		else
			return null;
		
		if (verifier_Premier == true && chaine.length() == 1)
			return "identificateur";
		
		if(chaine.charAt(i+1)==',') return "Identificateur";
		
		else if (chaine.length() > 1) {
			i = 1;
			while (i < chaine.length()) {

				if (isChar(chaine, i))
					{token_pososition++;
					tiret_unique=true;
					}
				else if (isNumber(chaine, i))
					{token_pososition++;
					tiret_unique=true;
					}
				else if (chaine.charAt(i) == '_' && tiret_unique) {
					tiret_unique=false;
					token_pososition++ ;
				}
				
				i++;
			}
			if (token_pososition == chaine.length())
				return "Identificateur";
		}
		return null;
	}
	
	
	//---------------------------------------------//

	public String UL_reserve(String chaine) {
		
		String[] mot_reserve = {"Int_Number", "Real_Number", "Start_Program", "End_Program", "Start", "Finish", "Affect", "to", "ShowMes", "ShowVal",
				"If", "Else", "Give", ":", ",", ";;", "--", "<", ">", "=", "!="	, ">=", "<=", "//." ,"\"","String","'"};

		String[] Affichage = {"Mot reserve declaration d'un entier","Mot reserve declaration d'un reel","Mot reserve debut d'un programme","Mot reserve fin d'un programme",
				"Mot reserve debut d'un bloc","Mot reserve fin d'un bloc", "Mot reserve pour affectation entre variable", "Mot reserve", 
				"Mot reservé pour afficher un message","Mot reservé pour afficher une valeur", " Mot reserve pour une condition SI", 
				"Mot reserve pour une condition SINON", "Mot reserve pour affectation d une valeur a une variable", "caractere reserve", "caractere reservé virgule",
				"Mot reserve fin instruction", "caractere reserve pour une condition", "symbol inferieur", "symbol superieur", "symbole egale" ,
				 "symbole different", "symbol superieur ou egale", "symbol inferieur ou egale", "Mot reservé pour un commentaire" ,"caractere reserve",
				 "Mot reserve declaration d une chaine de caractère","caractere reservé apostrophe"};
		
		int i = 0;
		while (i < mot_reserve.length) {
			if (chaine.equals(mot_reserve[i])) {
				return Affichage[i];
			}
			i++;
		}
		return null;
	}


	//---------------------------------------------//
	public void analyseLexicale(){
		int i = 0;

		while (i < mots.size()) {
			if (UL_reserve(mots.get(i)) != null) {
				sortie_lexic.add(UL_reserve(mots.get(i)));
			} else if (id(mots.get(i)) != null) {
				sortie_lexic.add(id(mots.get(i)));
			} else if (nombre(mots.get(i)) != null) {
				sortie_lexic.add(nombre(mots.get(i)));
			}
			else sortie_lexic.add("Erreur Lexical");

			i++;
		}
	}
/*************************************************Analyse synatxique********************************************************/
	//-------------------les instructions : Give, Affect, ShowMsg et ShowVal
	public String action(String chaine) {
		int i = 0;
		mot1 = chaine.split("\\s+");   //stocker tous les mots de la chaine pour pouvoir analyser la chaine mot par mot
		
		if(mot1[i].equals("Give")){
			i++;	
			if(id(mot1[i]) != null){
				i++;
				if(mot1[i].equals(":")) {
					i++;
					if(nombre(mot1[i]) == "Nombre entier") {
						i++;
						if(mot1[i].equals(" "))
							i++;
							if(mot1[i].equals(";;")) 
								return "Affectation d'une valeur entiere à "+mot1[i-3];
						}
						else if(nombre(mot1[i]) == "Nombre reel"){
							i++;
							if(mot1[i].equals(";;"))
								return "Affectation d'une valeur reel à "+mot1[i-3];
						}
				}}
			}
		
		else if(mot1[i].equals("Affect")){
			i++;
			if(id(mot1[i]) != null){
				i++;
			if(mot1[i].equals("to")){
				i++;
				if(id(mot1[i]) != null) {
					i++;
					if(mot1[i].equals(";;")) 
						return "Affectation entre variable de "+mot1[i-3]+" à "+mot1[i-1];
					}
				}
			}
		}
		
		else if(mot1[i].equals("ShowVal")){
			i++;
			if(mot1[i].equals(":")) {
				i++;
				if(id(mot1[i]) != null) {
					i++;
					if(mot1[i].equals(";;")) 
						return "Affichage de la valeur de "+mot1[i-1];
				}}}
		
		else if(mot1[i].equals("ShowMes")){
			i++;
			int guill = 2;
			if(mot1[i].equals(":")) {
				i++;
				if(mot1[i].equals("\"")){
					i++; guill--; 
					while(id(mot1[i]) != null || mot1[i].contains("'")&& guill==1)
					{	i++;
						if(mot1[i].equals("\""))
							{ guill--; 	i++;}
					} 
						if(mot1[i].equals(";;")&&guill==0)
							return "Affichage d'un message à l'écran";
		}}}
		
		return "Erreur Syntaxique!"; 
	}
	
	/*************************************************************************************************************************/	
  //-------compte le nbr d occurence d un caractere dans une chaine de caractere
	public int nbrOccurence(String str, char c) { 
		int compteur = 0;
		for (int i = 0; i<str.length() ; i++)
			if(str.charAt(i)==c)
			compteur++;
		return compteur;
			
	}	
	//-----
	public String analyseSyntaxique(String chaine) {
		String res = "Erreur Syntaxique!";
		
		if(chaine.equals("Start_Program")) return "Début du programme";
		else if (chaine.equals("End_Program")) return "fin du programme";
		else if(chaine.equals("Start")) return "Début d'un bloc";
		else if(chaine.equals("Finish")) return "Fin d'un bloc";
		else if(chaine.equals("Else")) return "Sinon"; //Else avec une seule instructions
		else if(chaine.startsWith("//.")) return "un commentaire";
		
		/* verifier si il existe : 
		 * une instruction par ligne.
		 * et si l instruction se termine par deux points virgules.
		 */
		else if(chaine.contains(" ")&&chaine.endsWith(";;")&&nbrOccurence(chaine,';')==2) {
			int i = 0, k=1;
			mot = chaine.split("\\s+");  //stocker tous les mots de la chaine pour pouvoir analyser la chaine mot par mot
			
			if(mot[i].equals("Int_Number")){
				i++;
				if(mot[i].equals(":")) {
					i++;
					if(id(mot[i]) != null){
						i++;
						while(mot[i].equals(",")){
							i++;
							k++;
							if(id(mot[i]) != null) i++;
						}
						if(mot[i].equals(";;")) 
							return "Déclaration de "+k+" entiers";
						}}}
			
			else if(mot[i].equals("Real_Number")){
				i++;
				if(mot[i].equals(":")) {
					i++;
					if(id(mot[i]) != null){
						i++;
						while(mot[i].equals(",")){
							i++;
							k++;
							if(id(mot[i]) != null) i++;
						}
						if(mot[i].equals(";;"))
							return "Déclaration de "+k+" reels";
						}}}
			
			else if(mot[i].equals("String")){
				i++;
				if(mot[i].equals(":")) {
					i++;
					if(id(mot[i]) != null){
						i++;
						while(mot[i].equals(",")){
							i++;
							k++;
							if(id(mot[i]) != null) i++;
						}
						if(mot[i].equals(";;"))
							return "Déclaration de "+k+" chaine de caractères";
						}}}
				
			//If avec plusieurs instructions
			else if(mot[i].equals("If")){
				i++;
					if(mot[i].equals("--")) {
						i++;
						if(id(mot[i]) != null) {
							i++;
							if(mot[i].equals("<") || mot[i].equals(">") || mot[i].equals("==")||mot[i].equals("<=") || mot[i].equals(">=") || mot[i].equals("!=")) {
								i++;
								if(id(mot[i]) != null) {
									i++;
									if(mot[i].equals("--")) {
										String r = action(chaine.substring(chaine.indexOf(mot[i+1])));
										if(r=="Erreur Synaxique!")
											return "Erreur Synaxique!";
										return "Condition alors "+ r;
									}
			
			}}}}}
			
			//Else avec plusieurs instructions
			else if(mot[i].equals("Else")) {
				String r = action(chaine.substring(chaine.indexOf(mot[i+1])));
				if(r=="Erreur Synaxique!")
					return "Erreur Synaxique!";
					
				return "Else alors " +r;
			}
		
			else 
				res = action(chaine);
				}
		
		
		//If avec une seule instructions
		else if(chaine.contains(" ")&&chaine.startsWith("If")) {
			String[] motIf = chaine.split(" ");
			int i = 0;
			if(motIf[i].equals("If")) {
				i++;
				if(motIf[i].equals("--")) {
					i++;
					if(id(motIf[i]) != null) {
						i++;
						if(motIf[i].equals("<") || motIf[i].equals(">") || motIf[i].equals("==")||motIf[i].equals("<=") || motIf[i].equals(">=") || motIf[i].equals("!=")) {
							i++;
							if(id(motIf[i]) != null) {	
								i++;
								if(motIf[i].equals("--")) return "Condition If";
							}
						}}}}}
		return res;	
	}
	/**************************************************Analyse sémantique***************************************************************************************/
	public Boolean start_end_prgm() { //methode qui verifie le debut et la fin du code
		
		//vérifier le début du prgm
		int i =0;
		
		while(lignes.get(i).isEmpty()) {
			i++;
		}

		if(lignes.get(i).equals("Start_Program")) 
			return true;
			
		//vérifier la fin du prgm
		int k = lignes.size()-1;
		
		while(lignes.get(k).isEmpty()) {
			i++;
		}
		if(lignes.get(k).equals("End_Program")) 
			return true;
		
		
		return false;
	}
	
	//----------------------
	public String actionSemantique(String chaine) {
		int i = 0;
		mot1 = chaine.split("\\s+"); //stocker tous les mots de la chaine 
		
		if(mot1[i].equals("Give")){
			String id=""; //stocker le type du l'identificateur utilisé
			i++;	
			if(id(mot1[i]) != null){//verifier si l'identificateur n'est pas declaré et stocker son type 
				if(ListeInt.contains(mot1[i])) id = "int"; 
				else if(ListeReel.contains(mot1[i])) id = "float";
				else {
					nbr_erreur++; //stocker l erreur et le ligne de l erreur
					ListeErrDcl.add("Ligne "+ligneCode+": "+mot1[i]+" n est pas declarer comme un nombre!");
				}
				i++;
				if(mot1[i].equals(":"))
					i++;
					if(nombre(mot1[i]) == "Nombre entier") {
						if(id.equals("float")){  
							nbr_erreur++;   //si id est un reel et on lui affecte un valeur entiere 
							ListeErrDcl.add("Ligne "+ligneCode+": affectation d une valeur entiere a une variable de type reele!");
						}
						i++;
							if(mot1[i].equals(";;"))
								return mot1[i-3]+" = "+mot1[i-1];
						}
						else if(nombre(mot1[i]) == "Nombre reel"){
							if(id.equals("int")){
								nbr_erreur++;	//si id est un entier et on lui affecte un valeur reeele 
								ListeErrDcl.add("Ligne "+ligneCode+": affectation d une valeur reele a une variable de type entier!");
							}
							i++;
							if(mot1[i].equals(";;")) return mot1[i-3]+" = "+mot1[i-1]+" ;";
						}
				}
			}
		
		else if(mot1[i].equals("Affect")){
			String id1="" ,id2 = "";
			i++;
			if(id(mot1[i]) != null){	//verifier si l'identificateur 1 n'est pas declaré et stocker son type 
				if(ListeInt.contains(mot1[i])) id1 = "int";
				else if(ListeReel.contains(mot1[i])) id1 = "float";
				else if (ListeString.contains(mot1[i])) id1 = "string";
				else {
					nbr_erreur++;
					ListeErrDcl.add("Ligne "+ligneCode+": "+mot1[i]+" n est pas declarer!");
				}
				i++;
			if(mot1[i].equals("to")){
				i++;
				if(id(mot1[i]) != null) {	//verifier si l'identificateur 2 n'est pas declaré et stocker son type 
					if(ListeInt.contains(mot1[i])) id2 = "int";
					else if(ListeReel.contains(mot1[i])) id2 = "float";
					else if (ListeString.contains(mot1[i])) id2 = "string";
					else {
						nbr_erreur++;
						ListeErrDcl.add("Ligne "+ligneCode+": "+mot1[i]+" n est pas declarer!");
					}
					i++;
					if(mot1[i].equals(";;")) {
						if(!id1.equals(id2)&&!id1.isEmpty()&&!id2.isEmpty()) { //comparer les types des identificateurs
							nbr_erreur++;
							ListeErrDcl.add("Ligne "+ligneCode+": affectation de variables de type different!");
						}
						return mot1[i-1]+" = "+mot1[i-3]+" ;";
					}
					}
				}
			}
		}
		
		else if(mot1[i].equals("ShowVal")){
			i++;
			if(mot1[i].equals(":")) {
				i++;
				if(id(mot1[i]) != null) {  //stocker le type du l'identificateur utilisé
					if(!ListeInt.contains(mot1[i])&&!ListeReel.contains(mot1[i])&&!ListeString.contains(mot1[i])) { 
						//verifier si l identificateur n'est pas declaré
						nbr_erreur++;
						ListeErrDcl.add("Ligne "+ligneCode+": "+mot1[i]+" n est pas declarer!");
					}
					i++;
					if(mot1[i].equals(";;")) 
						return "printf("+mot1[i-1]+");";
				}}}
		
		else if(mot1[i].equals("ShowMes")){
			i++;
			int guill = 2;
			if(mot1[i].equals(":"))
				i++;
				if(mot1[i].equals("\""))
					{ i++; guill--; }
					while(id(mot1[i]) != null && guill>0){
						i++;
						if(mot1[i].equals("\""))
							{ i++; guill--; }	
						}
						if(mot1[i].equals(";;"))
							return "printf(\"Affichage d'un message à l'écran\");";
				}
		
		return "Erreur Sémantique!";
		
		
	}
	
	//-------------
	
	public String analyseSemantique(String chaine) {
		//s'il y a une erreur syntaxique alors il y a une erreur semantique
		if(analyseSyntaxique(chaine)=="Erreur Syntaxique!") return "Erreur Sémentique!";
		else if(chaine.equals("Start_Program"))  nbr_start_prgm++; 
		else if(chaine.equals("End_Program")) nbr_end_prgm++;
		else if(chaine.equals("Start")) nbr_start++;
		else if(chaine.equals("Finish")) nbr_finish++;
		else if(chaine.startsWith("Else")) {
			if(nbr_If == 0) { // si le code contient un Else sans If
				nbr_erreur++;
				ListeErrDcl.add("Ligne "+ligneCode+": Else sans If!");
			}
			
			else return "else";
		}
		else if(chaine.startsWith("//.")) return "/*un commentaire*/";
		else if(chaine.contains(" ")) {
			int i = 0;
			mot = chaine.split("\\s+");
			String rtn; //stocker le resultat de l'analyse
			
			if(mot[i].equals("Int_Number")){
				i++;
				rtn = "int ";
				i++;
				rtn = rtn + mot[i];
				ListeInt.add(mot[i]);
				i++; 
				while(mot[i].equals(",")){
					i++;
					rtn = rtn + " , "+ mot[i];
					if(ListeInt.contains(mot[i])) {
						ListeErrDcl.add("Ligne "+ligneCode+": "+mot[i]+" est deja declarer!");
						nbr_erreur++;
					}
					ListeInt.add(mot[i]);
					i++;
					}
						if(mot[i].equals(";;")) 
							return rtn + ";";                      
					}
			
			else if(mot[i].equals("Real_Number")){
				i++;
				rtn = "float ";
				i++;
				rtn = rtn + mot[i];
				ListeReel.add(mot[i]);
				i++;
						while(mot[i].equals(",")){
							i++;
							rtn = rtn + " , " + mot[i];
							if(ListeReel.contains(mot[i])) {
								nbr_erreur++;
								ListeErrDcl.add("Ligne "+ligneCode+": "+mot[i]+" est deja declarer!");
							}
							ListeReel.add(mot[i]);
							i++;
						}
						if(mot[i].equals(";;")) 
							return rtn + ";";
					}
				
				
			else if(mot[i].equals("String")){
				i++;
				rtn = "String";
				i++;
				rtn = rtn + " " + mot[i];
				ListeString.add(mot[i]);
				i++;
					while(mot[i].equals(",")){
							i++;
							rtn = rtn + " , " + mot[i];
							if(ListeString.contains(mot[i])) {
								nbr_erreur++;
								ListeErrDcl.add("Ligne "+ligneCode+": "+mot[i]+" est deja declarer!");
							}
							ListeString.add(mot[i]);
							i++;
						}
						if(mot[i].equals(";;"))
							return rtn+";";
						}
			
			//If avc une seule instruction
			else if(mot[i].equals("If")){
				String id1 ="" ,id2 = "";
				i++;
					if(mot[i].equals("--")){
						i++;
						if(id(mot[i]) != null){
							if(ListeInt.contains(mot[i])) id1 = "int";
							else if(ListeReel.contains(mot[i])) id1 = "float";
							else {
								nbr_erreur++;
								ListeErrDcl.add("Ligne "+ligneCode+": "+mot[i]+" n est pas declarer comme un nombre!");
							}
							i++;
							if(mot[i].equals("<") || mot[i].equals(">") || mot[i].equals("==")||mot[i].equals("<=") || mot[i].equals(">=") || mot[i].equals("!=")){
								i++;
								if(id(mot[i]) != null){
									if(ListeInt.contains(mot[i])) id2 = "int";
									else if(ListeReel.contains(mot[i])) id2 = "float";
									else {
										nbr_erreur++;
										ListeErrDcl.add("Ligne "+ligneCode+": "+mot1[i]+" n est pas declarer comme un nombre!");
									}
									i++;
									if(mot[i].equals("--")) {
										nbr_If++;
										String r = actionSemantique(chaine.substring(chaine.indexOf(mot[i+1])));
										if(r=="Erreur Sémantique!")
											 return r;
										else {
											if(!id1.equals(id2)&&!id1.isEmpty()&&!id2.isEmpty()) {
												nbr_erreur++;
												ListeErrDcl.add("Ligne "+ligneCode+": comparaison de variables de type different!");
											}
											return "if("+mot[i-3]+mot[i-2]+mot[i-1]+") "+ r; 
										}
									}
								}
							}
						}
					}
			}
			
			//Else avec plusieurs instructions
			else if(mot[i].equals("Else")) {
				String r = actionSemantique(chaine.substring(chaine.indexOf(mot[i+1])));
				if(r=="Erreur Sémantique!")
					return "Erreur Sémantique!";
				
				if(nbr_If == 0) { // si le code contient un Else sans If
					nbr_erreur++;
					ListeErrDcl.add("Ligne "+ligneCode+": Else sans If!");
				}	
				
				return "Else " +r;
			}
			
			else return actionSemantique(chaine);
		}
		
		//If avc plusieurs instructions
		else if(chaine.contains("\\s+")&&chaine.startsWith("If")) {
			String[] motIf = chaine.split("\\s+");
			String id1 ="" ,id2 = "";
			int i = 0;
			if(motIf[i].equals("If")){
				i++;
				if(motIf[i].equals("--")){
					i++;
					if(id(motIf[i]) != null){
						if(ListeInt.contains(motIf[i])) id1 = "int";
						else if(ListeReel.contains(motIf[i])) id1 = "float";
						else {
							nbr_erreur++;
							ListeErrDcl.add("Ligne "+ligneCode+": "+motIf[i]+" n est pas declarer comme un nombre!");
						}
						i++;
						if(motIf[i].equals("<") || motIf[i].equals(">") || motIf[i].equals("==")||motIf[i].equals("<=") || motIf[i].equals(">=") || motIf[i].equals("!=")){
							i++;
							if(id(motIf[i]) != null){
								if(ListeInt.contains(motIf[i])) id2 = "int";
								else if(ListeReel.contains(motIf[i])) id2 = "float";
								else {
									nbr_erreur++;
									ListeErrDcl.add("Ligne "+ligneCode+": "+motIf[i]+" n est pas declarer comme un nombre!");
								}
								i++;
								if(motIf[i].equals("--")) {
									nbr_If++;
									if(!id1.equals(id2)&&!id1.isEmpty()&&!id2.isEmpty()) {
										nbr_erreur++;
										ListeErrDcl.add("Ligne "+ligneCode+": comparaison de variables de type different!");
									}
									return "if("+motIf[i-3]+motIf[i-2]+motIf[i-1]+")"; 
								}
							}
						}
					}
				}
		}
		}
		return "Erreur Sémantique";	
	}
	
	
	
	
	
	
/*************************************************************************************************************************************************/
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		new Compila();
	}

}

