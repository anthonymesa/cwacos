import javafx.scene.control.*;
import javafx.event.*;

public class CwacosUI  {

    public CwacosUI(){}
    public static void startUI(String[] args){
        CwacosView.beginUI(args);
    }

    public Button addDialogFunction(TextInputDialog td, Button btn){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // show the text input dialog
                td.show();
            }
        };

        btn.setOnAction(event);

        return btn;
    }
}