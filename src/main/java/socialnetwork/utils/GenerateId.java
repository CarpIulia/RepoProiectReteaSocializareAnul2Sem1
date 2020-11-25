package socialnetwork.utils;
public class GenerateId {
    Long nr;
    public GenerateId(Long maxId) {
        nr=maxId;
    }

    public Long newId() {
        return ++nr;
    }
}
