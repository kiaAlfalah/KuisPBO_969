public class MenuReguler extends Menu {
    public MenuReguler(String kode, String namaMenu, int stok) {
        super(kode, namaMenu, stok);
    }
    @Override
    public String getKategori() { return "Reguler"; }
}