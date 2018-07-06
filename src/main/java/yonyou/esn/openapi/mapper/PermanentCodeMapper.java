package yonyou.esn.openapi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import yonyou.esn.openapi.bo.PermanentCodeBo;

/**
 * permanent_code DAO操作
 * created by zhaohlp
 */
@Mapper
public interface PermanentCodeMapper {

    int insert(PermanentCodeBo permanentCodeBo);

    PermanentCodeBo get(@Param("qzId") int qzId);

    int delete(@Param("suiteKey") String suiteKey,@Param("qzId") Integer qzId);

}