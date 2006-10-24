package minimesh;

public class Link {

    private final String title;
    private final String href;

    public Link(String title, String href) {
        this.title = title;
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }
}
