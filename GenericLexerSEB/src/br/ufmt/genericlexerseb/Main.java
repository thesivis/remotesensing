/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufmt.genericlexerseb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author raphael
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        String eq = "transmissividade = 0.35 + 0.627 * exp((-0.00146 * P / (Kt * cosZ)) - 0.075 * pow((W / cosZ), 0.4))";
        
//        List<String> variables = new ArrayList<>();
//        variables.add("Ts");
//        variables.add("emissividadeNB");
//        variables.add("banda6");
//        variables.add("K2");
//        variables.add("K1");

//        eq = "4*x+5.2024-(logb(x,y)^-z)-300.12";
//        eq = "(-1.0 + 2.123)*(2*a/b)-sqrt(5.0+6)";
//        eq = "-2*ln(2)-(a-(b^-2))";
        
        ExpressionParser ex = new ExpressionParser();
//        System.out.println(Arrays.toString(ret.toArray()));
//        Object[] t = (Object[])ret.toArray();
//        String[] a = ExpressionParser.tokenize(eq);
//        System.out.println(Arrays.toString(a));
//        for (int i = 0; i < a.length; i++) {
////            System.out.println(i);
//            a[i]=(String)t[i];
//            
//        }
        System.out.println(ex.evaluateExpr(eq));
        System.out.println(Arrays.toString(ex.getOutput()));
//        System.out.println(Arrays.toString(ExpressionParser.infixToRPN(a)));

    }
    

}
