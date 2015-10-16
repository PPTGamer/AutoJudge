/**
 * Judge.java
 *
 * A class that handles judging problems as well as giving verdicts.
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

class Judge{
	private Bundle lastBundle;
	private JFrame verdictWindow;

	public Judge(){
		verdictWindow = new JFrame();
		verdictWindow.setVisible(false);
	}
		
	public void judgeProblem(int problem, String language, String code){
		AutoJudge.gui.disableJudge();
		Bundle params = new Bundle(language, code, AutoJudge.problemHandler.getProblems().get(problem));
		
		AutoJudge.runWindow.reset(AutoJudge.problemHandler.getProblems().get(problem));
		try{
			File tmpf = new File("submissions\\");
			tmpf.mkdirs();
			
			FileWriter fw = new FileWriter(new File(params.saveFile));
			fw.write(code);
			fw.flush();
			fw.close();
			
			new CompileThread(params).start();
		}catch(Exception e){
			AutoJudge.gui.enableJudge();
			JOptionPane.showMessageDialog(null, "Failed to judge run");
		}
	}
	
	public boolean checkForCompileErrorOverride(String error){
		DialogBox d = new DialogBox(null, "Compile Error", "The given program failed to compile properly. Do you want to continue?", "Continue");
		JTextArea errorText = HelperLib.getTextArea(error);
		errorText.setEditable(false);
		d.setContents(errorText);
		return d.showToUser();
	}
	
	public void showLastBundle(){
		if(lastBundle.terminated != Integer.MAX_VALUE){
			giveRunTerminatedVerdict(lastBundle);
		}else{
			giveVerdict(lastBundle);
		}
	}
	
	public void giveVerdict(Bundle b){
		AutoJudge.runWindow.disableTerminate();
		lastBundle = b;
		
		if(b.problem.checkerFile != null){
			if(b.runtimeError < Math.min(Math.min(b.timeLimitExceeded, b.checkerRuntimeError), Math.min(b.wrongAnswer, b.outputFormatError))){
				giveCasesDisplayedVerdict("Runtime Error in test case "+(b.runtimeError+1), b.runtimeError, 3, new String[]{"Input:", "Runtime Error:", "Checker Notes:"}, new ArrayList[]{b.inputs, b.errors, b.checkerNotes});
			}else if(b.checkerRuntimeError < Math.min(Math.min(b.timeLimitExceeded, b.runtimeError), Math.min(b.wrongAnswer, b.outputFormatError))){
				giveCasesDisplayedVerdict("Checker Runtime Error in test case "+(b.checkerRuntimeError+1), b.checkerRuntimeError, 3, new String[]{"Input:", "Runtime Error:", "Checker Notes:"}, new ArrayList[]{b.inputs, b.errors, b.checkerNotes});
			}else if(b.timeLimitExceeded < Math.min(Math.min(b.runtimeError, b.checkerRuntimeError), Math.min(b.wrongAnswer, b.outputFormatError))){
				giveCasesDisplayedVerdict("Time Limit Exceeded in test case "+(b.timeLimitExceeded+1), b.timeLimitExceeded, 4, new String[]{"Input:", "Team's Output:", "Judge's Output:", "Checker Notes:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs, b.checkerNotes});
			}else if(b.wrongAnswer < Math.min(Math.min(b.timeLimitExceeded, b.checkerRuntimeError), Math.min(b.runtimeError, b.outputFormatError))){
				giveCasesDisplayedVerdict("Wrong in test case "+(b.wrongAnswer+1), b.wrongAnswer, 4, new String[]{"Input:", "Team's Output:", "Judge's Output:", "Checker Notes:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs, b.checkerNotes});
			}else if(b.outputFormatError < Math.min(Math.min(b.timeLimitExceeded, b.checkerRuntimeError), Math.min(b.wrongAnswer, b.runtimeError))){
				giveCasesDisplayedVerdict("Output format error in test case "+(b.outputFormatError+1), b.outputFormatError, 4, new String[]{"Input:", "Team's Output:", "Judge's Output:", "Checker Notes:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs, b.checkerNotes});
			}else{
				giveCasesDisplayedVerdict("Accepted", 0, 4, new String[]{"Input:", "Team's Output:", "Judge's Output:", "Checker Notes:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs, b.checkerNotes});
			}
		}else{
			if(b.runtimeError < Math.min(b.timeLimitExceeded, Math.min(b.wrongAnswer, b.outputFormatError))){
				giveCasesDisplayedVerdict("Runtime Error in test case "+(b.runtimeError+1), b.runtimeError, 2, new String[]{"Input:", "Runtime Error:"}, new ArrayList[]{b.inputs, b.errors});
			}else if(b.timeLimitExceeded < Math.min(b.runtimeError, Math.min(b.wrongAnswer, b.outputFormatError))){
				giveCasesDisplayedVerdict("Time Limit Exceeded in test case "+(b.timeLimitExceeded+1), b.timeLimitExceeded, 3, new String[]{"Input:", "Team's Output:", "Judge's Output:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs});
			}else if(b.wrongAnswer < Math.min(b.timeLimitExceeded, Math.min(b.runtimeError, b.outputFormatError))){
				giveCasesDisplayedVerdict("Wrong in test case "+(b.wrongAnswer+1), b.wrongAnswer, 3, new String[]{"Input:", "Team's Output:", "Judge's Output:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs});
			}else if(b.outputFormatError < Math.min(b.timeLimitExceeded, Math.min(b.wrongAnswer, b.runtimeError))){
				giveCasesDisplayedVerdict("Output format error in test case "+(b.outputFormatError+1), b.outputFormatError, 3, new String[]{"Input:", "Team's Output:", "Judge's Output:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs});
			}else{
				giveCasesDisplayedVerdict("Accepted", 0, 3, new String[]{"Input:", "Team's Output:", "Judge's Output:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs});
			}
		}
		
		AutoJudge.gui.enableJudge();
	}
	
	public void giveRunTerminatedVerdict(Bundle b){
		giveCasesDisplayedVerdict("Terminated", b.terminated, 3, new String[]{"Input:", "Team's Output:", "Judge's Output:"}, new ArrayList[]{b.inputs, b.teamOutputs, b.judgeOutputs});
		AutoJudge.gui.enableJudge();
	}
	
	public boolean checkWrongAnswer(String a, String b){
		String atmp = a.replaceAll("\\s+", "");
		String btmp = b.replaceAll("\\s+", "");
		return !atmp.equalsIgnoreCase(btmp);
	}
	
	public boolean checkWrongAnswer(String a, String b,int p){
		String atmp = a.replaceAll("\\s+", "");
		String btmp = b.replaceAll("\\s+", "");
		return PrecisionChecker.judge(a,b,p);
	}
	
	public boolean checkOutputFormatError(String a, String b,int p){
		//TODO: Add proper OFE check
		return PrecisionChecker.judge(a,b,p);
	}
	
	public void giveNoCasesVerdict(String type, String message){
		AutoJudge.runWindow.disableTerminate();
		JTextArea tmp = HelperLib.getTextArea(message);
		JScrollPane scroll = new JScrollPane(tmp);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setPreferredSize(new Dimension(400, 300));
		JOptionPane.showMessageDialog(null, scroll, type, JOptionPane.PLAIN_MESSAGE);
		AutoJudge.gui.enableJudge();
	}
	
	public void giveCasesDisplayedVerdict(String type, int i, int cols, String[] labels, ArrayList<String>[] strings){
		verdictWindow.setVisible(false);
		verdictWindow.getContentPane().removeAll();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		JLabel display = new JLabel("Currently Displaying Test Case "+(i+1));
		mainPanel.add(display, BorderLayout.NORTH);
		
		JPanel compPanel = new JPanel();
		compPanel.setLayout(new GridLayout(1, cols));
		
		JTextArea[] lines = new JTextArea[cols];
		JTextArea[] text = new JTextArea[cols];
		for(int cur=0; cur<cols; cur++){
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JLabel top = new JLabel(labels[cur]);
			panel.add(top, BorderLayout.NORTH);
			JPanel body = new JPanel();
			BorderLayout layout = new BorderLayout();
			layout.setHgap(5);
			body.setLayout(layout);
			lines[cur] = HelperLib.getTextArea(HelperLib.getLines(strings[cur].get(i)));
			lines[cur].setEditable(false);
			lines[cur].setPreferredSize(new Dimension(30, 300));
			body.add(lines[cur], BorderLayout.WEST);
			text[cur] = HelperLib.getTextArea(strings[cur].get(i));
			text[cur].setEditable(false);
			body.add(text[cur], BorderLayout.CENTER);
			JScrollPane scroll = new JScrollPane(body);
			scroll.getVerticalScrollBar().setUnitIncrement(16);
			panel.add(scroll, BorderLayout.CENTER);
			compPanel.add(panel);
		}
		
		mainPanel.add(compPanel, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		
		JPanel changeCase = new JPanel();
		changeCase.setLayout(new FlowLayout());
		
		JLabel testCase = new JLabel("Select Test Case: ");
		changeCase.add(testCase);
		class CircularNumberModel extends SpinnerNumberModel {
			int min, max;
			public CircularNumberModel(int init, int min, int max, int step) {
				super(init, min, max, step);
				this.min = min;
				this.max = max;
			}

			public Object getNextValue() {
				if(getValue() == max) return min;
				return (int)getValue()+1;
			}

			public Object getPreviousValue() {
				if(getValue() == min) return max;
				return (int)getValue()-1;
			}
		}
		CircularNumberModel spinnerModel = new CircularNumberModel(i+1, 1, strings[0].size(), 1);
		JSpinner spinner = new JSpinner(spinnerModel);
		spinner.setPreferredSize(new Dimension(50, 20));
		class SpinnerListener implements ChangeListener{
			JLabel display;
			int cols;
			JTextArea[] lines, text;
			ArrayList<String>[] strings;
			public SpinnerListener(JLabel display, int cols, JTextArea[] lines, JTextArea[] text, ArrayList<String>[] strings){
				this.display = display;
				this.cols = cols;
				this.lines = lines;
				this.text = text;
				this.strings = strings;
			}
			
			public void stateChanged(ChangeEvent e){
				JSpinner spinner = (JSpinner)e.getSource();
				SpinnerModel model = spinner.getModel();
				
				int index = (int)model.getValue();
				for(int i=0; i<cols; i++){
					lines[i].setText(HelperLib.getLines(strings[i].get(index-1)));
					text[i].setText(strings[i].get(index-1));
				}
				display.setText("Currently Displaying Test Case "+index);
			}
		}
		spinner.addChangeListener(new SpinnerListener(display, cols, lines, text, strings));
		class SpinnerMouseWheelListener implements MouseWheelListener{
			CircularNumberModel model;
			public SpinnerMouseWheelListener(CircularNumberModel model){
				this.model = model;
			}
			
			public void mouseWheelMoved(MouseWheelEvent e){
				int mov = e.getWheelRotation();
				while(mov < 0){
					model.setValue(model.getNextValue());
					mov++;
				}
				while(mov > 0){
					model.setValue(model.getPreviousValue());
					mov--;
				}
			}
		}
		mainPanel.addMouseWheelListener(new SpinnerMouseWheelListener(spinnerModel));
		class SpinnerKeyListener implements KeyListener{
			CircularNumberModel model;
			public SpinnerKeyListener(CircularNumberModel model){
				this.model = model;
			}
			
			public void keyPressed(KeyEvent e){
				switch(e.getKeyCode()){
					case KeyEvent.VK_UP: 
						model.setValue(model.getNextValue()); break;
					case KeyEvent.VK_DOWN: 
						model.setValue(model.getPreviousValue()); break;
				}
			}
			public void keyReleased(KeyEvent e){}
			public void keyTyped(KeyEvent e){}
		}
		//mainPanel.addKeyListener(new SpinnerKeyListener(spinnerModel)); --- y u no work :<
		changeCase.add(spinner);
		bottomPanel.add(changeCase, BorderLayout.CENTER);
		
		JButton closeButton = new JButton("Close");
		class CloseButtonListener implements ActionListener{
			JFrame parent;
			public CloseButtonListener(JFrame parent){
				this.parent = parent;
			}
			public void actionPerformed(ActionEvent e){
				parent.setVisible(false);
			}
		}
		closeButton.addActionListener(new CloseButtonListener(verdictWindow));
		bottomPanel.add(closeButton, BorderLayout.SOUTH);
		
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		compPanel.setPreferredSize(new Dimension(850, 300));
		
		verdictWindow.add(mainPanel);
		verdictWindow.setTitle(type);
		verdictWindow.pack();
		verdictWindow.setVisible(true);
	}
}