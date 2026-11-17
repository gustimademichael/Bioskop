import java.io.Serializable;

public abstract class JadwalFilm implements Serializable {
    private static final long serialVersionUID = 1L;

    private String kode;
    private String judul;
    private int kuota;

    public JadwalFilm(String kode, String judul, int kuota) {
        this.kode = kode;
        this.judul = judul;
        this.kuota = kuota;
    }

    public String getKode() { return kode; }
    public String getJudul() { return judul; }
    public int getKuota() { return kuota; }

    public void kurangiKuota(int jumlah) throws KuotaTidakCukupException {
        if (jumlah > kuota) {
            throw new KuotaTidakCukupException("Kuota " + kode + " (" + judul + ") tersisa "
                    + kuota + ", diminta " + jumlah);
        }
        kuota -= jumlah;
    }

    public abstract double getHargaTiket();

    public double hitungTotal(int jumlah) {
        return getHargaTiket() * jumlah;
    }
}
