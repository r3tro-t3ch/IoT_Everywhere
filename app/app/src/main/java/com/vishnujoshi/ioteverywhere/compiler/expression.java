package com.vishnujoshi.ioteverywhere.compiler;

import android.content.SyncAdapterType;
import android.os.CpuUsageInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

public class expression {

    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<token> token_list;
    private Stack<token> stack;
    private boolean STRING_FLAG;

    public expression(ArrayList<token> token_list){

        this.token_list = token_list;
        stack = new Stack<token>();

    }

    public expression(){
        stack = new Stack<token>();
    }

    public boolean is_expression_token(token t){

        if(t.get_type().equals("T_IDENTIFIER") ||
           t.get_type().equals("T_CONSTANT") ||
           t.get_type().equals("T_PLUS") ||
           t.get_type().equals("T_MINUS") ||
           t.get_type().equals("T_ASTERIX") ||
           t.get_type().equals("T_FSLASH") ||
           t.get_type().equals("T_MOD") ||
           t.get_type().equals("T_STRING") ){

            return true;

        }else{
            return false;
        }

    }

    public boolean is_operator(token t){

        if(
            t.get_type().equals("T_PLUS") ||
            t.get_type().equals("T_MINUS") ||
            t.get_type().equals("T_ASTERIX") ||
            t.get_type().equals("T_FSLASH") ||
            t.get_type().equals("T_MOD")){

            return true;

        }else{
            return false;
        }
    }

    public boolean is_logical_operator(token t){

        if(
            t.get_type().equals("T_EE") ||
            t.get_type().equals("T_NE") ||
            t.get_type().equals("T_GE") ||
            t.get_type().equals("T_LE") ||
            t.get_type().equals("T_GREATER") ||
            t.get_type().equals("T_LESSER") ||
            t.get_type().equals("T_LOR") ||
            t.get_type().equals("T_LAND") ||
            t.get_type().equals("T_BOR") ||
            t.get_type().equals("T_BAND") ||
            t.get_type().equals("T_BNOT")
        ){

            return true;

        }else{
            return false;
        }

    }

    public boolean is_logical_expression_token(token t){

        if(
            is_logical_operator(t) ||
            t.get_type().equals("T_IDENTIFIER") ||
            t.get_type().equals("T_CONSTANT")   ||
            t.get_type().equals("T_STRING")
        ){
            return true;
        }else{
            return false;
        }

    }

    public boolean string_present(ArrayList<token> token_list, symbol_table table){

        boolean flag = false;

        for(token t: token_list){

            if( t.get_type().equals("T_STRING") ){
                flag = true;
                break;
            }else if( t.get_type().equals("T_IDENTIFIER")){

                symbol s = table.search_symbol(t.get_content());

                if(s.getData_type().equals("STRING")){
                    flag = true;
                    break;
                }

            }

        }

        return flag;

    }

    public boolean is_valid_string_expr(ArrayList<token> token_list){

        boolean flag = true;

        for(token t : token_list){

            if( t.get_type().equals("T_MINUS") ||
                t.get_type().equals("T_ASTERIX") ||
                t.get_type().equals("T_FSLASH") ||
                t.get_type().equals("T_LOR") ||
                t.get_type().equals("T_LAND") ||
                t.get_type().equals("T_EE") ||
                t.get_type().equals("T_NE") ||
                t.get_type().equals("T_GE") ||
                t.get_type().equals("T_LE") ||
                t.get_type().equals("T_BOR") ||
                t.get_type().equals("T_BAND") ||
                t.get_type().equals("T_LNOT") ||
                t.get_type().equals("T_GREATER") ||
                t.get_type().equals("T_LESSER")
            ){

                flag = false;
                break;

            }

        }

        return flag;

    }

    public boolean is_valid_string_logical_expr(ArrayList<token> token_list){

        boolean flag = true;

        for(token t : token_list){

            if(
                    t.get_type().equals("T_PLUS") ||
                    t.get_type().equals("T_MINUS") ||
                    t.get_type().equals("T_ASTERIX") ||
                    t.get_type().equals("T_FSLASH") ||
                    t.get_type().equals("T_GE") ||
                    t.get_type().equals("T_LE") ||
                    t.get_type().equals("T_BOR") ||
                    t.get_type().equals("T_BAND") ||
                    t.get_type().equals("T_LNOT") ||
                    t.get_type().equals("T_GREATER") ||
                    t.get_type().equals("T_LESSER")){

                flag = false;
                break;

            }

        }

        return flag;

    }

