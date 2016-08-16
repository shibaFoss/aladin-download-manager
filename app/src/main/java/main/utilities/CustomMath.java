package main.utilities;

import java.text.DecimalFormat;

/**
 * This class handles all the math related task. Use it as static import or like static functions.
 * <p/>
 * Created by shibaprasad on 10/17/2014.
 */
@SuppressWarnings("UnusedDeclaration")
public class CustomMath {

    /**
     * Get format value of the given offset.
     *
     * @param offset the offset.
     * @return the value.
     */
    public static float formatTheOffset(float offset) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Float.valueOf(df.format(offset));
    }


}
