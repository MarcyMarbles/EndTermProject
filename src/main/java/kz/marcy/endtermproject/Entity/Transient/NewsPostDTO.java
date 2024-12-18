package kz.marcy.endtermproject.Entity.Transient;

import kz.marcy.endtermproject.Entity.News;
import lombok.Data;

import java.util.List;

@Data
public class NewsPostDTO {
    private News newsDTO;
    private String[] ids;
}
