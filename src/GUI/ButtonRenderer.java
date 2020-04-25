package GUI;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
class ButtonRenderer extends JButton implements  TableCellRenderer{ //this class is for reading the File, after pressing button 
																	//with File's path
	public ButtonRenderer() { //constructor, set button properties
		setOpaque(true);
	}
  
	@Override
	public Component getTableCellRendererComponent(JTable table, Object obj, boolean selected, boolean focused, int row, int col) {
		setText((obj==null) ? "":obj.toString()); //set passed object as button text
		return this;
	}
}