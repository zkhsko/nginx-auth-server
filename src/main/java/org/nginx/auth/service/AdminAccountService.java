package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.util.BasicPaginationUtils;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2025/1/10 16:22
 */
@Service
public class AdminAccountService {

    @Autowired
    private UserRepository userRepository;

    public BasicPaginationVO<User> userListPage(int page, int size) {
        PageHelper.startPage(page, size, "id desc");
        List<User> userList = userRepository.selectList(null);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return BasicPaginationUtils.create(pageInfo);
    }

    public void changeUserBlock(Long id, Boolean block) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getId, id);
        lambdaUpdateWrapper.set(User::getBlocked, block);
        userRepository.update(null, lambdaUpdateWrapper);
    }

}
