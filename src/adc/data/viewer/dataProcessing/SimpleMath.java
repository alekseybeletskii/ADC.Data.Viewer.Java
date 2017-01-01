package adc.data.viewer.dataProcessing;

import java.util.Arrays;
import java.util.OptionalDouble;


public class SimpleMath {
    private static double max=0;
    private static double min=0;

    public static double getMax() {
        return max;
    }

    public static double getMin() {
        return min;
    }

    public static void findMaxMin(double [] anArray){

        OptionalDouble arrayMax = Arrays.stream(anArray).max();
        OptionalDouble arrayMin = Arrays.stream(anArray).min();

        if (arrayMax.isPresent()) max = arrayMax.getAsDouble();
        if (arrayMin.isPresent()) min = arrayMin.getAsDouble();

    }
}
