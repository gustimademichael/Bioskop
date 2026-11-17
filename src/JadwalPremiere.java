public class JadwalPremiere extends JadwalFilm {
    private static final long serialVersionUID = 1L;
    public JadwalPremiere(String kode, String judul, int kuota) { super(kode, judul, kuota); }
    @Override
    public double getHargaTiket() { return 75000; }
}
