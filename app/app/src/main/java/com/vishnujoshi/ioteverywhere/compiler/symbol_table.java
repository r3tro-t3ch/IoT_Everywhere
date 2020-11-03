package com.vishnujoshi.ioteverywhere.compiler;

import java.util.ArrayList;

public class symbol_table {

    private ArrayList<symbol> table;

    public symbol_table(){

        this.table = new ArrayList<>();

    }

    public void add_new_symbol(symbol s){
        table.add(s);
    }

    public ArrayList<symbol> getTable() {
        return table;
    }

    public void setTable(ArrayList<symbol> table) {
        this.table = table;
    }

    public symbol search_symbol(String symbol_name){

        for(symbol s : this.table){
            if(symbol_name.equals(s.getName())){
                return s;
            }
        }

        return null;
    }

    public void update_table(symbol s){

        symbol temp = this.search_symbol(s.getName());

        if( temp != null){

            for(symbol i : this.table){

                if(s.getName().equals(i.getName())){

                    s = i;
                    break;

                }

            }

        }

    }

}

class symbol{

    private String name;
    private String type;

    //var
    private String value;
    private String data_type;

    //function
    private int arg_count;
    private ArrayList<function_arg> args;
    private String return_type;

    //constructor
    public symbol(){

    }

    public symbol(String var_name, String value, String data_type){
        this.name = var_name;
        this.value = value;

        if(data_type.equals("T_STRING")){
            this.data_type = "STRING";
        }else{
            this.data_type = "NUMBER";
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public int getArg_count() {
        return arg_count;
    }

    public void setArg_count(int arg_count) {
        this.arg_count = arg_count;
    }

    public ArrayList<function_arg> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<function_arg> args) {
        this.args = args;
    }

    public String getReturn_type() {
        return return_type;
    }

    public void setReturn_type(String return_type) {
        this.return_type = return_type;
    }
}
