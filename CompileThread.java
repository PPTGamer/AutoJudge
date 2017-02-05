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
			System.out.println("CompileThread: about to compile run source code");
			Process proc;
			InputStreamReader stdError;
			String s;
			if(bundle.compileCommand == null){
				System.out.println("CompileThread: skipping compilation for " + bundle.language);
			}else{
				proc = rt.exec(bundle.compileCommand);
				System.out.println("CompileThread: compiling run source code");
				proc.waitFor();

				stdError = new InputStreamReader(proc.getErrorStream());

				s = HelperLib.inputStreamReaderToString(stdError);
				System.out.println("CompileThread: compiled run source code");
				
				if(s.length() != 0){
					if(!AutoJudge.judge.checkForCompileErrorOverride(s)){
						AutoJudge.judge.giveNoCasesVerdict("Compile Error", s);
						return;
					}
				}
			}
			
			if(bundle.problem.checkerFile != null){
				System.out.println("CompileThread: about to compile checker program");
				if(bundle.checkerCompileCommand == null){
					System.out.println("CompileThread: skipping compilation for " + bundle.problem.checkerLanguage);
				}else{
					proc = rt.exec(bundle.checkerCompileCommand);
					System.out.println("CompileThread: compiling checker source code");
					proc.waitFor();

					stdError = new InputStreamReader(proc.getErrorStream());

					s = HelperLib.inputStreamReaderToString(stdError);
					System.out.println("CompileThread: compiled checker source code");
					
					if(s.length() != 0){
						if(!AutoJudge.judge.checkForCompileErrorOverride(s)){
							AutoJudge.judge.giveNoCasesVerdict("Compile Error in Checker Program", s);
							return;
						}
					}
				}
			}
			
			if(!AutoJudge.runWindow.isTerminated()){
				new RunTestCaseThread(bundle, 0).start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}