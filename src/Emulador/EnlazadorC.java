package Emulador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

public class EnlazadorC {

    ArrayList<String> currentLine;
    ArrayList<String> tags;
    HashMap<String, String> tagsMemoryPos;
    Z80 z80 ;
    int memPosition ;
    //Funcion Principal

    public EnlazadorC() {
        tagsMemoryPos =new HashMap<>();
        z80 = new Z80();
        memPosition = 0;
    }

    public void analizeAssembler(String fileName, ArrayList<String> tags, ArrayList<String> originalLines) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            this.tags = tags;
            String line;
            while ((line = br.readLine()) != null) {
                //Saltar lineas en blanco
                if ("".equals(line)) {
                    continue;
                }

                StringTokenizer tokenizer = new StringTokenizer(line, " \t");
                currentLine = new ArrayList<>();
                //Guardar tokens en el arreglo de la instruccion actual
                while (tokenizer.hasMoreTokens()) {
                    String tok = tokenizer.nextToken();
                    currentLine.add(tok.toUpperCase());

                }
                //Verificar banderas en las instrucciones
                try {
                    switch (currentLine.size()) {
                        case 1:
                            if (currentLine.get(0).matches("[01]+")) {
                                memPosition++;
                                break;
                            } else {
                                throw new Exception();
                            }
                        case 2:
                            if (!currentLine.get(0).matches(".*\\d+.*") && currentLine.get(1).matches("[01]+")) {
                                //es una etiqueta que hay que verificar
                                if (tags.contains(currentLine.get(0))) {
                                    //Guardar posicion de la etiqueta para poner en codigo
                                    String position = complete8bits(memPosition);
                                    //Agregar a Tabla de simbolos para etiquetas y dir de memoria
                                    tagsMemoryPos.put(currentLine.get(0), position);
                                    
                                    //Modificar instruccion original para quitar etiqueta ya guardada
                                    originalLines.set(memPosition, currentLine.get(1));
                                    System.out.println("Cambie la linea: "+memPosition);
                                    System.out.println("Encontre una etiqueta en la linea: "+memPosition);
                                    //Dejar lista sig posicion de memoria
                                    memPosition++;
                                    break;
                                } else {
                                    throw new Exception();
                                }
                            } else if (!currentLine.get(1).matches(".*\\d+.*") && currentLine.get(0).matches("[01]+")) {
                                if (tags.contains(currentLine.get(1))) {
                                    //Si la etiqueta ya esta en la tabla de simbolos, poner su codigo en la instruccion de una vez
                                    if (tagsMemoryPos.containsKey(currentLine.get(1))) {
                                        String newCommand = currentLine.get(0).concat(tagsMemoryPos.get(currentLine.get(1)));
                                        originalLines.set(memPosition, newCommand);
                                        System.out.println("Cambie la linea: "+memPosition);
                                    }
                                    memPosition++;
                                    break;
                                } else {
                                    throw new Exception();
                                }

                            } else {
                                throw new Exception();
                            }
                        case 3:
                            if (!currentLine.get(0).matches(".*\\d+.*") && !currentLine.get(2).matches(".*\\d+.*") && currentLine.get(1).matches("[01]+")) {
                                if (tags.contains(currentLine.get(0)) && tags.contains(currentLine.get(2))) {
                                    //Guardar posicion de la etiqueta para poner en codigo
                                    String position = complete8bits(memPosition);
                                    //Agregar a Tabla de simbolos para etiquetas y dir de memoria
                                    tagsMemoryPos.put(currentLine.get(0), position);
                                    String newCommand;
                                    //Si la etiqueta 2 ya esta en la tabla de simbolos, poner su codigo en la instruccion de una vez
                                    if (tagsMemoryPos.containsKey(currentLine.get(2))) {
                                        newCommand = currentLine.get(1).concat(tagsMemoryPos.get(currentLine.get(2)));
                                        originalLines.set(memPosition, newCommand);
                                        
                                    
                                    System.out.println("Cambie la linea: "+memPosition);
                                    System.out.println("2 Etiq Encontre una etiqueta en la linea: "+memPosition);
                                        //Dejar lista sig posicion de memoria
                                        memPosition++;
                                        break;
                                    } else {
                                        //Modificar instruccion original para quitar etiqueta ya guardada unicamente
                                        newCommand = currentLine.get(1).concat(" " + currentLine.get(2));
                                        originalLines.set(memPosition, newCommand);
                                        System.out.println("2 Etiq Encontre una etiqueta en la linea: "+memPosition);
                                        //Dejar lista sig posicion de memoria
                                        memPosition++;
                                        break;
                                    }
                                } else {
                                    throw new Exception();
                                }
                            } else {
                                throw new Exception();
                            }

                        default:
                            System.out.println("La linea: " + line + " NO es una instruccion valida. Archivo Ensamblador Corrupto!");
                            System.exit(-1);
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("La linea: " + line + " NO es una instruccion valida. Archivo Ensamblador Corrupto!");
                    System.exit(-1);
                }
            }
            br.close();
            
            //Despues de verificar las etiquetas se procede a guardar en memoria y cambiar etiquetas por su posicion en memoria
            try {
                memPosition = 0;
                putInstruc(originalLines);

            } catch (Exception e) {
                System.out.println("ERROR! Instrucciones Corruptas");
                System.exit(-1);
            }
            

        } catch (IOException e) {
            System.out.println("ERROR! Archivo no encontrado");
            System.exit(-1);
        }
    }

    public void putInstruc(ArrayList<String> originalLines) throws Exception {

        for (int i = 0; i < originalLines.size(); i++) {
            String actualLine = originalLines.get(i);
            StringTokenizer tokenizer = new StringTokenizer(actualLine, " \t");
            currentLine = new ArrayList<>();
            //Guardar tokens en el arreglo de la instruccion actual
            while (tokenizer.hasMoreTokens()) {
                String tok = tokenizer.nextToken();
                currentLine.add(tok.toUpperCase());

            }
            try {
                switch (currentLine.size()) {
                    case 1:
                        if (currentLine.get(0).matches("[01]+")) {
                            memPosition++;
                            break;
                        } else {
                            throw new Exception();
                        }
                    case 2:
                        if (currentLine.get(0).matches("[01]+") & !currentLine.get(1).matches(".*\\d+.*")) {
                                //Hay una etiqueta sin reemplazar

                            if (tags.contains(currentLine.get(1)) && tagsMemoryPos.containsKey(currentLine.get(1))) {
                                String newCommand = currentLine.get(0).concat(tagsMemoryPos.get(currentLine.get(1)));
                                        originalLines.set(memPosition, newCommand);
                                        System.out.println("Hice ultimo cambio en la linea: "+memPosition);
                                memPosition++;
                                break;
                            } else {
                                throw new Exception();
                            }
                        } else  {
                            throw new Exception();
                        }

                    default:
                        System.out.println("La linea: " + currentLine + " Tiene problemas aun. ");
                        System.exit(-1);
                        break;

                }
            } catch (Exception e) {
                System.out.println("La linea: " + currentLine + " Tiene problemas aun. ");
                System.exit(-1);
            }

            
        }

        //Poner todo en la memoria
        Path file = Paths.get("Memory.txt");
            Files.write(file, originalLines, Charset.forName("UTF-8"));
        //z80.setMEMORY(originalLines);

    }

    //Funciones Auxiliares
    public String complete8bits(int number) throws Exception {
        try {
            String bin = Integer.toBinaryString(number);
            if (bin.length() < 8) {
                char c = '0';
                int faltan = 8 - bin.length();

                char[] repeat = new char[faltan];
                Arrays.fill(repeat, c);
                String binComplete = new String(repeat);
                binComplete += bin;
                return binComplete;
            } else if (bin.length() == 8) {
                return bin;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

}
