package Emulador;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.math.BigInteger;

public class Ensamblador {

    //Hash tables para las representaciones binarias de registros y banderas
    HashMap<String, String> registers = new HashMap<>(7);
    HashMap<String, String> flags = new HashMap<>(7);
    //Palabras reservadas para instrucciones del Z80 NO SE PUEDEN USAR COMO OPERADORES O ETIQUETAS
    String[] keys = {"ADD", "AND", "BIT", "CP", "CPL", "DEC", "EX", "EXX", "HALT", "IN", "INC", "JP", "LD", "NEG", "OR", "OUT", "RESET", "SET", "SUB", "XOR"};
    ArrayList<String> keyWords = new ArrayList<>(Arrays.asList(keys));
    //ArrayList para los tokens de la instruccion actual
    ArrayList<String> currentInstr;
    //ArrayList para las etiquetas encontradas
    ArrayList<String> etiquetas = new ArrayList<>();
    //ArrayList para las instrucciones que pasaran al Enlazador-Cargador
    ArrayList<String> lines = new ArrayList<>();
    //CONSTRUCTOR

    public Ensamblador() {
        //Valores de registros
        registers.put("A", "000");
        registers.put("B", "001");
        registers.put("C", "010");
        registers.put("D", "011");
        registers.put("E", "100");
        registers.put("H", "101");
        registers.put("L", "110");
        //Valores de banderas
        flags.put("ZERO", "000");
        flags.put("CARRY", "001");
        flags.put("SIGN", "010");
        flags.put("PARSOB", "011");
        flags.put("AUXCAR", "100");

    }

