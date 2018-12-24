package Emulador;

import java.util.*;
public class Z80 {

    /**
     * DEFINICION PINES PASTILLA Z80
     */
    // PINES PRINCIPALES
    public static final int PIN_VCC = 11; //ALIMENTACION +
    public static final int PIN_GND = 29;//ALIMENTACION -
    public static final int PIN_CK = 6;  // RELOJ
    // LINEAS DE DIRECCIONES-ADDRESS BUS
    public static final int PIN_A0 = 30;
    public static final int PIN_A1 = 31;
    public static final int PIN_A2 = 32;
    public static final int PIN_A3 = 33;
    public static final int PIN_A4 = 34;
    public static final int PIN_A5 = 35;
    public static final int PIN_A6 = 36;
    public static final int PIN_A7 = 37;
    public static final int PIN_A8 = 38;
    public static final int PIN_A9 = 39;
    public static final int PIN_A10 = 40;
    public static final int PIN_A11 = 1;
    public static final int PIN_A12 = 2;
    public static final int PIN_A13 = 3;
    public static final int PIN_A14 = 4;
    public static final int PIN_A15 = 5;
    // LINEAS DE DATOS-DATA BUS
    public static final int PIN_D0 = 14;
    public static final int PIN_D1 = 15;
    public static final int PIN_D2 = 12;
    public static final int PIN_D3 = 8;
    public static final int PIN_D4 = 7;
    public static final int PIN_D5 = 9;
    public static final int PIN_D6 = 10;
    public static final int PIN_D7 = 13;
    // SYSTEM CONTROL PINS
    public static final int PIN_M1 = 27;// CICLO DE MAQUINA UNO (M1)
    public static final int PIN_MREQ = 19;// REQUERIMIENTO DE MEMORIA MREQ
    public static final int PIN_IOREQ = 20;// REQUERIMIENTO DE E/S
    public static final int PIN_RD = 21;// LECTURA
    public static final int PIN_WR = 22;// ESCRITURA
    public static final int PIN_RFSH = 28;// REFRESCAR MEMORIA DINAMICA
    // CPU CONTROL PINS
    public static final int PIN_HALT = 18;// PARO
    public static final int PIN_WAIT = 24;// ESPERA
    public static final int PIN_INTMASK = 16;// REQUISICION DE INTERRUPCION MASCARABLE
    public static final int PIN_NMASKINT = 17;// INTERRUPCION NO MASCARABLE
    public static final int PIN_RESET = 26;// REHABILITACION
    // CPU BUS CONTROL
    public static final int PIN_BUSRQ = 25;// REQUERIMIENTO TERMINALES CPU
    public static final int PIN_BUSAK = 23;// ENTREGA TERMINALES CPU
    // PASTILLA Z80
    //Convenciones: 0-estado bajo 1-estado alto 2-tercer estado
    private int[] pastillaZ80 = new int[40];
    private int[] ordenLecPastilla = {11, 29, 6, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 1, 2, 3, 4, 5, 14, 15, 12, 8, 7, 9, 10, 13, 27, 19, 20, 21, 22, 28, 18, 24, 16, 17, 26, 25, 23};
    private String[] ordenNombresPast = {"VCC", "GND", "CLOCK", "A0", "A1", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A15", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7",
        "CICLO_M1", "MREQ", "IOREQ", "READ", "WRITE", "REFRESH", "HALT", "WAIT", "INTMASK", "NMASKINT", "RESET", "BUSRQ", "BUSAK"};

    // PROGRAM COUNTER
    private int PC;
    //STACK POINTER direccion de memoria RAM a partir de donde hay registros 
    private int[] SP;
    private Stack<int[]> stackP;

    // REGISTROS PROPOSITO GENERAL
    // GRUPO 1- binary codes
    private int[] A;//000
    private int[] B;//001
    private int[] C;//010
    private int[] D;//011
    private int[] E;//100
    private int[] H;//101
    private int[] L;//110
    // GRUPO 2
    private int[] Ap;
    private int[] Bp;
    private int[] Cp;
    private int[] Dp;
    private int[] Ep;
    private int[] Hp;
    private int[] Lp;
    //Registros Indices IX IY
    private int[] IX;
    private int[] IY;
    //Registro interrupcion
    private int[] I;
    //BANDERAS DE ESTADO
    private int[] banderas;
    // Posciciones de banderas estado en el arregloo flags
    public static final int ZERO = 6;//000
    public static final int CARRY = 0;//001
    public static final int SIGN = 7;//010
    public static final int PARSOB = 2;//011
    public static final int AUXCARR = 4; //???//100