    public String evaluate_expression(ArrayList<token> token_list,errors err_list, symbol_table table, int line){

        if(token_list.size() == 1){

            if(this.string_present(token_list ,table)){
                if(this.is_valid_string_expr(token_list )){
                    STRING_FLAG = true;
                }
            }

            return token_list.get(0).get_content();

        }

        String answer = new String();

        if(string_present(token_list , table)){

            if(this.is_valid_string_expr(token_list)){

                STRING_FLAG = true;

                for(token t : token_list){

                    if( t.get_type().equals("T_CONSTANT") ||
                        t.get_type().equals("T_STRING") ||
                        t.get_content().equals("HIGH") ||
                        t.get_content().equals("LOW")){

                        if( t.get_content().equals("HIGH") ){

                            stack.push(new token("T_CONSTANT", "1"));

                        }else if( t.get_content().equals("LOW")){

                            stack.push(new token("T_CONSTANT", "0"));

                        }else {

                            stack.push(t);

                        }
                    }else if( t.get_type().equals("T_IDENTIFIER")){

                        symbol s = table.search_symbol(t.get_content());

                        if( s != null){

                            if( !s.getValue().equals("NA")){

                                stack.push(t);

                            }else{

                                err_list.add_new_error(new error("Variable " + s.getName() + " is not defined", line));
                                return null;

                            }

                        }else{

                            err_list.add_new_error(new error("variable " + t.get_content() + " is not declared", line));
                            return  null;

                        }

                    }else{

                        String left_operand, right_operand;

                        token left = stack.pop();
                        token right = stack.pop();

                        if( left.get_type().equals("T_IDENTIFIER") ||
                            right.get_type().equals("T_IDENTIFIER")){

                            if( left.get_type().equals("T_IDENTIFIER") &&
                                !right.get_type().equals("T_IDENTIFIER")){

                                symbol s = table.search_symbol(left.get_content());
                                left_operand = s.getValue();
                                right_operand = right.get_content();

                            }else if(!left.get_type().equals("T_IDENTIFIER") &&
                                    right.get_type().equals("T_IDENTIFIER")){

                                symbol s = table.search_symbol(right.get_content());

                                right_operand = s.getValue();
                                left_operand = left.get_content();

                            }else{

                                symbol l, r;

                                l = table.search_symbol(left.get_content());
                                r = table.search_symbol(right.get_content());

                                left_operand = l.getValue();
                                right_operand = r.getValue();

                            }

                        }else{

                            left_operand = left.get_content();
                            right_operand = right.get_content();
                        }

                        if( t.get_type().equals("T_PLUS")){

                            answer = left_operand + right_operand;

                            stack.push(new token("T_STRING", answer));
                        }

                    }

                }

                return stack.peek().get_content();

            }else{

                err_list.add_new_error(new error("Invalid expression", line));

            }

        }else{

            for( token t: token_list){

                if( t.get_type().equals("T_CONSTANT")){

                    stack.push(t);

                }else if( t.get_type().equals("T_IDENTIFIER")){

                    symbol s = table.search_symbol(t.get_content());

                    if( s != null ){

                        if( !s.getValue().equals("NA")){

                            stack.push(t);

                        }else{

                            err_list.add_new_error(new error("Variable " + s.getName() + " is not defined", line));
                            return null;

                        }

                    }else{

                        err_list.add_new_error(new error("Variable " + t.get_content() + " is not declared", line));
                        return null;

                    }

                }else if (t.get_content().equals("HIGH") ||
                        t.get_content().equals("LOW")){

                    if( t.get_content().equals("HIGH") ){

                        stack.push(new token("T_CONSTANT", "1"));

                    }else if( t.get_content().equals("LOW")){

                        stack.push(new token("T_CONSTANT", "0"));

                    }

                } else {

                    float left_operand = 0, right_operand = 0;

                    token left = stack.pop();
                    token right = stack.pop();

                    if( left.get_type().equals("T_IDENTIFIER") ||
                            right.get_type().equals("T_IDENTIFIER")){

                        if( left.get_type().equals("T_IDENTIFIER") &&
                                !right.get_type().equals("T_IDENTIFIER")){

                            symbol s = table.search_symbol(left.get_content());

                            keywords k = new keywords();

                            if(s != null ) {

                                left_operand = Float.parseFloat(s.getValue());
                                right_operand = Float.parseFloat(right.get_content());

                            }

                        }else if(!left.get_type().equals("T_IDENTIFIER") &&
                                right.get_type().equals("T_IDENTIFIER")){

                            symbol s = table.search_symbol(right.get_content());

                            right_operand = Float.parseFloat(s.getValue());
                            left_operand = Float.parseFloat(left.get_content());

                        }else{

                            symbol l, r;

                            l = table.search_symbol(left.get_content());
                            r = table.search_symbol(right.get_content());

                            left_operand = Float.parseFloat(l.getValue());
                            right_operand = Float.parseFloat(r.getValue());

                        }

                    }else{

                        left_operand = Float.parseFloat(left.get_content());
                        right_operand = Float.parseFloat(right.get_content());

                    }

                    switch (t.get_type()) {
                        case "T_PLUS":

                            answer = String.valueOf(left_operand + right_operand);

                            String tempAnswer = answer.replaceAll("^\\d*\\.","");

                            int temp = Integer.parseInt(tempAnswer);

                            if( temp == 0){

                                stack.push(new token("T_CONSTANT", String.valueOf( (int) (Math.round(Float.parseFloat(answer))))));

                            }else{

                                stack.push(new token("T_CONSTANT", answer));

                            }


                            break;
                        case "T_MINUS":

                            answer = String.valueOf(left_operand - right_operand);

                            tempAnswer = answer.replaceAll("^\\d*\\.","");

                            temp = Integer.parseInt(tempAnswer);

                            if( temp == 0){

                                stack.push(new token("T_CONSTANT", String.valueOf( (int) (Math.round(Float.parseFloat(answer))))));

                            }else{

                                stack.push(new token("T_CONSTANT", answer));

                            }
                            break;
                        case "T_ASTERIX":

                            answer = String.valueOf(left_operand * right_operand);

                            tempAnswer = answer.replaceAll("^\\d*\\.","");

                            temp = Integer.parseInt(tempAnswer);

                            if( temp == 0){

                                stack.push(new token("T_CONSTANT", String.valueOf( (int) (Math.round(Float.parseFloat(answer))))));

                            }else{

                                stack.push(new token("T_CONSTANT", answer));

                            }
                            break;
                        case "T_FSLASH":

                            answer = String.valueOf(left_operand / right_operand);

                            tempAnswer = answer.replaceAll("^\\d*\\.","");

                            temp = Integer.parseInt(tempAnswer);

                            if( temp == 0){

                                stack.push(new token("T_CONSTANT", String.valueOf( (int) (Math.round(Float.parseFloat(answer))))));

                            }else{

                                stack.push(new token("T_CONSTANT", answer));

                            }
                            break;
                        case "T_MOD" :

                            answer = String.valueOf(left_operand % right_operand);

                            tempAnswer = answer.replaceAll("^\\d*\\.","");

                            temp = Integer.parseInt(tempAnswer);

                            if( temp == 0){

                                stack.push(new token("T_CONSTANT", String.valueOf( (int) (Math.round(Float.parseFloat(answer))))));

                            }else{

                                stack.push(new token("T_CONSTANT", answer));

                            }

                            break;

                    }

                }

            }

            return stack.peek().get_content();

        }

        return null;

    }