    public void readFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = br.readLine()) != null) {
                //Saltar lineas en blanco
                if("".equals(line)){
                continue;
                }

                StringTokenizer tokenizer = new StringTokenizer(line, " ,\t");
                currentInstr = new ArrayList<>();
                //Guardar tokens en el arreglo de la instruccion actual
                while (tokenizer.hasMoreTokens()) {
                    String tok = tokenizer.nextToken();
                    currentInstr.add(tok.toUpperCase());

                }
                //Procesar Instruccion
                try {
                    switch (currentInstr.size()) {
                        case 4:
                            //Etiqueta mas instruccion con 2 param
                            //Añadir etiqueta a la lista
                            if (!etiquetas.contains(currentInstr.get(0)) && !currentInstr.get(0).matches(".*\\d+.*")) {
                                etiquetas.add(currentInstr.get(0));

                            } else {
                                System.out.println("Etiqueta: " + currentInstr.get(0) + " ya existe o esta en formato invalido. No se admiten duplicados ni numeros. Por favor corregir archivo fuente!");
                                System.exit(-1);
                            }
                            if (!keyWords.contains(currentInstr.get(1))) {
                                System.out.println("La linea: " + line + " NO es una instruccion valida. Por favor corregir archivo fuente!");
                                System.exit(-1);
                            } else {
                                //procesar
                                lines.add(getBinInstruc(currentInstr.get(0), currentInstr.get(1), currentInstr.get(2), currentInstr.get(3)));
                            }
                            break;
                        case 3:

                            if (!keyWords.contains(currentInstr.get(0))) {
                                //Etiqueta con instruccion de 1 param (indicator = 1)
                                if (!etiquetas.contains(currentInstr.get(0)) && !currentInstr.get(0).matches(".*\\d+.*")) {
                                    etiquetas.add(currentInstr.get(0));

                                } else {
                                    System.out.println("Etiqueta: " + currentInstr.get(0) + " ya existe o esta en formato invalido. No se admiten duplicados ni numeros. Por favor corregir archivo fuente!");
                                    System.exit(-1);
                                }
                                if (!keyWords.contains(currentInstr.get(1))) {
                                    System.out.println("La linea: " + line + " NO es una instruccion valida. Por favor corregir archivo fuente!");
                                    System.exit(-1);
                                } else {
                                    //procesar
                                    lines.add(getBinInstruc(currentInstr.get(0), currentInstr.get(1), currentInstr.get(2), 1));
                                }
                            } else {
                                //O Instrucc con 2 parametros (indicator = 2)
                                //procesar
                                lines.add(getBinInstruc(currentInstr.get(0), currentInstr.get(1), currentInstr.get(2), 2));
                            }

                            break;
                        case 2:

                            if (!keyWords.contains(currentInstr.get(0))) {
                                //Etiqueta con instrucc sin parametros (indicator = 1)
                                if (!etiquetas.contains(currentInstr.get(0)) && !currentInstr.get(0).matches(".*\\d+.*")) {
                                    etiquetas.add(currentInstr.get(0));

                                } else {
                                    System.out.println("Etiqueta: " + currentInstr.get(0) + " ya existe o esta en formato invalido. No se admiten duplicados ni numeros. Por favor corregir archivo fuente!");
                                    System.exit(-1);
                                }
                                if (!keyWords.contains(currentInstr.get(1))) {
                                    System.out.println("La linea: " + line + " NO es una instruccion valida. Por favor corregir archivo fuente!");
                                    System.exit(-1);
                                } else {
                                    lines.add(getBinInstruc(currentInstr.get(0), currentInstr.get(1), 1));
                                }
                            } else if (keyWords.contains(currentInstr.get(0))) {
                                //Instruccion con 1 param (indicator = 2)
                                lines.add(getBinInstruc(currentInstr.get(0), currentInstr.get(1), 2));
                            }
                            break;
                        case 1:
                            if (!keyWords.contains(currentInstr.get(0))) {
                                System.out.println("La linea: " + line + " NO es una instruccion valida. Por favor corregir archivo fuente!");
                                System.exit(-1);
                            } else {
                                //Instruccion sin parametros
                                lines.add(getBinInstruc(currentInstr.get(0)));
                            }
                            break;
                        default:
                            System.out.println("La linea: " + line + " NO es una instruccion valida. Por favor corregir archivo fuente!");
                            System.exit(-1);
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("La linea: " + line + " NO es una instruccion valida. Por favor corregir archivo fuente!");
                    System.exit(-1);
                }
            }
            Path file = Paths.get("EnlazadorIN.txt");
            Files.write(file, lines, Charset.forName("UTF-8"));
            br.close();
            EnlazadorC e = new EnlazadorC();
            e.analizeAssembler("EnlazadorIN.txt", etiquetas, lines);

        } catch (IOException e) {
            System.out.println("ERROR! Archivo no encontrado");
            System.exit(-1);
        }
    }
//Funciones para hayar el codigo de cada instruccion segun sus tokens
//FINAL
    public String getBinInstruc(String etiq, String inst, String param1, String param2) throws Exception {
        String fin = "";
        fin = fin.concat(etiq);
        fin = fin.concat(" ");
        fin = fin.concat(getBinInstruc(inst, param1, param2, 2));
        return fin;

    }