    //MEMORY
    private ArrayList<String> MEMORY = new ArrayList<>();
    int[] aux = {0, 0, 0, 0, 0, 0, 0, 0};
//CONSTRUCTOR

    public Z80() {
        // Definicion de pines
        pastillaZ80[ordenLecPastilla[0] - 1] = 1;
        pastillaZ80[ordenLecPastilla[1] - 1] = 1;
        pastillaZ80[ordenLecPastilla[2] - 1] = 1;
        for (int i = 3; i < pastillaZ80.length; i++) {
            // Hay que poner los pins en desactivado por ahora todos estaran en 2 (Tercer estado=desactivado)
            pastillaZ80[ordenLecPastilla[i] - 1] = 2;
        }

        // Defincion de variables
        PC = 0; //Primer poscicion de memoria
        SP = new int[16];
        stackP = new Stack<int[]>();
        //Inizializar registros
        A = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        B = new int[]{0, 0, 0, 0, 0, 0, 0, 0};// new int[8];
        C = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        D = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        E = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        H = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        L = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        Ap = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        Bp = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        Cp = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        Dp = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        Ep = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        Hp = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        Lp = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        IX = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IY = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        I = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        banderas = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

    }

//FUNCION DE FUNCIONAMIENTO
    public void Start(int numInstrucc) {
        for (int i = 1; i <= numInstrucc; i++) {
            String inst = getMEMORY()[getPC()];
            execute(inst);
            setPC(getPC() + 1);
        }
    }
//FUNCIONES
    /*
     GUIA INSTRUCCIONES DISPONIBLES (ver manual tecnico)
     */

    //Funciones utiles para manejo de datos
    public void execute(String inst) {
        switch (inst.length()) {
            case 8:
                switch (inst.substring(0, 4)) {

                }
                break;
            case 16:
        }
    }

//FINAL
    public int[] getRegister(char reg) {
        switch (reg) {
            case 'A':
                return getA();
            case 'B':
                return getB();
            case 'C':
                return getC();
            case 'D':
                return getD();
            case 'E':
                return getE();
            case 'H':
                return getH();
            case 'L':
                return getL();
            default:
                return null;
        }
    }

    public int getIntegerValue(int[] val) {
        int a = 0;
        for (int i = val.length - 1; i > 0; i--) {
            if (val[i] == 1) {
                a += Math.pow(2, val.length - (i + 1));
            }
        }
        if (val[0] == 1) {
            a = 0 - a;
        }
        return a;

    }

//FINAL
    public boolean isInRange(int ver) {
        return (ver >= (-127) && ver <= 127);
    }

//FINAL
    public int getFlag(char bandera) {
        switch (bandera) {
            case 'Z':
                return banderas[ZERO];
            case 'C':
                return banderas[CARRY];
            case 'S':
                return banderas[SIGN];
            case 'K':
                return banderas[AUXCARR];
            case 'P':
                return banderas[PARSOB];
            default:
                System.out.println("GETFLAG: Bandera no existe");
                return -1;
        }

    }

//FINAL
    public String getString(int[] data) {
        String r = "";

        for (int i = 0; i < data.length; i++) {
            r = r.concat(Integer.toString(data[i]));
        }
        return r;
    }

