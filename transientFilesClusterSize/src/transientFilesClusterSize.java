
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Arrays;

public class transientFilesClusterSize {

    public transientFilesClusterSize (int startFileIndex) throws FileNotFoundException {
        File workDirectory = new File("../TRANS");

        FileOutputStream fos = new FileOutputStream("../clusterSizeTransJava.txt",true);
        PrintStream printStream = new PrintStream(fos);
        System.setErr(printStream);  
        
        File[] workDirectoryFiles=workDirectory.listFiles();
        Arrays.sort(workDirectoryFiles);
        for (int i=startFileIndex;i<workDirectoryFiles.length;i++) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(workDirectoryFiles[i]));
                double avDrSquared=0,avDr=0,avVRel=0,avMaxDr=0,a=Math.PI/3.0,b=Math.PI/6.0;
                int lineLicznik=0; String line; 
                long startTime1=System.currentTimeMillis();
                while ((line=reader.readLine())!=null) {
                    long startTime2=System.currentTimeMillis();
                    lineLicznik++;
                    String configurationFile[] = line.split("\t");
                    System.out.println("File: "+String.valueOf(i+1)+"/"+String.valueOf(workDirectoryFiles.length)+", configuration: "+String.valueOf(lineLicznik)+"/"+String.valueOf(100));
                    double boxSize[] = new double[]{Double.parseDouble(configurationFile[1]),Double.parseDouble(configurationFile[2])};
                    String multimers[] = configurationFile[4].split("m");
                    double multimersTableD[][] = new double[multimers.length-1][3];
                    for (int j=1;j<multimers.length;j++) {
                        String multimersTable[]=multimers[j].substring(1,multimers[j].indexOf("]")).split(",");
                        for (int k=0;k<3;k++) multimersTableD[j-1][k]=Double.parseDouble(multimersTable[k]);
                    }
                    long startTime3=System.currentTimeMillis();
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
                    long startTime4=System.currentTimeMillis();
                    System.out.println("readingFileTime: "+(startTime2-startTime1)+", readedFileTextAnalysisTime: "+(startTime3-startTime2)+", mathAnalysisTime: "+(startTime4-startTime3));
                    startTime1=System.currentTimeMillis();
                    System.gc();
                }
                reader.close();
                avDrSquared /= 49920.0*(double)lineLicznik;
                avDr /= 49920.0*(double)lineLicznik;
                avVRel /= (double)lineLicznik;
                avMaxDr /= (double)lineLicznik;
                String resultsLine = workDirectoryFiles[i].getName().substring(23,28)+"\t"+
                                     String.valueOf(avVRel)+"\t"+String.valueOf(avDrSquared)+"\t"+
                                     String.valueOf(avDr)+"\t"+String.valueOf(avMaxDr);
                System.err.println(resultsLine);
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
            new transientFilesClusterSize(Integer.parseInt(args[0]));  //args[0] FROM 0
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}