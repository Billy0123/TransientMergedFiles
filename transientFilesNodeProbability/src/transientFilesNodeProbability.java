
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Locale;

public class transientFilesNodeProbability {

    public transientFilesNodeProbability () throws FileNotFoundException {
        File workDirectory = new File("../TRANS");
        for (int i=0;i<workDirectory.listFiles().length;i++) {
        //for (int i=30;i<30+23;i++) {//SZTUCZNE ZMNIEJSZENIE LICZBY KONFIGURACJI
            System.setErr(new PrintStream(new File("../j-none_NodeProbability_arg-"+workDirectory.listFiles()[i].getName().substring(23,28)+".txt")));
            try {
                BufferedReader reader = new BufferedReader(new FileReader(workDirectory.listFiles()[i]));
                double latticeCoef,multimerS=1.0,b=Math.PI/6.0,ROkreguOpisanego=multimerS/(2.0*Math.sin(b));
                int X=14,Y=16,N=X*Y;
                double histogramRange[]=new double[]{-5,5}, rozdzielczosc=2500, cellDr=(histogramRange[1]-histogramRange[0])/rozdzielczosc;
                int probabDens[][]=new int[(int)rozdzielczosc][(int)rozdzielczosc]; for (int j=0;j<rozdzielczosc;j++) for (int k=0;k<rozdzielczosc;k++) probabDens[j][k]=0;
                int lineLicznik=0; String line; 
                while ((line=reader.readLine())!=null) {
                    long startTime=System.currentTimeMillis();
                    lineLicznik++; 
                    String configurationFile[] = line.split("\t");
                    System.out.println("File: "+String.valueOf(i+1)+"/"+String.valueOf(workDirectory.listFiles().length)+", configuration: "+String.valueOf(lineLicznik)+"/"+String.valueOf(100));
                    double boxSize[] = new double[]{Double.parseDouble(configurationFile[1]),Double.parseDouble(configurationFile[2]),Double.parseDouble(configurationFile[3])};
                    latticeCoef=(boxSize[0]/(double)X+boxSize[1]/(double)Y/Math.sqrt(3)*2.0)/2.0;
                    String multimers[] = configurationFile[4].split("m");
                    double multimersTableD[][] = new double[multimers.length-1][3];
                    for (int j=1;j<multimers.length;j++) {
                        String multimersTable[]=multimers[j].substring(1,multimers[j].indexOf("]")).split(",");
                        for (int k=0;k<3;k++) multimersTableD[j-1][k]=Double.parseDouble(multimersTable[k]);
                    }
                    
                    double shiftCoord[] = new double[]{multimersTableD[0][0]-latticeCoef/4.0,multimersTableD[0][1]-latticeCoef*Math.sqrt(3)/4.0},
                           SMCoord[] = new double[]{0,0};
                    for (int j=0;j<multimersTableD.length;j++) {
                        for (int k=0;k<2;k++) multimersTableD[j][k]-=shiftCoord[k];
                        double xNegative=multimersTableD[j][0]<0?1:multimersTableD[j][0]>boxSize[0]?-1:0,
                               yNegative=multimersTableD[j][1]<0?1:multimersTableD[j][1]>boxSize[1]?-1:0;
                        multimersTableD[j][0]+=xNegative*boxSize[0]+yNegative*boxSize[2];
                        multimersTableD[j][1]+=xNegative*boxSize[2]+yNegative*boxSize[1];
                        for (int k=0;k<2;k++) SMCoord[k]+=multimersTableD[j][k];
                    }
                    for (int k=0;k<2;k++) SMCoord[k]/=(double)N;
                    for (int j=0;j<multimersTableD.length;j++) { 
                        for (int k=0;k<2;k++) multimersTableD[j][k]-=SMCoord[k];
                        /*if (multimersTableD[j][0]>=-12 && multimersTableD[j][0]<=12
                           && multimersTableD[j][1]>=-12 && multimersTableD[j][1]<=12)
                            System.err.format(Locale.ENGLISH,"{%.3f,%.3f},{%.3f,%.3f},",
                                         multimersTableD[j][0],multimersTableD[j][1],
                                         multimersTableD[j][0]+ROkreguOpisanego*Math.cos(multimersTableD[j][2]),
                                         multimersTableD[j][1]+ROkreguOpisanego*Math.sin(multimersTableD[j][2]));*/
                        for (int k=0;k<6;k++) {
                            int x=(int)Math.round((multimersTableD[j][0]+ROkreguOpisanego*Math.cos(multimersTableD[j][2]+k*2.0*b)-histogramRange[0])/cellDr);
                            if (x>=0 && x<rozdzielczosc) {
                                int y=(int)Math.round((multimersTableD[j][1]+ROkreguOpisanego*Math.sin(multimersTableD[j][2]+k*2.0*b)-histogramRange[0])/cellDr);
                                if (y>=0 && y<rozdzielczosc) probabDens[x][y]++;
                            }
                        }
                    }                    
                    System.gc();
                    System.out.println("timeUsed: "+(System.currentTimeMillis()-startTime));
                }
                reader.close();
                System.err.print("{");
                for (int j=0;j<rozdzielczosc;j++) {
                    System.err.print("{");
                    for (int k=0;k<rozdzielczosc-1;k++)
                        System.err.format(Locale.ENGLISH,"%d,",probabDens[j][k]);
                    if (j<rozdzielczosc-1) System.err.format(Locale.ENGLISH,"%d},",probabDens[j][(int)rozdzielczosc-1]);
                    else System.err.format(Locale.ENGLISH,"%d}",probabDens[j][(int)rozdzielczosc-1]);
                }
                System.err.print("}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    static double getRemainder (double a, double b) { //assuming: b>0, MATHEMATICA Mod[a,b] sprowadza a zawsze do dodatniej wartości (javove metody kończą gdy Abs[a]<b)
        while (a>=b || a<0) if (a>0) a-=b; else a+=b;
        return a;
    }
    
    public static void main(String[] args) {        
        try {
            new transientFilesNodeProbability();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}