    public int[] getIntArray(int value) {
        String str = Integer.toBinaryString(Math.abs(value));

        int j = 0;
        if (str.length() <= 15 && 7 < str.length()) {
            aux = new int[16];
            j = 15;
        } else if (isInRange(value)) {
            aux = new int[8];
            j = 7;
        } else {
            System.out.println("GETINTARR: EL valor no esta en el rango");
            return new int[8];
        }
        if (value < 0) {
            aux[0] = 1;
        }

        for (int i = str.length() - 1; i >= 0; i--) {

            aux[j] = Integer.parseInt(str.substring(i, i + 1));
            j--;
        }
        return aux;

    }

//FINAL
    public int[] getIntArray(String number) {
        if (number != null && !"".equals(number)) {
            int[] result = new int[number.length()];
            char[] ch = number.toCharArray();
            for (int i = 0; i < result.length; i++) {
                result[i] = ch[i] - '0';
            }
            return result;
        } else {
            System.out.println("GETINTARR: Cadena vacia/nula imposible");
            return new int[8];
        }
    }

//FINAL
    public void setRegister(char register, int[] binData) {
        if (isInRange(getIntegerValue(binData))) {
            switch (register) {
                case 'A':
                    setA(binData);
                    break;
                case 'B':
                    setB(binData);
                    break;
                case 'C':
                    setC(binData);
                    break;
                case 'D':
                    setD(binData);
                    break;
                case 'E':
                    setE(binData);
                    break;
                case 'H':
                    setH(binData);
                    break;
                case 'L':
                    setL(binData);
                    break;
                default:
                    break;
            }
        } else {
            System.out.println("SETREGISTER: El valor es muy grande");
        }
    }

// FUNCIONES Z80
//FINAL
    public void LD(char destino, char fuente) {
        if (isInRange(getIntegerValue(getRegister(fuente)))) {
            setRegister(destino, getRegister(fuente));
        } else {
            System.out.println("LD: Registro fuente fuera de rango");
        }

    }
//FINAL
    public void LD(char reg, int[] num) {

        if (isInRange(getIntegerValue(num)) && num.length == 8) {
            setRegister(reg, num);
        } else {
            System.out.println("LD: El numero no es de largo 8 ");
        }
    }
//FINAL
    public void LD(int memory, char reg) {
        if (memory < getMEMORY().length && 0 <= memory) {
            String r = getString(getRegister(reg));

            getMEMORY()[memory] = r;
        } else {
            System.out.println("LD: Posicion de memoria no existe");
        }

    }
//FINAL
    public void LD(char reg, int memPosition) {
        if (memPosition < getMEMORY().length && 0 <= memPosition) {
            String str = getMEMORY()[memPosition];
            if (str.length() == 8) {
                aux = getIntArray(str);
                setRegister(reg, aux);
            } else {
                System.out.println("LD: Cadena no cabe en registro");
            }
        } else {
            System.out.println("LD: Posicion de memoria no existe");
        }
    }

    public void IN() {

    }
//PROVISIONAL
    public void OUT() {
        System.out.println("= " + getIntegerValue(getRegister('A')));
    }
//PROVISIONAL
    public void HALT() {
        //STOP EJECUTION
        System.exit(1);
    }
//FINAL
    public void EX(char regist) {

        switch (regist) {
            case 'A':
                aux = getAp();
                setAp(this.A);
                setA(aux);
                break;
            case 'B':
                aux = getBp();
                setBp(this.B);
                setB(aux);
                break;
            case 'C':
                aux = getCp();
                setCp(this.C);
                setC(aux);
                break;
            case 'D':
                aux = getDp();
                setDp(this.D);
                setD(aux);
                break;
            case 'E':
                aux = getEp();
                setEp(this.E);
                setE(aux);
                break;
            case 'H':
                aux = getHp();
                setHp(this.H);
                setH(aux);
                break;
            case 'L':
                aux = getLp();
                setLp(this.L);
                setL(aux);
                break;
            default:
                System.out.println("EX: No encontre registro");
                break;
        }
    }
//FINAL   
    public void EXX() {
        aux = getAp();
        setAp(getA());
        setA(aux);
        aux = getBp();
        setBp(getB());
        setB(aux);
        aux = getCp();
        setCp(getC());
        setC(aux);
        aux = getDp();
        setDp(getD());
        setD(aux);
        aux = getEp();
        setEp(getE());
        setE(aux);
        aux = getHp();
        setHp(getH());
        setH(aux);
        aux = getLp();
        setLp(getL());
        setL(aux);

    }

    public void ADD(int[] binNumber) {
        int a = getIntegerValue(binNumber);
        a += getIntegerValue(getRegister('A'));

        if (isInRange(a)) {
            setA(getIntArray(a));
            this.banderas[SIGN] = getRegister('A')[0];
            if (a == 0) {
                this.banderas[ZERO] = 1;
            } else if (a < 0) {
                this.banderas[ZERO] = 0;
            }
            this.banderas[CARRY] = 0;
        } else {
            this.banderas[SIGN] = 0;
            this.banderas[ZERO] = 0;
            this.banderas[CARRY] = 1;

            System.out.println("ADD: ES UN NUMERO MUY GRANDE A NO SE ALTERO");
        }
        this.banderas[PARSOB] = 0;
        this.banderas[AUXCARR] = 0;
    }

