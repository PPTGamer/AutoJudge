/**
 * RunWindow.java
 *
 * A window that shows details on each test case when a run is judged.
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
import java.text.*;

class RunWindow extends JFrame{
	private Container c;
	private JScrollPane scroll;
	private JButton terminate, showResults;
	private JTable table;
	private JLabel label;
	private RunDataTableModel runTableModel;
	private Vector<Vector<Object>> caseData;
	private RunTimer runTimer;
	private Problem problem;
	private boolean terminated;

	public RunWindow(){
		setVisible(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		
		setLayout(new BorderLayout());
		
		label = new JLabel();
		add(label, BorderLayout.NORTH);
		
		caseData = new Vector<Vector<Object>>();
		runTableModel = new RunDataTableModel(caseData);
		table = new JTable(runTableModel);
		scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(300, 300));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scroll, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2, 1));
		
		terminate = new JButton("Terminate");
		terminate.setEnabled(false);
		class TerminateListener implements ActionListener{
			RunWindow parent;
			public TerminateListener(RunWindow parent){
				this.parent = parent;
			}
			public void actionPerformed(ActionEvent e){
				parent.terminate();
			}
		}
		terminate.addActionListener(new TerminateListener(this));
		buttons.add(terminate);
		
		showResults = new JButton("Open Results Window");
		showResults.setEnabled(false);
		class ShowResultsListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				AutoJudge.judge.showLastBundle();
			}
		}
		showResults.addActionListener(new ShowResultsListener());
		buttons.add(showResults);
		
		add(buttons, BorderLayout.SOUTH);
		terminated = true;
		
		pack();
	}
	
	public void reset(Problem p){
		caseData = new Vector<Vector<Object>>();
		runTableModel = new RunDataTableModel(caseData);
		table.setModel(runTableModel);
		DefaultTableCellRenderer caseNumRenderer = new DefaultTableCellRenderer();
		caseNumRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		table.getColumnModel().getColumn(0).setCellRenderer(caseNumRenderer);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setCellRenderer(new DecimalFormatRenderer());
		table.getColumnModel().getColumn(1).setMaxWidth(70);
		table.getColumnModel().getColumn(1).setMaxWidth(70);
		label.setText("Now judging: " + p.title);
		terminate.setEnabled(true);
		
		problem = p;
		terminated = false;
		
		setResizable(false);
		revalidate();
		repaint();
		setVisible(true);
	}
	
	public void terminate(){
		stopTimer();
		disableTerminate();
		terminated = true;
	}
	
	public boolean isTerminated(){
		return terminated;
	}
	
	public void startTimer(Process p){
		runTimer = new RunTimer(this, p);
		runTimer.start();
		showResults.setEnabled(false);
	}
	
	public void stopTimer(){
		runTimer.stopRun();
		showResults.setEnabled(true);
	}
	
	public boolean isTimeLimit(){
		return runTimer.isTimeLimit();
	}
	
	public double getRunTime(){
		return runTimer.getTime();
	}
	
	public void disableTerminate(){
		terminate.setEnabled(false);
	}
	
	public void addCaseVerdict(int casenum, double time, String verdict){
		Vector<Object> tmp = new Vector<Object>();
		tmp.add(casenum);
		tmp.add(Math.min(time, problem.timeLimit));
		tmp.add(verdict);
		caseData.add(tmp);
		runTableModel.fireTableDataChanged();
	}
	
	class RunDataTableModel extends AbstractTableModel{
		private Vector<String> columnNames = new Vector<String>();
		private Vector<Vector<Object>> data;
		
		public RunDataTableModel(Vector<Vector<Object>> data){
			columnNames.add("Case #");
			columnNames.add("Run Time");
			columnNames.add("Verdict");
			this.data = data;
		}
		
		public int getColumnCount(){return columnNames.size();}
		public int getRowCount(){return data.size();}
		public String getColumnName(int col){return columnNames.get(col);}
		public Object getValueAt(int row, int col){return data.get(row).get(col);}
		public Class getColumnClass(int c){
			switch(c){
				case 0:
					return Integer.class;
				case 1:
					return Double.class;
				case 2:
					return String.class;
			}
			return String.class;
		}
		
		public boolean isCellEditable(int row, int col){return false;}
		public void setValueAt(Object value, int row, int col){
			data.get(row).set(col, value);
		}
	}
	
	class RunTimer extends Thread{
		RunWindow parent;
		Process process;
		boolean flag, timeLimit;
		long start, stop;
		
		public RunTimer(RunWindow parent, Process process){
			this.parent = parent;
			this.process = process;
			start = stop = 0;
			timeLimit = false;
		}
		
		public void run(){
			start = System.currentTimeMillis();
			flag = true;
			timeLimit = false;
			while(flag){
				stop = System.currentTimeMillis();
				if(stop-start > parent.problem.timeLimit*1000){
					timeLimit = true;
					break;
				}
			}
			process.destroy();
		}
		
		public void stopRun(){
			flag = false;
		}
		
		public boolean isTimeLimit(){
			return timeLimit;
		}
		
		public double getTime(){
			return (stop-start)/1000.;
		}
	}
	
	class DecimalFormatRenderer extends DefaultTableCellRenderer {
		private final DecimalFormat formatter = new DecimalFormat( "#0.000" );
		public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			value = formatter.format((Number)value);
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column );
		}
	}
}