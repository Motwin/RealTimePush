/**
 * 
 */
package com.motwin.sample.realTimePush.datamodel;

/**
 * Datamodel of RealTimePush application
 * 
 * @author Motwin
 * 
 */
public final class M {
    /**
     * RealTimePush table
     */
    public final class RealTimePush {
        public static final String title  = "title";
        public static final String param1 = "param1";
        public static final String param2 = "param2";
        public static final String param3 = "param3";
        public static final String param4 = "param4";
        public static final String param5 = "param5";
        public static final String param6 = "param6";
        public static final String param7 = "param7";
        public static final String param8 = "param8";
        public static final String price  = "price";

        public String entityName() {
            return RealTimePush.class.getSimpleName();
        }
    }
}
