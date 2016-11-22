
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

public class resultmerger {

    public resultmerger () throws FileNotFoundException {
        File workDirectory = new File("../TRANS");
        System.setErr(new PrintStream(new File("../clusterSizeTransJava.txt")));
        for (int i=0;i<workDirectory.listFiles().length;i++) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(workDirectory.listFiles()[i]));
                double avDrSquared=0,avDr=0,avVRel=0,avMaxDr=0,a=Math.PI/3.0,b=Math.PI/6.0;
                int lineLicznik=0; String line; 
                while ((line=reader.readLine())!=null) {
                    long startTime=System.currentTimeMillis();
                    lineLicznik++;
                    String configurationFile[] = line.split("\t");
                    System.out.println("File: "+String.valueOf(i+1)+"/"+String.valueOf(workDirectory.listFiles().length)+", configuration: "+String.valueOf(lineLicznik)+"/"+String.valueOf(100));
                    double boxSize[] = new double[]{Double.parseDouble(configurationFile[1]),Double.parseDouble(configurationFile[2])};
                    String multimers[] = configurationFile[4].split("m");
                    double multimersTableD[][] = new double[multimers.length-1][3];
                    for (int j=1;j<multimers.length;j++) {
                        String multimersTable[]=multimers[j].substring(1,multimers[j].indexOf("]")).split(",");
                        for (int k=0;k<3;k++) multimersTableD[j-1][k]=Double.parseDouble(multimersTable[k]);
                    }
                    double maxDr=0;
                    for (int k=0;k<multimersTableD.length;k++) {
                        double drSquared=100000000;
                        double phi1=getRemainder(multimersTableD[k][2],a)-b;
                        for (int l=0;l<multimersTableD.length;l++) if (k!=l) {
                            double phi2=getRemainder(multimersTableD[l][2],a)-b;
                            if (phi1*phi2<0) {
                                double rx = multimersTableD[k][0] - multimersTableD[l][0];
                                rx -= boxSize[0]*Math.round(rx/boxSize[0]);
                                double ry = multimersTableD[k][1] - multimersTableD[l][1];
                                ry -= boxSize[1]*Math.round(ry/boxSize[1]);
                                drSquared = Math.min(drSquared,rx*rx + ry*ry);
                            }
                        }
                        avDrSquared += drSquared;
                        double dr = Math.sqrt(drSquared);
                        avDr += dr;
                        maxDr = Math.max(maxDr, dr);
                    }
                    avVRel += boxSize[0]*boxSize[1]/6.062177826489734/(double)multimersTableD.length;
                    avMaxDr += maxDr;
                    System.out.println("timeUsed: "+(System.currentTimeMillis()-startTime));
                }
                reader.close();
                avDrSquared /= 12480.0*(double)lineLicznik;
                avDr /= 12480.0*(double)lineLicznik;
                avVRel /= (double)lineLicznik;
                avMaxDr /= (double)lineLicznik;
                String resultsLine = workDirectory.listFiles()[i].getName().substring(23,28)+"\t"+
                                     String.valueOf(avVRel)+"\t"+String.valueOf(avDrSquared)+"\t"+
                                     String.valueOf(avDr)+"\t"+String.valueOf(avMaxDr);
                System.err.println(resultsLine);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    double getRemainder (double a, double b) { //assuming: b>0, MATHEMATICA Mod[a,b] sprowadza a zawsze do dodatniej wartości (javove metody kończą gdy Abs[a]<b)
        while (a>=b || a<0) if (a>0) a-=b; else a+=b;
        return a;
    }
    
    public static void main(String[] args) {
        try {
            new resultmerger();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}