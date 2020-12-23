package com.vishnujoshi.ioteverywhere.compiler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.util.TimeUtils;

import net.mabboud.android_tone_player.ContinuousBuzzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;

public class evaluator {

    private ast_l ast_list;
    private static String TAG = "evaluator";
    private Context context;


    SharedPreferences SENSOR_DATA;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public evaluator(){
    }

    public evaluator(ast_l ast_list){
        this.ast_list = ast_list;
    }

    public void run(){

        keywords k = new keywords();
        errors err_list = new errors();
        symbol_table table = new symbol_table();
        symbol temp_s = new symbol();

        SENSOR_DATA = context.getSharedPreferences("SENSOR_DATA", context.MODE_PRIVATE);

        for(ast temp_ast : ast_list.getAst_list()){

            switch (temp_ast.get_type()) {
                case "AST_BUILTIN_FUNCTION_CALL":

                    this.evaluate_function_call(k, temp_ast, err_list, table, null);

                    break;
                case "AST_VAR_DEF":

                    temp_s = table.search_symbol(temp_ast.get_var_def_var_name());

                    if (temp_s == null) {

                        table.add_new_symbol(new symbol(temp_ast.get_var_def_var_name(),
                                "NA",
                                "NA"));

                    } else {

                        err_list.add_new_error(new error("symbol " + temp_ast.get_var_def_var_name() + " already present", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;

                    }

                    break;
                case "AST_VAR_DEF_ASSIGNMENT": {

                    expression e = new expression();

                    symbol t_s = table.search_symbol(temp_ast.get_var_def_var_name());

                    if (t_s == null) {

                        ArrayList<token> list = temp_ast.getVar_def_var_expr();

                        String answer = e.evaluate_expression(list, err_list, table, temp_ast.get_ast_node_index());

                        symbol s;

                        if (answer != null) {

                            Log.e("answer", ": " + answer);

                            if (!e.getSTRING_FLAG()) {

                                s = new symbol(temp_ast.get_var_def_var_name(), answer, "T_IDENTIFIER");

                            } else {

                                s = new symbol(temp_ast.get_var_def_var_name(), answer, "T_STRING");

                            }

                            table.add_new_symbol(s);

                        }

                    } else {

                        err_list.add_new_error(new error("variable " + temp_ast.get_var_def_var_name() + " is already declared", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;

                    }

                    break;
                }
                case "AST_VAR_DEF_ASSIGNMENT_FUNCTION": {

                    symbol s = table.search_symbol(temp_ast.get_var_def_var_name());

                    if (s == null) {

                        s = new symbol(temp_ast.get_var_def_var_name(), "NA", "NA");

                        String answer;

                        if (temp_ast.get_function_name().equals("input")) {

                            answer = this.evaluate_input_function_call(temp_ast, k, err_list);

                            if (answer != null) {

                                s.setValue(answer);

                                s.setData_type("NUMBER");

                                table.add_new_symbol(s);

                                Log.e("sensor_val : ", s.getValue());

                            }

                        }

                    } else {

                        err_list.add_new_error(new error("variable " + temp_ast.get_var_def_var_name() + " is already declared", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;

                    }

                    break;
                }
                case "AST_VAR_ASSIGNMENT": {

                    symbol s = table.search_symbol(temp_ast.get_var_name());

                    expression e = new expression();

                    if (s != null) {

                        ArrayList<token> list = temp_ast.getVar_expr();

                        String answer = e.evaluate_expression(list, err_list, table, temp_ast.get_ast_node_index());

                        if (answer != null) {

                            Log.e("answer", ": " + answer);

                            if (!e.getSTRING_FLAG()) {

                                s.setValue(answer);

                                s.setData_type("NUMBER");

                                table.update_table(s);

                            } else {

                                s.setValue(answer);

                                s.setData_type("STRING");

                                table.update_table(s);

                            }

                        }

                    } else {

                        err_list.add_new_error(new error("Variable " + temp_ast.get_var_name() + " is not declared", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;
                    }

                    break;
                }
                case "AST_VAR_ASSIGNMENT_FUNCTION": {

                    symbol s = table.search_symbol(temp_ast.get_var_name());

                    if (s != null) {

                        String answer;

                        if (temp_ast.get_function_name().equals("input")) {

                            answer = this.evaluate_input_function_call(temp_ast, k, err_list);

                            if (answer != null) {

                                s.setValue(answer);

                                s.setData_type("NUMBER");

                                table.update_table(s);

                                Log.e("sensor_val : ", s.getValue());

                            }

                        }

                    } else {

                        err_list.add_new_error(new error("Variable " + temp_ast.get_var_def_var_name() + " is not declared", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;

                    }

                    break;
                }
                case "AST_CONDITIONAL_IF" : {

                    Log.e(TAG, "AST_if");
                    Log.e(TAG,"True block");
                    temp_ast.getTrue_block().print_ast();

                    expression e = new expression();

                    String answer = e.evaluate_logical_expression(temp_ast.getConditional_statement_expr() , err_list, table, temp_ast.get_ast_node_index());

                    if( answer != null) {

                        Log.e(TAG, answer);

                        if (answer.equals("true")) {

                            this.run_block(table, temp_ast.getTrue_block(), err_list);

                        }

                    }else{

                        err_list.print_errors();

                    }

                    break;

                }
                case "AST_CONDITIONAL_IF_ELSE" : {

                    Log.e(TAG, "AST_if_else");
                    Log.e(TAG,"True block");
                    temp_ast.getTrue_block().print_ast();
                    Log.e(TAG,"False block");
                    temp_ast.getFalse_block().print_ast();

                    expression e = new expression();

                    String answer = e.evaluate_logical_expression(temp_ast.getConditional_statement_expr(), err_list, table, temp_ast.get_ast_node_index());

                    if(answer != null) {

                        Log.e(TAG, answer);

                        if (answer.equals("true")) {

                            this.run_block(table, temp_ast.getTrue_block(), err_list);

                        } else {

                            this.run_block(table, temp_ast.getFalse_block(), err_list);

                        }

                    }else{

                        err_list.print_errors();

                    }

                    break;

                }

                case "AST_LOOP_INFINITE" : {

                    while (true) {
                        this.run_block(table, temp_ast.getLoop_code_block(), err_list);
                    }

                }

            }

        }

    }

    public void run_block( symbol_table parent_symbol_table, ast_l code_block, errors err_list){

        keywords k = new keywords();
        symbol_table table = new symbol_table();
        symbol temp_s;
        symbol temp_s_parent = new symbol();

        SENSOR_DATA = context.getSharedPreferences("SENSOR_DATA", context.MODE_PRIVATE);

        for(ast temp_ast : code_block.getAst_list()){

            switch (temp_ast.get_type()) {
                case "AST_BUILTIN_FUNCTION_CALL":

                    this.evaluate_function_call(k, temp_ast, err_list, table, parent_symbol_table);

                    break;
                case "AST_VAR_DEF": {

                    temp_s_parent = parent_symbol_table.search_symbol(temp_ast.get_var_def_var_name());

                    temp_s = table.search_symbol(temp_ast.get_var_def_var_name());

                    if (temp_s == null && temp_s_parent == null) {

                        table.add_new_symbol(new symbol(temp_ast.get_var_def_var_name(),
                                "NA",
                                "NA"));

                    } else {

                        err_list.add_new_error(new error("symbol " + temp_ast.get_var_def_var_name() + " already present", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;


                    }

                    break;
                }
                case "AST_VAR_DEF_ASSIGNMENT": {

                    Log.e(TAG,"here inside block var def assignment");

                    expression e = new expression();

                    temp_s_parent = parent_symbol_table.search_symbol(temp_ast.get_var_def_var_name());

                    temp_s = table.search_symbol(temp_ast.get_var_def_var_name());

                    if (temp_s == null && temp_s_parent == null) {

                        ArrayList<token> list = temp_ast.getVar_def_var_expr();

                        String answer = e.evaluate_expression(list, err_list, table, temp_ast.get_ast_node_index());

                        symbol s;

                        if (answer != null) {

                            Log.e("answer", ": " + answer);

                            if (!e.getSTRING_FLAG()) {

                                s = new symbol(temp_ast.get_var_def_var_name(), answer, "T_IDENTIFIER");

                            } else {

                                s = new symbol(temp_ast.get_var_def_var_name(), answer, "T_STRING");

                            }

                            table.add_new_symbol(s);

                        }

                    }else {

                        err_list.add_new_error(new error("symbol " + temp_ast.get_var_def_var_name() + " already present", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;


                    }

                    break;
                }
                case "AST_VAR_DEF_ASSIGNMENT_FUNCTION": {

                    symbol s;
                    temp_s = table.search_symbol(temp_ast.get_var_def_var_name());
                    temp_s_parent = parent_symbol_table.search_symbol(temp_ast.get_var_def_var_name());

                    if (temp_s == null && temp_s_parent == null ) {

                        s = new symbol(temp_ast.get_var_def_var_name(), "NA", "NA");

                        String answer;

                        if (temp_ast.get_function_name().equals("input")) {

                            answer = this.evaluate_input_function_call(temp_ast, k, err_list);

                            if (answer != null) {

                                s.setValue(answer);

                                s.setData_type("NUMBER");

                                table.add_new_symbol(s);

                                Log.e("sensor_val : ", s.getValue());

                            }

                        }

                    } else {

                        err_list.add_new_error(new error("variable " + temp_ast.get_var_def_var_name() + " is already declared", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;


                    }

                    break;
                }
                case "AST_VAR_ASSIGNMENT": {

                    symbol s = table.search_symbol(temp_ast.get_var_name());
                    symbol parent_s = table.search_symbol(temp_ast.get_var_name());

                    expression e = new expression();

                    if (s != null && parent_s == null) {

                        ArrayList<token> list = temp_ast.getVar_expr();

                        String answer = e.evaluate_expression(list, err_list, table, temp_ast.get_ast_node_index());

                        if (answer != null) {

                            Log.e("answer", ": " + answer);

                            if (!e.getSTRING_FLAG()) {

                                s.setValue(answer);

                                s.setData_type("NUMBER");

                                table.update_table(s);

                            } else {

                                s.setValue(answer);

                                s.setData_type("STRING");

                                table.update_table(s);

                            }

                        }

                    }else if(parent_s != null && s == null ){

                        ArrayList<token> list = temp_ast.getVar_expr();

                        String answer = e.evaluate_expression(list, err_list, table, temp_ast.get_ast_node_index());

                        if (answer != null) {

                            Log.e("answer", ": " + answer);

                            if (!e.getSTRING_FLAG()) {

                                parent_s.setValue(answer);

                                parent_s.setData_type("NUMBER");

                                parent_symbol_table.update_table(parent_s);

                            } else {

                                parent_s.setValue(answer);

                                parent_s.setData_type("STRING");

                                parent_symbol_table.update_table(parent_s);

                            }

                        }

                    } else {

                        err_list.add_new_error(new error("Variable " + temp_ast.get_var_name() + " is not declared", temp_ast.get_ast_node_index()));

                        err_list.print_errors();
                        return;


                    }

                    break;
                }
                case "AST_VAR_ASSIGNMENT_FUNCTION": {

                    symbol s = table.search_symbol(temp_ast.get_var_name());
                    symbol parent_s = parent_symbol_table.search_symbol(temp_ast.get_var_name());


                    if (s != null && parent_s == null) {

                        String answer;

                        if (temp_ast.get_function_name().equals("input")) {

                            answer = this.evaluate_input_function_call(temp_ast, k, err_list);

                            if (answer != null) {

                                s.setValue(answer);

                                s.setData_type("NUMBER");

                                table.update_table(s);

                                Log.e("sensor_val : ", s.getValue());

                            }

                        }

                    }else if( parent_s != null && s == null){

                        String answer;

                        if (temp_ast.get_function_name().equals("input")) {

                            answer = this.evaluate_input_function_call(temp_ast, k, err_list);

                            if (answer != null) {

                                parent_s.setValue(answer);

                                parent_s.setData_type("NUMBER");

                                parent_symbol_table.update_table(parent_s);

                                Log.e("sensor_val : ", parent_s.getValue());

                            }

                        }

                    } else {

                        err_list.add_new_error(new error("Variable " + temp_ast.get_var_def_var_name() + " is not declared", temp_ast.get_ast_node_index()));
                        err_list.print_errors();
                        return;

                    }

                    break;
                }
                case "AST_CONDITIONAL_IF" : {

                    Log.e(TAG, "AST_if");
                    Log.e(TAG,"True block");
                    temp_ast.getTrue_block().print_ast();

                    expression e = new expression();

                    symbol_table temp_table = new symbol_table();

                    if (parent_symbol_table != null) {

                        temp_table.getTable().addAll(parent_symbol_table.getTable());
                        temp_table.getTable().addAll(table.getTable());

                    } else {

                        temp_table.getTable().addAll(table.getTable());

                    }

                    temp_table.printTable();

                    for(token t: temp_ast.getConditional_statement_expr()){

                        Log.e(TAG, t.get_content());

                    }

                    String answer = e.evaluate_logical_expression(temp_ast.getConditional_statement_expr() , err_list, temp_table, temp_ast.get_ast_node_index());

                    if( answer != null ) {

                        Log.e(TAG, answer);

                        if (answer.equals("true")) {

                            this.run_block(temp_table, temp_ast.getTrue_block(), err_list);

                        }
                    }else{

                        err_list.print_errors();
                        return;

                    }
                    break;
                }
                case "AST_CONDITIONAL_IF_ELSE" : {

                    Log.e(TAG, "Block if else");

                    Log.e(TAG, "AST_if_else");
                    Log.e(TAG,"True block");
                    temp_ast.getTrue_block().print_ast();
                    Log.e(TAG,"False block");
                    temp_ast.getFalse_block().print_ast();

                    expression e = new expression();

                    symbol_table temp_table = new symbol_table();

                    if (parent_symbol_table != null) {

                        temp_table.getTable().addAll(parent_symbol_table.getTable());
                        temp_table.getTable().addAll(table.getTable());

                    } else {

                        temp_table.getTable().addAll(table.getTable());

                    }

                    temp_table.printTable();

                    for(token t: temp_ast.getConditional_statement_expr()){

                        Log.e(TAG, t.get_type());

                    }

                    String answer = e.evaluate_logical_expression(temp_ast.getConditional_statement_expr(), err_list, temp_table, temp_ast.get_ast_node_index());

                    if( answer != null) {

                        Log.e(TAG, answer);

                        if (answer.equals("true")) {

                            this.run_block(temp_table, temp_ast.getTrue_block(), err_list);

                        } else {

                            this.run_block(temp_table, temp_ast.getFalse_block(), err_list);

                        }

                    }else{

                        err_list.print_errors();
                        return;

                    }
                    break;

                }
            }


        }

    }

    private void evaluate_function_call(keywords k, ast temp_ast, errors err_list, symbol_table table, symbol_table parent_symbol_table){


        symbol_table temp_table = new symbol_table();

        if( parent_symbol_table != null){

            temp_table.getTable().addAll(table.getTable());
            temp_table.getTable().addAll(parent_symbol_table.getTable());

        }else{

            temp_table.getTable().addAll(table.getTable());

        }

        if(k.is_builtin_function(temp_ast.get_function_name())) {

            switch (temp_ast.get_function_name()) {
                case "output":

                    this.evaluate_output_function_call(temp_ast);

                    break;
                case "wait":

                    this.evaluate_wait_function_call(temp_ast, k, temp_table, err_list);

                    break;
                case "input":

                    this.evaluate_input_function_call(temp_ast, k, err_list);

                    break;
            }

        }

    }

    private void evaluate_output_function_call(ast temp_ast){
        ArrayList args = temp_ast.get_args_list();

        function_arg arg1 = (function_arg) args.get(0);
        function_arg arg2 = (function_arg) args.get(1);

        switch (arg1.get_arg_name()) {

            case "led": {

                CameraManager cm = (CameraManager) context.getSystemService(context.CAMERA_SERVICE);

                if (arg2.get_arg_name().equals("HIGH")) {

                    try {
                        cm.setTorchMode("0", true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                } else if (arg2.get_arg_name().equals("LOW")) {

                    try {
                        cm.setTorchMode("0", false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                break;

            }
            case "speaker" : {

                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

                Thread t = new Thread();

                if( arg2.get_arg_name().equals("HIGH")){

                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 3600000);

                }else if( arg2.get_arg_name().equals("LOW")){

                    toneGenerator.stopTone();

                }

            }

        }
    }

    private void evaluate_wait_function_call(ast temp_ast, keywords k, symbol_table table, errors err_list){

        ArrayList args = temp_ast.get_args_list();

        function_arg arg1 = (function_arg) args.get(0);

        if(arg1.get_arg_type().equals("T_IDENTIFIER")){

            symbol s = table.search_symbol(arg1.get_arg_name());

            if( s != null){

                if(k.is_num(s.getValue())){

                    try {
                        TimeUnit.MILLISECONDS.sleep(Long.parseLong(s.getValue()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else{
                    err_list.add_new_error(new error("symbol " + s.getValue() + " is not number", temp_ast.get_ast_node_index()));
                }

            }else{
                err_list.add_new_error(new error("symbol " + arg1.get_arg_name() + " not present", temp_ast.get_ast_node_index()));
            }


        }else if(arg1.get_arg_type().equals("T_CONSTANT")){

            if(k.is_num(arg1.get_arg_name())){

                try {
                    TimeUnit.MILLISECONDS.sleep(Long.parseLong(arg1.get_arg_name()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else{
                err_list.add_new_error(new error("symbol " + arg1.get_arg_name() + " is not number", temp_ast.get_ast_node_index()));
            }

        }

    }

    private String evaluate_input_function_call(ast temp_ast, keywords k, errors err_list) {

        ArrayList arg = temp_ast.get_args_list();

        function_arg arg1 = (function_arg) arg.get(0);

        if (k.is_keyword(arg1.get_arg_name())) {

            switch (arg1.get_arg_name()) {
                case "light": {

                    String val = SENSOR_DATA.getString("LIGHT", "");

                    return val;

                }
                case "temperature": {

                    String val = SENSOR_DATA.getString("TEMPERATURE", "");

                    return val;

                }
                case "gyroscopeX": {

                    String val = SENSOR_DATA.getString("GYROSCOPEX", "");

                    return val;

                }
                case "gyroscopeY": {

                    String val = SENSOR_DATA.getString("GYROSCOPEY", "");

                    return val;

                }
                case "gyroscopeZ": {

                    String val = SENSOR_DATA.getString("GYROSCOPEZ", "");

                    return val;

                }
                case "accelerometerX": {

                    String val = SENSOR_DATA.getString("ACCELEROMETERX", "");

                    return val;

                }
                case "accelerometerY": {

                    String val = SENSOR_DATA.getString("ACCELEROMETERY", "");

                    return val;

                }
                case "accelerometerZ": {

                    String val = SENSOR_DATA.getString("ACCELEROMETERZ", "");

                    return val;

                }
                case "humidity": {

                    String val = SENSOR_DATA.getString("HUMIDITY", "");

                    return val;

                }
                case "airPressure": {

                    String val = SENSOR_DATA.getString("AIRPRESSURE", "");

                    return val;

                }
            }

        } else {

            err_list.add_new_error(new error("sensor " + arg1.get_arg_name() + " not present", temp_ast.get_ast_node_index()));

        }

        return null;

    }
}


