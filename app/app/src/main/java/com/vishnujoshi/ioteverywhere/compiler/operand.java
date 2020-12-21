package com.vishnujoshi.ioteverywhere.compiler;

public class operand {

    private String value;
    private String data_type;

    operand(String value, String data_type){

        this.value = value;

        if( data_type.equals("T_STRING") ||
            data_type.equals("STRING")){

            this.data_type = "STRING";

        }else if( data_type.equals("T_BOOLEAN")){

            this.data_type = "BOOL";

        }else{

            this.data_type = "NUMBER";

        }

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
}
