package com.chinasofti.prjboothmdemo.service;


import com.chinasofti.prjboothmdemo.entity.Evntable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.*;
/**
 * (Evntable)表服务接口
 *
 * @author makejava
 * @since 2024-03-06 12:51:09
 */
public interface EvntableService {

    /**
     * 通过ID查询单条数据
     *
     * @param eid 主键
     * @return 实例对象
     */
    Evntable queryById(Integer eid);

    /**
     * 分页查询
     *
     * @param evntable 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<Evntable> queryByPage(Evntable evntable, PageRequest pageRequest);
    /**
    查找所有数据
    */
    public List<Evntable> queryAll();
    /**
     * 统计总行数
     *
     * @param evntable 查询条件
     * @return 总行数
     */
     public long count(Evntable evntable);

    /**
     * 新增数据
     *
     * @param evntable 实例对象
     * @return 实例对象
     */
    Evntable insert(Evntable evntable);

    /**
     * 修改数据
     *
     * @param evntable 实例对象
     * @return 实例对象
     */
    Evntable update(Evntable evntable);

    /**
     * 通过主键删除数据
     *
     * @param eid 主键
     * @return 是否成功
     */
    boolean deleteById(Integer eid);

    /**
     * 饼图显示的函数
     * */
    public List<Map<String,Object>> findGroupVlaue();

    public List<Float> findLastWendu();

    public List<Float> findLastShidu();

    public List<Float> findLastQiwei();

    public List<Float> findRightnow();

    public List<Float> findLastGuangqiang();
}
