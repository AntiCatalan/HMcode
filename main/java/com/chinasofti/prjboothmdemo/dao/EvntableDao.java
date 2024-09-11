package com.chinasofti.prjboothmdemo.dao;


import com.chinasofti.prjboothmdemo.entity.Evntable;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.*;

/**
 * (Evntable)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-06 12:51:08
 */
@Mapper
public interface EvntableDao {

    /**
     * 通过ID查询单条数据
     *
     * @param eid 主键
     * @return 实例对象
     */
    Evntable queryById(Integer eid);

    /**
     * 查询指定行数据
     *
     * @param evntable 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<Evntable> queryAllByLimit(Evntable evntable, @Param("pageable") Pageable pageable);
    /**
    查找所有数据
    */
    List<Evntable> queryAll();
    /**
     * 统计总行数
     *
     * @param evntable 查询条件
     * @return 总行数
     */
    long count(Evntable evntable);

    /**
     * 新增数据
     *
     * @param evntable 实例对象
     * @return 影响行数
     */
    int insert(Evntable evntable);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Evntable> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Evntable> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Evntable> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Evntable> entities);

    /**
     * 修改数据
     *
     * @param evntable 实例对象
     * @return 影响行数
     */
    int update(Evntable evntable);

    /**
     * 通过主键删除数据
     *
     * @param eid 主键
     * @return 影响行数
     */
    int deleteById(Integer eid);

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

