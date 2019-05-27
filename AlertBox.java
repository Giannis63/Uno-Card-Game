import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
public class AlertBox {


    private static String selection;

    public static void infoBox(String infoMessage, String titleBar)
    {
        ButtonType blue = new ButtonType("blue");
        ButtonType red = new ButtonType("red");
        ButtonType yellow = new ButtonType("yellow");
        ButtonType green = new ButtonType("green");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selection + " ?", blue, red, yellow,green);
        alert.showAndWait();

        if (alert.getResult() == red) {

        }

        /* By specifying a null headerMessage String, we cause the dialog to
           not have a header */
        infoBox(infoMessage, titleBar, null);
    }

    public static void infoBox(String infoMessage, String titleBar, String headerMessage)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();

    }
}