    public void ADD(int memory) {
        if (memory < getMEMORY().length && 0 <= memory) {
            int a = getIntegerValue(getIntArray(getMEMORY()[memory]));
            a += getIntegerValue(getRegister('A'));

            if (isInRange(a)) {
                setA(getIntArray(a));
                this.banderas[SIGN] = getRegister('A')[0];
                if (a == 0) {
                    this.banderas[ZERO] = 1;
                } else if (a < 0) {
                    this.banderas[ZERO] = 0;
                }
                this.banderas[CARRY] = 0;
            } else {
                this.banderas[SIGN] = 0;
                this.banderas[ZERO] = 0;
                this.banderas[CARRY] = 1;

                System.out.println("ADD: ES UN NUMERO MUY GRANDE A NO SE ALTERO");
            }
            this.banderas[PARSOB] = 0;
            this.banderas[AUXCARR] = 0;
        } else {
            System.out.println("ADD: Posicion de memoria no existe");
        }
    }

    public void ADD(char reg) {
        int a = getIntegerValue(getRegister(reg));
        a += getIntegerValue(getRegister('A'));

        if (isInRange(a)) {
            setA(getIntArray(a));
            this.banderas[SIGN] = getRegister('A')[0];
            if (a == 0) {
                this.banderas[ZERO] = 1;
            } else if (a < 0) {
                this.banderas[ZERO] = 0;
            }
            this.banderas[CARRY] = 0;
        } else {
            this.banderas[SIGN] = 0;
            this.banderas[ZERO] = 0;
            this.banderas[CARRY] = 1;

            System.out.println("ADD: ES UN NUMERO MUY GRANDE A NO SE ALTERO");
        }
        this.banderas[PARSOB] = 0;
        this.banderas[AUXCARR] = 0;
    }

    public void SUB(int[] binNumber) {
        int a = getIntegerValue(binNumber);
        a -= getIntegerValue(getRegister('A'));

        if (isInRange(a)) {
            setA(getIntArray(a));
            if (a == 0) {
                this.banderas[ZERO] = 1;
            } else if (a < 0) {
                this.banderas[SIGN] = 1;
            }
        } else {
            this.banderas[CARRY] = 1;
            System.out.println("SUB: ES UN NUMERO MUY GRANDE");
        }
    }

    public void SUB(int memory) {
    }

    public void SUB(char reg) {
        int b = getIntegerValue(getRegister(reg));
        b -= getIntegerValue(getRegister('A'));
        if (isInRange(b)) {
            setA(getIntArray(b));
            if (b == 0) {
                this.banderas[ZERO] = 1;
            } else if (b < 0) {
                this.banderas[SIGN] = 1;
            }
        } else {
            this.banderas[CARRY] = 1;
            System.out.println("SUB: ES UN NUMERO MUY GRANDE");
        }
    }

    public void INC(char register) {
        int a = getIntegerValue(getRegister(register)) + 1;

        if (isInRange(a)) {
            setRegister(register, getIntArray(a));
            this.banderas[SIGN] = getRegister(register)[0];
            if (a == 0) {
                this.banderas[ZERO] = 1;
            } else if (a < 0) {
                this.banderas[ZERO] = 0;
            }
            this.banderas[CARRY] = 0;
        } else {
            this.banderas[SIGN] = 0;
            this.banderas[ZERO] = 0;
            this.banderas[CARRY] = 1;

            System.out.println("INC: ES UN NUMERO MUY GRANDE A NO SE ALTERO");
        }
        this.banderas[PARSOB] = 0;
        this.banderas[AUXCARR] = 0;
    }

    public void INC(int memPosition) {
        if (memPosition < getMEMORY().length && 0 <= memPosition) {
            int newInt = getIntegerValue(getIntArray(getMEMORY()[memPosition])) + 1;
            if ((newInt >= (-32767) && newInt <= 32767)) {
                getMEMORY()[memPosition] = getString(getIntArray(newInt));
                this.banderas[SIGN] = getIntArray(getMEMORY()[memPosition])[0];
                if (newInt == 0) {
                    this.banderas[ZERO] = 1;
                } else if (newInt < 0) {
                    this.banderas[ZERO] = 0;
                }
                this.banderas[CARRY] = 0;
            } else {
                System.out.println("INC: El numero es muy grande para guardar en memoria");
            }
        } else {
            System.out.println("INC: Direccion de mem no existe");
        }
    }

