package Hospital_Project;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;


import static Hospital_Project.HospitalService.*;
import static Hospital_Project.DataBankService.*;

public class DoctorService implements Methods {


    private static final LinkedList<Doctor> doctorList = new LinkedList<>();

    @Override
    public void entryMenu() throws InterruptedException, IOException, SQLException {

        int secim = -1;
        do {
            bringEntryMenu();
            entryMenuSwitch(secim);
        } while (exitStatus(false));

    }

    @Override
    public void add() {
        String doktorAdi, doktorSoyadi, doktorUnvan, addDoctorSql;
        // Doktor Ekleme Metodu



        doktorAdi = inputHandler("Eklemek istediğiniz doktorun ismini girin");
        doktorSoyadi = inputHandler("Eklemek istediginiz doktor soy ismini giriniz");
        doktorUnvan = inputHandler("Eklemek İstediginiz doktor Unvanini Giriniz: \n \t=> Allergist\n\t=> Norolog\n\t=> Genel Cerrah\n\t" +
                "=> Cocuk Doktoru\n\t=> Dahiliye\n\t=> Kardiolog");

        addDoctorSql = "INSERT INTO doctors (doctor_name, doctor_surname, doctor_title) VALUES(?,?,?)";
        try {
            PreparedStatement prst = con.prepareStatement(addDoctorSql);
            prst.setString(1, doktorAdi);
            prst.setString(2, doktorSoyadi);
            prst.setString(3, doktorUnvan);
            prst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //  Doctor doctor = new Doctor(doktorAdi, doktorSoyadi, doktorUnvan);
        //  doctorList.add(doctor);
        System.out.println(doktorAdi + " " + doktorSoyadi + " isimli doktor sisteme başarıyla eklenmiştir...");
        list();
        // Doktor objesini istersek bir listeye ekleyebilir veya başka bir şekilde saklayabiliriz

    }


    @Override
    public void remove() {
        String doktorName, doktorSurname;

        list();

        doktorName = inputHandler("Silmek istediğiniz doktor'un ismini girin.");

        doktorSurname = inputHandler("Silmek istediğiniz doktor'un ismini girin.");

        deleteDoctor(doktorName, doktorSurname);

        list();
    }

    public void findDoctorByTitle() {
        String unvan;
        unvan = inputHandler("Bulmak Istediginiz Doktorun Unvanini Giriniz:\n\t=> Allergist\n\t=> Norolog\n\t" +
                 "=> Genel Cerrah\n\t=> Cocuk Doktoru\n\t=> Dahiliye Uzmanı\n\t=> Kardiolog");
        System.out.println("------------------------------------------------------");
        System.out.println("---------- HASTANEDE BULUNAN DOKTORLARİMİZ -----------");
        System.out.printf("%-13s | %-15s | %-15s\n", "DOKTOR İSİM", "DOKTOR SOYİSİM", "DOKTOR UNVAN");
        System.out.println("------------------------------------------------------");
        isDoctorPresent(unvan);


        if (!isDoctorPresent(unvan)) {
            System.out.println("BU UNVANA AİT DOKTOR BULUNMAMAKTADIR");
            slowPrint("\033[39mANAMENU'YE YONLENDIRILIYORSUNUZ...\033[0m\n", 20);
        }
        System.out.println("------------------------------------------------------");

    }

    public void list() {
        System.out.println("------------------------------------------------------");
        System.out.println("---------- HASTANEDE BULUNAN DOKTORLARİMİZ -----------");
        System.out.printf("%-13s | %-15s | %-15s\n", "DOKTOR İSİM", "DOKTOR SOYİSİM", "DOKTOR UNVAN");
        System.out.println("------------------------------------------------------");
        bringDoctors();

    }


    public Doctor findDoctor(String unvan) {
        Doctor doctor = new Doctor();
        for (int i = 0; i < hospital.unvanlar.size(); i++) {
            if (hospital.unvanlar.get(i).equals(unvan)) {
                doctor.setIsim(hospital.doctorIsimleri.get(i));
                doctor.setSoyIsim(hospital.doctorSoyIsimleri.get(i));
                doctor.setUnvan(hospital.unvanlar.get(i));
                break;
            }
        }
        return doctor;
    }


    public void createList() {
        for (String w : DataBank.unvanlar) {
            doctorList.add(findDoctor(w));
        }
    }


    private void bringEntryMenu() {
        System.out.println("=========================================");
        System.out.println("""
                LUTFEN YAPMAK ISTEDIGINIZ ISLEMI SECINIZ:
                \t=> 1-DOKTORLARI LISTELE
                \t=> 2-UNVANDAN DOKTOR BULMA
                \t=> 3-HASTA BULMA
                \t=> 4-HASTALARI LISTELE\s
                \t=> 0-ANAMENU""");
        System.out.println("=========================================");
    }

    public void entryMenuSwitch(int secim) throws SQLException, IOException, InterruptedException {
        do {


            try {
                secim = scan.nextInt();

            } catch (Exception e) {
                System.out.println("\"LUTFEN SIZE SUNULAN SECENEKLERIN DISINDA VERI GIRISI YAPMAYINIZ!\"");

            }

            switch (secim) {
                case 1:
                    list();
                    break;
                case 2:
                    findDoctorByTitle();
                    break;
                case 3:
                    System.out.println("BULMAK İSTEDİĞİNİZ HASTANIN DURUMUNU GİRİNİZ...");
                    String durum = scan.nextLine().trim();
                    //System.out.println(hastaBul(durum));
                    patientService.listPatientByCase(durum);
                    //o durumda bir hasta yoksa hicbir sey dondurmuyor
                    break;
                case 4:
                    patientService.list();
                    break;
                case 0:
                    slowPrint("ANA MENUYE YÖNLENDİRİLİYORSUNUZ...\n", 20);
                    exitStatus(true);
                    break;
                default:
                    System.out.println("HATALI GİRİŞ, TEKRAR DENEYİNİZ...\n");
            }
        } while (exitStatus(false));
    }

    public boolean isDoctorPresent(String unvan) {
        for (Doctor w : doctorList) {
            if (w.getUnvan().equalsIgnoreCase(unvan)) {
                System.out.printf("%-13s | %-15s | %-15s\n", w.getIsim(), w.getSoyIsim(), w.getUnvan());
                break;
            }
        }
        return true;
    }


    public void bringDoctors() {
        for (Doctor w : doctorList) {
            System.out.printf("%-13s | %-15s | %-15s\n", w.getIsim(), w.getSoyIsim(), w.getUnvan());
        }
    }


    @Override
    public boolean exitStatus(boolean status) {
        return status;
    }

    @Override
    public String inputHandler(String prompt) {
        System.out.println(prompt); // Displays the prompt to the user
        return scan.nextLine().trim(); // Returns the user input directly
    }

    public void deleteDoctor(String name, String surname) {
        boolean isDeleted = false;
        for (Doctor w : doctorList) {
            if (w.getIsim().equalsIgnoreCase(name) && w.getSoyIsim().equalsIgnoreCase(surname)) {
                System.out.println(w.getIsim() + " " + w.getSoyIsim() + " isimli doktor sistemden basariyla silinmistir...");
                doctorList.remove(w);
                isDeleted = true;
                break;
            }
        }
        if (!isDeleted) {
            System.out.println("SİLMEK İSTEDİGİNİZ DOKTOR LİSTEMİZDE BULUNMAMAKTADIR");
        }
    }


}



