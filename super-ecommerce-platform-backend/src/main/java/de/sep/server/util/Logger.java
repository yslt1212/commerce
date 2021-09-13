package de.sep.server.util;

import lombok.Data;

import java.util.Arrays;
import java.util.Date;


@Data
public class Logger {
    String message;
    String parentClassName;
    Date date;

    public Logger(String parentClassName) {
        this.parentClassName = parentClassName;
    }

    public void log(Object... val) {

        this.message = valToString(val);
        this.date = new Date();
        System.out.println(this.toString());
    }

    private String valToString(Object... val) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for(Object data: val) {
            String addData = "";
            if(data.toString().length() > 300) {
                addData = data.toString().substring(0, 300) + "...";
            } else {
                addData = data.toString();
            }
            if(first) {
                stringBuilder.append("\n \t" + addData);
                first = false;
            } else {
                stringBuilder.append(",\n \t" + addData);
            }
        }
        Arrays.stream(val).forEach(data -> {





        });
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        String returnString = String.format(
                "--- Log Start --- \n" +
                "Message: %s,\n" +
                "in: %s,\n" +
                "atTime: %s \n" +
                "-- Log End ---",
                this.message, this.parentClassName, this.date.toString());
        return returnString;
    }
}
