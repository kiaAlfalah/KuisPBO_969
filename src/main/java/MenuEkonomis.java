public class MenuEkonomis extends Menu {
    public MenuEkonomis(String kode, String namaMenu, int stok) {
        super(kode, namaMenu, stok);
    }
    @Override
    public String getKategori() { return "Ekonomis"; }
}