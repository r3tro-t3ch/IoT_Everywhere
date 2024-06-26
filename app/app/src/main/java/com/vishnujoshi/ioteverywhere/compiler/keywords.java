package com.vishnujoshi.ioteverywhere.compiler;



import java.util.*;
import java.util.function.DoubleUnaryOperator;

class keywords{

	private static String KEYWORDS[] = {
	    "var",
	    "loop",
		"if",
    	"else",
	    "in",
    	"or",
	    "and",
    	"serial",
    	"HIGH",
    	"LOW",
    	"return",
    	"fun",
    	"break",
    	"true",
    	"false",
		"led",
		"speaker",
		"light",
		"mic",
		"temperature",
		"accelerometerX",
		"accelerometerY",
		"accelerometerZ",
		"gyroscopeX",
		"gyroscopeY",
		"gyroscopeZ",
		"airPressure",
		"humidity",
	};

	private static String BUILTIN_FUNCTION[] = {
		"output",
		"input",
		"wait",
		"print",
		"call",
		"message",
		"connectWifi",
		"get",
		"post",
		"delete",
		"put",
		"json",
	};

	private static ArrayList<String> BUILTIN_FUNCTION_LIST = new ArrayList<String>(Arrays.asList(BUILTIN_FUNCTION));

	private static ArrayList<String> KEYWORD_LIST = new ArrayList<String>(Arrays.asList(KEYWORDS));

	private boolean search_keyword(String identifier){
	
		for(int i = 0; i < KEYWORD_LIST.size(); i++){
			
			if(KEYWORDS[i].equals(identifier)){
				return true;	
			}

		}

		return false;

	}

	public boolean is_keyword(String identifier){

		 return this.search_keyword(identifier);

	}

	public boolean is_builtin_function( String identifier ){

		for(String i : BUILTIN_FUNCTION_LIST ){

			if(i.equals(identifier)){
				return true;
			}

		}

		return false;

	}

	public boolean is_num(String str) {
	    if (str == null) {
	        return false;
	    }
	    try {
	        int d = Integer.parseInt(str);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    return true;
	}

	public static boolean is_numeric(String str) {
		if (str == null) {
			return false;
		}
		try {
			Double d = Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean is_float(String str) {
		if (str == null) {
			return false;
		}
		try {
			float d = Float.parseFloat(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}

