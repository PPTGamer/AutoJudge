/**
 * CompileThread.java
 *
 * A thread that accepts a bundle, compiles the given code and begins the first test case.
 *
 */

import java.util.*;
import java.io.*;

class CompileThread extends Thread{
	Bundle bundle;
	public CompileThread(Bundle b){
		bundle = b;
	}
	
	public void run(){
		try{
			Runtime rt = Runtime.getRuntime();
			System.out.println("about to compile");
			Process proc = rt.exec(bundle.compileCommand);
			System.out.println("compiling");
			proc.waitFor();

			InputStreamReader stdError = new InputStreamReader(proc.getErrorStream());

			String s = HelperLib.inputStreamReaderToString(stdError);
			System.out.println("compiled");
			
			if(s.length() != 0){
				if(!AutoJudge.judge.checkForCompileErrorOverride(s)){
					AutoJudge.judge.giveNoCasesVerdict("Compile Error", s);
					return;
				}
			}
			
			if(bundle.problem.checkerFile != null){
				System.out.println("about to compile checker program");
				proc = rt.exec(bundle.checkerCompileCommand);
				System.out.println("compiling");
				proc.waitFor();

				stdError = new InputStreamReader(proc.getErrorStream());

				s = HelperLib.inputStreamReaderToString(stdError);
				System.out.println("compiled");
				
				if(s.length() != 0){
					if(!AutoJudge.judge.checkForCompileErrorOverride(s)){
						AutoJudge.judge.giveNoCasesVerdict("Compile Error in Checker Program", s);
						return;
					}
				}
			}
			
			if(!AutoJudge.runWindow.isTerminated()){
				new RunTestCaseThread(bundle, 0).start();
			}
		}catch(Exception e){
		}
	}
}