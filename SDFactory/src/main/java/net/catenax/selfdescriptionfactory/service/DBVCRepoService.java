package net.catenax.selfdescriptionfactory.service;

import lombok.RequiredArgsConstructor;
import net.catenax.selfdescriptionfactory.repo.DBVCEntity;
import net.catenax.selfdescriptionfactory.repo.DBVCRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DBVCRepoService {
    private final DBVCRepository repo;

    public List<DBVCEntity> deleteByIds(List<String> ids) {
        return repo.deleteByIds(formatList(ids));
    }

    private String formatList(List<String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return String.join(",", params);
    }
}
