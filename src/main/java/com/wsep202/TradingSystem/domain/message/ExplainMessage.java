/**
 * Requirement number e3:
 * this class represents an explanation to the user about the system operation results.
 */

package com.wsep202.TradingSystem.domain.message;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
public class ExplainMessage {
//    private boolean isSucceeded;    //tells if there are failing messages in the explanations.
    private ArrayList<String> explanations; // list of system explain messages for the user

    public ExplainMessage(){
        explanations = new ArrayList<>();
//        isSucceeded = true;
    }

    /**
     * add explanation to the user about the system operation results
     * @param msg - success or error message
     */
    public void addExplanation(String msg){
        this.explanations.add(msg);
    }

}
