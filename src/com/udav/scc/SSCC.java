
package com.udav.scc;

import java.io.*;
import java.util.*;

class SCC {

    public Vector result;
    public static int count;
    public int x;
    public int y;

    public SCC() {
        result = new Vector();
        x = 0;
        y = 0;
    }
}

class SSCC {

    private Stack st;
    private int size = 0;
    private int count = 0;
    private int v = 0;
    private boolean visited[];
    private boolean visited2[];
    private int originalArr[][];
    private int inverseArr[][];
    public int condArr[][];
    public SCC[] component;

    private void dfs(int curr) {
        visited[curr] = true; /* помечаем текущую вершину как пройденную */
        for (int i = 0; i < size; i++) {
            if ((!visited[i]) && (inverseArr[curr][i] == 1)) {
                dfs(i);
            }
        }
        st.push(curr);
    }

    private void dfs2(int curr) {
        visited2[curr] = true; /* помечаем текущую вершину как пройденную */
        component[count].result.add(curr);
        for (int i = 0; i < size; i++) {
            if ((!visited2[i]) && (originalArr[curr][i] == 1)) {
                dfs2(i);
            }
        }
    }

    public String searchComponent(int arr[][]) {
        originalArr = arr;
        inverseArr = new int[arr.length][arr.length];
        SCC.count = 0;
        component = new SCC[100];
        
        size = originalArr.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                inverseArr[j][i] = originalArr[i][j];
            }
        }

        visited = new boolean[size];
        visited2 = new boolean[size];

        st = new Stack();

        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                dfs(i);
            }
        }

        for (int i = 0; i < size; i++) {
            if (st.empty() == false) {
                v = (Integer) st.pop();
                if (!visited2[v]) {
                    component[count] = new SCC();
                    SCC.count++;
                    dfs2(v);
                    count++;
                }
            }
        }

        condArr = new int[count][count];


        int c = 0;
        int k = SCC.count - 1;
        while (c < k) {
            SCC temp;
            temp = component[c];
            component[c] = component[k];
            component[k] = temp;
            c++;
            k--;
        }



        for (int i = 0; i < SCC.count; i++) {
            for (int j = 0; j < SCC.count; j++) {
                for (int i1 = 0; i1 < component[i].result.size(); i1++) {
                    for (int j1 = 0; j1 < component[j].result.size(); j1++) {
                        if ((originalArr[(Integer) component[i].result.get(i1)][(Integer) component[j].result.get(j1)] == 1) && (i != j)) {
                            condArr[i][j] = 1;
                        }
                    }
                }
            }
        }
        String Res = "Информация о графе\n";
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "/out.txt")), true);
            out.println("Матрица смежности исходного графа:");
            Res += "Матрица смежности исходного графа:\n";
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    out.print(originalArr[i][j] + "");
                    Res += originalArr[i][j] + "";
                }
                out.println();
                Res += "\n";
            }
            out.println("Компоненты сильной связности:");
            Res += "Компоненты сильной связности:\n";
            for (int i = 0; i < SCC.count; i++) {
                out.print(i + 1 + ") ");
                Res += i + 1 + ") ";
                for (int j = 0; j < component[i].result.size(); j++) {
                    out.print(component[i].result.get(j));
                    out.print(" ");
                    Res += component[i].result.get(j) + " ";
                }
                out.println();
                Res += "\n";
            }

            out.println("Конденсация орграфа:");
            Res += "Конденсация орграфа:\n";
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < count; j++) {
                    out.print(condArr[i][j] + "");
                    Res += condArr[i][j] + "";
                }
                out.println();
                Res += "\n";
            }
            out.close();
        } catch (IOException ex) {
        }
        return Res;
    }
}
