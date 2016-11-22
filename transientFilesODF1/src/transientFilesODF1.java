
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Arrays;

public class transientFilesODF1 {

    public transientFilesODF1 (int startFileIndex) throws FileNotFoundException {
        File workDirectory = new File("../TRANS");
        
        FileOutputStream fos = new FileOutputStream("../ODF1TransJava.txt",true);
        PrintStream printStream = new PrintStream(fos);
        System.setErr(printStream); 
        
        File[] workDirectoryFiles=workDirectory.listFiles();
        Arrays.sort(workDirectoryFiles);
        for (int i=startFileIndex;i<workDirectoryFiles.length;i++) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(workDirectoryFiles[i]));
                int ODFLength=201;
                double componentCounter=0, averageCos6PhiAll=0, ODF_AllP[]=new double[ODFLength],
                       a=Math.PI/3.0,b=Math.PI/6.0;
                for (int j=0;j<ODFLength;j++) ODF_AllP[j]=0;
                int lineLicznik=0; String line; 
                while ((line=reader.readLine())!=null) {
                    long startTime=System.currentTimeMillis();
                    lineLicznik++;
                    String configurationFile[] = line.split("\t");
                    System.out.println("File: "+String.valueOf(i+1)+"/"+String.valueOf(workDirectoryFiles.length)+", configuration: "+String.valueOf(lineLicznik)+"/"+String.valueOf(100));
                    String multimers[] = configurationFile[4].split("m");
                    double multimersTableD[][] = new double[multimers.length-1][3];
                    for (int j=1;j<multimers.length;j++) {
                        String multimersTable[]=multimers[j].substring(1,multimers[j].indexOf("]")).split(",");
                        for (int k=0;k<3;k++) multimersTableD[j-1][k]=Double.parseDouble(multimersTable[k]);
                    }
                    for (int k=0;k<multimersTableD.length;k++) {
                        double angle = getRemainder(multimersTableD[k][2],a)-b;
                        averageCos6PhiAll+=Math.cos(6.0*angle); componentCounter++;
                        int index = (int)Math.round((angle+b)/2.0/b*(double)(ODFLength-1.0));
                        ODF_AllP[index]++;
                    }
                    System.gc();
                    System.out.println("timeUsed: "+(System.currentTimeMillis()-startTime));
                }
                reader.close();
                String resultsLine = workDirectoryFiles[i].getName().substring(23,28)+"\t"+
                                     String.valueOf(averageCos6PhiAll)+"\t"+String.valueOf(componentCounter);
                for (int j=0;j<ODFLength;j++) resultsLine+="\t"+String.valueOf(ODF_AllP[j]);
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
            new transientFilesODF1(Integer.parseInt(args[0]));  //args[0] FROM 0
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}