    public void DEC(char register) {
        int a = getIntegerValue(getRegister(register)) - 1;

        if (isInRange(a)) {
            setRegister(register, getIntArray(a));
            this.banderas[SIGN] = getRegister(register)[0];
            if (a == 0) {
                this.banderas[ZERO] = 1;
            } else if (a < 0) {
                this.banderas[ZERO] = 0;
            }
            this.banderas[CARRY] = 0;
        } else {
            this.banderas[SIGN] = 0;
            this.banderas[ZERO] = 0;
            this.banderas[CARRY] = 1;

            System.out.println("INC: ES UN NUMERO MUY GRANDE A NO SE ALTERO");
        }
        this.banderas[PARSOB] = 0;
        this.banderas[AUXCARR] = 0;
    }

    public void DEC(int memPosition) {
        if (memPosition < getMEMORY().length && 0 <= memPosition) {
            int newInt = getIntegerValue(getIntArray(getMEMORY()[memPosition])) - 1;
            if ((newInt >= (-32767) && newInt <= 32767)) {
                getMEMORY()[memPosition] = getString(getIntArray(newInt));
                this.banderas[SIGN] = getIntArray(getMEMORY()[memPosition])[0];
                if (newInt == 0) {
                    this.banderas[ZERO] = 1;
                } else if (newInt < 0) {
                    this.banderas[ZERO] = 0;
                }
                this.banderas[CARRY] = 0;
            } else {
                System.out.println("INC: El numero es muy grande para guardar en memoria");
            }
        } else {
            System.out.println("INC: Direccion de mem no existe");
        }
    }
//FINAL
    public void CPL() {
        aux = getRegister('A');
        for (int i = 0; i < aux.length; i++) {
            if (aux[i] == 1) {
                aux[i] = 0;
            } else {
                aux[i] = 1;
            }

        }
        int a = getIntegerValue(aux);
        this.banderas[AUXCARR] = 1;
        setA(aux);
    }

    public void NEG() {
        aux = CPL(getRegister('A'));
        int a = getIntegerValue(getRegister('A'));
        this.banderas[PARSOB] = 0;
        this.banderas[CARRY] = 0;
        if (a == -112) {
            this.banderas[PARSOB] = 1;
        }
        if (a != 0) {
            this.banderas[CARRY] = 1;
        }
        a += 1;
        if (isInRange(a)) {
            INC('A');

        } else {

            int[] b = new int[8];
            aux = getIntArray(a);
            System.arraycopy(aux, aux.length - 8, b, 0, 8);
            setRegister('A', b);
        }
        this.banderas[SIGN] = getRegister('A')[0];
        if (a == 0) {
            this.banderas[ZERO] = 1;
        } else {
            this.banderas[ZERO] = 0;
        }
        this.banderas[AUXCARR] = 0;
    }
//FINAL
    public int[] CPL(int[] num) {
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 1) {
                num[i] = 0;
            } else {
                num[i] = 1;
            }

        }
        int a = getIntegerValue(num);
        this.banderas[AUXCARR] = 1;

        return num;
    }

    public int[] NEG(int[] num) {
        num = CPL(num);
        int a = getIntegerValue(num);
        this.banderas[PARSOB] = 0;
        this.banderas[CARRY] = 0;
        if (a == -112) {
            this.banderas[PARSOB] = 1;
        }
        if (a != 0) {
            this.banderas[CARRY] = 1;
        }
        a += 1;
        int[] c = getIntArray(a);

        if (!isInRange(a)) {
            this.banderas[PARSOB] = 1;
            int[] b = new int[8];
            System.arraycopy(c, c.length - 8, b, 0, 8);
            for (int i = 0; i < b.length; i++) {
                System.out.println(b[i]);
            }
            this.banderas[SIGN] = b[0];
            if (getIntegerValue(b) == 0) {
                this.banderas[ZERO] = 1;
            } else {
                this.banderas[ZERO] = 0;
            }
            this.banderas[AUXCARR] = 0;
            return b;
        } else {
            this.banderas[SIGN] = c[0];
            if (getIntegerValue(c) == 0) {
                this.banderas[ZERO] = 1;
            } else {
                this.banderas[ZERO] = 0;
            }
            this.banderas[AUXCARR] = 0;
            return c;
        }

    }

    public void CP(int[] number) {
        int a = getIntegerValue(getRegister('A'));
        a -= getIntegerValue(number);
        if (a == 0) {
            this.banderas[CARRY] = 0;
            this.banderas[ZERO] = 1;
        } else if (a < 0) {
            this.banderas[CARRY] = 1;
            this.banderas[ZERO] = 0;
        } else {
            this.banderas[CARRY] = 0;
            this.banderas[ZERO] = 0;
        }

    }

    public void CP(char reg) {
        int a = getIntegerValue(getRegister('A'));
        a -= getIntegerValue(getRegister(reg));
        if (a == 0) {
            this.banderas[CARRY] = 0;
            this.banderas[ZERO] = 1;
        } else if (a < 0) {
            this.banderas[CARRY] = 1;
            this.banderas[ZERO] = 0;
        } else {
            this.banderas[CARRY] = 0;
            this.banderas[ZERO] = 0;
        }
    }

    public void CP(int memory) {
        if (memory < getMEMORY().length && 0 <= memory) {
            int a = getIntegerValue(getRegister('A'));
            a -= getIntegerValue(getIntArray(getMEMORY()[memory]));
            if (a == 0) {
                this.banderas[CARRY] = 0;
                this.banderas[ZERO] = 1;
            } else if (a < 0) {
                this.banderas[CARRY] = 1;
                this.banderas[ZERO] = 0;
            } else {
                this.banderas[CARRY] = 0;
                this.banderas[ZERO] = 0;
            }
        } else {
            System.out.println("CP: Direccion de memoria no existe");
        }
    }
