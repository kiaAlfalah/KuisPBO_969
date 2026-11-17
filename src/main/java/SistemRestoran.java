import java.io.*;
import java.util.*;

public class SistemRestoran implements Serializable {
    private static final long serialVersionUID = 1L;
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
        if (kode.equalsIgnoreCase("OBJ-A")) {
            daftarMenu.put(kode, new MenuPremium(kode, namaMenu, stok));
        } else if (kode.equalsIgnoreCase("OBJ-B")) {
            daftarMenu.put(kode, new MenuReguler(kode, namaMenu, stok));
        } else {
            daftarMenu.put(kode, new MenuEkonomis(kode, namaMenu, stok));
        }
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

            try {
                String[] parts = item.split("\\.");
                String idPelanggan = "";
                String kodeMenu = "";
                int jumlah = 0;

                for (String part : parts) {
                    part = part.trim();
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
                    throw new TransaksiException("ID Pelanggan '" + idPelanggan + "' tidak terdaftar di sistem.");
                }
                if (m == null) {
                    throw new TransaksiException("Kode Menu '" + kodeMenu + "' tidak ditemukan.");
                }
                if (m.getStok() < jumlah) {
                    throw new TransaksiException("Stok menu '" + m.getNamaMenu() + "' (" + m.getKode() + ") tidak mencukupi. Tersedia: " + m.getStok() + ", Diminta: " + jumlah);
                }

                m.kurangiStok(jumlah);
                riwayatTransaksi.add(new Transaksi(p, m, jumlah));
                System.out.println("[BERHASIL] " + p.getNama() + " (" + p.getId() + ") memesan " + jumlah + " porsi " + m.getNamaMenu() + " [" + m.getKategori() + "]");

            } catch (TransaksiException | NumberFormatException e) {
                System.out.println("[GAGAL TRANSAKSI Baris " + nomorBaris + "] " + e.getMessage());
            } finally {
                nomorBaris++;
            }
        }
    }

    public void simpanLaporanAkhir() {
        System.out.println("\n--- MENYIMPAN LAPORAN AKHIR & SERIALISASI ---");

        // 1. Serialization ke state.bak (Digit Ketiga = 9)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("state.bak"))) {
            oos.writeObject(this);
            System.out.println("[SUKSES SERIALISASI] Status objek sistem berhasil disimpan ke 'state.bak'.");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal melakukan serialisasi: " + e.getMessage());
        }

        // 2. Penulisan laporan format kolom rata ke laporan.txt (Digit Ketiga = 9)
        try (PrintWriter writer = new PrintWriter(new FileWriter("laporan.txt"))) {
            writer.println("=========================================================================");
            writer.println("               LAPORAN TRANSAKSI " + namaResto.toUpperCase());
            writer.println("=========================================================================");
            writer.println(String.format("%-10s | %-15s | %-25s | %-10s | %-5s",
                    "ID PEL", "NAMA PELANGGAN", "NAMA MENU", "KATEGORI", "QTY"));
            writer.println("-------------------------------------------------------------------------");

            for (Transaksi t : riwayatTransaksi) {
                String baris = String.format("%-10s | %-15s | %-25s | %-10s | %-5d",
                        t.getPelanggan().getId(),
                        t.getPelanggan().getNama(),
                        t.getMenu().getNamaMenu(),
                        t.getMenu().getKategori(),
                        t.getJumlah());
                writer.println(baris);
            }
            writer.println("=========================================================================");
            System.out.println("[SUKSES LAPORAN] Laporan format kolom berhasil ditulis ke 'laporan.txt'.");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis file laporan: " + e.getMessage());
        }
    }
}