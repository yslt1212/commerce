package de.sep.javafx.util;

import de.sep.domain.model.User;
import javafx.beans.property.ObjectProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HomeControllerState {
    String labelPartOne;
    String labelPartTwo;
    String label;
    HomeControllerStates state;
    Runnable stateFunction;
    ObjectProperty<User> user;
    public HomeControllerState(String labelPartOne,String labelPartTwo, HomeControllerStates state, Runnable stateFunction, ObjectProperty<User> user) {
        this.labelPartOne = labelPartOne;
        this.labelPartTwo = labelPartTwo;
        this.state = state;
        this.stateFunction = stateFunction;
        this.user = user;
        user.addListener((observableValue, user1, t1) -> {
            if(t1 != null) {
                this.setLabel(t1);
            }

        });
        setLabel(user.get());
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(User user)  {
        String baseText = "%s  %s\n" +
                "%s";
        baseText = String.format(baseText,labelPartOne, user.getUsername(), labelPartTwo);
        label = baseText;
    }

}