//FINAL
    public String getBinInstruc(String etiqInst, String instParam1, String param1Param2, int indicator) throws Exception {
        String fin = "";
        if (indicator == 1) {
            fin = fin.concat(etiqInst);
            fin = fin.concat(" ");
            fin = fin.concat(getBinInstruc(instParam1, param1Param2, 2));
            return fin;
        } else if (indicator == 2) {
            switch (etiqInst) {
                case "LD":

                    switch (getModoDirecc(instParam1)) {

                        case "Register":
                            String aux;
                            switch (getModoDirecc(param1Param2)) {
                                case "HexaDec":
                                    fin = fin.concat("00000");
                                    aux = HextoBinary(param1Param2.substring(0, param1Param2.length() - 1));
                                    if (aux.equals("NO")) {
                                        throw new NumberFormatException();
                                    } else {
                                        fin = fin.concat(registers.get(instParam1));
                                        fin = fin.concat(aux);
                                        return fin;
                                    }
                                case "MemoryPointer":
                                    param1Param2 = param1Param2.substring(1, param1Param2.length() - 1);
                                    switch (getModoDirecc(param1Param2)) {
                                        case "HexaDec":
                                            fin = fin.concat("00001");

                                            aux = HextoBinary(param1Param2.substring(0, param1Param2.length() - 1));
                                            if (aux.equals("NO")) {
                                                throw new NumberFormatException();
                                            } else {
                                                int a = Integer.parseInt(aux, 2);
                                                if (a < 150 && 0 <= a) {
                                                    fin = fin.concat(registers.get(instParam1));
                                                    fin = fin.concat(aux);
                                                    return fin;
                                                } else {
                                                    throw new NumberFormatException();
                                                }
                                            }

                                        case "Register":
                                            fin = fin.concat("10");
                                            fin = fin.concat(registers.get(instParam1));
                                            fin = fin.concat(registers.get(param1Param2));
                                            return fin;
                                        default:
                                            throw new Exception();
                                    }
                                case "Register":
                                    fin = fin.concat("00");
                                    fin = fin.concat(registers.get(instParam1));
                                    fin = fin.concat(registers.get(param1Param2));
                                    return fin;
                                default:
                                    throw new Exception();
                            }
                        case "MemoryPointer":
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);
                            String au;
                            switch (getModoDirecc(instParam1)) {
                                case "HexaDec":
                                    fin = fin.concat("00011");

                                    au = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                                    if (au.equals("NO")) {
                                        throw new NumberFormatException();
                                    } else {
                                        int a = Integer.parseInt(au, 2);
                                        if (a < 150 && 0 <= a) {
                                            if ("Register".equals(getModoDirecc(param1Param2))) {
                                                fin = fin.concat(au);
                                                fin = fin.concat(registers.get(param1Param2));
                                            } else {
                                                throw new Exception();
                                            }
                                            return fin;
                                        } else {
                                            throw new NullPointerException();
                                        }

                                    }
                                case "Register":
                                    switch (getModoDirecc(param1Param2)) {
                                        case "Register":
                                            fin = fin.concat("01");
                                            fin = fin.concat(registers.get(instParam1));
                                            fin = fin.concat(registers.get(param1Param2));
                                            return fin;
                                        case "HexaDec":
                                            fin = fin.concat("10000");
                                            fin = fin.concat(registers.get(instParam1));
                                            au = HextoBinary(param1Param2.substring(0, param1Param2.length() - 1));
                                            if (au.equals("NO")) {
                                                throw new NumberFormatException();
                                            } else {

                                                fin = fin.concat(au);
                                                return fin;
                                            }
                                        default:
                                            throw new Exception();
                                    }

                                default:
                                    throw new Exception();
                            }

                        default:
                            throw new Exception();
                    }
                case "JP":
                    if ("Flag".equals(getModoDirecc(instParam1))) {
                        switch (getModoDirecc(param1Param2)) {

                            case "HexaDec":

                                fin = fin.concat("00010");
                                fin = fin.concat(flags.get(instParam1));
                                String aux = HextoBinary(param1Param2.substring(0, param1Param2.length() - 1));
                                if (aux.equals("NO")) {
                                    throw new NumberFormatException();
                                } else {
                                    int a = Integer.parseInt(aux, 2);
                                    if (a < 150 && 0 <= a) {
                                        fin = fin.concat(aux);
                                        return fin;
                                    } else {
                                        throw new NullPointerException();
                                    }

                                }

                            case "Tag":
                                //Hay que confiar en que la etiqueta esta o estara mas adelante
                                //La verificacion la hara el enlazador-cargador en su momento
                                fin = fin.concat("0101");
                                fin = fin.concat(flags.get(instParam1));
                                fin = fin.concat(" ");

                                fin = fin.concat(param1Param2);
                                return fin;
                            default:
                                throw new Exception();
                        }
                    } else {
                        throw new Exception();
                    }
                case "BIT":
                    if ("Bit".equals(getModoDirecc(instParam1))) {
                        int val = Integer.parseInt(instParam1);
                        String aux = Integer.toBinaryString(val);

                        switch (getModoDirecc(param1Param2)) {

                            case "MemoryPointer":

                                if (val < 16 && val >= 0) {

                                    fin = fin.concat("0010");
                                    fin = fin.concat(completeFourBits(aux));
                                    fin = fin.concat("00000");
                                    param1Param2 = param1Param2.substring(1, param1Param2.length() - 1);
                                    fin = fin.concat(registers.get(param1Param2));
                                    return fin;
                                } else {

                                    throw new NumberFormatException();
                                }
                            case "Register":

                                if (val < 8 && val >= 0) {

                                    fin = fin.concat("000001110");
                                    fin = fin.concat(completeFourBits(aux));
                                    fin = fin.concat(registers.get(param1Param2));
                                    return fin;
                                } else {

                                    throw new NumberFormatException();
                                }

                            default:

                                throw new Exception();

                        }

                    } else {
                        throw new NumberFormatException();
                    }
                case "SET":
                    if ("Bit".equals(getModoDirecc(instParam1))) {
                        int val = Integer.parseInt(instParam1);
                        String aux = Integer.toBinaryString(val);

                        switch (getModoDirecc(param1Param2)) {

                            case "MemoryPointer":

                                if (val < 16 && val >= 0) {

                                    fin = fin.concat("0100");
                                    fin = fin.concat(completeFourBits(aux));
                                    fin = fin.concat("00000");
                                    param1Param2 = param1Param2.substring(1, param1Param2.length() - 1);
                                    fin = fin.concat(registers.get(param1Param2));
                                    return fin;
                                } else {

                                    throw new NumberFormatException();
                                }
                            case "Register":

                                if (val < 8 && val >= 0) {

                                    fin = fin.concat("000011110");
                                    fin = fin.concat(completeFourBits(aux));
                                    fin = fin.concat(registers.get(param1Param2));
                                    return fin;
                                } else {

                                    throw new NumberFormatException();
                                }

                            default:

                                throw new Exception();

                        }

                    } else {
                        throw new NumberFormatException();
                    }
                case "RESET":

                    if ("Bit".equals(getModoDirecc(instParam1))) {
                        int val = Integer.parseInt(instParam1);
                        String aux = Integer.toBinaryString(val);

                        switch (getModoDirecc(param1Param2)) {

                            case "MemoryPointer":

                                if (val < 16 && val >= 0) {

                                    fin = fin.concat("0110");
                                    fin = fin.concat(completeFourBits(aux));
                                    fin = fin.concat("00000");
                                    param1Param2 = param1Param2.substring(1, param1Param2.length() - 1);
                                    fin = fin.concat(registers.get(param1Param2));
                                    return fin;
                                } else {

                                    throw new NumberFormatException();
                                }
                            case "Register":

                                if (val < 8 && val >= 0) {

                                    fin = fin.concat("000101110");
                                    fin = fin.concat(completeFourBits(aux));
                                    fin = fin.concat(registers.get(param1Param2));
                                    return fin;
                                } else {

                                    throw new NumberFormatException();
                                }

                            default:

                                throw new Exception();

                        }

                    } else {
                        throw new NumberFormatException();
                    }
                default:
                    throw new Exception();
            }

        } else {

            throw new Exception();
        }

    }
