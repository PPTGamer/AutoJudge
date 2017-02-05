import java.util.*;
import java.util.regex.Pattern;
import java.io.*;

public class PrecisionChecker{
	static ArrayList<String> runoutTokens, judgeoutTokens;
	public static ArrayList<String> tokenize(String s){
		ArrayList<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		boolean currentStringIsNumericLiteral = false;
		for(int i = 0; i<s.length(); i++){
			char currChar = s.charAt(i);
			boolean isNumericLiteral = (Character.isDigit(currChar) || (i>0 && i<s.length()-1 && Character.isDigit(s.charAt(i-1)) && currChar=='.' && Character.isDigit(s.charAt(i+1))));
			
			if(sb.length()==0){
				currentStringIsNumericLiteral = isNumericLiteral;
				sb.append(currChar);
			}else{
				if(currentStringIsNumericLiteral==isNumericLiteral){
					sb.append(currChar);
				}else{
					tokens.add(sb.toString());
					sb = new StringBuilder();
					sb.append(currChar);
					currentStringIsNumericLiteral = isNumericLiteral;
				}
			}
		}
		if(sb.length()!=0){
			tokens.add(sb.toString());
		}
		return tokens;
	}
	
	public static int compare(String runout, String judgeout, int precision){
		Pattern numericLiteralPattern = Pattern.compile("[0-9]+|[0-9]+.[0-9]+");
		
		if(runoutTokens.size() != judgeoutTokens.size()){
			return -2; //WA!
		}
		
		for(int i = 0; i<runoutTokens.size(); i++){
			String runtoken = runoutTokens.get(i);
			String judgetoken = judgeoutTokens.get(i);
			boolean isNumericLiteral1 = numericLiteralPattern.matcher(runtoken).matches();
			boolean isNumericLiteral2 = numericLiteralPattern.matcher(judgetoken).matches();
			if(isNumericLiteral1 != isNumericLiteral2){ 
				System.out.println("Numeric literal/string literal mismatch");
				return -2; //WA!
			}else if (isNumericLiteral1){
				System.out.println("Comparing:" + runtoken + "|" + judgetoken);
				double num1 = Double.parseDouble(runtoken);
				double num2 = Double.parseDouble(judgetoken);
				double diff = Math.abs(num1-num2);
				System.out.println("Tokens differ by " + diff);
				if(diff>=Math.pow(10,precision)){
					System.out.println("Error not less than " + Math.pow(10,precision) + ".");
					return -1; //Precision error.
				}
			}
		}
		return 0;
	}
	
	public static boolean judge(String runout, String judgeout,int precision)
	{
		runoutTokens = tokenize(runout);
		System.out.println("Team output tokenized.");
		judgeoutTokens  = tokenize(judgeout);
		System.out.println("Judge output tokenized.");
		int verdict = compare(runout,judgeout,precision);
		switch(verdict){
			case 0:
				System.out.println("Accepted");
				break;
			case -2:
				System.out.println("Wrong Answer");
				break;
			case -1:
				System.out.println("Precision error");
				break;
		}
		return verdict < 0;
	}
}	