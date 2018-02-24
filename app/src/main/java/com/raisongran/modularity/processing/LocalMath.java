package com.raisongran.modularity.processing;

public class LocalMath {

    public static class SmoothFilter {

        private double last = 0;

        public SmoothFilter() {}

        public double Get(double raw, double alpha) {

            double result = alpha * raw + (1 - alpha) * last;
            last = result;
            return result;
        }
    }
}
