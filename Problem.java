/**
 * Problem.java
 *
 * A class that represents a single problem.
 *
 */

import java.util.*;

class Problem{
	ArrayList<String> inputFiles, outputFiles;
	String title, folder, inputFormat, outputFormat, checkerLanguage, checkerFile;
	int timeLimit, precisionExponent;
	boolean usePrecisionChecker;
	String checkOFEMode;
	
	public Problem(){
		this("");
	}
	
	public Problem(String s){
		title = s;
		inputFiles = new ArrayList<String>();
		outputFiles = new ArrayList<String>();
	}
	
	public void setCases(int n){
		inputFiles.clear();
		outputFiles.clear();
		while(inputFiles.size() < n){
			inputFiles.add(folder + "\\" + String.format(inputFormat, inputFiles.size()+1));
			outputFiles.add(folder + "\\" + String.format(outputFormat, outputFiles.size()+1));
		}
	}
}