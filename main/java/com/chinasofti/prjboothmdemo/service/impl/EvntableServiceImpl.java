package com.chinasofti.prjboothmdemo.service.impl;

import com.chinasofti.prjboothmdemo.dao.EvntableDao;
import com.chinasofti.prjboothmdemo.entity.Evntable;
import com.chinasofti.prjboothmdemo.service.EvntableService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.*;


/**
 * (Evntable)表服务实现类
 *
 * @author makejava
 * @since 2024-03-06 12:51:10
 */
@Service("evntableService")
public class EvntableServiceImpl implements EvntableService {
    @Resource
    private EvntableDao evntableDao;

    /**
     * 通过ID查询单条数据
     *
     * @param eid 主键
     * @return 实例对象
     */
    @Override
    public Evntable queryById(Integer eid) {
        return this.evntableDao.queryById(eid);
    }

    /**
     * 分页查询
     *
     * @param evntable 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Evntable> queryByPage(Evntable evntable, PageRequest pageRequest) {
        long total = this.evntableDao.count(evntable);
        return new PageImpl<>(this.evntableDao.queryAllByLimit(evntable, pageRequest), pageRequest, total);
    }
    /**
    查找所有数据
    */
    public List<Evntable> queryAll(){
        return this.evntableDao.queryAll();
    }
    /**
     * 统计总行数
     *
     * @param evntable 查询条件
     * @return 总行数
     */
    public long count(Evntable evntable){
          return   this.evntableDao.count(evntable);
    }


    /**
     * 新增数据
     *
     * @param evntable 实例对象
     * @return 实例对象
     */
    @Override
    public Evntable insert(Evntable evntable) {
        this.evntableDao.insert(evntable);
        return evntable;
    }

    /**
     * 修改数据
     *
     * @param evntable 实例对象
     * @return 实例对象
     */
    @Override
    public Evntable update(Evntable evntable) {
        this.evntableDao.update(evntable);
        return this.queryById(evntable.getEid());
    }

    /**
     * 通过主键删除数据
     *
     * @param eid 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer eid) {
        return this.evntableDao.deleteById(eid) > 0;
    }

    @Override
    public List<Map<String, Object>> findGroupVlaue() {
        return evntableDao.findGroupVlaue();
    }

    @Override
    public List<Float> findLastWendu() {
        return evntableDao.findLastWendu();
    }

    @Override
    public List<Float> findLastShidu() {
        return evntableDao.findLastShidu();
    }

    @Override
    public List<Float> findLastQiwei() {
        return evntableDao.findLastQiwei();
    }

    @Override
    public List<Float> findRightnow() {
        return evntableDao.findRightnow();
    }

    @Override
    public List<Float> findLastGuangqiang() {
        return evntableDao.findLastGuangqiang();
    }


}
