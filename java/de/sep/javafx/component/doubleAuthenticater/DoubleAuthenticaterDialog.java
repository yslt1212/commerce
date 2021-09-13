package de.sep.javafx.component.doubleAuthenticater;


import de.sep.javafx.services.AuthenticationService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Dialog;



public class DoubleAuthenticaterDialog extends Dialog<Boolean> {

    SimpleIntegerProperty success = new SimpleIntegerProperty(-1);
    DoubleAuthenticater doubleAuthenticater;
    public void setUp() {
        success.addListener((observableValue, oldval, newval)-> {
            switch (newval.intValue()) {
                case -1 -> {

                }
                case 0 -> {
                    setResult(false);
                }
                case 1 -> {
                    setResult(true);
                }
            }

        });
        getDialogPane().setContent(doubleAuthenticater);
    }

    public DoubleAuthenticaterDialog(AuthenticationService authenticationService, String email) {
        super();
        doubleAuthenticater = new DoubleAuthenticater(success, authenticationService, email);
        setUp();
    }

}
