package com.kkkoke.gtransaction.intercept;

import com.kkkoke.gtransaction.constant.TransactionProperty;
import com.kkkoke.gtransaction.transactional.GlobalTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author KeyCheung
 * @date 2023/10/28
 * @desc
 */
@Component
public class GlobalTransactionRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String groupId = request.getHeader(TransactionProperty.GROUP_ID);
        String transactionCount = request.getHeader(TransactionProperty.TRANSACTION_COUNT);
        GlobalTransactionManager.setCurrentGroupId(groupId);
        GlobalTransactionManager.setTransactionCount(Integer.parseInt(transactionCount == null ? "0" : transactionCount));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
