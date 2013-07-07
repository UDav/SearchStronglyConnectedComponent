
package com.udav.scc;

import java.io.Serializable;

class Vertex implements Serializable {
    public int x, y ;
    public static int size = 20;
    public static int halfSize = Math.round(size/2) ;
    public static int count ;
    public static int count2 ;
    public int idComponent ;

    public Vertex(int x, int y) {
        this.x = x; this.y = y ;
        idComponent = -1 ;
       // size = Config.sizeVertex;
    }
}
