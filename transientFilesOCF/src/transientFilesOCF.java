
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Locale;

public class transientFilesOCF {

    public transientFilesOCF () throws FileNotFoundException {
        File workDirectory = new File("../TRANS");
        //for (int i=0;i<workDirectory.listFiles().length;i++) {
        for (int i=30;i<30+23;i++) {//SZTUCZNE ZMNIEJSZENIE LICZBY KONFIGURACJI 1/2
            System.setErr(new PrintStream(new File("../j-none_OrientatCorrelFun_arg-"+workDirectory.listFiles()[i].getName().substring(23,28)+".txt")));
            try {
                BufferedReader reader = new BufferedReader(new FileReader(workDirectory.listFiles()[i]));
                double a=Math.PI/3.0,b=Math.PI/6.0,multimerN=6,multimerS=1,multimerD=1,neighRadiusMod=1.2,
                       ROkreguOpisanego=multimerS/(2.0*Math.sin(b)),
                       maxDistance=ROkreguOpisanego*2+multimerD,
                       neighRadius=neighRadiusMod*maxDistance, 
                       neighRadius2=neighRadius*neighRadius;
                
                int lineLicznik=0; String line; 
                while ((line=reader.readLine())!=null) {
                    long startTime=System.currentTimeMillis();
                    lineLicznik++;
                    if (lineLicznik>1) break;//SZTUCZNE ZMNIEJSZENIE LICZBY KONFIGURACJI 2/2
                    String configurationFile[] = line.split("\t");
                    System.out.println("File: "+String.valueOf(i+1)+"/"+String.valueOf(workDirectory.listFiles().length)+", configuration: "+String.valueOf(lineLicznik)+"/"+String.valueOf(100));
                    double boxSize[] = new double[]{Double.parseDouble(configurationFile[1]),Double.parseDouble(configurationFile[2]),Double.parseDouble(configurationFile[3])};
                    String multimers[] = configurationFile[4].split("m");
                    double multimersTableD[][] = new double[multimers.length-1][3];
                    for (int j=1;j<multimers.length;j++) {
                        String multimersTable[]=multimers[j].substring(1,multimers[j].indexOf("]")).split(",");
                        for (int k=0;k<3;k++) multimersTableD[j-1][k]=Double.parseDouble(multimersTable[k]);
                    }
                    
                    //lista sasiadow
                    double normR[][] = new double[multimersTableD.length][2];  
                    int neighbours[][] = new int[multimersTableD.length][50], neighCounter[] = new int[multimersTableD.length];
                    double detBoxMatrix=boxSize[0]*boxSize[1]-boxSize[2]*boxSize[2];
                    for (int j=0;j<multimersTableD.length;j++) {
                        neighCounter[j]=0;
                        normR[j][0]=(boxSize[1]*multimersTableD[j][0]-boxSize[2]*multimersTableD[j][1])/detBoxMatrix;
                        normR[j][1]=-(boxSize[2]*multimersTableD[j][0]-boxSize[0]*multimersTableD[j][1])/detBoxMatrix;
                    }
                    for (int j=0;j<multimersTableD.length-1;j++) for (int k=j+1;k<multimersTableD.length;k++) {
                        double normalizedRX=normR[j][0]-normR[k][0],
                               normalizedRY=normR[j][1]-normR[k][1],
                               rx=multimersTableD[j][0]-multimersTableD[k][0],
                               ry=multimersTableD[j][1]-multimersTableD[k][1];
                        rx-=Math.round(normalizedRX)*boxSize[0]+Math.round(normalizedRY)*boxSize[2];
                        ry-=Math.round(normalizedRX)*boxSize[2]+Math.round(normalizedRY)*boxSize[1];
                        double r2=rx*rx+ry*ry;
                        if (r2<neighRadius2) {
                            neighbours[j][neighCounter[j]++]=k;
                            neighbours[k][neighCounter[k]++]=j;
                        }
                    }
                    
                    //OCF
                    for (int j=0;j<multimersTableD.length;j++) for (int k=0;k<neighCounter[j];k++) {
                        if (j<neighbours[j][k]) {
                            double normalizedRX=normR[j][0]-normR[neighbours[j][k]][0],
                                   normalizedRY=normR[j][1]-normR[neighbours[j][k]][1],
                                   rx=multimersTableD[j][0]-multimersTableD[neighbours[j][k]][0],
                                   ry=multimersTableD[j][1]-multimersTableD[neighbours[j][k]][1];
                            rx-=Math.round(normalizedRX)*boxSize[0]+Math.round(normalizedRY)*boxSize[2];
                            ry-=Math.round(normalizedRX)*boxSize[2]+Math.round(normalizedRY)*boxSize[1];
                            double gamma=Math.atan(ry/rx),
                                   aAngle=multimersTableD[neighbours[j][k]][2]-gamma,
                                   bAngle=multimersTableD[j][2]-gamma;
                            if (multimerN%2!=0) {
                                if (rx>0) bAngle-=b;
                                else aAngle-=b;
                            }
                            aAngle=getRemainder(aAngle,a)-b; bAngle=getRemainder(bAngle,a)-b;
                            System.err.format(Locale.ENGLISH,"{%.17f,%.17f},",aAngle,bAngle);
                        }
                    }
                    System.gc();
                    System.out.println("timeUsed: "+(System.currentTimeMillis()-startTime));
                }
                reader.close();
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
            new transientFilesOCF();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}