//FINAL
    public String getBinInstruc(String etiqInst, String instParam1, int indicator) throws Exception {
        String fin = "";
        if (indicator == 1) {
            fin = fin.concat(etiqInst);
            fin = fin.concat(" ");
            fin = fin.concat(getBinInstruc(instParam1));
            return fin;
        } else if (indicator == 2) {
            switch (etiqInst) {
                case "ADD":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("00111");
                            fin = fin.concat(registers.get(instParam1));
                            return fin;

                        case "HexaDec":

                            fin = fin.concat("11010000");
                            String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                            if (aux.equals("NO")) {
                                throw new NumberFormatException();
                            } else {
                                fin = fin.concat(aux);
                            }
                            return fin;
                        case "MemoryPointer":
                            fin = fin.concat("1100000000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);

                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        default:
                            throw new Exception();

                    }

                case "AND":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("11000");
                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        case "HexaDec":

                            fin = fin.concat("10001000");
                            String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                            if (aux.equals("NO")) {
                                throw new NumberFormatException();
                            } else {
                                fin = fin.concat(aux);
                            }
                            return fin;
                        case "MemoryPointer":
                            fin = fin.concat("1001000000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);

                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        default:
                            throw new Exception();
                    }

                case "CP":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("01");
                            fin = fin.concat(registers.get(instParam1));
                            fin = fin.concat("111");
                            return fin;
                        case "HexaDec":

                            fin = fin.concat("11110000");
                            String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                            if (aux.equals("NO")) {
                                throw new NumberFormatException();
                            } else {
                                fin = fin.concat(aux);
                            }
                            return fin;
                        case "MemoryPointer":
                            fin = fin.concat("1111100000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);

                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        default:
                            throw new Exception();
                    }

                case "DEC":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("11111");
                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        case "MemoryPointer":
                            fin = fin.concat("1110100000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);

                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        default:
                            throw new Exception();
                    }

                case "EX":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("10");
                            fin = fin.concat(registers.get(instParam1));
                            fin = fin.concat("111");
                            return fin;

                        default:
                            throw new Exception();
                    }

                case "IN":

                    switch (getModoDirecc(instParam1)) {

                        case "MemoryPointer":

                            instParam1 = instParam1.substring(1, instParam1.length() - 1);
                            if ("HexaDec".equals(getModoDirecc(instParam1))) {

                                String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                                if (aux.equals("NO")) {
                                    throw new NumberFormatException();
                                } else {
                                    fin = fin.concat("00100111");
                                }
                                return fin;
                            } else {
                                throw new NumberFormatException();
                            }

                        default:
                            throw new Exception();
                    }

                case "INC":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":
                            fin = fin.concat("10111");
                            fin = fin.concat(registers.get(instParam1));
                            return fin;
                        case "MemoryPointer":
                            fin = fin.concat("1110000000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);
                            fin = fin.concat(registers.get(instParam1));
                            return fin;
                        default:
                            throw new Exception();
                    }

                case "JP":

                    switch (getModoDirecc(instParam1)) {

                        case "HexaDec":

                            fin = fin.concat("11111111");
                            String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                            if (aux.equals("NO")) {
                                throw new NumberFormatException();
                            } else {
                                int a = Integer.parseInt(aux, 2);
                                if (a < 150 && 0 <= a) {
                                    fin = fin.concat(aux);
                                    return fin;
                                } else {
                                    throw new NullPointerException();
                                }

                            }

                        case "Tag":
                            //Hay que confiar en que la etiqueta esta o estara mas adelante
                            //La verificacion la hara el enlazador-cargador en su momento
                            fin = fin.concat("10111000");
                            fin = fin.concat(" ");
                            fin = fin.concat(instParam1);
                            return fin;
                        default:
                            throw new Exception();
                    }

                case "OR":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("11001");
                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        case "HexaDec":

                            fin = fin.concat("10011000");
                            String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                            if (aux.equals("NO")) {
                                throw new NumberFormatException();
                            } else {
                                fin = fin.concat(aux);
                                return fin;
                            }

                        case "MemoryPointer":
                            fin = fin.concat("1010000000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);

                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        default:
                            throw new Exception();
                    }

                case "OUT":
                    switch (getModoDirecc(instParam1)) {

                        case "MemoryPointer":

                            instParam1 = instParam1.substring(1, instParam1.length() - 1);
                            if ("HexaDec".equals(getModoDirecc(instParam1))) {

                                String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                                if (aux.equals("NO")) {
                                    throw new NumberFormatException();
                                } else {
                                    fin = fin.concat("00101111");
                                }
                                return fin;
                            } else {
                                throw new NumberFormatException();
                            }

                        default:
                            throw new Exception();
                    }

                case "SUB":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("01111");
                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        case "HexaDec":

                            fin = fin.concat("11011000");
                            String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                            if (aux.equals("NO")) {
                                throw new NumberFormatException();
                            } else {
                                fin = fin.concat(aux);
                            }
                            return fin;
                        case "MemoryPointer":
                            fin = fin.concat("1100100000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);

                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        default:
                            throw new Exception();
                    }

                case "XOR":

                    switch (getModoDirecc(instParam1)) {
                        case "Register":

                            fin = fin.concat("11010");
                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        case "HexaDec":

                            fin = fin.concat("10101000");
                            String aux = HextoBinary(instParam1.substring(0, instParam1.length() - 1));
                            if (aux.equals("NO")) {
                                throw new NumberFormatException();
                            } else {
                                fin = fin.concat(aux);
                            }
                            return fin;
                        case "MemoryPointer":
                            fin = fin.concat("1011000000000");
                            instParam1 = instParam1.substring(1, instParam1.length() - 1);

                            fin = fin.concat(registers.get(instParam1));

                            return fin;
                        default:
                            throw new Exception();
                    }
                default:
                    throw new Exception();
            }

        } else {
            throw new Exception();
        }

    }
