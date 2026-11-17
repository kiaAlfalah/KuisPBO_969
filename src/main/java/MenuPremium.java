public class MenuPremium extends Menu {
    public MenuPremium(String kode, String namaMenu, int stok) {
        super(kode, namaMenu, stok);
    }
    @Override
    public String getKategori() { return "Premium"; }
}