package com.exp.demo.dao;

import com.exp.demo.pojo.*;

public interface SdSgMapper
{
    int deleteByPrimaryKey(final String p0);
    
    int insert(final SdSg p0);
    
    int insertSelective(final SdSg p0);
    
    SdSg selectByPrimaryKey(final String p0);
    
    int updateByPrimaryKeySelective(final SdSg p0);
    
    int updateByPrimaryKey(final SdSg p0);
}
