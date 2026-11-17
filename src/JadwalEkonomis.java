public class JadwalEkonomis extends JadwalFilm {
    private static final long serialVersionUID = 1L;
    public JadwalEkonomis(String kode, String judul, int kuota) { super(kode, judul, kuota); }
    @Override
    public double getHargaTiket() { return 35000; }
}
