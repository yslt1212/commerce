package de.sep.javafx.component.sidebar;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.concurrent.Callable;

@AllArgsConstructor
@Data
public class SidebarButton {
    private String outLabel;
    private Image inIcon;
    private Button Button;



}