//FINAL
    public String getBinInstruc(String inst) throws Exception {
        String fin = "";
        switch (inst) {
            case "CPL":
                fin = fin.concat("00010111");
                return fin;

            case "EXX":
                fin = fin.concat("00001111");
                return fin;

            case "HALT":
                fin = fin.concat("00000111");
                return fin;

            case "NEG":
                fin = fin.concat("00011111");
                return fin;

            default:
                throw new Exception();
        }

    }
//Funciones auxiliares
    public String HextoBinary(String hexadecimal) {
        try {
            String f = new BigInteger(hexadecimal, 16).toString(2);
            if (f.length() < 8) {

                char c = '0';
                int number = 8 - f.length();

                char[] repeat = new char[number];
                Arrays.fill(repeat, c);
                String bin = new String(repeat);
                bin += f;

                return bin;
            } else if (f.length() == 8) {
                return f;
            } else {
                return "NO";
            }
        } catch (NumberFormatException e) {
            return "NO";
        }

    }

    public String getModoDirecc(String operando) {
        try {
            if (operando.startsWith("(") && operando.endsWith(")")) {
                return "MemoryPointer";
            } else if (operando.length() == 1 && registers.containsKey(operando)) {
                return "Register";
            } else if (operando.length() > 1 && (operando.endsWith("H") || operando.endsWith("h"))) {
                return "HexaDec";
            } else if (flags.containsKey(operando)) {
                return "Flag";
            } else if (etiquetas.contains(operando) || !operando.matches(".*\\d+.*")) {
                return "Tag";
            } else if (0 <= Integer.parseInt(operando) && Integer.parseInt(operando) < 16) {
                return "Bit";
            } else {
                return "NO DEFINED!";
            }
        } catch (NumberFormatException e) {
            return "NO DEFINED!";
        }
    }

    public String completeFourBits(String toComplete) throws Exception {
        if (toComplete.length() < 4) {

            char c = '0';
            int number = 4 - toComplete.length();

            char[] repeat = new char[number];
            Arrays.fill(repeat, c);
            String bin = new String(repeat);
            bin += toComplete;

            return bin;
        } else if (toComplete.length() == 4) {
            return toComplete;
        } else {

            throw new NumberFormatException();
        }
    }
}
