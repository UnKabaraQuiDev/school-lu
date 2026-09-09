/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Power {
    
    public static double power(double x, int n) {
        if (n < 0) {
            return power(1/x, -n);
        } else if (n == 0) {
            return 1;
        } else {
            return x * power(x, n-1);
        }
        
        //return (n<0?power(1/x,-n):n==0?1:x*power(x,n-1));
    }
    
    public static void main(String[] args) {
        System.out.println(power(5,3));
    }
}
