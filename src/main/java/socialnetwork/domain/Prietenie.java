package socialnetwork.domain;

import java.time.LocalDateTime;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    Long id1;
    Long id2;

    public Long getId1() {
        return id1;
    }

    public Long getId2() {
        return id2;
    }



    public Prietenie(Long id1, Long id2, LocalDateTime date) {
        this.id1 = id1;
        this.id2 = id2;
        this.date = date;
    }


    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}
