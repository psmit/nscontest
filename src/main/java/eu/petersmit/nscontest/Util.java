package eu.petersmit.nscontest;

import java.util.*;

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

    public static <T> List<Set<T>> powerset(Collection<T> collection) {
        List<Set<T>> powerset = new ArrayList<Set<T>>();

        // Add empty class
        powerset.add(new HashSet<T>());

        for (T item : collection) {
            List<Set<T>> nextPowerset = new ArrayList<Set<T>>();

            for (Set<T> set : powerset) {
                nextPowerset.add(set);

                Set<T> newSet = new HashSet<T>(set);
                newSet.add(item);
                nextPowerset.add(newSet);
            }

            powerset = nextPowerset;
        }

        return powerset;
    }


}