    //implement logical expressions of numbers and test logical expression evaluation of strings

    public String evaluate_logical_expression(ArrayList<token> token_list,errors err_list, symbol_table table, int line){

        if(token_list.size() == 1){

            if(this.string_present(token_list ,table)){
                if(this.is_valid_string_expr(token_list )){
                    STRING_FLAG = true;
                }
            }

            return token_list.get(0).get_content();

        }

        String answer = new String();

            for(token t : token_list){

                if( t.get_type().equals("T_CONSTANT") ||
                        t.get_type().equals("T_STRING")){

                    stack.push(t);

                }else if( t.get_type().equals("T_IDENTIFIER")){

                    symbol s = table.search_symbol(t.get_content());

                    if( s != null){

                        if( !s.getValue().equals("NA")) {

                            stack.push(t);

                        }else{

                            err_list.add_new_error(new error("Variable " + s.getName() + " is not defined", line));
                            return null;
                        }

                    }else{

                        err_list.add_new_error(new error("variable " + t.get_content() + " is not declared", line));
                        return null;

                    }

                }else{

                    token right = stack.pop();
                    token left = stack.pop();

                    operand left_operand ,right_operand;

                    if( left.get_type().equals("T_IDENTIFIER") ||
                            right.get_type().equals("T_IDENTIFIER")){

                        if( left.get_type().equals("T_IDENTIFIER") &&
                                !right.get_type().equals("T_IDENTIFIER")){

                            symbol s = table.search_symbol(left.get_content());

                            left_operand = new operand(s.getValue(), s.getData_type());
                            right_operand = new operand(right.get_content(), right.get_type());

                        }else if(!left.get_type().equals("T_IDENTIFIER") &&
                                right.get_type().equals("T_IDENTIFIER")){

                            symbol s = table.search_symbol(right.get_content());

                            right_operand = new operand(s.getValue(), s.getData_type());
                            left_operand = new operand(left.get_content(), left.get_type());

                        }else{

                            symbol l, r;

                            l = table.search_symbol(left.get_content());
                            r = table.search_symbol(right.get_content());

                            left_operand = new operand(l.getValue(), l.getData_type());
                            right_operand = new operand(r.getValue(), r.getData_type());

                        }

                    }else{

                        left_operand = new operand(left.get_content(), left.get_type());
                        right_operand = new operand(right.get_content(), right.get_type());
                    }

                    Log.e(TAG, "Left : " + left_operand.getValue() + ", " + left_operand.getData_type());
                    Log.e(TAG, "Right : " + right_operand.getValue() + ", " + right_operand.getData_type());

                    switch (t.get_type()) {

                        case "T_EE": {

                            if (left_operand.getData_type().equals(right_operand.getData_type()) &&
                                left_operand.getValue().equals(right_operand.getValue()) ) {

                                stack.push(new token("T_BOOLEAN", "true"));

                            } else {

                                stack.push(new token("T_BOOLEAN", "false"));

                            }

                            break;
                        }
                        case "T_GREATER": {

                            if( left_operand.getData_type().equals("NUMBER") &&
                                 right_operand.getData_type().equals("NUMBER") ){

                                double l = Double.parseDouble(left_operand.getValue());
                                double r = Double.parseDouble(right_operand.getValue());

                                if( l > r){

                                    stack.push(new token("T_BOOLEAN", "true"));

                                }else{

                                    stack.push(new token("T_BOOLEAN", "false"));

                                }

                            }else{

                                err_list.add_new_error(new error("Cannot compare numbers to bool or strings", line));

                            }
                            break;

                        }
                        case "T_LESSER":{

                            if( left_operand.getData_type().equals("NUMBER") &&
                                    right_operand.getData_type().equals("NUMBER") ){

                                double l = Double.parseDouble(left_operand.getValue());
                                double r = Double.parseDouble(right_operand.getValue());

                                if( l < r){

                                    stack.push(new token("T_BOOLEAN", "true"));

                                }else{

                                    stack.push(new token("T_BOOLEAN", "false"));

                                }

                            }else{

                                err_list.add_new_error(new error("Cannot compare numbers to bool or strings", line));

                            }
                            break;

                        }
                        case "T_GE" : {

                            if( left_operand.getData_type().equals("NUMBER") &&
                                    right_operand.getData_type().equals("NUMBER") ){

                                double l = Double.parseDouble(left_operand.getValue());
                                double r = Double.parseDouble(right_operand.getValue());

                                if( l >= r){

                                    stack.push(new token("T_BOOLEAN", "true"));

                                }else{

                                    stack.push(new token("T_BOOLEAN", "false"));

                                }

                            }else{

                                err_list.add_new_error(new error("Cannot compare numbers to bool or strings", line));

                            }
                            break;

                        }
                        case "T_LE":{

                            if( left_operand.getData_type().equals("NUMBER") &&
                                    right_operand.getData_type().equals("NUMBER") ){

                                double l = Double.parseDouble(left_operand.getValue());
                                double r = Double.parseDouble(right_operand.getValue());

                                if( l <= r){

                                    stack.push(new token("T_BOOLEAN", "true"));

                                }else{

                                    stack.push(new token("T_BOOLEAN", "false"));

                                }

                            }else{

                                err_list.add_new_error(new error("Cannot compare numbers to bool or strings", line));

                            }
                            break;

                        }
                        case "T_NE": {

                            if ( !left_operand.getData_type().equals(right_operand.getData_type()) ||
                                 !left_operand.getValue().equals(right_operand.getValue()) ) {

                                stack.push(new token("T_BOOLEAN", "true"));

                            } else {

                                stack.push(new token("T_BOOLEAN", "false"));

                            }

                            break;
                        }

                        case "T_LOR": {

                            if (left_operand.getValue().equals("true") || right_operand.getValue().equals("true")) {

                                stack.push(new token("T_BOOLEAN", "true"));

                            } else {

                                stack.push(new token("T_BOOLEAN", "false"));

                            }

                            break;
                        }

                        case "T_LAND": {

                            if (left_operand.getValue().equals("true") && right_operand.getValue().equals("true")) {

                                stack.push(new token("T_BOOLEAN", "true"));

                            } else {

                                stack.push(new token("T_BOOLEAN", "false"));

                            }

                            break;
                        }
                    }

                }

            }

            return stack.peek().get_content();

    }

