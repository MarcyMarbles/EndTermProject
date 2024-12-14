package kz.marcy.endtermproject.Entity.Transient;

import lombok.Data;

@Data
public class PageWrapper {
    private int page;
    private int size;

    public static PageWrapper of(int page, int size) {
        PageWrapper pageWrapper = new PageWrapper();
        pageWrapper.setPage(page);
        pageWrapper.setSize(size);
        return pageWrapper;
    }
}