//FINAL
    public void JP(char bandera, int memory) {
        if (memory < getMEMORY().length && 0 <= memory) {
            if (getFlag(bandera) == 1) {
                this.PC = memory;
            }
        } else {
            System.out.println("JP: Direccion de memoria no existe");
        }
    }
//FINAL
    public void JP(int memory) {
        if (memory < getMEMORY().length && 0 <= memory) {
            this.PC = memory;
        } else {
            System.out.println("JP: Direccion de memoria no existe");
        }
    }
//FINAL
    public void AND(char reg) {
        int[] b = getRegister(reg);
        aux = getRegister('A');
        for (int i = 0; i < b.length; i++) {
            if (aux[i] == 1 && b[i] == 1) {
                b[i] = 1;
            } else {
                b[i] = 0;
            }

        }
        setRegister(reg, b);
    }

    public int[] AND(int[] num) {
        aux = getRegister('A');
        if (isInRange(getIntegerValue(num))) {
            for (int i = 0; i < num.length; i++) {
                if (aux[i] == 1 && num[i] == 1) {
                    num[i] = 1;
                } else {
                    num[i] = 0;
                }

            }
        } else {
            System.out.println("AND: El numero no es del mismo largo que A");
        }
        return num;
    }

    public void AND(int memory) {
        aux = getRegister('A');
        if (memory < getMEMORY().length && 0 <= memory) {
            int[] a = getIntArray(getMEMORY()[memory]);
            if (isInRange(getIntegerValue(a))) {
                for (int i = 0; i < a.length; i++) {
                    if (aux[i] == 1 && a[i] == 1) {
                        a[i] = 1;
                    } else {
                        a[i] = 0;
                    }

                }
                getMEMORY()[memory] = getString(a);
            } else {
                System.out.println("AND: El numero no es del mismo largo que A");
            }

        } else {
            System.out.println("AND: Direccion de memoria no existe");
        }
    }
//FINAL

    public void OR(char reg) {
        int[] b = getRegister(reg);
        aux = getRegister('A');
        for (int i = 0; i < b.length; i++) {
            if (aux[i] == 1 || b[i] == 1) {
                b[i] = 1;
            } else {
                b[i] = 0;
            }

        }
        setRegister(reg, b);
    }

    public int[] OR(int[] num) {
        aux = getRegister('A');
        if (isInRange(getIntegerValue(num))) {
            for (int i = 0; i < num.length; i++) {
                if (aux[i] == 1 || num[i] == 1) {
                    num[i] = 1;
                } else {
                    num[i] = 0;
                }

            }
        } else {
            System.out.println("AND: El numero no es del mismo largo que A");
        }
        return num;
    }

    public void OR(int memory) {
    }