    private int check_precedence(token t){

        switch (t.get_type()) {
            case "T_NOT":
                return 10;
            case "T_ASTERIX":
            case "T_FSLASH":
            case "T_MOD":

                return 9;

            case "T_PLUS":
            case "T_MINUS":

                return 8;

            case "T_LE":
            case "T_GE":
            case "T_GREATER":
            case "T_LESSER":

                return 7;

            case "T_EE":
            case "T_NE":

                return 6;

            case "T_BAND":
                return 5;
            case "T_BOR":
                return 4;
            case "T_LAND":
                return 3;
            case "T_LOR":
                return 2;
            default:
                return 1;
        }

    }

    public ArrayList<token> infix_to_postfix(ArrayList<token> list){

        ArrayList<token> rev = list;

        rev.add(0, new token("T_LPAREN", "("));

        rev.add(new token("T_RPAREN", ")"));

        ArrayList<token>postfix = new ArrayList<token>();

        Stack<token> stack = new Stack<token>();

        for(token t : rev){

            if( t.get_type().equals("T_CONSTANT") ||
                t.get_type().equals("T_IDENTIFIER") ||
                t.get_type().equals("T_STRING") ||
                t.get_type().equals("T_KEYWORD")
            ){

                if( t.get_content().equals("HIGH")){

                    postfix.add(new token("T_CONSTANT", "1"));

                }else if ( t.get_content().equals("LOW")) {

                    postfix.add(new token("T_CONSTANT", "0"));

                }else {
                    postfix.add(t);
                }

            }else if( t.get_type().equals("T_LPAREN")){

                stack.push(t);

            }else if( t.get_type().equals("T_RPAREN")){

                while ( !stack.peek().get_type().equals("T_LPAREN") ){

                    postfix.add(stack.peek());
                    stack.pop();

                }

                stack.pop();

            }else{

                if( this.is_operator(t) || this.is_logical_operator(t)){

                    while (this.check_precedence(t) <= this.check_precedence(stack.peek())){

                        postfix.add(stack.peek());
                        stack.pop();

                    }

                    stack.push(t);

                }

            }

        }

        return postfix;

    }

