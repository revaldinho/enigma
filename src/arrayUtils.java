/** Provide facilities for manipulating arrays of primitive types similar to those that the Collections class provides for arrays of objects. */

public class arrayUtils { 

   /** Rotate an array of integers by a given number of places. The original array is unchanged and the routine                                         
        returns a pointer to the rotated version. i.e. use   a = rotate(a,n) to update the original array contents.*/
    public static int [] rotate ( int[] c , int places ) {
        // Create a new array in which to store the rotated version                                                                                      
        int d [] = new int[ c.length];
        // Convert all rotates to a rotate left operation                                                                                                
        int absPlaces = (places+c.length)%c.length;
        System.arraycopy(c, c.length - absPlaces, d, 0, absPlaces );
        System.arraycopy(c, 0, d, absPlaces, c.length-absPlaces );
        return d;
    }

     /** Rotate an array of chars by a given number of places. The original array is unchanged and the routine                                         
        returns a pointer to the rotated version. i.e. use   a = rotate(a,n) to update the original array contents.*/
    public static char [] rotate ( char[] c , int places ) {
        // Create a new array in which to store the rotated version                                                                                      
        char d [] = new char[ c.length];
        // Convert all rotates to a rotate left operation                                                                                                
        int absPlaces = (places+c.length)%c.length;
        System.arraycopy(c, c.length - absPlaces, d, 0, absPlaces );
        System.arraycopy(c, 0, d, absPlaces, c.length-absPlaces );
        return d;
    }


   /** Rotate in place an array of chars by a given number of places. */
    public static void rotateInPlace ( char[] c , int places ) {
        // First convert all rotates to a single rotate left operation                                                                                   
        int absPlaces = (places+c.length)%c.length;
        // Take a copy of bits which would otherwise be overwritten                                                                                      
        char d [] = new char[ c.length-absPlaces];
        System.arraycopy(c, 0, d, 0, d.length );
        // Move bits from the RHS to the LHS of the array as required - these segments don't overlap                                                     
        System.arraycopy(c, c.length - absPlaces, c, 0, absPlaces );
        // Move bits from the LHS of the array to the RHS as required. Use the copy of the array here else                                               
        // we'd pick up bits which has already been moved by the copy above.                                                                             
        System.arraycopy(d, 0, c, absPlaces, c.length-absPlaces );

    }

 /** Rotate in place an array of integers by a given number of places. */
    public static void rotateInPlace ( int[] c , int places ) {
        // First convert all rotates to a single rotate left operation                                                                                   
        int absPlaces = (places+c.length)%c.length;
        // Take a copy of bits which would otherwise be overwritten                                                                                      
        int d [] = new int[ c.length-absPlaces];
        System.arraycopy(c, 0, d, 0, d.length );
        // Move bits from the RHS to the LHS of the array as required - these segments don't overlap                                                     
        System.arraycopy(c, c.length - absPlaces, c, 0, absPlaces );
        // Move bits from the LHS of the array to the RHS as required. Use the copy of the array here else                                               
        // we'd pick up bits which has already been moved by the copy above.                                                                             
        System.arraycopy(d, 0, c, absPlaces, c.length-absPlaces );

    }

}