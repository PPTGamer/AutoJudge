/**
 * GUI.java
 *
 * This class handles the main GUI or judge terminal shown to the user.
 *
 */

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

class GUI extends JFrame{
	private Container c;
	private JComboBox<String> problemBox, languageBox;
	private JScrollPane scroll;
	private JTextArea textArea;
	private JButton judgeButton;
	private JTabbedPane tabPane;
	private JPanel p_judge, p_config, p_selection;

	public GUI(String version){
		super("AutoJudge (v " + version + ")");
		c = getContentPane();
		
		setVisible(false);
		
		// Main tabbed pane containing the judging and config tabs
		tabPane = new JTabbedPane();
		
		p_judge = new JPanel();
		p_judge.setLayout(new BorderLayout());
		
		// Problem and language selection combo boxes for the judge
		p_selection = new JPanel();
		p_selection.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		
		JLabel problemLabel = new JLabel("Select problem: ");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		p_selection.add(problemLabel, gbc);
		problemBox = new JComboBox<String>();
		problemBox.setMaximumRowCount(30);
		refreshJudgeList(AutoJudge.problemHandler.getProblems());
		gbc.gridx = 1;
		gbc.weightx = 1;
		p_selection.add(problemBox, gbc);
		JLabel languageLabel = new JLabel("Select language: ");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		p_selection.add(languageLabel, gbc);
		languageBox = new JComboBox<String>();
		languageBox.addItem("C++");
		languageBox.addItem("Java");
		languageBox.setMaximumRowCount(30);
		gbc.gridx = 1;
		gbc.weightx = 1;
		p_selection.add(languageBox, gbc);
		p_judge.add(p_selection, BorderLayout.NORTH);
		
		// Area to paste the team's code
		JPanel text = new JPanel();
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		text.setLayout(layout);
		
		JTextArea lines = HelperLib.getTextArea("");
		lines.setEditable(false);
		lines.setPreferredSize(new Dimension(40, 500));
		text.add(lines, BorderLayout.WEST);
		
		textArea = HelperLib.getTextArea("");
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
		
		scroll = new JScrollPane(text);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setPreferredSize(new Dimension(800, 450));
		p_judge.add(scroll, BorderLayout.CENTER);
		
		// Button to judge the current run
		judgeButton = new JButton("JUDGE");
		class JudgeListener implements ActionListener{
			JComboBox<String> problem, language;
			JTextArea code;
			public JudgeListener(JComboBox<String> problem, JComboBox<String> language, JTextArea code){
				this.problem = problem;
				this.language = language;
				this.code = code;
			}
			public void actionPerformed(ActionEvent e){
				AutoJudge.judge.judgeProblem(problem.getSelectedIndex(), (String)language.getSelectedItem(), code.getText());
			}
		}
		judgeButton.addActionListener(new JudgeListener(problemBox, languageBox, textArea));
		p_judge.add(judgeButton, BorderLayout.SOUTH);
		
		tabPane.add("Judge", p_judge);
		
		// Judge configuration tab
		p_config = new JPanel();
		p_config.setLayout(new BorderLayout());
		Vector<Vector<Object>> problemData = new Vector<Vector<Object>>();
		updateProblemData(problemData, AutoJudge.problemHandler.getProblems());
		class ProblemDataTableModel extends AbstractTableModel{
			private Vector<String> columnNames = new Vector<String>();
			private Vector<Vector<Object>> data;
			private ArrayList<Problem> problems;
			
			public ProblemDataTableModel(Vector<Vector<Object>> data, ArrayList<Problem> problems){
				columnNames.add("Letter");
				columnNames.add("Name");
				columnNames.add("Cases");
				columnNames.add("Folder");
				columnNames.add("Input File Format");
				columnNames.add("Output File Format");
				columnNames.add("Time Limit");
				columnNames.add("Show Input");
				columnNames.add("Checker Language");
				columnNames.add("Checker File");
				this.problems = problems;
				this.data = data;
			}
			
			public int getColumnCount(){
				return columnNames.size();
			}
			
			public int getRowCount(){
				return data.size();
			}
			
			public String getColumnName(int col){
				return columnNames.get(col);
			}
			
			public Object getValueAt(int row, int col){
				return data.get(row).get(col);
			}
			
			public Class getColumnClass(int c){
				switch(c){
					case 0: case 1: case 3: case 4: case 5: case 8: case 9:
						return String.class;
					case 2: case 6:
						return Integer.class;
					case 7:
						return Boolean.class;
				}
				return String.class;
			}
			
			public boolean isCellEditable(int row, int col){
				return col != 0;
			}
			
			public void setValueAt(Object value, int row, int col){
				data.get(row).set(col, value);
				switch(col){
					case 1: problems.get(row).title = (String)value; return;
					case 2: problems.get(row).setCases((int)value); return;
					case 3: problems.get(row).folder = (String)value; return;
					case 4: problems.get(row).inputFormat = (String)value; return;
					case 5: problems.get(row).outputFormat = (String)value; return;
					case 6: problems.get(row).timeLimit = (int)value; return;
					case 7: problems.get(row).showInput = (boolean)value; return;
					case 8: problems.get(row).checkerLanguage = (String)value; return;
					case 9: problems.get(row).checkerFile = (String)value; return;
				}
			}
		}
		ProblemDataTableModel tableModel = new ProblemDataTableModel(problemData, AutoJudge.problemHandler.getProblems());
		JTable problemTable = new JTable(tableModel);
		DefaultListSelectionModel problemSelect = new DefaultListSelectionModel();
		problemSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		problemTable.setSelectionModel(problemSelect);
		
		JScrollPane problemScroll = new JScrollPane(problemTable);
		problemScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		p_config.add(problemScroll, BorderLayout.CENTER);
		
		// Buttons for configuration
		JPanel configBotPanel = new JPanel();
		configBotPanel.setLayout(new FlowLayout());
		JButton addProblem = new JButton("Add Problem");
		
		class AddProblemListener implements ActionListener{
			Vector<Vector<Object>> data;
			ArrayList<Problem> problems;
			ProblemDataTableModel tm;
			
			public AddProblemListener(ArrayList<Problem> problems, Vector<Vector<Object>> data, ProblemDataTableModel tm){
				this.data = data;
				this.problems = problems;
				this.tm = tm;
			}
			
			public void actionPerformed(ActionEvent e){
				String message = "Please enter the necessary information.";
				JTextField title = new JTextField();
				JTextField cases = new JTextField();
				JTextField path = new JTextField();
				JTextField infileName = new JTextField("judge%d.in");
				JTextField outfileName = new JTextField("judge%d.out");
				JTextField timeLimit = new JTextField();
				JCheckBox input = new JCheckBox();
				JButton checker = new JButton("Add Checker Program?");
				Problem prob = new Problem();
				
				while(true){
					DialogBox d = new DialogBox(null, "Add Problem", message, "Ok");
					
					JPanel mainPanel = new JPanel();
					mainPanel.setLayout(new BorderLayout());
					
					JPanel panel = new JPanel();
					panel.setLayout(new GridLayout(7, 2));
					
					JLabel l_title = new JLabel("Title: "); panel.add(l_title);
					panel.add(title);
					
					JLabel l_cases = new JLabel("Num Cases: "); panel.add(l_cases);
					panel.add(cases);
					
					JLabel l_path = new JLabel("Default IO Folder: "); panel.add(l_path);
					panel.add(path);
					
					JLabel l_infileName = new JLabel("Input file name format: "); panel.add(l_infileName);
					panel.add(infileName);
					
					JLabel l_outfileName = new JLabel("Output file name format: "); panel.add(l_outfileName);
					panel.add(outfileName);
					
					JLabel l_timeLimit = new JLabel("Time Limit (secs): "); panel.add(l_timeLimit);
					panel.add(timeLimit);
					
					JLabel l_input = new JLabel("Show Input: "); panel.add(l_input);
					panel.add(input);
					
					mainPanel.add(panel, BorderLayout.CENTER);
					
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
					checker.addActionListener(new CheckerActionListener(prob));
					
					mainPanel.add(checker, BorderLayout.SOUTH);
					
					d.setContents(mainPanel);
					if(!d.showToUser()) return;
					
					try{
						int n = Integer.parseInt(cases.getText());
						if(n < 1){
							message = "Please fix input.";
							continue;
						}
						n = Integer.parseInt(timeLimit.getText());
						if(n < 1){
							message = "Please fix input.";
							continue;
						}
					}catch(Exception ex){
						message = "Please fix input.";
						continue;
					}
					
					prob.showInput = input.isSelected();
					prob.title = title.getText();
					prob.folder = path.getText();
					prob.timeLimit = Integer.parseInt(timeLimit.getText());
					prob.inputFormat = infileName.getText();
					prob.outputFormat = outfileName.getText();
					prob.setCases(Integer.parseInt(cases.getText()));
					problems.add(prob);
					
					updateProblemData(data, problems);
					break;
				}
				
				tm.fireTableDataChanged();
				refreshJudgeList(problems);
			}
		}
		addProblem.addActionListener(new AddProblemListener(AutoJudge.problemHandler.getProblems(), problemData, tableModel));
		configBotPanel.add(addProblem);
		
		JButton removeProblem = new JButton("Remove Problem");
		
		class RemoveProblemListener implements ActionListener{
			Vector<Vector<Object>> data;
			ArrayList<Problem> problems;
			ProblemDataTableModel tm;
			JTable table;
			
			public RemoveProblemListener(ArrayList<Problem> problems, Vector<Vector<Object>> data, ProblemDataTableModel tm, JTable table){
				this.data = data;
				this.problems = problems;
				this.tm = tm;
				this.table = table;
			}
			
			public void actionPerformed(ActionEvent e){
				if(table.getSelectedRow() == -1) return;
				problems.remove(table.getSelectedRow());
				updateProblemData(data, problems);
				
				tm.fireTableDataChanged();
				refreshJudgeList(problems);
			}
		}
		removeProblem.addActionListener(new RemoveProblemListener(AutoJudge.problemHandler.getProblems(), problemData, tableModel, problemTable));
		configBotPanel.add(removeProblem);
		
		JButton editCases = new JButton("Edit Test Cases");
		class EditCasesListener implements ActionListener{
			ArrayList<Problem> problems;
			JTable table;
			
			public EditCasesListener(ArrayList<Problem> problems, JTable table){
				this.problems = problems;
				this.table = table;
			}
			
			public void actionPerformed(ActionEvent e){
				if(table.getSelectedRow() == -1) return;
				try{
					Problem prob = problems.get(table.getSelectedRow());
					int cases = prob.inputFiles.size();
					String[] inputFiles = new String[cases];
					String[] outputFiles = new String[cases];
					String[] input = new String[cases];
					String[] output = new String[cases];
					for(int i=0; i<cases; i++){
						inputFiles[i] = prob.inputFiles.get(i);
						input[i] = HelperLib.fileToString(prob.inputFiles.get(i));
						outputFiles[i] = prob.outputFiles.get(i);
						output[i] = HelperLib.fileToString(prob.outputFiles.get(i));
					}
					
					DialogBox d = new DialogBox(null, "Edit Test Cases: " + prob.title, "Don't forget to save config after!", "Ok");
					
					JPanel panel = new JPanel();
					panel.setLayout(new GridLayout(1, 2, 3, 3));
					
					JPanel leftPanel = new JPanel();
					leftPanel.setLayout(new GridBagLayout());
					JPanel rightPanel = new JPanel();
					rightPanel.setLayout(new GridBagLayout());
					GridBagConstraints gbc = new GridBagConstraints();
					gbc.fill = GridBagConstraints.HORIZONTAL;
					gbc.insets = new Insets(2, 2, 2, 2);
					
					JLabel[] labels = new JLabel[2*cases];
					JTextField[] fileNames = new JTextField[2*cases];
					JTextArea[] judgeIO = new JTextArea[2*cases];

					for(int i=0; i<cases; i++){
						gbc.gridx = 0;
						gbc.gridy = i*2;
						gbc.weightx = 0;
						gbc.gridwidth = 1;
						labels[2*i] = new JLabel("Input for case "+(i+1)+": ");
						leftPanel.add(labels[2*i], gbc);
						labels[2*i+1] = new JLabel("Output for case "+(i+1)+": ");
						rightPanel.add(labels[2*i+1], gbc);
						
						gbc.gridx = 1;
						gbc.weightx = 1;
						fileNames[2*i] = new JTextField(inputFiles[i]);
						leftPanel.add(fileNames[2*i], gbc);
						fileNames[2*i+1] = new JTextField(outputFiles[i]);
						rightPanel.add(fileNames[2*i+1], gbc);
						
						gbc.gridx = 0;
						gbc.gridy = i*2+1;
						gbc.gridwidth = GridBagConstraints.REMAINDER;
						judgeIO[2*i] = HelperLib.getTextArea(input[i]);
						JScrollPane scroller1 = new JScrollPane(judgeIO[2*i]);
						scroller1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
						scroller1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						scroller1.getVerticalScrollBar().setUnitIncrement(16);
						scroller1.setPreferredSize(new Dimension(350, 100));
						leftPanel.add(scroller1, gbc);
						judgeIO[2*i+1] = HelperLib.getTextArea(output[i]);
						JScrollPane scroller2 = new JScrollPane(judgeIO[2*i+1]);
						scroller2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
						scroller2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						scroller2.getVerticalScrollBar().setUnitIncrement(16);
						scroller2.setPreferredSize(new Dimension(350, 100));
						rightPanel.add(scroller2, gbc);
					}
					
					panel.add(leftPanel);
					panel.add(rightPanel);
					
					JScrollPane scroll = new JScrollPane(panel);
					scroll.setPreferredSize(new Dimension(800, 600));
					scroll.getVerticalScrollBar().setUnitIncrement(16);
					d.setContents(scroll);
					
					JButton genOutput = new JButton("Generate output with judge program");
					class GenOutputListener implements ActionListener{
						JTextArea[] judgeIO;
						public GenOutputListener(JTextArea[] judgeIO){
							this.judgeIO = judgeIO;
						}
						public void actionPerformed(ActionEvent e){
							DialogBox d = new DialogBox(null, "Judge Solution", "Input judge solution", "Generate Output");
							
							JPanel panel = new JPanel();
							panel.setLayout(new BorderLayout());
							
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
							languageBox = new JComboBox<String>();
							languageBox.addItem("C++");
							languageBox.addItem("Java");
							languageBox.setMaximumRowCount(30);
							gbc.gridx = 1;
							gbc.weightx = 1;
							languageSelection.add(languageBox, gbc);
							panel.add(languageSelection, BorderLayout.NORTH);
							
							JPanel text = new JPanel();
							BorderLayout layout = new BorderLayout();
							layout.setHgap(5);
							text.setLayout(layout);
							
							JTextArea lines = HelperLib.getTextArea("");
							lines.setEditable(false);
							lines.setPreferredSize(new Dimension(40, 500));
							text.add(lines, BorderLayout.WEST);
							
							JTextArea textArea = HelperLib.getTextArea("");
							textArea.setFont(new Font("Courier", Font.PLAIN, 12));
							textArea.setTabSize(4);
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
							panel.add(scroll, BorderLayout.CENTER);
							
							d.setContents(panel);
							
							if(!d.showToUser()) return;
							String language = (String)languageBox.getSelectedItem();
							String code = textArea.getText();
							
							try{
								File tmp2 = new File("judgeSolutions\\");
								tmp2.mkdirs();
								FileWriter fw = new FileWriter(new File(HelperLib.getSaveFileString(language, code, "judgeSolutions")));
								fw.write(code);
								fw.flush();
								fw.close();
								
								Runtime rt = Runtime.getRuntime();
								System.out.println("about to compile");
								Process proc = rt.exec(HelperLib.getCompileString(language, code, "judgeSolutions"));
								System.out.println("compiling");
								proc.waitFor();

								InputStreamReader stdInput = new InputStreamReader(proc.getInputStream());
								InputStreamReader stdError = new InputStreamReader(proc.getErrorStream());
								FileReader br;

								String s = HelperLib.inputStreamReaderToString(stdError);
								System.out.println("compiled");
								
								if(s.length() != 0){
									if(!AutoJudge.judge.checkForCompileErrorOverride(s)){
										return;
									}
								}
								
								ArrayList<String> inputs = new ArrayList<String>();
								ArrayList<String> errors = new ArrayList<String>();
								ArrayList<String> outputs = new ArrayList<String>();
								boolean runtimeError = false;
								
								for(int i=0; i<judgeIO.length; i+=2){
									System.out.println("about to run test case: " + (i+1));
									rt = Runtime.getRuntime();
									proc = rt.exec(HelperLib.getRunString(language, code, "judgeSolutions"));
									System.out.println("running test case: " + (i+1));
									
									System.out.println("pushing input");
									BufferedOutputStream out = new BufferedOutputStream(proc.getOutputStream());
									s = judgeIO[i].getText();
									inputs.add(s);
									byte[] tmp = s.getBytes();
									out.write(tmp, 0, tmp.length);
									out.flush();
									out.close();
									System.out.println("done pushing input");
									proc.waitFor();
									
									stdInput = new InputStreamReader(proc.getInputStream());
									String output = HelperLib.inputStreamReaderToString(stdInput);
									System.out.println("Output: " + output);
									
									stdError = new InputStreamReader(proc.getErrorStream());
									String error = HelperLib.inputStreamReaderToString(stdError);
									System.out.println("Error: " + error);
									
									errors.add(error);
									if(error.length() != 0){
										runtimeError = true;
									}
									
									System.out.println(output);
									outputs.add(output);
								}
								
								if(runtimeError){
									for(int i=0; i<errors.size(); i++){
										if(!errors.get(i).equals("")){
											AutoJudge.judge.giveCasesDisplayedVerdict("Runtime Error in test case "+(i+1), i, 2, new String[]{"Input:", "Runtime Error:"}, new ArrayList[]{inputs, errors});
											return;
										}
									}
								}
								
								for(int i=0; i<judgeIO.length; i+=2){
									judgeIO[i+1].setText(outputs.get(i/2));
								}
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}
					}
					genOutput.addActionListener(new GenOutputListener(judgeIO));
					d.addButton(genOutput);
					
					if(!d.showToUser()) return;
					
					for(int i=0; i<2*cases; i++){
						HelperLib.stringToFile(fileNames[i].getText(), judgeIO[i].getText());
					}
				}catch(Exception ex){
					
				}
			}
		}
		editCases.addActionListener(new EditCasesListener(AutoJudge.problemHandler.getProblems(), problemTable));
		configBotPanel.add(editCases);
		
		JButton saveConfig = new JButton("Save Config");
		class SaveConfigListener implements ActionListener{
			
			public SaveConfigListener(){
			}
			
			public void actionPerformed(ActionEvent e){
				AutoJudge.problemHandler.saveConfig();
			}
		}
		saveConfig.addActionListener(new SaveConfigListener());
		configBotPanel.add(saveConfig);
		p_config.add(configBotPanel, BorderLayout.SOUTH);
		
		tabPane.add("Config", p_config);
		
		add(tabPane);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void disableJudge(){
		judgeButton.setEnabled(false);
	}
	
	public void enableJudge(){
		judgeButton.setEnabled(true);
	}
	
	public void refreshJudgeList(ArrayList<Problem> problems){
		problemBox.removeAllItems();
		for(int i=0; i<problems.size(); i++){
			problemBox.addItem(problems.get(i).title);
		}
	}
	
	public static void updateProblemData(Vector<Vector<Object>> problemData, ArrayList<Problem> problems){
		problemData.clear();
		for(int i=0; i<problems.size(); i++){
			Vector<Object> temp = new Vector<Object>();
			temp.add((char)('A' + i) + "");
			temp.add(problems.get(i).title);
			temp.add(problems.get(i).inputFiles.size());
			temp.add(problems.get(i).folder);
			temp.add(problems.get(i).inputFormat);
			temp.add(problems.get(i).outputFormat);
			temp.add(problems.get(i).timeLimit);
			temp.add(problems.get(i).showInput);
			temp.add(problems.get(i).checkerLanguage);
			temp.add(problems.get(i).checkerFile);
			problemData.add(temp);
		}
	}
}