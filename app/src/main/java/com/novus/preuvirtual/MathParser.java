package com.novus.preuvirtual;

/**
 * Created by Martín on 30-05-2015.
 */
public class MathParser {

    public String parse(String equation){
        String newEquation = "";
        char c, v;
        int equalsMarker = 0, divisionMarker = 0, anotherMarker = 0, newMarker = 0;
        for(int i = 0; i < equation.length(); i++){
            c = equation.charAt(i);
            switch(c){
                case '=':
                    anotherMarker = 0;
                    equalsMarker = i;
                    newEquation = newEquation + c;
                    newMarker++;
                    if(divisionMarker != 0){
                        newEquation = newEquation + "<sub>";
                        newMarker = newMarker + 5;
                        for(int j = divisionMarker+1; j < i-1; j++){
                            v = equation.charAt(j);
                            newEquation = newEquation + v;
                            newMarker++;
                        }
                        newEquation = newEquation + "</sub> ";
                        newMarker = newMarker + 7;
                        divisionMarker = 0;
                    }
                    break;
                case '/':
                    newMarker = newMarker - anotherMarker;
                    newEquation = newEquation.substring(0, newMarker);
                    anotherMarker = 0;
                    newEquation = newEquation + " <sup>";
                    newMarker = newMarker + 6;
                    for(int j = equalsMarker+2; j < i; j++){
                        v = equation.charAt(j);
                        newEquation = newEquation + v;
                        newMarker++;
                    }
                    newEquation = newEquation + "</sup>" + c;
                    newMarker = newMarker + 7;
                    divisionMarker = i;
                    equalsMarker = 0;
                    break;
                case ',':
                    newMarker = newMarker - anotherMarker;
                    newEquation = newEquation.substring(0, newMarker);
                    anotherMarker = 0;
                    if(divisionMarker != 0){
                        newEquation = newEquation + "<sub>";
                        newMarker = newMarker + 5;
                        for(int j = divisionMarker+1; j < i; j++){
                            v = equation.charAt(j);
                            newEquation = newEquation + v;
                            newMarker++;
                        }
                        newEquation = newEquation + "</sub> ";
                        newMarker = newMarker + 6;
                        divisionMarker = 0;
                    }
                    newEquation = newEquation + c;
                    newMarker++;
                    break;
                default:
                    newEquation = newEquation + c;
                    newMarker++;
                    anotherMarker++;
                    break;
            }
            if(i == equation.length()-1){
                if(divisionMarker > 0){
                    newMarker = newMarker - anotherMarker;
                    newEquation = newEquation.substring(0, newMarker);
                    anotherMarker = 0;
                    newEquation = newEquation + "<sub>";
                    newMarker = newMarker + 5;
                    for(int j = divisionMarker+1; j < i; j++){
                        v = equation.charAt(j);
                        newEquation = newEquation + v;
                        newMarker++;
                    }
                    newEquation = newEquation + "</sub>";
                    newMarker = newMarker + 6;
                    divisionMarker = 0;
                }
                newEquation = newEquation + c;
                newMarker++;
            }

        }

        return newEquation;
    }

}
