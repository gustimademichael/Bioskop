public class JadwalReguler extends JadwalFilm {
    private static final long serialVersionUID = 1L;
    public JadwalReguler(String kode, String judul, int kuota) { super(kode, judul, kuota); }
    @Override
    public double getHargaTiket() { return 50000; }
}