//FINAL
    public void XOR(char reg) {
        
        int[] b = getRegister(reg);
        aux = getRegister('A');
        for (int i = 0; i < b.length; i++) {
            if ((aux[i] == 1 || b[i] == 1) && (aux[i] != b[i])) {
                b[i] = 1;
            } else {
                b[i] = 0;
            }

        }
        setRegister(reg, b);
    }

    public void XOR(int[] num) {
        aux = getRegister('A');
        if (isInRange(getIntegerValue(num))) {
            for (int i = 0; i < num.length; i++) {
                if ((aux[i] == 1 || num[i] == 1) && (aux[i] != num[i])) {
                    num[i] = 1;
                } else {
                    num[i] = 0;
                }

            }
        }
    }

    public void XOR(int memory) {
    }

//FINAL
    public int BIT(int bit, int memory) {
        if (memory < getMEMORY().length && 0 <= memory) {
            if (0 <= bit && bit < getMEMORY()[memory].length()) {
                return (getMEMORY()[memory].charAt(bit)) - '0';
            } else {
                System.out.println("BIT: El bit no existe");
                return -1;
            }

        } else {
            System.out.println("BIT: Direccion de memoria no existe");
            return -1;
        }
    }
//FINAL
    public int BIT(int bit, char reg) {
        if (0 <= bit && bit < 8) {
            return getRegister(reg)[bit];
        } else {
            System.out.println("BIT: El bit no existe");
            return -1;
        }

    }
//FINAL
    public void SET(int bit, int memory) {
        if (memory < getMEMORY().length && 0 <= memory) {
            if (0 <= bit && bit < getMEMORY()[memory].length()) {
                aux = getIntArray(getMEMORY()[memory]);
                aux[bit] = 1;
                getMEMORY()[memory] = getString(aux);
            } else {
                System.out.println("SET: El bit no existe");

            }

        } else {
            System.out.println("SET: Direccion de memoria no existe");

        }
    }
//FINAL
    public void SET(int bit, char reg) {

        if (0 <= bit && bit < 8) {
            aux = getRegister(reg);
            aux[bit] = 1;
            setRegister(reg, aux);
        } else {
            System.out.println("SET: El bit no existe");

        }
    }
//FINAL
    public void RESET(int bit, int memory) {
        if (memory < getMEMORY().length && 0 <= memory) {
            if (0 <= bit && bit < getMEMORY()[memory].length()) {
                aux = getIntArray(getMEMORY()[memory]);
                aux[bit] = 0;
                getMEMORY()[memory] = getString(aux);
            } else {
                System.out.println("RESET: El bit no existe");

            }

        } else {
            System.out.println("RESET: Direccion de memoria no existe");

        }
    }
