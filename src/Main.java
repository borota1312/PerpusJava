import java.io.*;
import java.time.Year;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner ranoMenu = new Scanner(System.in);
        String ranoPilihan;
        Boolean ranoLanjut = true;
        while (ranoLanjut){
            ranoHapusLayar();
            System.out.println("Database Perpustakaan\n");
            System.out.println("1.\tLihat Seluruh Buku");
            System.out.println("2.\tCari Data Buku");
            System.out.println("3.\tTambah Data Buku");
            System.out.println("4.\tUbah Data Buku");
            System.out.println("5.\tHapus Data Buku");
            System.out.println("6.\tKeluar");
            System.out.println("\n\nPilihan Anda: ");
            ranoPilihan = ranoMenu.next();

            switch (ranoPilihan){
                case "1":
                    System.out.println("\n=================");
                    System.out.println("LIST SELURUH BUKU");
                    System.out.println("=================");
                    ranoTampilkanData();
                    break;
                case "2":
                    System.out.println("\n=================");
                    System.out.println("CARI BUKU");
                    System.out.println("=================");
                    ranoCariData();
                    break;
                case "3":
                    System.out.println("\n=================");
                    System.out.println("TAMBAH DATA BUKU");
                    System.out.println("=================");
                    ranoTambahData();
                    ranoTampilkanData();
                    break;
                case "4":
                    System.out.println("\n=================");
                    System.out.println("UBAH DATA BUKU");
                    System.out.println("=================");
                    ranoUpdateData();
                    break;
                case "5":
                    System.out.println("\n=================");
                    System.out.println("HAPUS DATA BUKU");
                    System.out.println("=================");
                    ranoDeleteData();
                    break;
                case "6":
                    ranoLanjut = false;
                    ranoLanjut = ranoYaAtauTidak("Apakah anda ingin keluar");
                    continue;
                default:
                    System.err.println("\nInput anda tidak ditemukan\nSilahkan pilih 1-6");
            }
            ranoLanjut = ranoYaAtauTidak("Apakah anda ingin melanjutkan");
        }

    }

    private static void ranoUpdateData() throws IOException{
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        System.out.println("List Buku");
        ranoTampilkanData();

        Scanner terminalInput = new Scanner(System.in);
        System.out.println("\nMasukkan nomor buku yang akan diupdate: ");
        int updateNum = terminalInput.nextInt();

        String data = bufferedInput.readLine();
        int entryCounts = 0;

        while (data!=null){
            entryCounts++;

            StringTokenizer st = new StringTokenizer(data,",");

            if (updateNum == entryCounts){
                System.out.println("\nData yang ingin anda update adalah: ");
                System.out.println("-----------------------------------");
                System.out.println("Referensi         : "+st.nextToken());
                System.out.println("Tahun             : "+st.nextToken());
                System.out.println("Penulis           : "+st.nextToken());
                System.out.println("Penerbit          : "+st.nextToken());
                System.out.println("Judul             : "+st.nextToken());

                String[] fieldData = {"tahun","penulis","penerbit","judul"};
                String[] tempData = new String[4];

                st = new StringTokenizer(data,",");
                String originalData = st.nextToken();

                for (int i = 0; i < fieldData.length; i++) {
                    boolean isUpdate = ranoYaAtauTidak("Apakah anda ingin merubah "+fieldData[i]);
                    originalData = st.nextToken();
                    if (!isUpdate){
                        if (fieldData[i].equalsIgnoreCase("tahun")){
                            System.out.println("masukkan tahun terbit, format= (YYYY): ");
                            tempData[i] = ranoAmbilTahun();
                        }else {
                            terminalInput = new Scanner(System.in);
                            System.out.println("\nMasukkan "+fieldData[i]+" baru: ");
                            tempData[i] = terminalInput.nextLine();
                        }
                    }else {
                        tempData[i] = originalData;
                    }
                }
                st = new StringTokenizer(data,",");
                st.nextToken();
                System.out.println("\nData baru anda adalah: ");
                System.out.println("-----------------------------------");
                System.out.println("Tahun             : "+st.nextToken()+" --> "+tempData[0]);
                System.out.println("Penulis           : "+st.nextToken()+" --> "+tempData[1]);
                System.out.println("Penerbit          : "+st.nextToken()+" --> "+tempData[2]);
                System.out.println("Judul             : "+st.nextToken()+" --> "+tempData[3]);

                boolean isUpdate = ranoYaAtauTidak("Apakah anda yakin ingin mengupdate data tersebut");

                if (isUpdate){
                    boolean isExist = cekBukuDiDatabase(tempData,false);
                    if (isExist){
                        System.err.println("data buku sudah ada di database, proses update dibatalkan, \nSilahkan delete data yang bersangkutan");
                        bufferedOutput.write(data);
                    }else {
                        String tahun = tempData[0];
                        String penulis = tempData[1];
                        String penerbit = tempData[2];
                        String judul = tempData[3];

                        long nomorEntry = ambilEntryPerTahun(penulis, tahun)+1;
                        String penulisTanpaSpasi = penulis.replaceAll("\\S+","");
                        String primaryKey = penulisTanpaSpasi+"_"+tahun+"_"+nomorEntry;

                        bufferedOutput.write(primaryKey+","+tahun+","+penulis+","+penerbit+","+judul);
                    }
                } else {
                    bufferedOutput.write(data);
                }
            } else {
                bufferedOutput.write(data);
            }
            bufferedOutput.newLine();
            data = bufferedInput.readLine();
        }
        bufferedOutput.flush();
        fileInput.close();
        fileOutput.close();

        database.delete();
        tempDB.renameTo(database);
    }

    private static void ranoDeleteData() throws IOException{
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        System.out.println("List Buku");
        ranoTampilkanData();

        Scanner terminalInput = new Scanner(System.in);
        System.out.println("\nMasukkan nomor buku yang akan dihapus: ");
        int deleteNum = terminalInput.nextInt();

        boolean isFound = false;
        int entryCounts = 0;
        String data = bufferedInput.readLine();

        while(data!=null){
            entryCounts++;
            boolean isDelete = false;

            StringTokenizer st = new StringTokenizer(data,",");

            if (deleteNum==entryCounts){
                System.out.println("\nData yang ingin anda hapus adalah: ");
                System.out.println("-----------------------------------");
                System.out.println("Referensi         : "+st.nextToken());
                System.out.println("Tahun             : "+st.nextToken());
                System.out.println("Penulis           : "+st.nextToken());
                System.out.println("Penerbit          : "+st.nextToken());
                System.out.println("Judul             : "+st.nextToken());

                isDelete = ranoYaAtauTidak("Apakah anda yakin akan menghapus?");
                isFound = true;
            }
            if (isDelete){
                System.out.println("Data berhasil dihapus");
            } else {
                bufferedOutput.write(data);
                bufferedOutput.newLine();
            }
            data = bufferedInput.readLine();
        }

        if (!isFound){
            System.err.println("Buku tidak ditemukan");
        }

        bufferedOutput.flush();
        database.delete();
        tempDB.renameTo(database);
    }

    public static void ranoTampilkanData() throws IOException{
        FileReader fileInput;
        BufferedReader bufferInput;
        try {
            fileInput = new FileReader ("database.txt") ;
            bufferInput = new BufferedReader (fileInput) ;
        } catch (Exception e) {
            System.err.println("Database Tidak ditemukan");
            System.err.println("Silahkan tambah data terlebih dahoeloe");
            ranoTambahData();
            return;
        }
        System.out.println ("\n| No |\tTahun |\tPenulis     |\tPenerbit     |\tJudul Buku");
        System.out.println(
                "-------------------------------------------------------------------------------------------------------");
        String data = bufferInput.readLine();
        int nomorData = 0;
        while(data != null){
            nomorData++;
            StringTokenizer stringToken = new StringTokenizer(data, ",");
            stringToken.nextToken();
            System.out.printf("|%2d", nomorData);
            System.out.printf("|\t%4s", stringToken.nextToken());
            System.out.printf("|\t%-20s", stringToken.nextToken());
            System.out.printf("|\t%-20s", stringToken.nextToken());
            System.out.printf("|\t%s", stringToken.nextToken());
            System.out.print("\n");

            data = bufferInput.readLine();
        }
        System.out.println(
                "-------------------------------------------------------------------------------------------------------");
    }

    private static void ranoCariData() throws IOException{
        try {
            File file = new File("database.txt");
        }catch (Exception e){
            System.err.println("Database tidak ditemukan");
            System.err.println("Silahkan tambah data terlebih dahulu");
            ranoTambahData();
            return;
        }
        Scanner terminalInput = new Scanner(System.in);
        System.out.println("Masukkan kata kunci untuk mencari buku: ");
        String cariString = terminalInput.nextLine();
        String[] keywords = cariString.split("\\s+");

        cekBukuDiDatabase(keywords,true);
    }

    private static void ranoTambahData() throws IOException{
        FileWriter fileOutput = new FileWriter("database.txt",true);
        BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

        Scanner terminalInput = new Scanner(System.in);
        String penulis, judul, penerbit, tahun;

        System.out.println("masukan nama penulis: ");
        penulis = terminalInput.nextLine();
        System.out.println("masukan judul buku: ");
        judul = terminalInput.nextLine();
        System.out.println("masukan nama penerbit: ");
        penerbit = terminalInput.nextLine();
        System.out.println("masukan tahun terbit: ");
        tahun = ranoAmbilTahun();

        String[] keywords = {tahun+","+penulis+","+penerbit+","+judul};
        System.out.println(Arrays.toString(keywords));

        boolean isExist = cekBukuDiDatabase(keywords, false);

        if (!isExist){
            System.out.println(ambilEntryPerTahun(penulis,tahun));
            long nomorEntry = ambilEntryPerTahun(penulis, tahun)+1;

            String penulisTanpaSpasi = penulis.replaceAll("\\s+", "");
            String primaryKey = penulisTanpaSpasi+"_"+tahun+"_"+nomorEntry;
            System.out.println("\nData yang akan anda masukkan adalah");
            System.out.println("----------------------------------------------");
            System.out.println("primary key  : "+primaryKey);
            System.out.println("tahun terbit : "+tahun);
            System.out.println("penulis      : "+penulis);
            System.out.println("judul        : "+judul);
            System.out.println("penerbit     : "+penerbit);

            boolean isTambah = ranoYaAtauTidak("Apakah anda ingin menambah data tersebut?");

            if (isTambah){
                bufferOutput.write(primaryKey+','+tahun+','+penulis+','+penerbit+','+judul);
                bufferOutput.newLine();
                bufferOutput.flush();
            }

        }else {
            System.out.println("Buku yang anda masukan sudah tersedia di database dengan data tersebut");
            cekBukuDiDatabase(keywords,true);
        }

        bufferOutput.close();
    }

    private static long ambilEntryPerTahun(String penulis, String tahun) throws IOException{
        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        long entry = 0;
        String data = bufferInput.readLine();
        Scanner dataScanner;
        String primaryKey;

        while(data != null){
            dataScanner = new Scanner(data);
            dataScanner.useDelimiter(",");
            primaryKey = dataScanner.next();
            dataScanner = new Scanner(primaryKey);
            dataScanner.useDelimiter("_");

            penulis = penulis.replaceAll("\\s+","");

            if(penulis.equalsIgnoreCase(dataScanner.next()) && tahun.equalsIgnoreCase(dataScanner.next())){
                entry = dataScanner.nextInt();
            }

            data = bufferInput.readLine();
        }
        return entry;
    }

    public static String ranoAmbilTahun() throws IOException{
        boolean tahunValid = false;
        Scanner terminalInput = new Scanner(System.in);
        String tahunInput = terminalInput.nextLine();

        while (!tahunValid){
            try{
                Year.parse(tahunInput);
                tahunValid = true;
            } catch (Exception e){
                System.out.println("Format tahun yang anda masukan salah, format=[yyyy]");
                System.out.println("silahkan masukan tahun lagi: ");
                tahunValid = false;
                tahunInput = terminalInput.nextLine();
            }
        }
        return tahunInput;
    }

    private static boolean cekBukuDiDatabase(String[] keywords, boolean isDisplay) throws IOException{
        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        String data = bufferInput.readLine();
        boolean isExist = false;
        int nomorData = 0;
        if (isDisplay){
            System.out.println ("\n| No |\tTahun |\tPenulis     |\tPenerbit     |\tJudul Buku");
            System.out.println(
                    "-------------------------------------------------------------------------------------------------------");
        }

        while(data != null){
            isExist = true;
            for (String keyword : keywords){
                isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
            }
            if (isExist){
                if (isDisplay){
                    nomorData++;
                    StringTokenizer stringToken = new StringTokenizer(data, ",");
                    stringToken.nextToken();
                    System.out.printf("|%2d", nomorData);
                    System.out.printf("|\t%4s", stringToken.nextToken());
                    System.out.printf("|\t%-20s", stringToken.nextToken());
                    System.out.printf("|\t%-20s", stringToken.nextToken());
                    System.out.printf("|\t%s", stringToken.nextToken());
                    System.out.print("\n");
                }else {
                    break;
                }
            }
            data = bufferInput.readLine();
        }

        if (isDisplay){
            System.out.println(
                    "-------------------------------------------------------------------------------------------------------");
        }

        return isExist;
    }

    private static Boolean ranoYaAtauTidak(String ranoPesan) {
        Scanner ranoMenu = new Scanner(System.in);
        System.out.println("\n"+ranoPesan+" (y/t)? ");
        String ranoPilihan = ranoMenu.next();

        while (!ranoPilihan.equalsIgnoreCase("y") && !ranoPilihan.equalsIgnoreCase("t")){
            System.err.println("Pilihan anda bukan y/t");
            System.out.println("\n"+ranoPesan+" (y/t)? ");
            ranoPilihan = ranoMenu.next();
        }
        if (ranoPesan.equalsIgnoreCase("Apakah anda ingin keluar")){
            if (ranoPilihan.equalsIgnoreCase("y")){
                System.out.println("Dibuat oleh Muhammad Rano Pane Trias");
                return ranoPilihan.equalsIgnoreCase("t");
            }else {
                return ranoPilihan.equalsIgnoreCase("t");
            }
        } else if (ranoPesan.equalsIgnoreCase("Apakah anda ingin melanjutkan")) {
            if (ranoPilihan.equalsIgnoreCase("y")){
                return ranoPilihan.equalsIgnoreCase("y");
            }else {
                System.out.println("Dibuat oleh Muhammad Rano Pane Trias");
                return ranoPilihan.equalsIgnoreCase("y");
            }
        }else if (ranoPesan.equalsIgnoreCase("Apakah anda ingin menambah data tersebut?")) {
            if (ranoPilihan.equalsIgnoreCase("y")){

                return ranoPilihan.equalsIgnoreCase("y");
            }else {
                System.out.println("Data tidak jadi ditambahkan");
                return ranoPilihan.equalsIgnoreCase("y");
            }
        } else if (ranoPesan.equalsIgnoreCase("Apakah anda yakin ingin mengupdate data tersebut")) {
            if (ranoPilihan.equalsIgnoreCase("y")){
                System.out.println("Data Berhasil Diupdate");
                return ranoPilihan.equalsIgnoreCase("y");
            }else {
                System.out.println("Data tidak jadi diupdate");
                return ranoPilihan.equalsIgnoreCase("y");
            }
        }else if (ranoPesan.equalsIgnoreCase("Apakah anda ingin merubah ")) {
            if (ranoPilihan.equalsIgnoreCase("y")){
                System.out.println("Data Berhasil Diupdate");
                return ranoPilihan.equalsIgnoreCase("y");
            }else {
                System.out.println("Data tidak jadi dihapus");
                return ranoPilihan.equalsIgnoreCase("y");
            }
        }else if (ranoPesan.equalsIgnoreCase("Apakah anda yakin akan menghapus?")) {
            if (ranoPilihan.equalsIgnoreCase("y")){

                return ranoPilihan.equalsIgnoreCase("y");
            }else {
                System.out.println("Data tidak jadi dihapus");
                return ranoPilihan.equalsIgnoreCase("y");
            }
        }else {
            return ranoPilihan.equalsIgnoreCase("t");
        }
    }

    private static void ranoHapusLayar() {
        try {
            if (System. getProperty ("os.name") .contains ("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print ("\033\143");
            }
        } catch (Exception ex) {
            System.err.println("Tidak Bisa Hapus Layar");
        }
    }
}