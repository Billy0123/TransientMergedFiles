
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;

public class transientFilesODF2 {

    public transientFilesODF2 () throws FileNotFoundException {
        System.setErr(new PrintStream(new File("ODF2TransJava.txt")));
        try {
            BufferedReader reader = new BufferedReader(new FileReader("ODF1TransJava.txt"));
            String line, pressures[] = new String[1000];
            double componentCounter[]=new double[1000], averageCos6PhiAll[]=new double[1000], 
                   ODFMaxAll[]=new double[1000], ODF_AllP[][]=new double[1000][];
            int ODFLength=0;
            while ((line=reader.readLine())!=null) {
                String splittedLine[] = line.split("\t");
                if (ODFLength==0) ODFLength=splittedLine.length-3;
                int licznik=0;
                for (int i=0;i<pressures.length;i++) 
                    if (pressures[i]!=null) {
                        if (splittedLine[0].equals(pressures[i])) {
                            licznik=i; break;
                        } 
                    } else {
                        pressures[i] = splittedLine[0];
                        ODFMaxAll[i] = 0;
                        ODF_AllP[i] = new double[ODFLength];
                        for (int j=0;j<ODFLength;j++) ODF_AllP[i][j]=0;
                        licznik=i; break;
                    }
                averageCos6PhiAll[licznik]+=Double.parseDouble(splittedLine[1]);
                componentCounter[licznik]+=Double.parseDouble(splittedLine[2]);
                for (int i=3;i<ODFLength+3;i++) ODF_AllP[licznik][i-3]+=Double.parseDouble(splittedLine[i]);
            }
            reader.close();
            double b=Math.PI/6.0, dPhi=2.0*b/((double)(ODFLength-1.0));
            for (int i=0;i<pressures.length;i++) 
                if (pressures[i]!=null) {
                    double suma=0;
                    for (int j=0;j<ODFLength;j++) suma+=ODF_AllP[i][j]; for (int j=0;j<ODFLength;j++) ODF_AllP[i][j]/=suma*dPhi;
                    averageCos6PhiAll[i]/=componentCounter[i]; for (int j=0;j<ODFLength;j++) if (ODFMaxAll[i]<ODF_AllP[i][j]) ODFMaxAll[i]=ODF_AllP[i][j];
                } else break;
            
            System.err.println("p*\tODFMaxAll\taverageCos6PhiAll");
            for (int i=0;i<pressures.length;i++) 
                if (pressures[i]!=null) {
                    System.err.println(pressures[i]+"\t"+String.valueOf(ODFMaxAll[i])+"\t"+String.valueOf(averageCos6PhiAll[i]));
                    BufferedWriter writer = new BufferedWriter(new FileWriter("ODF2TransJava_arg-"+pressures[i]+".txt"));
                    for (int j=0;j<ODFLength;j++) {
                        writer.write(String.valueOf(-b+j*dPhi)+"\t"+String.valueOf(ODF_AllP[i][j]));
                        writer.newLine();
                    }
                    writer.close();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static double getRemainder (double a, double b) { //assuming: b>0, MATHEMATICA Mod[a,b] sprowadza a zawsze do dodatniej wartości (javove metody kończą gdy Abs[a]<b)
        while (a>=b || a<0) if (a>0) a-=b; else a+=b;
        return a;
    }
    
    public static void main(String[] args) {
        try {
            new transientFilesODF2();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}