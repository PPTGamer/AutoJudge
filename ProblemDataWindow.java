import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.Dialog.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.imageio.*;


class ProblemDataWindow extends JDialog{
	String message = "Please enter the necessary information.";
	JTextField title = new JTextField();
	JTextField cases = new JTextField();
	JTextField path = new JTextField();
	JTextField infileName = new JTextField("judge%d.in");
	JTextField outfileName = new JTextField("judge%d.out");
	JTextField timeLimit = new JTextField();
	JTextField precisionExponent = new JTextField("0");
	JComboBox OFEMode = new JComboBox(new String[]{"Tolerate all whitespace","Tolerate all newlines","Tolerate all blank lines"});
	JCheckBox input = new JCheckBox();
	JCheckBox usesPrecisionChecker = new JCheckBox();
	JButton checker = new JButton("Add Checker Program?");
	JButton exit = new JButton("Exit without Saving");
	JButton saveAndExit = new JButton("Save and Exit");
	
	Problem problem;
	public ProblemDataWindow(Problem p){
		this.problem = p;

		JLabel header = new JLabel();
		if(problem.title.equals("")){
			header.setText("Editing New Problem");
			this.setTitle("New problem");
		}else{
			header.setText("Editing "+ problem.title);
			this.setTitle(problem.title);
			title.setText(problem.title);
			cases.setText(Integer.toString(problem.inputFiles.size()));
			path.setText(problem.folder);
			infileName.setText(problem.inputFormat);
			outfileName.setText(problem.outputFormat);
			timeLimit.setText(Integer.toString(problem.timeLimit));
			usesPrecisionChecker.setSelected(problem.usePrecisionChecker);
			precisionExponent.setText(Integer.toString(problem.precisionExponent));
			OFEMode.setSelectedItem(problem.checkOFEMode);
			input.setSelected(problem.showInput);
		}
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		//North subpanel:
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		mainPanel.add(header, BorderLayout.NORTH);
		
		//Center subpanel:
		JPanel subpanel = new JPanel();
		subpanel.setLayout(new GridLayout(11, 2));

		JLabel l_title = new JLabel("Title: ");
		subpanel.add(l_title);
		subpanel.add(title);
		
		JLabel l_cases = new JLabel("Num Cases: "); 
		subpanel.add(l_cases);
		subpanel.add(cases);

		JLabel l_path = new JLabel("Default IO Folder: ");
		subpanel.add(l_path);
		subpanel.add(path);

		JLabel l_infileName = new JLabel("Input file name format: "); 
		subpanel.add(l_infileName);
		subpanel.add(infileName);

		JLabel l_outfileName = new JLabel("Output file name format: "); 
		subpanel.add(l_outfileName);
		subpanel.add(outfileName);

		JLabel l_timeLimit = new JLabel("Time Limit (secs): "); 
		subpanel.add(l_timeLimit);
		subpanel.add(timeLimit);

		JLabel l_usesPrecisionChecker = new JLabel("Use precision checker?: "); 
		subpanel.add(l_usesPrecisionChecker);
		subpanel.add(usesPrecisionChecker);

		JLabel l_precisionExponent = new JLabel("Precision (power of 10): "); 
		if(!usesPrecisionChecker.isSelected()){
			l_precisionExponent.setEnabled(false);
			precisionExponent.setEnabled(false);
		}
		subpanel.add(l_precisionExponent);
		subpanel.add(precisionExponent);
		
		usesPrecisionChecker.addActionListener(new ToggleListener(l_precisionExponent,precisionExponent));
		
		JLabel l_OFEMode = new JLabel("Output Format Error Checking Mode: "); 
		subpanel.add(l_OFEMode);
		subpanel.add(OFEMode);

		JLabel l_input = new JLabel("Show Input: "); 
		subpanel.add(l_input);
		subpanel.add(input);

		checker.addActionListener(new CheckerActionListener(problem));
		
		subpanel.add(new JLabel());
		subpanel.add(checker);
		
		mainPanel.add(subpanel, BorderLayout.CENTER);
		//Bottom panel
		JPanel bottomPanel = new JPanel( new GridLayout(1,2) );
		saveAndExit.addActionListener(new SaveListener());
		exit.addActionListener(new ExitListener());
		bottomPanel.add(saveAndExit);
		bottomPanel.add(exit);
		mainPanel.add(bottomPanel,BorderLayout.SOUTH);
		
		
		getContentPane().add(mainPanel);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(500, 300);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	public Problem showDialog(){
		setVisible(true);
		return problem;
	}
	class ToggleListener implements ActionListener{
		JLabel label;
		JTextField field;
		public ToggleListener(JLabel l, JTextField f){
			this.label = l;
			this.field = f;
		}
		public void actionPerformed(ActionEvent e){
			JCheckBox cb = (JCheckBox) e.getSource();
			this.label.setEnabled(cb.isSelected());
			this.field.setEnabled(cb.isSelected());

			repaint();
		}
	}

	class CheckerActionListener implements ActionListener{
		Problem p;
		public CheckerActionListener(Problem p){
			this.p = p;
		}
		public void actionPerformed(ActionEvent e){
			DialogBox d = new DialogBox(null, "Set Problem Checker", "<html>The problem checker MUST print out the correct judgement code (AC - accepted, WA - wrong answer, OFE - output format error, O - other)<br>on a line by itself at the beginning of its output. Any succeeding lines will be treated as notes and will be shown when comparing team<br>outputs. The checker will be run from the directory of the autojudge with the current test case number (1-n)as its only command line<br>argument and the team's output file-redirected into it (ie. \"&lt;checker&gt; &lt;casenum&gt; &lt; &lt;team's output&gt;\"). The checker program is saved as<br>\"&lt;File Name&gt;\" in the checkers folder in the same folder as autojudge (file name ignored for Java). Please make sure this file name is unique<br>among all problems.", "Ok");
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			
			JPanel languageSelection = new JPanel();
			languageSelection.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(2, 2, 2, 2);
			
			JLabel languageLabel = new JLabel("Select language: ");
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			languageSelection.add(languageLabel, gbc);
			JComboBox<String> languageBox = new JComboBox<String>();
			languageBox.addItem("C++");
			languageBox.addItem("Java");
			languageBox.addItem("Python");
			languageBox.setMaximumRowCount(30);
			gbc.gridx = 1;
			gbc.weightx = 1;
			languageSelection.add(languageBox, gbc);
			mainPanel.add(languageSelection, BorderLayout.NORTH);
			JLabel fileLabel = new JLabel("File name: ");
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 0;
			languageSelection.add(fileLabel, gbc);
			JTextField fileBox = new JTextField();
			gbc.gridx = 1;
			gbc.weightx = 1;
			languageSelection.add(fileBox, gbc);
			mainPanel.add(languageSelection, BorderLayout.NORTH);
			
			JPanel text = new JPanel();
			BorderLayout layout = new BorderLayout();
			layout.setHgap(5);
			text.setLayout(layout);
			
			JTextArea lines = HelperLib.getTextArea("");
			lines.setEditable(false);
			lines.setPreferredSize(new Dimension(40, 500));
			text.add(lines, BorderLayout.WEST);
			
			JTextArea textArea = HelperLib.getTextArea("");
			class textListener implements DocumentListener{
				JTextArea lines, code;
				public textListener(JTextArea lines, JTextArea code){
					this.lines = lines;
					this.code = code;
				}
				public void removeUpdate(DocumentEvent e){
					lines.setText(HelperLib.getLines(code.getText()));
				}
				public void insertUpdate(DocumentEvent e){
					lines.setText(HelperLib.getLines(code.getText()));
				}
				public void changedUpdate(DocumentEvent e){
					lines.setText(HelperLib.getLines(code.getText()));
				}
			}
			textArea.getDocument().addDocumentListener(new textListener(lines, textArea));
			
			text.add(textArea, BorderLayout.CENTER);
			
			JScrollPane scroll = new JScrollPane(text);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.getVerticalScrollBar().setUnitIncrement(16);
			scroll.setPreferredSize(new Dimension(800, 450));
			mainPanel.add(scroll, BorderLayout.CENTER);
			
			d.setContents(mainPanel);
			if(!d.showToUser()) return;
			
			if(fileBox.getText().length() == 0){
				JOptionPane.showMessageDialog(null, "Failed to set checker program. Please input a file name.");
			}else{
				p.checkerLanguage = (String)languageBox.getSelectedItem();
				p.checkerFile = fileBox.getText();
				HelperLib.stringToFile(HelperLib.getSaveFileString(p.checkerLanguage, textArea.getText(), "checkers", p.checkerFile), textArea.getText());
			}
		}
	}
	
	class SaveListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
			int n = Integer.parseInt(cases.getText());
			if(n < 1){
				throw new Exception();
			}
			n = Integer.parseInt(timeLimit.getText());
			if(n < 1){
				throw new Exception();
			}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Please fix input fields");
				ex.printStackTrace();
				return;
			}
			
			problem.showInput = input.isSelected();
			problem.title = title.getText();
			problem.folder = path.getText();
			problem.timeLimit = Integer.parseInt(timeLimit.getText());
			problem.usePrecisionChecker = usesPrecisionChecker.isSelected();
			problem.precisionExponent = Integer.parseInt(precisionExponent.getText());
			problem.checkOFEMode = (String)OFEMode.getSelectedItem();
			problem.inputFormat = infileName.getText();
			problem.outputFormat = outfileName.getText();
			problem.setCases(Integer.parseInt(cases.getText()));
			
			setVisible(false);
			dispose();
		}
	}
	
	class ExitListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(false);
			dispose();
		}
	}
}