//FINAL
    public void RESET(int bit, char reg) {
        
        if (0 <= bit && bit < 8) {
            aux = getRegister(reg);
            aux[bit] = 0;
            setRegister(reg, aux);
        } else {
            System.out.println("RESET: El bit no existe");

        }
    }

    /**
     * @return the pastillaZ80
     */
    public int[] getPastillaZ80() {
        return pastillaZ80;
    }

    /**
     * @param pastillaZ80 the pastillaZ80 to set
     */
    public void setPastillaZ80(int[] pastillaZ80) {
        this.pastillaZ80 = pastillaZ80;
    }

    /**
     * @return the ordenLecPastilla
     */
    public int[] getOrdenLecPastilla() {
        return ordenLecPastilla;
    }

    /**
     * @param ordenLecPastilla the ordenLecPastilla to set
     */
    public void setOrdenLecPastilla(int[] ordenLecPastilla) {
        this.ordenLecPastilla = ordenLecPastilla;
    }

    /**
     * @return the ordenNombresPast
     */
    public String[] getOrdenNombresPast() {
        return ordenNombresPast;
    }

    /**
     * @param ordenNombresPast the ordenNombresPast to set
     */
    public void setOrdenNombresPast(String[] ordenNombresPast) {
        this.ordenNombresPast = ordenNombresPast;
    }

    /**
     * @return the PC
     */
    public int getPC() {
        return PC;
    }

    /**
     * @param PC the PC to set
     */
    public void setPC(int PC) {
        this.PC = PC;
    }

    /**
     * @return the SP
     */
    public int[] getSP() {
        return SP;
    }

    /**
     * @param SP the SP to set
     */
    public void setSP(int[] SP) {
        this.SP = SP;
    }

    /**
     * @return the stackP
     */
    public Stack<int[]> getStackP() {
        return stackP;
    }

    /**
     * @param stackP the stackP to set
     */
    public void setStackP(Stack<int[]> stackP) {
        this.stackP = stackP;
    }

    /**
     * @return the A
     */
    public int[] getA() {
        return A;
    }

    /**
     * @param A the A to set
     */
    public void setA(int[] A) {
        this.A = A;
    }

    /**
     * @return the B
     */
    public int[] getB() {
        return B;
    }

    /**
     * @param B the B to set
     */
    public void setB(int[] B) {
        this.B = B;
    }

    /**
     * @return the C
     */
    public int[] getC() {
        return C;
    }

    /**
     * @param C the C to set
     */
    public void setC(int[] C) {
        this.C = C;
    }

    /**
     * @return the D
     */
    public int[] getD() {
        return D;
    }

    /**
     * @param D the D to set
     */
    public void setD(int[] D) {
        this.D = D;
    }

    /**
     * @return the E
     */
    public int[] getE() {
        return E;
    }

    /**
     * @param E the E to set
     */
    public void setE(int[] E) {
        this.E = E;
    }

    /**
     * @return the H
     */
    public int[] getH() {
        return H;
    }

    /**
     * @param H the H to set
     */
    public void setH(int[] H) {
        this.H = H;
    }

    /**
     * @return the L
     */
    public int[] getL() {
        return L;
    }

    /**
     * @param L the L to set
     */
    public void setL(int[] L) {
        this.L = L;
    }

    /**
     * @return the Ap
     */
    public int[] getAp() {
        return Ap;
    }

    /**
     * @param Ap the Ap to set
     */
    public void setAp(int[] Ap) {
        this.Ap = Ap;
    }

    /**
     * @return the Bp
     */
    public int[] getBp() {
        return Bp;
    }

    /**
     * @param Bp the Bp to set
     */
    public void setBp(int[] Bp) {
        this.Bp = Bp;
    }

    /**
     * @return the Cp
     */
    public int[] getCp() {
        return Cp;
    }

    /**
     * @param Cp the Cp to set
     */
    public void setCp(int[] Cp) {
        this.Cp = Cp;
    }

    /**
     * @return the Dp
     */
    public int[] getDp() {
        return Dp;
    }

    /**
     * @param Dp the Dp to set
     */
    public void setDp(int[] Dp) {
        this.Dp = Dp;
    }

    /**
     * @return the Ep
     */
    public int[] getEp() {
        return Ep;
    }

    /**
     * @param Ep the Ep to set
     */
    public void setEp(int[] Ep) {
        this.Ep = Ep;
    }

    /**
     * @return the Hp
     */
    public int[] getHp() {
        return Hp;
    }

    /**
     * @param Hp the Hp to set
     */
    public void setHp(int[] Hp) {
        this.Hp = Hp;
    }

    /**
     * @return the Lp
     */
    public int[] getLp() {
        return Lp;
    }

    /**
     * @param Lp the Lp to set
     */
    public void setLp(int[] Lp) {
        this.Lp = Lp;
    }

    /**
     * @return the IX
     */
    public int[] getIX() {
        return IX;
    }

    /**
     * @param IX the IX to set
     */
    public void setIX(int[] IX) {
        this.IX = IX;
    }

    /**
     * @return the IY
     */
    public int[] getIY() {
        return IY;
    }

    /**
     * @param IY the IY to set
     */
    public void setIY(int[] IY) {
        this.IY = IY;
    }

    /**
     * @return the I
     */
    public int[] getI() {
        return I;
    }

    /**
     * @param I the I to set
     */
    public void setI(int[] I) {
        this.I = I;
    }

    /**
     * @return the banderas
     */
    public int[] getBanderas() {
        return banderas;
    }

    /**
     * @param banderas the banderas to set
     */
    public void setBanderas(int[] banderas) {
        this.banderas = banderas;
    }

    /**
     * @return the MEMORY
     */
    public ArrayList<String> getMEMORY() {
        return MEMORY;
    }

    /**
     * @param MEMORY the MEMORY to set
     */
    public void setMEMORY(ArrayList<String> MEMORY) {
        this.MEMORY = MEMORY;
    }

}
