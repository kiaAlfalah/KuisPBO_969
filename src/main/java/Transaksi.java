import java.io.Serializable;

public class Transaksi implements Serializable {
    private Pelanggan pelanggan;
    private Menu menu;
    private int jumlah;

    public Transaksi(Pelanggan pelanggan, Menu menu, int jumlah) {
        this.pelanggan = pelanggan;
        this.menu = menu;
        this.jumlah = jumlah;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getJumlah() {
        return jumlah;
    }
}
