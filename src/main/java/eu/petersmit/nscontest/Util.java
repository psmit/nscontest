package eu.petersmit.nscontest;

/**
 * Utility class (should not be initizalized)
 */
public class Util {
    private Util() {
    }


    /**
     * Deep-clone multi dimensional array a
     *
     * @param a input array
     * @return deep cloned array
     */
    public static int[][] clone(int[][] a) {
        int[][] b = new int[a.length][];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i].clone();
        }
        return b;
    }

}
