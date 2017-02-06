/**
 * Bundle.java
 *
 * A helper class used to move a large number of parameters between processing threads.
 *
 */

import java.util.*;

class Bundle{
	ArrayList<String> teamOutputs, judgeOutputs, inputs, errors, checkerNotes;
	ArrayList<Double> runTimes;
	String saveFile, compileCommand, runCommand, language, code, checkerCompileCommand, checkerRunCommand;
	Problem problem;
	
	int runtimeError, timeLimitExceeded, wrongAnswer, outputFormatError, terminated, checkerRuntimeError;
	
	public Bundle(String language, String code, Problem problem){
		teamOutputs = new ArrayList<String>();
		judgeOutputs = new ArrayList<String>();
		inputs = new ArrayList<String>();
		errors = new ArrayList<String>();
		checkerNotes = new ArrayList<String>();
		runTimes = new ArrayList<Double>();
		this.language = language;
		this.code = code;
		this.problem = problem;
		
		saveFile = HelperLib.getSaveFileString(language, code, "submissions");
		compileCommand = HelperLib.getCompileString(language, code, "submissions");
		runCommand = HelperLib.getRunString(language, code, "submissions");
		if(problem.checkerFile != null){
			String checkerFilecode = problem.folder+"\\"+problem.checkerFile;
			switch(problem.checkerLanguage){
				case "C++": checkerFilecode+=".cpp"; break;
				case "Java": checkerFilecode+=".java"; break;
				case "Python": checkerFilecode+=".py"; break;
			}
			checkerFilecode = HelperLib.fileToString(problem.folder+"\\"+problem.checkerFile);
			checkerCompileCommand = HelperLib.getCompileString(problem.checkerLanguage, checkerFilecode, problem.folder+"\\", problem.checkerFile);
			checkerRunCommand = HelperLib.getRunString(problem.checkerLanguage, checkerFilecode, problem.folder+"\\", problem.checkerFile);
		}
		runtimeError = Integer.MAX_VALUE;
		timeLimitExceeded = Integer.MAX_VALUE;
		wrongAnswer = Integer.MAX_VALUE;
		outputFormatError = Integer.MAX_VALUE;
		terminated = Integer.MAX_VALUE;
		checkerRuntimeError = Integer.MAX_VALUE;
	}
}
