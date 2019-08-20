package cn.duniqb.copydy.service.impl;


import cn.duniqb.copydy.common.utils.JSONResult;
import cn.duniqb.copydy.dao.UsersLikeVideosMapper;
import cn.duniqb.copydy.dao.UsersMapper;
import cn.duniqb.copydy.model.Users;
import cn.duniqb.copydy.model.UsersLikeVideos;
import cn.duniqb.copydy.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    /**
     * 查询用户是否存在
     *
     * @param username
     * @return
     */
    @SuppressWarnings("all")
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);

        return result != null;
    }

    /**
     * 保存用户
     *
     * @param user
     */
    @SuppressWarnings("all")
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        // 全局 ID
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    /**
     * 根据用户名和密码查询登录
     *
     * @param username
     * @param password
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {

        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        Users result = usersMapper.selectOneByExample(userExample);

        return result;
    }

    /**
     * 用户更新个人信息
     *
     * @param user
     */
    @Override
    public void updateUserInfo(Users user) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersMapper.updateByExampleSelective(user, userExample);
    }

    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);

        return usersMapper.selectOneByExample(userExample);
    }

    /**
     * 查询用户是否喜欢点赞视频
     *
     * @param userId
     * @param videoId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);

        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);

        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }
}
