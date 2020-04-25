package GUI;

import luceneCode.Search;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.apache.lucene.queryparser.classic.ParseException;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.Rectangle;

public class Menu {
	
	private JFrame frmSearchEngine;
	private JTextField textField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu window = new Menu(); //create a Menu object
					window.startMenu(); //call startMenu method
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Menu() {} //empty constractor
	
	public void startMenu() { //first window with choices: Search Restaurants and Search Reviews
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblRestaurantReviews = new JLabel("\"Restaurant & Reviews\"");
		lblRestaurantReviews.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblRestaurantReviews.setBounds(127, 21, 181, 37);
		frame.getContentPane().add(lblRestaurantReviews);
		
		JButton btnNewButton = new JButton("Search Restaurants");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false); //after user's selection, close this window
				search("Restaurants"); 	 //and call search method (next window)
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton.setBounds(127, 86, 173, 51);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnSearchReviews = new JButton("Search Reviews");
		btnSearchReviews.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false); //after user's selection, close this window
				search("Reviews");       //and call search method (next window)
			}
		});
		btnSearchReviews.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSearchReviews.setBounds(127, 162, 173, 51);
		frame.getContentPane().add(btnSearchReviews);
		frame.setVisible(true);
	}
	
	public void search(String kindOfSearch) { //Search Engine's window
		frmSearchEngine = new JFrame();
		frmSearchEngine.setTitle("Search Engine");
		frmSearchEngine.setBounds(100, 100, 735, 369);
		frmSearchEngine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSearchEngine.getContentPane().setLayout(null);
		frmSearchEngine.setVisible(true);
		
		JLabel lblPleaseSelectTheme = new JLabel("What are you searching for?");
		lblPleaseSelectTheme.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblPleaseSelectTheme.setBounds(45, 22, 202, 27);
		frmSearchEngine.getContentPane().add(lblPleaseSelectTheme);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
		comboBox.setBounds(45, 55, 202, 27);
		frmSearchEngine.getContentPane().add(comboBox);
		comboBox.addItem(""); //choices for Field's search
		if(kindOfSearch.equals("Restaurants")) { //for Restaurants
			comboBox.addItem("name");
			comboBox.addItem("address");
			comboBox.addItem("city");
			comboBox.addItem("state");
			comboBox.addItem("review");
			comboBox.addItem("location and review");
		}
		else if(kindOfSearch.equals("Reviews")) { //for Reviews
			comboBox.addItem("review");
			comboBox.addItem("name");
			comboBox.addItem("user");
			comboBox.addItem("stars_review");
			comboBox.addItem("name and review");
		}
		
		JLabel lblInsertTextHere = new JLabel("Insert text here:"); 
		lblInsertTextHere.setFont(new Font("AR CARTER", Font.PLAIN, 42));
		lblInsertTextHere.setBounds(44, 106, 327, 65);
		frmSearchEngine.getContentPane().add(lblInsertTextHere);
		
		textField = new JTextField(); //textField for user's query
		textField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textField.setBounds(44, 182, 526, 35);
		frmSearchEngine.getContentPane().add(textField);
		textField.setColumns(10);
		
		JComboBox<String> comboBox_1 = new JComboBox<String>();
		comboBox_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		comboBox_1.setBounds(368, 228, 202, 27);
		frmSearchEngine.getContentPane().add(comboBox_1); //choices for layout
		if(kindOfSearch.equals("Restaurants")) { //for Restaurants
			comboBox_1.addItem("Sort by: text (default)");
			comboBox_1.addItem("Sort by: number of reviews");
			comboBox_1.addItem("Sort by: number of stars");
		}
		else if(kindOfSearch.equals("Reviews")) { //for Reviews
			comboBox_1.addItem("Sort by: text (default)");
			comboBox_1.addItem("Sort by: useful count");
			comboBox_1.addItem("Sort by: more recent");
		}
		
		JButton btnNewButton = new JButton("Search");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //select user's choices here
				String field = comboBox.getSelectedItem().toString();
				String sortBy = comboBox_1.getSelectedItem().toString();
				if (field.equals("location and review")){
					field = "location and review";
				}
				else if (field.equals("name and review")) {
					field = "name and review";
				}
				
				String userQuery = textField.getText();
				Search searchObject = new Search(); //create a Search object
				try {
					searchObject.doSearch(userQuery, field, kindOfSearch, sortBy); //call doSearch method in Search class,
				} catch (IOException | ParseException e1) {						   //with user's choices, for result's search
					e1.printStackTrace();
				}
				frmSearchEngine.setVisible(false);
			}
		});
		btnNewButton.setBackground(new Color(204, 255, 204));
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton.setBounds(592, 179, 89, 40);
		frmSearchEngine.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("\u2190 Back");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmSearchEngine.setVisible(false);
				startMenu();
			}
		});
		btnNewButton_1.setBackground(SystemColor.inactiveCaption);
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton_1.setBounds(45, 280, 89, 27);
		frmSearchEngine.getContentPane().add(btnNewButton_1);
	}
	
	public void results(HashMap<String, String> hits, String userQuery){ //This method presents the results in a new window
	     JFrame frame = new JFrame("Results");
	     frame.setFont(new Font("Dialog", Font.PLAIN, 16));
	     frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 16));
	     JPanel paneContent = new JPanel(new BorderLayout());
	    
	     String  asColName[] = {"Document", "Document's Link", "A part of the Document"}; //the window has 3 columns
		 Object[][] aoData = new Object[hits.size()] [asColName.length];
		 int counter = 0;
		 for (String name : hits.keySet())
	     {
	         aoData[counter][0] = name; //first column has the name of the Files
	         aoData[counter][1] = "Documents\\"+hits.get(name)+".txt"; //second column has the path of the Files
	        
	         Scanner inputReader = null;
	         try
	         {
	        	 inputReader = new Scanner(new FileInputStream("Documents\\"+hits.get(name)+".txt")); 
	         }
	         catch(FileNotFoundException e)
	         {
	        	 System.out.println("File was not found");
	        	 System.out.println("or could not be opened.");
	        	 System.exit(0);
	         }
	       
	         while(inputReader.hasNextLine()) {
	        	 String line = inputReader.nextLine( );
	        	 if(line.contains(userQuery)) {
	        		 aoData[counter][2] = "..."+line+"..."; //third column has some content of the Files, that include user's query
	        		 break;
	        	 }
	         }
	         if(aoData[counter][2] == null) {
	        	 aoData[counter][2] = "...";
	         }
	         counter ++;
	     }
		 
		
	     JTable tableMain = new JTable(aoData, asColName);
	     
	     //second column is button, so user can read the full content of the File after pressing the button
	     tableMain.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer());;
	     tableMain.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor(new JTextField()));
	     
	     tableMain.setFont(new Font("Tahoma", Font.PLAIN, 16));
	     JScrollPane paneScrollingTable = new JScrollPane(tableMain);
	     paneScrollingTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
	     paneContent.add(paneScrollingTable, BorderLayout.CENTER);
	     paneContent.setFont(new Font("Tahoma", Font.PLAIN, 16));
	     frame.setContentPane(paneContent);
	     frame.setSize(new Dimension(1381, 729));
	     Toolkit theKit = frame.getToolkit(); 
	     Dimension wndSize = theKit.getScreenSize();  
	     Rectangle rc = frame.getBounds();
	     int x = (wndSize.width - rc.width)/2;
	     int y = (wndSize.height - rc.height)/2;
	     
	     frame.setBounds(x, y, rc.width, rc.height); 
	
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.setVisible(true);
	}
}

