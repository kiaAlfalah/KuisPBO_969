import java.io.Serializable;

public class Menu implements Serializable {
    private String kode;
    private String namaMenu;
    private int stok;

    public Menu(String kode, String namaMenu, int stok) {
        this.kode = kode;
        this.namaMenu = namaMenu;
        this.stok = stok;
    }

    public String getKode() {
        return kode;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public int getStok() {
        return stok;
    }

    public void kurangiStok(int jumlah) {
        this.stok -= jumlah;
    }
}
