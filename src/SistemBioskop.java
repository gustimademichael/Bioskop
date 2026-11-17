import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SistemBioskop implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nama;
    private HashMap<String, Penonton> daftarPenonton = new HashMap<>();
    private HashMap<String, JadwalFilm> daftarJadwal = new HashMap<>();
    private ArrayList<Transaksi> logTransaksi = new ArrayList<>();

    public SistemBioskop(String nama) {
        this.nama = nama;
    }

    public void tambahPenonton(String id, String nama) {
        daftarPenonton.put(id, new Penonton(id, nama));
    }

    // Polymorphism: jenis studio ditentukan dari nama jadwal
    public void tambahJadwalFilm(String kode, String judul, int kuota) {
        JadwalFilm jf;
        if (judul.startsWith("Premiere")) {
            jf = new JadwalPremiere(kode, judul, kuota);
        } else if (judul.startsWith("Reguler")) {
            jf = new JadwalReguler(kode, judul, kuota);
        } else {
            jf = new JadwalEkonomis(kode, judul, kuota);
        }
        daftarJadwal.put(kode, jf);
    }

    /**
     * Format Varian 9: ID=ACT-01,KD=OBJ-A,JML=3,|ID=ACT-02,KD=OBJ-C,JML=2,|...
     * Record dipisah "|", field dipisah ",", key-value dipisah "=".
     */
    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        File fileInput = cariFile(namaFile);
        if (fileInput == null) {
            throw new FileNotFoundException("File '" + namaFile + "' tidak ditemukan. Dicari di: "
                    + new File(".").getAbsolutePath() + ", "
                    + new File("src").getAbsolutePath() + ", "
                    + new File("..").getAbsolutePath());
        }
        System.out.println("[INFO] Membaca file: " + fileInput.getAbsolutePath());

        StringBuilder isi = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileInput))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                isi.append(baris.trim());   // sambung jika ada baris baru
            }
        }

        String[] records = isi.toString().split("\\|");
        int no = 0;
        for (String rec : records) {
            rec = rec.trim();
            if (rec.isEmpty()) continue;
            no++;
            try {
                prosesRecord(rec);
            } catch (FormatDataException | DataTidakDitemukanException | KuotaTidakCukupException e) {
                System.out.println("[GAGAL] Transaksi #" + no + ": " + e.getMessage());
            }
        }
    }

    // Cari file di folder kerja, folder src, dan folder induk (root project NetBeans)
    private File cariFile(String namaFile) {
        String[] lokasi = { ".", "src", "..", "../src" };
        for (String dir : lokasi) {
            File f = new File(dir, namaFile);
            if (f.exists() && f.isFile()) {
                return f;
            }
        }
        return null;
    }

    private void prosesRecord(String rec)
            throws FormatDataException, DataTidakDitemukanException, KuotaTidakCukupException {
        Map<String, String> data = new HashMap<>();
        for (String bagian : rec.split(",")) {
            bagian = bagian.trim();
            if (bagian.isEmpty()) continue;
            String[] kv = bagian.split("=", 2);
            if (kv.length != 2) {
                throw new FormatDataException("Format salah pada bagian '" + bagian + "'");
            }
            data.put(kv[0].trim(), kv[1].trim());
        }

        String idAktor = data.get("ID");
        String kode = data.get("KD");
        String jml = data.get("JML");
        if (idAktor == null || kode == null || jml == null) {
            throw new FormatDataException("Field ID/KD/JML tidak lengkap pada '" + rec + "'");
        }

        int qty;
        try {
            qty = Integer.parseInt(jml);
        } catch (NumberFormatException e) {
            throw new FormatDataException("JML bukan angka: '" + jml + "'");
        }
        if (qty <= 0) {
            throw new FormatDataException("JML harus lebih dari 0");
        }

        Penonton p = daftarPenonton.get(idAktor);
        if (p == null) {
            throw new DataTidakDitemukanException("Penonton " + idAktor + " tidak terdaftar");
        }
        JadwalFilm jf = daftarJadwal.get(kode);
        if (jf == null) {
            throw new DataTidakDitemukanException("Jadwal film " + kode + " tidak ditemukan");
        }

        jf.kurangiKuota(qty);   // bisa melempar KuotaTidakCukupException
        Transaksi t = new Transaksi(p, jf, qty);
        logTransaksi.add(t);
        System.out.println("[OK] " + p.getNama() + " memesan " + qty + " tiket " + jf.getJudul()
                + " | Total: Rp" + (long) t.getTotal() + " | Sisa kuota: " + jf.getKuota());
    }

    public void simpanLaporanAkhir() {
        // 1. Object Serialization -> dump_sys.dat
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dump_sys.dat"))) {
            oos.writeObject(this);
            System.out.println("[INFO] Serialisasi berhasil: dump_sys.dat");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal serialisasi: " + e.getMessage());
        }

        // 2. Laporan teks berformat tag ala XML -> laporan.txt
        try (FileWriter fw = new FileWriter("laporan.txt")) {
            for (Transaksi t : logTransaksi) {
                fw.write("<aktor>" + t.getPenonton().getId() + "</aktor>"
                        + "<qty>" + t.getQty() + "</qty>\n");
            }
            System.out.println("[INFO] Laporan berhasil ditulis: laporan.txt");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis laporan: " + e.getMessage());
        }
    }
}
