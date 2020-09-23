package com.vishnujoshi.ioteverywhere.compiler;

import java.util.*;

class error{

	private String error_msg;
	private int line_number;

	//constructor
	public error(String msg, int line_number){
		this.error_msg = msg;
		this.line_number = line_number;
	}

	//getters and setter
	public String get_error_msg(){
		return this.error_msg;
	}

	public int get_line_number(){
		return this.line_number;
	}


}

class errors{

	private ArrayList<error> error_list;

	//constructor
	public errors(){
		error_list = new ArrayList<error>();
	}

	//methods
	public void add_new_error(error e){

		this.error_list.add(e);

	}

	public void print_errors(){

		for(error i : error_list){

			System.out.println("ERROR : line " + i.get_line_number() + " -> " + i.get_error_msg());

		}

	}

	public int get_errors_count(){

		return this.error_list.size();

	}

}
