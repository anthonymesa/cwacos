
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cwacos {

    public static void main(String[] args) {

        /*
        Use the call below to print a random fact about Quokkas.
        CAREFUL! There only 5 calls/day on this API and I (Michael Leonard) get charged $.03 for each additional call.
        */
        //System.out.println(APIClass.getQuokkasFact());
        CwacosUI.startUI(args);
        //CwacosUIController.beginUI(args);
        CwacosTester.begin();
    }
}
