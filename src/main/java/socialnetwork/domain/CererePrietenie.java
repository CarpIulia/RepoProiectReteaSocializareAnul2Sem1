package socialnetwork.domain;

public class CererePrietenie extends Entity<Long>{
    Long id1;
    Long id2;
    String status;
    String mesaj;
    public CererePrietenie(Long id1, Long id2, String ststus, String mesaj) {
        this.id1 = id1;
        this.id2 = id2;
        this.status = ststus;
        this.mesaj = mesaj;
    }

    public Long getId1() {
        return id1;
    }

    public Long getId2() {
        return id2;
    }

    public String getStatus() {
        return status;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
