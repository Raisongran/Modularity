package com.raisongran.modularity.processing;

public class LocalMath {

    public static class SmoothFilter {

        public static double Get(double raw, double alpha, double last) {
            return alpha * raw + (1 - alpha) * last;
        }
    }
}
