/**
 * RunTestCaseThread.java
 *
 * A thread that runs a single test case and begins the next if not terminated.
 *
 */

import java.util.*;
import java.io.*;

class RunTestCaseThread extends Thread{
	Bundle bundle;
	int caseNum;
	public RunTestCaseThread(Bundle b, int caseNum){
		bundle = b;
		this.caseNum = caseNum;
	}
	
	public void run(){
		try{
			boolean verdictPassed = false;
			String s = HelperLib.inputStreamReaderToString(new FileReader(new File(bundle.problem.inputFiles.get(caseNum))));
			bundle.inputs.add(s);
			System.out.println("RunTestCaseThread: writing input to file");
			HelperLib.stringToFile("AutoJudgeTemporaryInputFile.in", s);
			
			System.out.println("RunTestCaseThread: setting up test case: " + (caseNum+1));
			// HelperLib.stringToFile("AutoJudgeRun.bat", bundle.runCommand + " < AutoJudgeTemporaryInputFile.in > AutoJudgeTemporaryOutputFile.out");
			// Runtime rt = Runtime.getRuntime();
			Runtime.getRuntime().gc();
			Runtime.getRuntime().runFinalization();
			
			String[] runCommand = bundle.runCommand.split(" ");
			ArrayList<String> runlist = new ArrayList<String>();
			for(int i=0; i<runCommand.length; i++) runlist.add(runCommand[i]);
			
			ProcessBuilder pb = new ProcessBuilder(runlist);
			pb.redirectError(new File("AutoJudgeTemporaryErrorFile.err"));
			pb.redirectInput(new File("AutoJudgeTemporaryInputFile.in"));
			pb.redirectOutput(new File("AutoJudgeTemporaryOutputFile.out"));
			
			System.out.println("RunTestCaseThread: running test case: " + (caseNum+1));
			Process proc = pb.start();
			// Process proc = rt.exec("AutoJudgeRun.bat");
			
			AutoJudge.runWindow.startTimer(proc);
			proc.waitFor();
			AutoJudge.runWindow.stopTimer();
			
			if(AutoJudge.runWindow.isTerminated()){
				bundle.terminated = caseNum;
				verdictPassed = true;
				AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Terminated");
			}
			if(AutoJudge.runWindow.isTimeLimit()){
				if(bundle.timeLimitExceeded == Integer.MAX_VALUE){
					bundle.timeLimitExceeded = caseNum;
				}
				if(!verdictPassed){
					verdictPassed = true;
					AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Time Limit Exceeded");
				}
			}
			if(proc.exitValue() != 0){
				if(!verdictPassed){
					verdictPassed = true;
					AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Runtime Error");
				}
			}
			bundle.runTimes.add(AutoJudge.runWindow.getRunTime());
			
			String team = HelperLib.inputStreamReaderToString(new FileReader(new File("AutoJudgeTemporaryOutputFile.out")));
			//System.out.println("Team: " + team);
			
			String error = HelperLib.inputStreamReaderToString(new FileReader(new File("AutoJudgeTemporaryErrorFile.err")));
			//System.out.println("Error: " + error);
			
			String judge = HelperLib.inputStreamReaderToString(new FileReader(new File(bundle.problem.outputFiles.get(caseNum))));
			//System.out.println("Judge: " + judge);
			
			bundle.errors.add(error);
			if(error.length() != 0){
				if(bundle.runtimeError == Integer.MAX_VALUE){
					bundle.runtimeError = caseNum;
				}
				if(!verdictPassed){
					verdictPassed = true;
					if(bundle.runtimeError == Integer.MAX_VALUE) bundle.runtimeError = caseNum;
					AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Runtime Error");
				}
			}
			
			bundle.teamOutputs.add(team);
			bundle.judgeOutputs.add(judge);
			System.out.println("RunTestCaseThread: checking test case " + (caseNum+1));
			
			if(bundle.problem.checkerFile != null){
				if(!verdictPassed){
					Runtime.getRuntime().gc();
					Runtime.getRuntime().runFinalization();
					System.out.println("RunTestCaseThread: runCommand="+bundle.checkerRunCommand);
					runCommand = bundle.checkerRunCommand.split(" ");
					ArrayList<String> runlist2 = new ArrayList<String>();
					for(int i=0; i<runCommand.length; i++) runlist2.add(runCommand[i]);
					runlist2.add((caseNum+1)+"");
					
					ProcessBuilder pb2 = new ProcessBuilder(runlist2);
					pb2.redirectError(new File("AutoJudgeTemporaryCheckerErrorFile.err"));
					pb2.redirectInput(new File("AutoJudgeTemporaryOutputFile.out"));
					pb2.redirectOutput(new File("AutoJudgeTemporaryCheckerOutputFile.out"));
					
					proc = pb2.start();
					proc.waitFor();
					
					String checkerError = HelperLib.fileToString("AutoJudgeTemporaryCheckerErrorFile.err");
					String checkerOutput = HelperLib.fileToString("AutoJudgeTemporaryCheckerOutputFile.out");
					if(proc.exitValue() != 0 || checkerError.length() > 0){
						AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Checker Runtime Error");
						bundle.checkerNotes.add(checkerError);
					}else{
						String code, notes;
						if(checkerOutput.indexOf("\n")==-1){
							code = "O";
							notes = "";
						}else{
							code = checkerOutput.substring(0, checkerOutput.indexOf("\n")).trim();
							notes = checkerOutput.substring(checkerOutput.indexOf("\n")+1);
						}
						switch(code){
							case "AC": AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Accepted"); break;
							case "WA": AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Wrong Answer"); break;
							case "OFE": AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Output Format Error"); break;
							default: AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Other"); break;
						}
						bundle.checkerNotes.add("verdict:"+code+"\n"+notes);
					}
					(new File("AutoJudgeTemporaryCheckerOutputFile.out")).delete();
					(new File("AutoJudgeTemporaryCheckerErrorFile.err")).delete();
				}else{
					bundle.checkerNotes.add("Checker not run");
				}
			}else{
				if(!verdictPassed){
					if(AutoJudge.judge.checkWrongAnswer(team, judge,bundle.problem)){
						if(bundle.wrongAnswer == Integer.MAX_VALUE) bundle.wrongAnswer = caseNum;
						AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Wrong Answer");
					}else if(AutoJudge.judge.checkOutputFormatError(team, judge,bundle.problem)){
						if(bundle.outputFormatError == Integer.MAX_VALUE) bundle.outputFormatError = caseNum;
						AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Output Format Error");
					}else{
						AutoJudge.runWindow.addCaseVerdict(caseNum+1, AutoJudge.runWindow.getRunTime(), "Accepted");
					}
				}
			}
			
			(new File("AutoJudgeTemporaryInputFile.in")).delete();
			(new File("AutoJudgeTemporaryOutputFile.out")).delete();
			(new File("AutoJudgeTemporaryErrorFile.err")).delete();
			
			if(!AutoJudge.runWindow.isTerminated()){
				if(caseNum == bundle.problem.inputFiles.size()-1){
					AutoJudge.judge.giveVerdict(bundle);
				}else{
					new RunTestCaseThread(bundle, caseNum+1).start();
				}
			}else{
				AutoJudge.judge.giveRunTerminatedVerdict(bundle);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}