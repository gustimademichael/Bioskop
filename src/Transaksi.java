import java.io.Serializable;

// Association: Transaksi menghubungkan Penonton dan JadwalFilm
public class Transaksi implements Serializable {
    private static final long serialVersionUID = 1L;

    private Penonton penonton;
    private JadwalFilm jadwal;
    private int qty;
    private double total;

    public Transaksi(Penonton penonton, JadwalFilm jadwal, int qty) {
        this.penonton = penonton;
        this.jadwal = jadwal;
        this.qty = qty;
        this.total = jadwal.hitungTotal(qty);
    }

    public Penonton getPenonton() { return penonton; }
    public JadwalFilm getJadwal() { return jadwal; }
    public int getQty() { return qty; }
    public double getTotal() { return total; }
}
