package com.vishnujoshi.ioteverywhere.compiler;


import java.util.regex.*;
import java.util.*;

class lexer{

	private char current_char;
	private int index;
	private char code[];

	public char get_current_char(){

		return this.current_char;

	}

	//creating and initializaing a lexer
	public lexer(String code){

		this.index = 0;
		this.code = code.toCharArray();
		this.current_char = this.code[this.index];

	}

	public lexer(){

	}


	public void next_char(){

		this.index++;
		this.current_char = this.code[this.index];
	
	}

	public char peek_next_char(){

		return this.code[index+1];

	}

	public String get_string(){

		ArrayList<Character> identifier = new ArrayList<Character>();
		this.next_char();
		identifier.add(this.current_char);
		this.next_char();
		while( this.current_char != '"' ){
			
			identifier.add(this.current_char);
			this.next_char();

		}

		StringBuilder identifier_str = new StringBuilder();
	
		for(char i : identifier){

			identifier_str.append(String.valueOf(i));

		}

		return(identifier_str.toString());
	}

	public String get_identifier(){

		ArrayList<Character> identifier = new ArrayList<Character>();
		identifier.add(this.current_char);
		this.next_char();
		while( is_alpha_numeric( this.current_char ) || this.current_char == '.'){

			identifier.add(this.current_char);
			this.next_char();

		}

		StringBuilder identifier_str = new StringBuilder();

		for(char i : identifier){

			identifier_str.append(String.valueOf(i));

		}

		return(identifier_str.toString());

	}

	public boolean is_alpha_numeric(char str)
    {
        String regex = "[a-zA-Z0-9]";

        Pattern p = Pattern.compile(regex);

        if (str == '\0') {
            return false;
        }

        Matcher m = p.matcher(String.valueOf(str));

        return m.matches();
    }

	//getters and setters
	public void set_code(char code[]){
		this.code = code;
	}

	public char[] get_code(){
		return this.code;
	}

	public void set_index(int index){
		this.index = index;
	}

	public int get_index(){
		return this.index;
	}

	public void set_current_char(char c){
		this.current_char = c;
	}

}