    boolean is_postfix_valid(ArrayList<token> list){

        if(list.size() == 1){
            return true;
        }

        int operand_count = 0, operator_count = 0;

        for(token t : list){

            if( this.is_operator(t) || this.is_logical_operator(t)){

                operator_count++;

            }else{

                operand_count++;

            }

        }

        //postfix expression validation
        //1 -> first two elements are operand
        //2 -> last element is always an operator
        //3 -> for every n operands there are n-1 operators

        if( !is_operator(list.get(0)) &&
            !is_operator(list.get(1)) &&
            operand_count == operator_count+1 &&
            (is_operator(list.get(list.size() - 1)) || is_logical_operator(list.get(list.size() - 1 )))
        ){

            return true;

        }

        return false;

    }

    public ArrayList<token> reverse_token_list(ArrayList<token> list){

        ArrayList<token> reverse = new ArrayList<token>();

        for(int i = list.size() - 1; i >= 0; i--){

            reverse.add(list.get(i));

        }

        return reverse;

    }


    public ArrayList<token> getToken_list() {
        return token_list;
    }

    public void setToken_list(ArrayList<token> token_list) {
        this.token_list = token_list;
    }

    public Stack<token> getStack() {
        return stack;
    }

    public void setStack(Stack<token> stack) {
        this.stack = stack;
    }

    public boolean getSTRING_FLAG() {
        return STRING_FLAG;
    }

    public void setSTRING_FLAG(boolean STRING_FLAG) {
        this.STRING_FLAG = STRING_FLAG;
    }
}
