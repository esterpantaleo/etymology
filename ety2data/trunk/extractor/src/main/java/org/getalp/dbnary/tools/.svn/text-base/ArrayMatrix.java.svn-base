package org.getalp.dbnary.tools;

import java.util.ArrayList;

/**
 * Created by serasset on 07/01/15.
 */
public class ArrayMatrix<T> {

    private ArrayList<ArrayList<T>> matrix = new ArrayList<>();

    public void set(int i, int j, T val) {
        if (matrix.size() <= i) {
            for (int k = matrix.size(); k <= i; k++) {
                matrix.add(new ArrayList<T>());
            }
        }
        ArrayList<T> row = matrix.get(i);
        if (row.size() <= j) {
            for (int k = row.size(); k <= j; k++) {
                row.add(null);
            }
        }
        row.set(j, val);
    }

    public T get(int i, int j) {
        if (matrix.size() <= i) return null;
        ArrayList<T> row = matrix.get(i);
        if (row.size() <= j) return null;
        return row.get(j);
    }

}
