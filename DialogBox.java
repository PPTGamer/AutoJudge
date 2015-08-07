/**
 * DialogBox.java
 *
 * A modifiable dialog box for use in the program's GUI.
 *
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.Dialog.*;
import javax.swing.*;

class DialogBox extends JDialog{
	boolean implement;
	JPanel mainPanel;
	class OkListener implements ActionListener{
		JDialog d;
		public OkListener(JDialog jd){
			d = jd;
		}
		public void actionPerformed(ActionEvent e){
			d.setVisible(false);
			implement = true;
		}
	}
	class CancelListener implements ActionListener{
		JDialog d;
		public CancelListener(JDialog jd){
			d = jd;
		}
		public void actionPerformed(ActionEvent e){
			d.setVisible(false);
			implement = false;
		}
	}
	
	public void setContents(Component c){
		add(c, BorderLayout.CENTER);
	}
	
	public boolean showToUser(){
		setModalityType(ModalityType.APPLICATION_MODAL);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		return implement;
	}
	
	public void addButton(JButton b){
		mainPanel.add(b);
	}

	public DialogBox(JFrame o, String s, String m, String button){
		super(o, s);
		implement = false;
		
		setVisible(false);
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton ok = null;
		ok = new JButton(button);
		ok.addActionListener(new OkListener(this));
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new CancelListener(this));
		mainPanel.add(ok);
		mainPanel.add(cancel);
		add(mainPanel, BorderLayout.SOUTH);
		JLabel t_message = new JLabel(m, JLabel.CENTER);
		add(t_message, BorderLayout.NORTH);
	}
}