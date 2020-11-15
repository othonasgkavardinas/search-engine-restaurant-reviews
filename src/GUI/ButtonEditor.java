package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
class ButtonEditor extends DefaultCellEditor{ //this class is for reading the File, after pressing button 
											  //with File's path
   protected JButton btn;
   private String lbl;
   private Boolean clicked;

   public ButtonEditor(JTextField txt) {
	   super(txt);

	   btn=new JButton();
	   btn.setOpaque(true);

	   btn.addActionListener(new ActionListener() { //action, when button is clicked

		   @Override
		   public void actionPerformed(ActionEvent e) {
			   fireEditingStopped(); //call fireEditingStopped method
		   }
	   });
   }
   
   public Component getTableCellEditorComponent(JTable table, Object obj, boolean selected, int row, int col) {
	   lbl=(obj==null) ? "":obj.toString(); //set text to button, set clicked to true, then return the btn object
	   btn.setText(lbl);
	   clicked=true;
	   return btn;
   }

	@Override
	public Object getCellEditorValue() { //if user press the button, this method creates a window with File's content for reading
		if(clicked)
		{
			JPanel panel = new JPanel(new BorderLayout());
		    File file = new File(lbl);
		    
	    	try {
	    		JFrame fr = new JFrame();
	    	    fr.setBounds(100, 100, 1016, 744);
	    	    @SuppressWarnings("resource")
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    	    String sCurrentLine;
	    	    String text = "";
	    	    while ((sCurrentLine = input.readLine()) != null)
	    	    	text += sCurrentLine +'\n';
	    	    JTextArea textArea = new JTextArea(text);
	    	    textArea.setBounds(10, 11, 966, 670);
	    	    textArea.setFont(new Font("Tahoma", Font.PLAIN, 15));
	    		panel.add(textArea);
	    		JScrollPane paneScrollingTable = new JScrollPane(textArea);
	    		paneScrollingTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
	    		panel.add(paneScrollingTable, BorderLayout.CENTER);
	    		panel.setFont(new Font("Tahoma", Font.PLAIN, 16));
	    		fr.setContentPane(panel);
	    		fr.setSize(new Dimension(1381, 729));
	     		fr.setVisible(true);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
		}
		clicked = false; //set it to false
		return new String(lbl);
	}

	@Override
	public boolean stopCellEditing() {
		clicked = false; //set clicked to false first
		return super.stopCellEditing();
	}

	@Override
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}
