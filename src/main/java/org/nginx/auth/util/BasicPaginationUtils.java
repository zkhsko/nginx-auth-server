package org.nginx.auth.util;

import com.github.pagehelper.PageInfo;
import org.nginx.auth.dto.vo.BasicPaginationVO;

/**
 * @author dongpo.li
 * @date 2025/1/2 17:14
 */
public class BasicPaginationUtils {


    public static <T> BasicPaginationVO<T> create(PageInfo<T> pageInfo) {
        BasicPaginationVO<T> basicPaginationVO = new BasicPaginationVO<>();
        basicPaginationVO.setData(pageInfo.getList());
        basicPaginationVO.setPage(pageInfo.getPageNum());
        basicPaginationVO.setSize(pageInfo.getPageSize());
        basicPaginationVO.setPages(pageInfo.getPages());
        basicPaginationVO.setTotal(pageInfo.getTotal());
        basicPaginationVO.setPrev(pageInfo.getPrePage());
        int nextPage = pageInfo.getNextPage();
        if (nextPage == 0 && pageInfo.getPages() > 0) {
            nextPage = pageInfo.getPages();
        }
        basicPaginationVO.setNext(nextPage);
        return basicPaginationVO;
    }

    public static <T> BasicPaginationVO<T> createEmpty() {
        BasicPaginationVO<T> basicPaginationVO = new BasicPaginationVO<>();
        basicPaginationVO.setData(null);
        basicPaginationVO.setPage(1);
        basicPaginationVO.setSize(10);
        basicPaginationVO.setPages(0);
        basicPaginationVO.setTotal(0L);
        basicPaginationVO.setPrev(0);
        basicPaginationVO.setNext(0);
        return basicPaginationVO;
    }

    public static <T> BasicPaginationVO<T> copy(PageInfo<?> pageInfo) {
        BasicPaginationVO<T> basicPaginationVO = new BasicPaginationVO<>();
//        basicPaginationVO.setData(pageInfo.getList());
        basicPaginationVO.setPage(pageInfo.getPageNum());
        basicPaginationVO.setSize(pageInfo.getPageSize());
        basicPaginationVO.setPages(pageInfo.getPages());
        basicPaginationVO.setTotal(pageInfo.getTotal());
        basicPaginationVO.setPrev(pageInfo.getPrePage());
        int nextPage = pageInfo.getNextPage();
        if (nextPage == 0 && pageInfo.getPages() > 0) {
            nextPage = pageInfo.getPages();
        }
        basicPaginationVO.setNext(nextPage);
        return basicPaginationVO;
    }

}
