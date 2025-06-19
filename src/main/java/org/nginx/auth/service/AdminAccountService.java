package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.AccountInfo;
import org.nginx.auth.repository.AccountInfoRepository;
import org.nginx.auth.util.BasicPaginationUtils;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2025/1/10 16:22
 */
@Service
public class AdminAccountService {

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    public BasicPaginationVO<AccountInfo> accountListPage(int page, int size) {
        PageHelper.startPage(page, size, "id desc");
        List<AccountInfo> accountInfoList = accountInfoRepository.selectList(null);
        PageInfo<AccountInfo> pageInfo = new PageInfo<>(accountInfoList);
        return BasicPaginationUtils.create(pageInfo);
    }

    public void changeAccountBlock(Long id, Boolean block) {
        LambdaUpdateWrapper<AccountInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AccountInfo::getId, id);
        lambdaUpdateWrapper.set(AccountInfo::getBlocked, block);
        accountInfoRepository.update(null, lambdaUpdateWrapper);
    }

}
