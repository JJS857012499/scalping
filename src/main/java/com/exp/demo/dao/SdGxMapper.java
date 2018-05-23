package com.exp.demo.dao;

import com.exp.demo.pojo.*;

public interface SdGxMapper
{
    int deleteByPrimaryKey(final String p0);
    
    int insert(final SdGx p0);
    
    int insertSelective(final SdGx p0);
    
    SdGx selectByPrimaryKey(final String p0);
    
    int updateByPrimaryKeySelective(final SdGx p0);
    
    int updateByPrimaryKey(final SdGx p0);
}
