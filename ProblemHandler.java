/**
 * ProblemHandler.java
 *
 * A class that handles the loading and saving of problems as well as keeping track of them.
 *
 */

import java.util.*;
import java.io.*;
import javax.swing.*;

class ProblemHandler{
	private ArrayList<Problem> problems;
	
	public ProblemHandler(){
		problems = new ArrayList<Problem>();
	}
	
	public void setup(){
		readConfig();
	}
	
	public ArrayList<Problem> getProblems(){
		return problems;
	}
	
	private void readConfig(){
		try{
			String config = HelperLib.inputStreamReaderToString(new FileReader(new File("config.ini")));
			Scanner sc = new Scanner(config);
			while(sc.hasNextLine()){
				String s = sc.nextLine();
				Problem problem = new Problem(s);
				String[] params = sc.nextLine().split("\t");
				int n = Integer.parseInt(params[0]);
				problem.folder = params[1];
				problem.timeLimit = Integer.parseInt(params[2]);
				problem.inputFormat = params[3];
				problem.outputFormat = params[4];
				problem.checkOFEMode = params[5];
				problem.usePrecisionChecker = Boolean.parseBoolean(params[6]);
				if(problem.usePrecisionChecker) 
					problem.precisionExponent = Integer.parseInt(params[7]);
				problem.checkerLanguage = params.length>8?params[8]:null;
				problem.checkerFile = params.length>8?params[9]:null;
				
				for(int i=0; i<n; i++){
					String[] io = sc.nextLine().split("\t");
					problem.inputFiles.add(io[0]);
					problem.outputFiles.add(io[1]);
				}
				problems.add(problem);
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Failed to read config. Resorting to unconfigured state.");
			e.printStackTrace();
			problems.clear();
		}
	}
	
	public void saveConfig(){
		try{
			FileWriter fw = new FileWriter("config.ini");
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<problems.size(); i++){
				sb.append(problems.get(i).title + '\n');
				sb.append(""+problems.get(i).inputFiles.size() + '\t');
				sb.append(""+problems.get(i).folder + '\t');
				sb.append(""+problems.get(i).timeLimit + '\t');
				sb.append(""+problems.get(i).inputFormat + '\t');
				sb.append(""+problems.get(i).outputFormat + '\t');
				sb.append(""+problems.get(i).checkOFEMode + '\t');
				sb.append(""+problems.get(i).usePrecisionChecker + '\t');
				sb.append(""+problems.get(i).precisionExponent + '\t');
				if(problems.get(i).checkerFile != null){
					sb.append(""+'\t' + problems.get(i).checkerLanguage);
					sb.append(""+'\t' + problems.get(i).checkerFile);
				}
				sb.append('\n');
				for(int j=0; j<problems.get(i).inputFiles.size(); j++){
					sb.append(problems.get(i).inputFiles.get(j) + '\t' + problems.get(i).outputFiles.get(j) + '\n');
				}
			}
			fw.write(sb.toString());
			fw.flush();
			fw.close();
			JOptionPane.showMessageDialog(null, "Config Saved");
		}catch(IOException e){
			JOptionPane.showMessageDialog(null, "Failed to save config.");
		}
	}
}