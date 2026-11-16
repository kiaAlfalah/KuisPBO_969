import java.io.*;
import java.util.*;

public class SistemRestoran implements Serializable {
    private String namaResto;
    private HashMap<String, Pelanggan> daftarPelanggan = new HashMap<>();
    private HashMap<String, Menu> daftarMenu = new HashMap<>();
    private ArrayList<Transaksi> riwayatTransaksi = new ArrayList<>();

    public SistemRestoran(String namaResto) {
        this.namaResto = namaResto;
    }

    public void tambahPelanggan(String id, String nama) {
        daftarPelanggan.put(id, new Pelanggan(id, nama));
    }

    public void tambahMenu(String kode, String namaMenu, int stok) {
        daftarMenu.put(kode, new Menu(kode, namaMenu, stok));
    }

    public void jalankanTransaksiDariFile(String filename) throws Exception {
        File file = new File(filename);
        if (!file.exists()) {
            throw new TransaksiException("File " + filename + " tidak ditemukan!");
        }

        Scanner scanner = new Scanner(file);
        StringBuilder konten = new StringBuilder();
        while (scanner.hasNextLine()) {
            konten.append(scanner.nextLine());
        }
        scanner.close();

        String[] dataTransaksi = konten.toString().split("\\|");
        int nomorBaris = 1;
        
        for (String item : dataTransaksi) {
            item = item.trim();
            if (item.isEmpty()) continue;

            String[] parts = item.split("\\.");
            String idPelanggan = "";
            String kodeMenu = "";
            int jumlah = 0;

            for (String part : parts) {
                if (part.startsWith("ID>")) {
                    idPelanggan = part.substring(3).trim();
                } else if (part.startsWith("KD>")) {
                    kodeMenu = part.substring(3).trim();
                } else if (part.startsWith("JML>")) {
                    jumlah = Integer.parseInt(part.substring(4).trim());
                }
            }

            Pelanggan p = daftarPelanggan.get(idPelanggan);
            Menu m = daftarMenu.get(kodeMenu);
 
            if (p == null) {
                System.out.println("[GAGAL TRANSAKSI Baris " + nomorBaris + "] ID Pelanggan '" + idPelanggan + "' tidak terdaftar di sistem.");
                nomorBaris++;
                continue;
            }
            if (m == null) {
                System.out.println("[GAGAL TRANSAKSI Baris " + nomorBaris + "] Kode Menu '" + kodeMenu + "' tidak ditemukan.");
                nomorBaris++;
                continue;
            }
            if (m.getStok() < jumlah) {
                System.out.println("[GAGAL TRANSAKSI Baris " + nomorBaris + "] Stok menu '" + m.getNamaMenu() + "' (" + m.getKode() + ") tidak mencukupi. Tersedia: " + m.getStok() + ", Diminta: " + jumlah);
                nomorBaris++;
                continue;
            }

            m.kurangiStok(jumlah);
            riwayatTransaksi.add(new Transaksi(p, m, jumlah));
            System.out.println("[BERHASIL] " + p.getNama() + " (" + p.getId() + ") memesan " + jumlah + " porsi " + m.getNamaMenu());
            
            nomorBaris++;
        }
    }

    public void simpanLaporanAkhir() {
        System.out.println("\n--- MENYIMPAN LAPORAN AKHIR & SERIALISASI ---");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("state.bak"))) {
            oos.writeObject(this);
            System.out.println("[SUKSES SERIALISASI] Status objek sistem berhasil disimpan ke 'state.bak'.");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal melakukan serialisasi: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("laporan.txt"))) {
            writer.println("==================================================================");
            writer.println("               LAPORAN TRANSAKSI " + namaResto.toUpperCase() + "          ");
            writer.println("==================================================================");
            writer.println(String.format("%-10s | %-15s | %-25s | %-5s", "ID PEL", "NAMA PELANGGAN", "NAMA MENU", "QTY"));
            writer.println("------------------------------------------------------------------");

            for (Transaksi t : riwayatTransaksi) {
                String baris = String.format("%-10s | %-15s | %-25s | %-5d",
                        t.getPelanggan().getId(),
                        t.getPelanggan().getNama(),
                        t.getMenu().getNamaMenu(),
                        t.getJumlah());
                writer.println(baris);
            }
            writer.println("==================================================================");
            System.out.println("[SUKSES LAPORAN] Laporan format kolom berhasil ditulis ke 'laporan.txt'.");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis file laporan: " + e.getMessage());
        }
    }
}