package com.liuxu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.common.constants.admin.AdminConstants;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.feigns.ArticleFeign;
import com.liuxu.feigns.WemediaFeign;
import com.liuxu.model.article.pojos.AppAuthor;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.dtos.AuthDTO;
import com.liuxu.model.user.dtos.RealNameDTO;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.pojos.AppUserRealName;
import com.liuxu.model.wemedia.pojos.WmUser;
import com.liuxu.user.mapper.AppUserMapper;
import com.liuxu.user.mapper.AppUserRealnameMapper;
import com.liuxu.user.service.AppUserRealnameService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * app端用户认证
 */
@Service
@Transactional
public class AppUserRealnameServiceImpl extends ServiceImpl<AppUserRealnameMapper, AppUserRealName>
        implements AppUserRealnameService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private WemediaFeign wemediaFeign;

    @Override
    public Boolean submit(RealNameDTO realNameDTO) {
        // 1. 获取当前登录用户
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 2. 检查是否已经提交过实名认证
        long count = count(Wrappers.<AppUserRealName>lambdaQuery()
                .eq(AppUserRealName::getUserId, userId)
                .eq(AppUserRealName::getStatus, AdminConstants.WAIT_AUTH));
        if (count > 0) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "已经提交过实名认证，请耐心等待审核");
        }
        AppUserRealName appUserRealName = new AppUserRealName();
        BeanUtils.copyProperties(realNameDTO, appUserRealName);
        appUserRealName.setUserId(userId);
        appUserRealName.setStatus(AdminConstants.WAIT_AUTH);
        appUserRealName.setCreatedTime(new Date());
        return this.save(appUserRealName);
    }

    /**
     * 根据状态查询需要认证相关的用户信息
     *
     * @param authDTO
     * @return
     */
    @Override
    public List<AppUserRealName> loadListByStatus(AuthDTO authDTO) {
        // 1. 校验参数 分页
        if (authDTO == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        authDTO.checkParam();
        // 2. 封装查询条件
        Page<AppUserRealName> pageReq = new Page<>(authDTO.getPage(), authDTO.getSize());
        LambdaQueryWrapper<AppUserRealName> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(authDTO.getStatus() != null, AppUserRealName::getStatus, authDTO.getStatus());
        // 3. 执行查询,封装分页查询结果
        IPage<AppUserRealName> page = page(pageReq, queryWrapper);
        return page.getRecords();
    }

    /**
     * 根据状态进行审核
     *
     * @param dto
     * @param status 2 审核失败 9 审核成功
     * @return
     */
    @Override
    public boolean updateStatusById(AuthDTO dto, Short status) {
        try {
            // 1.校验参数
            if (dto.getId() == null) {
                CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE, "实名认证id不能为空");
            }

            // 2. 根据实名认证的id 查询 ap_user_realname 数据
            AppUserRealName userRealName = getById(dto.getId());

            // 3. 判断实名认证的状态 是否为待审核 (1)
            if (!AdminConstants.WAIT_AUTH.equals(userRealName.getStatus())) {
                CustException.throwException(AppHttpCodeEnum.DATA_NOT_ALLOW, "实名认证的状态不是待审核状态");
            }

            // 4. 根据实名认证信息关联的apUserId 查询出apUser信息
            AppUser appUser = appUserMapper.selectById(userRealName.getUserId());
            if (appUser == null) {
                CustException.throwException(AppHttpCodeEnum.USER_NOT_EXIST);
            }
            // 5.修改实名认证的状态
            userRealName.setStatus(status);
            if (StringUtils.isNotBlank(dto.getReason())) {
                // 驳回原因
                userRealName.setReason(dto.getReason());
            }

            userRealName.setUpdatedTime(new Date());
            // 修改状态
            updateById(userRealName);

            // 6. 判断 状态是2（审核失败 方法结束） 还是9（审核成功 开通账户）
            if (AdminConstants.FAIL_AUTH.equals(status)) {
                return true;
            }

            // 7. 如果是9 代表审核通过
            appUser.setIdentityAuthentication(1);
            appUser.setFlag(1);
            appUserMapper.updateById(appUser);

            // 8. 开通自媒体账户(查询是否已经开通过,保存自媒体账户信息)
            WmUser wmUser = createWmUser(appUser);

            // 9. 创建作者信息(查询是否创建过,开通账户)
            createAppAuthor(appUser, wmUser);
            return true;
        } catch (Exception e) {
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, e.getMessage());
            return false;
        }
    }

    /**
     * 远程创建作者信息
     *
     * @param appUser
     * @param wmUser
     */
    private void createAppAuthor(AppUser appUser, WmUser wmUser) {
        // 1. 根据用户id 查询作者信息是否存在
        ResponseResult<AppAuthor> result = articleFeign.findByUserId(appUser.getId());
        if (result.checkCode()) {
            // 远程调用失败
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }

        // 2. 如果作者存在 抛出异常 提示已存在
        AppAuthor author = result.getData();
        if (author != null) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "作者已存在");
        }

        // 3. 如果作者不存在 创建作者信息，并远程保存
        author = new AppAuthor();
        author.setCreatedTime(new Date());
        author.setName(wmUser.getName());
        author.setType(AdminConstants.AUTHOR_TYPE); // 自媒体人类型
        author.setUserId(wmUser.getAppUserId()); // APP 用户ID
        author.setWmUserId(wmUser.getId()); // 自媒体用户ID
        ResponseResult<Boolean> resultSave = articleFeign.save(author);

        if (resultSave.checkCode()) {
            // 远程调用失败
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }
    }

    /**
     * 开通自媒体用户信息
     *
     * @param appUser
     * @return
     */
    private WmUser createWmUser(AppUser appUser) {
        // 1. 根据手机号查询自媒体用户信息
        ResponseResult<WmUser> result = wemediaFeign.findByPhone(appUser.getPhone());
        if (result.checkCode()) {
            // 远程调用失败
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }
        WmUser wmUser = result.getData();
        // 2. 如果已经存在
        if (wmUser != null) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "自媒体账户已存在");
        }
        // 3. 如果不存在，基于apUser创建wmUser 并远程调用保存方法
        wmUser = new WmUser();
        wmUser.setName(appUser.getName());
        wmUser.setSalt(appUser.getSalt());
        wmUser.setPassword(appUser.getPassword());
        wmUser.setPhone(appUser.getPhone());
        wmUser.setCreatedTime(new Date());
        wmUser.setType(0); // 个人
        wmUser.setAppUserId(appUser.getId()); // app端用户Id
        wmUser.setStatus(AdminConstants.PASS_AUTH.intValue());
        ResponseResult<WmUser> save = wemediaFeign.save(wmUser);
        if (save.checkCode()) {
            // 远程调用失败
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }
        return save.getData();
    }
}
